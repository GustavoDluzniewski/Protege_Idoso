package com.example.protege_idoso

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.protege_idoso.historico.AnaliseSalva
import com.example.protege_idoso.historico.HistoricoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.security.MessageDigest

class WhatsAppNotificationListener : NotificationListenerService() {
    private var ultimaMensagemAnalisada = ""
    private var ultimoHorarioAnalise = 0L

    private val mutexAnalise = Mutex()
    private var analisandoAgora = false

    private fun gerarHash(texto: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(texto.toByteArray())

        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun salvarNoHistorico(resultado: RiskResult, nomePacote: String) {
        try {
            HistoricoRepository(applicationContext).salvar(
                AnaliseSalva(
                    dataHora = System.currentTimeMillis(),
                    nivel = resultado.nivel,
                    explicacao = resultado.explicacao,
                    orientacao = resultado.orientacao,
                    origem = if (nomePacote == "com.whatsapp.w4b") {
                        "WhatsApp Business"
                    } else {
                        "WhatsApp"
                    }
                )
            )
            Log.d("ProtegeIdoso", "Resultado salvo no histórico.")
        } catch (e: Exception) {
            Log.e(
                "ProtegeIdoso",
                "Não foi possível salvar no histórico: ${e.javaClass.simpleName}"
            )
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()

        TrancoChecker.carregarLista(applicationContext)

        Log.d("ProtegeIdoso", "Serviço de notificações conectado!")
        Log.d("ProtegeIdoso", "Lista Tranco carregada para consulta.")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if (sbn == null) return

        val nomePacote = sbn.packageName

        if (nomePacote != "com.whatsapp" &&
            nomePacote != "com.whatsapp.w4b"
        ) {
            return
        }

        val preferencias = Preferencias(applicationContext)

        if (!PoliticaDeAlerta.deveAnalisar(
                termosAceitos = preferencias.aceitouTermosAtuais(),
                analisarWhatsApp = preferencias.analisarWhatsApp,
                avisoSuspeito = preferencias.avisoSuspeito,
                avisoGolpe = preferencias.avisoGolpe
            )
        ) {
            Log.d(
                "ProtegeIdoso",
                "Termos não aceitos ou análise desligada nas opções. Mensagem ignorada."
            )
            return
        }

        val extras = sbn.notification.extras

        val remetente =
            extras.getString("android.title") ?: "Remetente desconhecido"

        val texto =
            extras.getCharSequence("android.text")?.toString() ?: ""

        val textoGrande =
            extras.getCharSequence("android.bigText")?.toString() ?: ""

        val mensagemFinal = when {
            texto.isNotBlank() -> texto
            textoGrande.isNotBlank() -> textoGrande
            else -> ""
        }

        if (mensagemFinal.isBlank()) {
            Log.d("ProtegeIdoso", "Mensagem vazia, ignorando.")
            return
        }

        val urlsEncontradas = Regex(
            """(?i)\bhttps?://[^\s<>"']+|\bwww\.[^\s<>"']+"""
        ).findAll(mensagemFinal)
            .map {
                it.value.trimEnd(
                    '.', ',', ';', ':', '!', '?', ')', ']', '}'
                )
            }
            .filter { it.isNotBlank() }
            .distinct()
            .take(5)
            .toList()

        val urlEncontrada = urlsEncontradas.firstOrNull()

        val resultadoTranco = urlEncontrada?.let { url ->
            TrancoChecker.consultarUrl(url)
        }

        if (urlEncontrada != null) {
            Log.d(
                "ProtegeIdoso",
                when (resultadoTranco) {
                    true -> "Domínio encontrado no ranking Tranco."
                    false -> "Domínio não encontrado no ranking Tranco."
                    null -> "Não foi possível consultar o domínio no Tranco."
                }
            )
        }

        val mensagemSanitizada =
            SanitizationService.sanitizeMessage(mensagemFinal)

        Log.d(
            "ProtegeIdoso",
            "Mensagem sanitizada e pronta para análise."
        )

        val agora = System.currentTimeMillis()
        val chaveMensagem =
            gerarHash("$remetente|$mensagemSanitizada")

        if (
            chaveMensagem == ultimaMensagemAnalisada &&
            agora - ultimoHorarioAnalise < 120_000
        ) {
            Log.d("ProtegeIdoso", "Mensagem repetida ignorada.")
            return
        }

        ultimaMensagemAnalisada = chaveMensagem
        ultimoHorarioAnalise = agora

        val resultadoLocal = LocalRiskAnalyzer.analisarMensagem(
            mensagem = mensagemSanitizada,
            dominioForaDoRanking = resultadoTranco == false
        )

        Log.d(
            "ProtegeIdoso",
            "Resultado local: ${resultadoLocal.nivel}"
        )

        if (!LocalRiskAnalyzer.deveEnviarParaIA(
                mensagem = mensagemSanitizada,
                dominioForaDoRanking = resultadoTranco == false
            )
        ) {
            Log.d(
                "ProtegeIdoso",
                "Mensagem segura pelo filtro local. IA não será chamada."
            )
            return
        }

        val urlsParaBackend = urlsEncontradas.map { url ->
            if (url.startsWith("www.", ignoreCase = true)) {
                "https://$url"
            } else {
                url
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            mutexAnalise.withLock {
                if (analisandoAgora) {
                    Log.d(
                        "ProtegeIdoso",
                        "Já existe uma análise em andamento. Ignorando nova chamada."
                    )
                    return@withLock
                }

                analisandoAgora = true

                try {
                    Log.d(
                        "ProtegeIdoso",
                        "Enviando mensagem sanitizada e URLs para análise."
                    )

                    val resultadoFinal =
                        IARiskAnalyzer.analisarMensagem(
                            context = applicationContext,
                            mensagem = mensagemSanitizada,
                            urls = urlsParaBackend
                        )

                    Log.d(
                        "ProtegeIdoso",
                        "Resultado final: ${resultadoFinal.nivel}"
                    )
                    Log.d(
                        "ProtegeIdoso",
                        "Explicação: ${resultadoFinal.explicacao}"
                    )
                    Log.d(
                        "ProtegeIdoso",
                        "Orientação: ${resultadoFinal.orientacao}"
                    )

                    val avisar = PoliticaDeAlerta.deveAvisar(
                        nivel = resultadoFinal.nivel,
                        termosAceitos = preferencias.aceitouTermosAtuais(),
                        analisarWhatsApp = preferencias.analisarWhatsApp,
                        avisoSuspeito = preferencias.avisoSuspeito,
                        avisoGolpe = preferencias.avisoGolpe
                    )

                    if (!avisar) {
                        Log.d(
                            "ProtegeIdoso",
                            "Nenhum alerta exibido (mensagem segura ou aviso desligado nas opções)."
                        )
                    } else {
                        salvarNoHistorico(
                            resultado = resultadoFinal,
                            nomePacote = nomePacote
                        )

                        when (resultadoFinal.nivel) {
                            RiskLevel.SEGURO -> Unit

                            RiskLevel.SUSPEITO -> {
                                AlertHelper.mostrarAlertaSuspeito(
                                    context = applicationContext,
                                    resultado = resultadoFinal
                                )
                            }

                            RiskLevel.POSSIVEL_GOLPE -> {
                                AlertHelper.mostrarAlertaGolpe(
                                    context = applicationContext,
                                    resultado = resultadoFinal
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(
                        "ProtegeIdoso",
                        "Erro durante a análise da notificação: ${e.javaClass.simpleName}"
                    )
                } finally {
                    analisandoAgora = false
                }
            }
        }
    }
}
