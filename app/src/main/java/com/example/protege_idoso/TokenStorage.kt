package com.example.protege_idoso

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenStorage {

    private const val NOME_ARQUIVO = "protege_idoso_secure"
    private const val CHAVE_TOKEN = "token_instalacao"

    private fun preferencias(context: Context) =
        EncryptedSharedPreferences.create(
            context,
            NOME_ARQUIVO,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun obterToken(context: Context): String? {
        return preferencias(context).getString(CHAVE_TOKEN, null)
    }

    fun salvarToken(context: Context, token: String) {
        preferencias(context).edit()
            .putString(CHAVE_TOKEN, token)
            .apply()
    }

    fun apagarToken(context: Context) {
        preferencias(context).edit()
            .remove(CHAVE_TOKEN)
            .apply()
    }
}
