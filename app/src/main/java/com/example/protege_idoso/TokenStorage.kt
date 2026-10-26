package com.example.protege_idoso

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenStorage {

    private const val NOME_ARQUIVO = "protege_idoso_secure"
    private const val CHAVE_TOKEN = "token_instalacao"

    private fun criarPreferencias(context: Context): SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            NOME_ARQUIVO,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    @Synchronized
    private fun preferencias(context: Context): SharedPreferences =
        try {
            criarPreferencias(context)
        } catch (e: Exception) {
            Log.w(
                "ProtegeIdoso",
                "Armazenamento do token ilegível (${e.javaClass.simpleName}). Recriando."
            )
            context.deleteSharedPreferences(NOME_ARQUIVO)
            criarPreferencias(context)
        }

    fun obterToken(context: Context): String? {
        return try {
            preferencias(context).getString(CHAVE_TOKEN, null)
        } catch (e: Exception) {
            context.deleteSharedPreferences(NOME_ARQUIVO)
            null
        }
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
