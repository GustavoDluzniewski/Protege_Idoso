package com.example.protege_idoso

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat

object AlertHelper {


    private const val CANAL_SUSPEITO = "protege_idoso_suspeito"
    private const val CANAL_GOLPE = "protege_idoso_golpe"


    private const val CANAL_ANTIGO = "protege_idoso_alertas"

    private const val ID_SUSPEITO = 2001
    private const val ID_GOLPE = 2002

    private const val COR_SUSPEITO = 0xFFB26A00
    private const val COR_GOLPE = 0xFFC62828

    private const val TITULO_SUSPEITO = "⚠️ Atenção com esta mensagem"
    private const val TITULO_GOLPE = "⚠️ Cuidado: possível golpe"
    private const val ROTULO_MOTIVO = "Por quê:"
    private const val ROTULO_ORIENTACAO = "O que fazer:"

    private const val ROTULO_OFICIAL = "Canal oficial:"

    private fun textoCompleto(resultado: RiskResult, dicaOficial: String?): CharSequence {
        val texto = SpannableStringBuilder()

        adicionarRotulo(texto, ROTULO_MOTIVO)
        texto.append(" ").append(resultado.explicacao)
        texto.append("\n\n")
        adicionarRotulo(texto, ROTULO_ORIENTACAO)
        texto.append(" ").append(resultado.orientacao)

        if (dicaOficial != null) {
            texto.append("\n\n")
            adicionarRotulo(texto, ROTULO_OFICIAL)
            texto.append(" ").append(dicaOficial)
        }

        return texto
    }

    private fun intentAoTocar(context: Context, idAnalise: Long?): Intent =
        if (idAnalise != null && idAnalise > 0) {
            HistoricoActivity.criarIntent(context, idAnalise)
        } else {
            Intent(context, InstrucoesActivity::class.java)
        }

    private fun adicionarRotulo(texto: SpannableStringBuilder, rotulo: String) {
        val inicio = texto.length
        texto.append(rotulo)
        texto.setSpan(
            StyleSpan(Typeface.BOLD),
            inicio,
            texto.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun versaoPublica(
        context: Context,
        canal: String,
        titulo: String,
        cor: Long
    ) = NotificationCompat.Builder(context, canal)
        .setSmallIcon(R.drawable.ic_stat_shield)
        .setColor(cor.toInt())
        .setContentTitle(titulo)
        .setContentText("Desbloqueie o celular para ver o aviso.")
        .build()

    fun mostrarAlertaSuspeito(
        context: Context,
        resultado: RiskResult,
        idAnalise: Long? = null,
        dicaOficial: String? = null
    ) {
        criarCanais(context)

        val intent = intentAoTocar(context, idAnalise)

        val pendingIntent = PendingIntent.getActivity(
            context,
            ID_SUSPEITO,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacao = NotificationCompat.Builder(context, CANAL_SUSPEITO)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setColor(COR_SUSPEITO.toInt())
            .setContentTitle(TITULO_SUSPEITO)
            .setContentText(resultado.explicacao)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(textoCompleto(resultado, dicaOficial))
            )
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(
                versaoPublica(context, CANAL_SUSPEITO, TITULO_SUSPEITO, COR_SUSPEITO)
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(ID_SUSPEITO, notificacao)
    }

    fun mostrarAlertaGolpe(
        context: Context,
        resultado: RiskResult,
        idAnalise: Long? = null,
        dicaOficial: String? = null
    ) {
        criarCanais(context)

        val intent = intentAoTocar(context, idAnalise)

        val pendingIntent = PendingIntent.getActivity(
            context,
            ID_GOLPE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacao = NotificationCompat.Builder(context, CANAL_GOLPE)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setColor(COR_GOLPE.toInt())
            .setContentTitle(TITULO_GOLPE)
            .setContentText(resultado.explicacao)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(TITULO_GOLPE)
                    .bigText(textoCompleto(resultado, dicaOficial))
                    .setSummaryText("Toque para ver como se proteger")
            )
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(
                versaoPublica(context, CANAL_GOLPE, TITULO_GOLPE, COR_GOLPE)
            )
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