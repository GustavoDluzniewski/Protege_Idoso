package com.example.protege_idoso.contatos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContract

data class ContatoDaAgenda(
    val nome: String,
    val telefone: String
) {
    val nomeParaFormulario: String
        get() = ValidacaoContato.limparNome(nome).take(ValidacaoContato.TAMANHO_MAXIMO_NOME)

    val telefoneNormalizado: String?
        get() = Telefone.normalizar(telefone)

    val telefoneParaFormulario: String
        get() = telefoneNormalizado?.let { Telefone.formatar(it) }
            ?: telefone.filter { it.isDigit() || it in " ()-+" }.take(20)
}

class EscolherTelefoneDaAgenda : ActivityResultContract<Unit, Uri?>() {
    override fun createIntent(context: Context, input: Unit): Intent =
        Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
        if (resultCode == Activity.RESULT_OK) intent?.data else null
}

object LeitorDaAgenda {
    private val COLUNAS = arrayOf(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    fun ler(context: Context, uri: Uri): ContatoDaAgenda? = try {
        context.contentResolver.query(uri, COLUNAS, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nome = cursor.getString(0)?.trim().orEmpty()
                val numero = cursor.getString(1)?.trim().orEmpty()
                if (numero.isBlank()) null else ContatoDaAgenda(nome, numero)
            } else {
                null
            }
        }
    } catch (e: Exception) {
        null
    }
}