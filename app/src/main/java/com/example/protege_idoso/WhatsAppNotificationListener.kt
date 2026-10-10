package com.example.protege_idoso

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
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

        if (nomePacote != "com.whatsapp" && nomePacote != "com.whatsapp.w4b") {
            return
        }

        val extras = sbn.notification.extras

        val remetente = extras.getString("android.title") ?: "Remetente desconhecido"
        val texto = extras.getCharSequence("android.text")?.toString() ?: ""
        val textoGrande = extras.getCharSequence("android.bigText")?.toString() ?: ""

        val mensagemFinal = when {
            texto.isNotBlank() -> texto
            textoGrande.isNotBlank() -> textoGrande
            else -> ""
        }

        if (mensagemFinal.isBlank()) {
            Log.d("ProtegeIdoso", "Mensagem vazia, ignorando.")
            return
        }

        val mensagemSanitizada = SanitizationService.sanitizeMessage(mensagemFinal)


        Log.d("ProtegeIdoso", "Mensagem sanitizada e pronta para análise.")

        val agora = System.currentTimeMillis()
        val chaveMensagem = gerarHash("$remetente|$mensagemSanitizada")

        if (chaveMensagem == ultimaMensagemAnalisada && agora - ultimoHorarioAnalise < 120_000) {
            Log.d("ProtegeIdoso", "Mensagem repetida ignorada.")
            return
        }

        ultimaMensagemAnalisada = chaveMensagem
        ultimoHorarioAnalise = agora

        val urlEncontrada = Regex(
            """(?i)(?:https?://|www\.)[^\s]+|(?:[a-z0-9-]+\.)+[a-z]{2,}(?:/[^\s]*)?"""
        ).find(mensagemFinal)?.value

        val resultadoTranco = urlEncontrada?.let {
            TrancoChecker.consultarUrl(it)

        }
        Log.d(
            "ProtegeIdoso",
            when (resultadoTranco) {
                true -> "Domínio encontrado no ranking Tranco."
                false -> "Domínio não encontrado no ranking Tranco."
                null -> "Não foi possível consultar o domínio no Tranco."
            }
        )

        val resultadoLocal = LocalRiskAnalyzer.analisarMensagem(
            mensagem = mensagemSanitizada,
            dominioForaDoRanking = resultadoTranco == false
        )

        Log.d("ProtegeIdoso", "Resultado local: ${resultadoLocal.nivel}")

        if (!LocalRiskAnalyzer.deveEnviarParaIA(
                mensagem = mensagemSanitizada,
                dominioForaDoRanking = resultadoTranco == false
            )
        ) {
            Log.d("ProtegeIdoso", "Mensagem segura pelo filtro local. IA não será chamada.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            mutexAnalise.withLock {

                if (analisandoAgora) {
                    Log.d("ProtegeIdoso", "Já existe uma análise em andamento. Ignorando nova chamada.")
                    return@withLock
                }

                analisandoAgora = true

                try {
                    Log.d("ProtegeIdoso", "Enviando mensagem para IA...")

                    val resultadoFinal = OpenAIRiskAnalyzer.analisarMensagem(mensagemSanitizada)

                    Log.d("ProtegeIdoso", "Resultado final: ${resultadoFinal.nivel}")
                    Log.d("ProtegeIdoso", "Explicação: ${resultadoFinal.explicacao}")
                    Log.d("ProtegeIdoso", "Orientação: ${resultadoFinal.orientacao}")

                    when (resultadoFinal.nivel) {
                        RiskLevel.SEGURO -> {
                            Log.d("ProtegeIdoso", "Mensagem segura. Nenhum alerta exibido.")
                        }

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

                } finally {
                    analisandoAgora = false
                }
            }
        }
    }
}