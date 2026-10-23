package com.example.protege_idoso.historico

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import com.example.protege_idoso.dados.BancoLocal
import com.example.protege_idoso.dados.BancoLocal.Analises

class HistoricoRepository(context: Context) {
    private val banco = BancoLocal.obter(context)

    fun salvar(analise: AnaliseSalva): Long {
        val valores = ContentValues().apply {
            put(Analises.DATA_HORA, analise.dataHora)
            put(Analises.NIVEL, analise.nivel.name)
            put(Analises.EXPLICACAO, analise.explicacao)
            put(Analises.ORIENTACAO, analise.orientacao)
            put(Analises.ORIGEM, analise.origem)
            put(Analises.STATUS, analise.status.name)
            put(Analises.ATUALIZADO_EM, analise.atualizadoEm)
        }
        val id = banco.writableDatabase.insert(Analises.TABELA, null, valores)
        apagarExcedentes()
        return id
    }

    fun listar(): List<AnaliseSalva> {
        banco.readableDatabase.query(
            Analises.TABELA,
            null,
            null,
            null,
            null,
            null,
            "${Analises.DATA_HORA} DESC"
        ).use { cursor ->
            val lista = mutableListOf<AnaliseSalva>()
            while (cursor.moveToNext()) {
                lista.add(cursor.paraAnalise())
            }
            return lista
        }
    }

    fun buscarPorId(id: Long): AnaliseSalva? {
        banco.readableDatabase.query(
            Analises.TABELA,
            null,
            "${Analises.ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->
            return if (cursor.moveToFirst()) cursor.paraAnalise() else null
        }
    }

    fun contarPendentes(): Int {
        return DatabaseUtils.queryNumEntries(
            banco.readableDatabase,
            Analises.TABELA,
            "${Analises.STATUS} = ?",
            arrayOf(StatusAnalise.PENDENTE.name)
        ).toInt()
    }

    fun atualizarStatus(id: Long, status: StatusAnalise): Boolean {
        val valores = ContentValues().apply {
            put(Analises.STATUS, status.name)
            put(Analises.ATUALIZADO_EM, System.currentTimeMillis())
        }
        return banco.writableDatabase.update(
            Analises.TABELA,
            valores,
            "${Analises.ID} = ?",
            arrayOf(id.toString())
        ) > 0
    }

    fun excluir(id: Long): Boolean {
        return banco.writableDatabase.delete(
            Analises.TABELA,
            "${Analises.ID} = ?",
            arrayOf(id.toString())
        ) > 0
    }

    fun limparTudo(): Int {
        return banco.writableDatabase.delete(Analises.TABELA, null, null)
    }

    private fun apagarExcedentes() {
        banco.writableDatabase.execSQL(
            """
            DELETE FROM ${Analises.TABELA}
            WHERE ${Analises.ID} NOT IN (
                SELECT ${Analises.ID} FROM ${Analises.TABELA}
                ORDER BY ${Analises.DATA_HORA} DESC
                LIMIT $LIMITE_REGISTROS
            )
            """.trimIndent()
        )
    }

    private fun Cursor.paraAnalise(): AnaliseSalva {
        return AnaliseSalva(
            id = getLong(getColumnIndexOrThrow(Analises.ID)),
            dataHora = getLong(getColumnIndexOrThrow(Analises.DATA_HORA)),
            nivel = nivelDeTexto(getString(getColumnIndexOrThrow(Analises.NIVEL))),
            explicacao = getString(getColumnIndexOrThrow(Analises.EXPLICACAO)),
            orientacao = getString(getColumnIndexOrThrow(Analises.ORIENTACAO)),
            origem = getString(getColumnIndexOrThrow(Analises.ORIGEM)),
            status = StatusAnalise.deTexto(getString(getColumnIndexOrThrow(Analises.STATUS))),
            atualizadoEm = getLong(getColumnIndexOrThrow(Analises.ATUALIZADO_EM))
        )
    }

    companion object {
        const val LIMITE_REGISTROS = 200
    }
}
