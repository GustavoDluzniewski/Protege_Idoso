package com.example.protege_idoso

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object DisqueDenuncia {
    private const val NUMERO = "556932168940"

    fun abrir(context: Context, mensagem: String) {
        val url = "https://wa.me/$NUMERO?text=${Uri.encode(mensagem)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Não foi possível abrir o WhatsApp.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
