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


    var versaoTermosAceita: Int
        get() = preferencias.getInt("versao_termos_aceita", 0)
        set(valor) {
            preferencias.edit()
                .putInt("versao_termos_aceita", valor)
                .apply()
        }


    var dataDoAceite: Long
        get() = preferencias.getLong("data_do_aceite", 0L)
        set(valor) {
            preferencias.edit()
                .putLong("data_do_aceite", valor)
                .apply()
        }


    fun aceitouTermosAtuais(): Boolean {
        return versaoTermosAceita >= DadosLegais.VERSAO
    }

    fun registrarAceite() {
        preferencias.edit()
            .putInt("versao_termos_aceita", DadosLegais.VERSAO)
            .putLong("data_do_aceite", System.currentTimeMillis())
            .apply()
    }


    fun revogarAceite() {
        preferencias.edit()
            .clear()
            .apply()
    }
}
