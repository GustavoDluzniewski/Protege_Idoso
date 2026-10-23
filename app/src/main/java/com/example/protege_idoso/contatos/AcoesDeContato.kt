package com.example.protege_idoso.contatos

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object AcoesDeContato {
    private const val MENSAGEM_PADRAO =
        "Olá! Recebi uma mensagem estranha e queria confirmar com você antes de responder."

    fun ligar(context: Context, contato: ContatoConfianca) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(Telefone.uriDeLigacao(contato.telefone)))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Não foi possível abrir o telefone.", Toast.LENGTH_LONG).show()
        }
    }

    fun abrirWhatsApp(context: Context, contato: ContatoConfianca) {
        val url = "https://wa.me/${Telefone.paraWhatsApp(contato.telefone)}" +
                "?text=${Uri.encode(MENSAGEM_PADRAO)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Não foi possível abrir o WhatsApp.", Toast.LENGTH_LONG).show()
        }
    }
}
