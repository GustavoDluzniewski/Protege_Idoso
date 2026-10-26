package com.example.protege_idoso

import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.protege_idoso.historico.AnaliseSalva
import com.example.protege_idoso.historico.HistoricoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.security.MessageDigest
import com.example.protege_idoso.instituicoes.CatalogoInstituicoes
import com.example.protege_idoso.instituicoes.DetectorDeInstituicao
import com.example.protege_idoso.instituicoes.InstituicoesRepository
import com.example.protege_idoso.instituicoes.NumeroOficial
import com.example.protege_idoso.instituicoes.TipoCanal

class WhatsAppNotificationListener : NotificationListenerService() {
    private var ultimaMensagemAnalisada = ""
    private var ultimoHorarioAnalise = 0L

    // Mensagem que passou pelo filtro local e espera a análise com IA.
    private data class Pendente(
        val mensagem: String,
        val urls: List<String>,
        val dominioForaDoRanking: Boolean,
        val resultadoLocal: RiskResult,
        val nomePacote: String
    )

    private val escopo = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Fila de análises: as mensagens são analisadas uma de cada vez, em ordem,
    // em vez de serem descartadas enquanto outra está em análise.
    private val fila = Channel<Pendente>(
        capacity = 20,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onCreate() {
        super.onCreate()

        escopo.launch {
            for (item in fila) {
                processar(item)
            }
        }
    }

    override fun onDestroy() {
        fila.close()
        escopo.cancel()
        super.onDestroy()
    }

    private fun gerarHash(texto: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(texto.toByteArray())

        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun salvarNoHistorico(
        resultado: RiskResult,
        nomePacote: String,
        instituicao: String?
    ): Long? {
        return try {
            val id = HistoricoRepository(applicationContext).salvar(
                AnaliseSalva(
                    dataHora = System.currentTimeMillis(),
                    nivel = resultado.nivel,
                    explicacao = resultado.explicacao,
                    orientacao = resultado.orientacao,
                    origem = if (nomePacote == "com.whatsapp.w4b") {
                        "WhatsApp Business"
                    } else {
                        "WhatsApp"
                    },
                    instituicao = instituicao
                )
            )
            Log.d("ProtegeIdoso", "Resultado salvo no histórico.")
            id
        } catch (e: Exception) {
            Log.e(
                "ProtegeIdoso",
                "Não foi possível salvar no histórico: ${e.javaClass.simpleName}"
            )
            null
        }
    }

    // "3 novas mensagens", "5 mensagens de 2 conversas": resumo, não conteúdo.
    private fun ehResumoDeMensagens(texto: String): Boolean =
        Regex("""^\d+ (novas? )?mensage(m|ns)\b""", RegexOption.IGNORE_CASE)
            .containsMatchIn(texto.trim())

    private fun dicaDoCanalOficial(codigo: String?): String? {
        val modelo = CatalogoInstituicoes.porCodigo(codigo) ?: return null
        val cadastrada = try {
            InstituicoesRepository(applicationContext).buscarPorCodigo(modelo.codigo)
        } catch (e: Exception) {
            null
        }
        val telefone = cadastrada?.telefones?.firstOrNull()?.valor
            ?: modelo.canais.firstOrNull { it.tipo == TipoCanal.TELEFONE }?.valor
            ?: return null
        val nome = cadastrada?.nome ?: modelo.nome
        return "$nome ${NumeroOficial.formatar(telefone)}. " +
                "Não use números ou links que vieram na mensagem."
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

        // Em conversas, o WhatsApp manda as mensagens numa lista (MessagingStyle).
        // Quando várias chegam juntas, "android.text" vira só um resumo
        // ("3 novas mensagens"), então a última mensagem da lista é a que interessa.
        @Suppress("DEPRECATION")
        val ultimaDaConversa = extras.getParcelableArray("android.messages")
            ?.lastOrNull() as? Bundle

        val remetente =
            ultimaDaConversa?.getCharSequence("sender")?.toString()
                ?: extras.getString("android.title")
                ?: "Remetente desconhecido"

        val textoDaConversa =
            ultimaDaConversa?.getCharSequence("text")?.toString() ?: ""

        val texto =
            extras.getCharSequence("android.text")?.toString() ?: ""

        val textoGrande =
            extras.getCharSequence("android.bigText")?.toString() ?: ""

        // Estilo caixa de entrada: uma linha por mensagem.
        val ultimaLinha = extras.getCharSequenceArray("android.textLines")
            ?.lastOrNull()?.toString() ?: ""

        val mensagemFinal = when {
            textoDaConversa.isNotBlank() -> textoDaConversa
            texto.isNotBlank() && !ehResumoDeMensagens(texto) -> texto
            textoGrande.isNotBlank() && !ehResumoDeMensagens(textoGrande) -> textoGrande
            ultimaLinha.isNotBlank() -> ultimaLinha
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

        // Basta um link fora do ranking para contar como sinal de risco.
        val dominioForaDoRanking = urlsEncontradas.any { url ->
            TrancoChecker.consultarUrl(url) == false
        }

        if (urlsEncontradas.isNotEmpty()) {
            Log.d(
                "ProtegeIdoso",
                if (dominioForaDoRanking) {
                    "Algum domínio não foi encontrado no ranking Tranco."
                } else {
                    "Domínios encontrados no ranking Tranco."
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
            dominioForaDoRanking = dominioForaDoRanking
        )

        Log.d(
            "ProtegeIdoso",
            "Resultado local: ${resultadoLocal.nivel}"
        )

        if (!LocalRiskAnalyzer.deveEnviarParaIA(
                mensagem = mensagemSanitizada,
                dominioForaDoRanking = dominioForaDoRanking
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

        fila.trySend(
            Pendente(
                mensagem = mensagemSanitizada,
                urls = urlsParaBackend,
                dominioForaDoRanking = dominioForaDoRanking,
                resultadoLocal = resultadoLocal,
                nomePacote = nomePacote
            )
        )
    }

    private suspend fun processar(item: Pendente) {
        val preferencias = Preferencias(applicationContext)

        try {
            Log.d(
                "ProtegeIdoso",
                "Enviando mensagem sanitizada e URLs para análise."
            )

            val resultadoIA =
                IARiskAnalyzer.analisarMensagem(
                    context = applicationContext,
                    mensagem = item.mensagem,
                    urls = item.urls,
                    dominioForaDoRanking = item.dominioForaDoRanking
                )

            // A IA não pode rebaixar para "seguro" um golpe claro do filtro local.
            // Protege contra mensagens escritas para enganar a IA.
            val resultadoFinal =
                if (item.resultadoLocal.nivel == RiskLevel.POSSIVEL_GOLPE &&
                    resultadoIA.nivel == RiskLevel.SEGURO
                ) {
                    item.resultadoLocal.copy(nivel = RiskLevel.SUSPEITO)
                } else {
                    resultadoIA
                }

            Log.d(
                "ProtegeIdoso",
                "Resultado final: ${resultadoFinal.nivel}"
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
                return
            }

            val instituicaoCitada = (
                    DetectorDeInstituicao.detectar(item.mensagem)
                        ?: DetectorDeInstituicao.detectar(resultadoFinal.explicacao)
                    )?.codigo

            val idAnalise = salvarNoHistorico(
                resultado = resultadoFinal,
                nomePacote = item.nomePacote,
                instituicao = instituicaoCitada
            )

            val dicaOficial = dicaDoCanalOficial(instituicaoCitada)

            when (resultadoFinal.nivel) {
                RiskLevel.SEGURO -> Unit

                RiskLevel.SUSPEITO -> {
                    AlertHelper.mostrarAlertaSuspeito(
                        context = applicationContext,
                        resultado = resultadoFinal,
                        idAnalise = idAnalise,
                        dicaOficial = dicaOficial
                    )
                }

                RiskLevel.POSSIVEL_GOLPE -> {
                    AlertHelper.mostrarAlertaGolpe(
                        context = applicationContext,
                        resultado = resultadoFinal,
                        idAnalise = idAnalise,
                        dicaOficial = dicaOficial
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(
                "ProtegeIdoso",
                "Erro durante a análise da notificação: ${e.javaClass.simpleName}"
            )
        }
    }
}
