package com.example.protege_idoso.contatos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteConstraintException
import com.example.protege_idoso.dados.BancoLocal
import com.example.protege_idoso.dados.BancoLocal.Contatos

sealed class ResultadoContato {
    data class Sucesso(val id: Long) : ResultadoContato()
    data class Invalido(val erros: ErrosDoContato) : ResultadoContato()
    data object TelefoneRepetido : ResultadoContato()
    data object LimiteAtingido : ResultadoContato()
    data object NaoEncontrado : ResultadoContato()
}

class ContatosRepository(context: Context) {
    private val banco = BancoLocal.obter(context)

    fun cadastrar(nome: String, telefone: String, parentesco: String): ResultadoContato {
        val erros = ValidacaoContato.validar(nome, telefone, parentesco)
        if (erros.temErro) return ResultadoContato.Invalido(erros)

        if (contar() >= ValidacaoContato.LIMITE_DE_CONTATOS) {
            return ResultadoContato.LimiteAtingido
        }

        val digitos = Telefone.normalizar(telefone)
            ?: return ResultadoContato.Invalido(ErrosDoContato(telefone = "Telefone inválido."))

        if (existeTelefone(digitos, ignorarId = null)) return ResultadoContato.TelefoneRepetido

        val agora = System.currentTimeMillis()
        val valores = ContentValues().apply {
            put(Contatos.NOME, ValidacaoContato.limparNome(nome))
            put(Contatos.TELEFONE, digitos)
            put(Contatos.PARENTESCO, parentesco.trim())
            put(Contatos.CRIADO_EM, agora)
            put(Contatos.ATUALIZADO_EM, agora)
        }

        return try {
            val id = banco.writableDatabase.insertOrThrow(Contatos.TABELA, null, valores)
            ResultadoContato.Sucesso(id)
        } catch (e: SQLiteConstraintException) {
            ResultadoContato.TelefoneRepetido
        }
    }

    fun listar(): List<ContatoConfianca> {
        banco.readableDatabase.query(
            Contatos.TABELA,
            null,
            null,
            null,
            null,
            null,
            "${Contatos.NOME} COLLATE NOCASE ASC"
        ).use { cursor ->
            val lista = mutableListOf<ContatoConfianca>()
            while (cursor.moveToNext()) {
                lista.add(cursor.paraContato())
            }
            return lista
        }
    }

    fun buscarPorId(id: Long): ContatoConfianca? {
        banco.readableDatabase.query(
            Contatos.TABELA,
            null,
            "${Contatos.ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->
            return if (cursor.moveToFirst()) cursor.paraContato() else null
        }
    }

    fun contar(): Int =
        DatabaseUtils.queryNumEntries(banco.readableDatabase, Contatos.TABELA).toInt()

    fun atualizar(id: Long, nome: String, telefone: String, parentesco: String): ResultadoContato {
        val erros = ValidacaoContato.validar(nome, telefone, parentesco)
        if (erros.temErro) return ResultadoContato.Invalido(erros)

        val digitos = Telefone.normalizar(telefone)
            ?: return ResultadoContato.Invalido(ErrosDoContato(telefone = "Telefone inválido."))

        if (existeTelefone(digitos, ignorarId = id)) return ResultadoContato.TelefoneRepetido

        val valores = ContentValues().apply {
            put(Contatos.NOME, ValidacaoContato.limparNome(nome))
            put(Contatos.TELEFONE, digitos)
            put(Contatos.PARENTESCO, parentesco.trim())
            put(Contatos.ATUALIZADO_EM, System.currentTimeMillis())
        }

        return try {
            val linhas = banco.writableDatabase.update(
                Contatos.TABELA,
                valores,
                "${Contatos.ID} = ?",
                arrayOf(id.toString())
            )
            if (linhas > 0) ResultadoContato.Sucesso(id) else ResultadoContato.NaoEncontrado
        } catch (e: SQLiteConstraintException) {
            ResultadoContato.TelefoneRepetido
        }
    }

    fun excluir(id: Long): Boolean {
        return banco.writableDatabase.delete(
            Contatos.TABELA,
            "${Contatos.ID} = ?",
            arrayOf(id.toString())
        ) > 0
    }

    fun limparTudo(): Int = banco.writableDatabase.delete(Contatos.TABELA, null, null)

    private fun existeTelefone(digitos: String, ignorarId: Long?): Boolean {
        val selecao: String
        val argumentos: Array<String>
        if (ignorarId == null) {
            selecao = "${Contatos.TELEFONE} = ?"
            argumentos = arrayOf(digitos)
        } else {
            selecao = "${Contatos.TELEFONE} = ? AND ${Contatos.ID} != ?"
            argumentos = arrayOf(digitos, ignorarId.toString())
        }
        return DatabaseUtils.queryNumEntries(
            banco.readableDatabase,
            Contatos.TABELA,
            selecao,
            argumentos
        ) > 0
    }

    private fun Cursor.paraContato(): ContatoConfianca {
        return ContatoConfianca(
            id = getLong(getColumnIndexOrThrow(Contatos.ID)),
            nome = getString(getColumnIndexOrThrow(Contatos.NOME)),
            telefone = getString(getColumnIndexOrThrow(Contatos.TELEFONE)),
            parentesco = getString(getColumnIndexOrThrow(Contatos.PARENTESCO)),
            criadoEm = getLong(getColumnIndexOrThrow(Contatos.CRIADO_EM)),
            atualizadoEm = getLong(getColumnIndexOrThrow(Contatos.ATUALIZADO_EM))
        )
    }
}
