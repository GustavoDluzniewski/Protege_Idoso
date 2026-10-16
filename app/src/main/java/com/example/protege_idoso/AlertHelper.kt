package com.example.protege_idoso

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object AlertHelper {


    private const val CANAL_SUSPEITO = "protege_idoso_suspeito"
    private const val CANAL_GOLPE = "protege_idoso_golpe"


    private const val CANAL_ANTIGO = "protege_idoso_alertas"

    private const val ID_SUSPEITO = 2001
    private const val ID_GOLPE = 2002

    private const val COR_SUSPEITO = 0xFFB26A00
    private const val COR_GOLPE = 0xFFC62828

    fun mostrarAlertaSuspeito(context: Context, resultado: RiskResult) {
        criarCanais(context)

        val intent = Intent(context, InstrucoesActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            ID_SUSPEITO,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacao = NotificationCompat.Builder(context, CANAL_SUSPEITO)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setColor(COR_SUSPEITO.toInt())
            .setContentTitle("Mensagem suspeita")
            .setContentText("Esta mensagem pode ser suspeita. Verifique antes de responder.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Esta mensagem pode ser suspeita. Verifique antes de responder.\n\n" +
                                resultado.orientacao
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(ID_SUSPEITO, notificacao)
    }

    fun mostrarAlertaGolpe(context: Context, resultado: RiskResult) {
        criarCanais(context)

        val intent = Intent(context, InstrucoesActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            ID_GOLPE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacao = NotificationCompat.Builder(context, CANAL_GOLPE)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setColor(COR_GOLPE.toInt())
            .setContentTitle("POSSÍVEL GOLPE DETECTADO")
            .setContentText("Atenção! Esta mensagem pode ser um golpe.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Atenção! Esta mensagem pode ser um golpe.\n\n" +
                                "Motivo: ${resultado.explicacao}\n\n" +
                                "Orientação: ${resultado.orientacao}"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(PADRAO_VIBRACAO_GOLPE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(ID_GOLPE, notificacao)
    }

    private val PADRAO_VIBRACAO_SUSPEITO = longArrayOf(0, 300)
    private val PADRAO_VIBRACAO_GOLPE = longArrayOf(0, 600, 250, 600, 250, 600)

    private fun criarCanais(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val canalSuspeito = NotificationChannel(
                CANAL_SUSPEITO,
                "Mensagem suspeita",
                NotificationManager.IMPORTANCE_HIGH
            )
            canalSuspeito.description =
                "Avisos quando uma mensagem parece estranha"
            canalSuspeito.enableVibration(true)
            canalSuspeito.vibrationPattern = PADRAO_VIBRACAO_SUSPEITO

            val canalGolpe = NotificationChannel(
                CANAL_GOLPE,
                "Possível golpe",
                NotificationManager.IMPORTANCE_HIGH
            )
            canalGolpe.description =
                "Avisos fortes quando há grande chance de golpe"
            canalGolpe.enableVibration(true)
            canalGolpe.vibrationPattern = PADRAO_VIBRACAO_GOLPE

            manager.createNotificationChannel(canalSuspeito)
            manager.createNotificationChannel(canalGolpe)
            manager.deleteNotificationChannel(CANAL_ANTIGO)
        }
    }
}