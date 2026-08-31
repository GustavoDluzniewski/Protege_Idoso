package com.example.protege_idoso

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WhatsAppNotificationListener : NotificationListenerService() {

    private var ultimaMensagemAnalisada = ""
    private var ultimoHorarioAnalise = 0L

    private val mutexAnalise = Mutex()
    private var analisandoAgora = false

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("ProtegeIdoso", "Serviço de notificações conectado!")
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

        val agora = System.currentTimeMillis()
        val chaveMensagem = "$remetente|$mensagemFinal"

        if (chaveMensagem == ultimaMensagemAnalisada && agora - ultimoHorarioAnalise < 120_000) {
            Log.d("ProtegeIdoso", "Mensagem repetida ignorada.")
            return
        }

        ultimaMensagemAnalisada = chaveMensagem
        ultimoHorarioAnalise = agora

        Log.d("ProtegeIdoso", "Mensagem capturada: $mensagemFinal")

        val resultadoLocal = LocalRiskAnalyzer.analisarMensagem(mensagemFinal)

        Log.d("ProtegeIdoso", "Resultado local: ${resultadoLocal.nivel}")

        if (!LocalRiskAnalyzer.deveEnviarParaIA(mensagemFinal)) {
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

                    val resultadoFinal = OpenAIRiskAnalyzer.analisarMensagem(mensagemFinal)

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