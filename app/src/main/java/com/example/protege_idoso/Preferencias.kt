package com.example.protege_idoso

import android.content.Context

class Preferencias(context: Context) {

    private val preferencias = context.getSharedPreferences(
        "protege_idoso_preferencias",
        Context.MODE_PRIVATE
    )

    var analisarWhatsApp: Boolean
        get() = preferencias.getBoolean("analisar_whatsapp", true)
        set(valor) {
            preferencias.edit()
                .putBoolean("analisar_whatsapp", valor)
                .apply()
        }

    var avisoSuspeito: Boolean
        get() = preferencias.getBoolean("aviso_suspeito", true)
        set(valor) {
            preferencias.edit()
                .putBoolean("aviso_suspeito", valor)
                .apply()
        }

    var avisoGolpe: Boolean
        get() = preferencias.getBoolean("aviso_golpe", true)
        set(valor) {
            preferencias.edit()
                .putBoolean("aviso_golpe", valor)
                .apply()
        }
}