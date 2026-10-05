package com.example.protege_idoso

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object AlertHelper {

    private const val CHANNEL_ID = "protege_idoso_alertas"

    fun mostrarAlertaSuspeito(context: Context, resultado: RiskResult) {
        criarCanal(context)

        val intent = Intent(context, InstrucoesActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacao = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Mensagem suspeita")
            .setContentText("Esta mensagem pode ser suspeita. Verifique antes de responder.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Esta mensagem pode ser suspeita. Verifique antes de responder.\n\n" +
                                "${resultado.orientacao}"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(2001, notificacao)
    }

    fun mostrarAlertaGolpe(context: Context, resultado: RiskResult) {
        criarCanal(context)

        val intent = Intent(context, InstrucoesActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            2002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacao = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
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
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(2002, notificacao)
    }

    private fun criarCanal(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val canal = NotificationChannel(
                CHANNEL_ID,
                "Alertas do ProtegeIdoso",
                NotificationManager.IMPORTANCE_HIGH
            )

            canal.description =
                "Alertas sobre mensagens suspeitas e possíveis golpes"

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(canal)
        }
    }
}