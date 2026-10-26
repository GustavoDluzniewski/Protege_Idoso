package com.example.protege_idoso.instituicoes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import com.example.protege_idoso.dados.BancoLocal
import com.example.protege_idoso.dados.BancoLocal.Canais
import com.example.protege_idoso.dados.BancoLocal.Instituicoes

sealed class ResultadoInstituicao {
    data class Sucesso(val id: Long) : ResultadoInstituicao()
    data class NomeInvalido(val mensagem: String) : ResultadoInstituicao()
    data class CanalInvalido(val erros: ErrosDoCanal) : ResultadoInstituicao()
    data object JaCadastrada : ResultadoInstituicao()
    data object LimiteAtingido : ResultadoInstituicao()
    data object NaoEncontrada : ResultadoInstituicao()
}

class InstituicoesRepository(context: Context) {

    private val banco = BancoLocal.obter(context)

    fun listar(): List<Instituicao> {
        val db = banco.readableDatabase
        val canaisPorInstituicao = db.query(
            Canais.TABELA, null, null, null, null, null,
            "${Canais.ORDEM} ASC, ${Canais.ID} ASC"
        ).use { cursor ->
            val lista = mutableListOf<CanalOficial>()
            while (cursor.moveToNext()) lista.add(cursor.paraCanal())
            lista.groupBy { it.instituicaoId }
        }
        return db.query(
            Instituicoes.TABELA, null, null, null, null, null,
            "${Instituicoes.NOME} COLLATE NOCASE ASC"
        ).use { cursor ->
            val lista = mutableListOf<Instituicao>()
            while (cursor.moveToNext()) {
                val instituicao = cursor.paraInstituicao()
                lista.add(instituicao.copy(canais = canaisPorInstituicao[instituicao.id].orEmpty()))
            }
            lista
        }
    }

    fun buscarPorId(id: Long): Instituicao? = listar().firstOrNull { it.id == id }

    fun buscarPorCodigo(codigo: String): Instituicao? = listar().firstOrNull { it.codigo == codigo }

    fun contar(): Int =
        DatabaseUtils.queryNumEntries(banco.readableDatabase, Instituicoes.TABELA).toInt()

    fun codigosCadastrados(): Set<String> =
        banco.readableDatabase.query(
            Instituicoes.TABELA, arrayOf(Instituicoes.CODIGO),
            "${Instituicoes.CODIGO} IS NOT NULL", null, null, null, null
        ).use { cursor ->
            val codigos = mutableSetOf<String>()
            while (cursor.moveToNext()) codigos.add(cursor.getString(0))
            codigos
        }

    fun adicionarDoCatalogo(codigos: Collection<String>): Int {
        val db = banco.writableDatabase
        val existentes = codigosCadastrados()
        var adicionadas = 0
        db.beginTransaction()
        try {
            var total = contar()
            for (codigo in codigos) {
                if (codigo in existentes) continue
                if (total >= ValidacaoInstituicao.LIMITE_DE_INSTITUICOES) break
                val modelo = CatalogoInstituicoes.porCodigo(codigo) ?: continue
                val id = inserirInstituicao(db, modelo.codigo, modelo.nome)
                modelo.canais.forEachIndexed { ordem, canal ->
                    inserirCanal(db, id, canal.rotulo, canal.tipo, canal.valor, oficial = true, ordem = ordem)
                }
                adicionadas++
                total++
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return adicionadas
    }

    fun criarPersonalizada(nome: String): ResultadoInstituicao {
        ValidacaoInstituicao.validarNome(nome)?.let { return ResultadoInstituicao.NomeInvalido(it) }
        if (contar() >= ValidacaoInstituicao.LIMITE_DE_INSTITUICOES) {
            return ResultadoInstituicao.LimiteAtingido
        }
        val limpo = ValidacaoInstituicao.limparTexto(nome)
        if (existeNome(limpo, ignorarId = null)) return ResultadoInstituicao.JaCadastrada
        val id = inserirInstituicao(banco.writableDatabase, null, limpo)
        return ResultadoInstituicao.Sucesso(id)
    }

    fun renomear(id: Long, nome: String): ResultadoInstituicao {
        ValidacaoInstituicao.validarNome(nome)?.let { return ResultadoInstituicao.NomeInvalido(it) }
        val limpo = ValidacaoInstituicao.limparTexto(nome)
        if (existeNome(limpo, ignorarId = id)) return ResultadoInstituicao.JaCadastrada
        val valores = ContentValues().apply {
            put(Instituicoes.NOME, limpo)
            put(Instituicoes.ATUALIZADO_EM, System.currentTimeMillis())
        }
        val linhas = banco.writableDatabase.update(
            Instituicoes.TABELA, valores, "${Instituicoes.ID} = ?", arrayOf(id.toString())
        )
        return if (linhas > 0) ResultadoInstituicao.Sucesso(id) else ResultadoInstituicao.NaoEncontrada
    }

    fun excluirInstituicao(id: Long): Boolean {
        val db = banco.writableDatabase
        db.beginTransaction()
        try {
            db.delete(Canais.TABELA, "${Canais.INSTITUICAO_ID} = ?", arrayOf(id.toString()))
            val linhas = db.delete(Instituicoes.TABELA, "${Instituicoes.ID} = ?", arrayOf(id.toString()))
            db.setTransactionSuccessful()
            return linhas > 0
        } finally {
            db.endTransaction()
        }
    }

    fun adicionarCanal(
        instituicaoId: Long,
        rotulo: String,
        tipo: TipoCanal,
        valor: String
    ): ResultadoInstituicao {
        val erros = ValidacaoInstituicao.validarCanal(rotulo, tipo, valor)
        if (erros.temErro) return ResultadoInstituicao.CanalInvalido(erros)
        val instituicao = buscarPorId(instituicaoId) ?: return ResultadoInstituicao.NaoEncontrada
        if (instituicao.canais.size >= ValidacaoInstituicao.LIMITE_DE_CANAIS) {
            return ResultadoInstituicao.LimiteAtingido
        }
        val normalizado = ValidacaoInstituicao.normalizarValor(tipo, valor)
            ?: return ResultadoInstituicao.CanalInvalido(ErrosDoCanal(valor = "Valor inválido."))
        val proximaOrdem = (instituicao.canais.maxOfOrNull { it.ordem } ?: -1) + 1
        val id = inserirCanal(
            banco.writableDatabase,
            instituicaoId,
            ValidacaoInstituicao.limparTexto(rotulo),
            tipo,
            normalizado,
            oficial = false,
            ordem = proximaOrdem
        )
        tocarInstituicao(instituicaoId)
        return ResultadoInstituicao.Sucesso(id)
    }

    fun atualizarCanal(id: Long, rotulo: String, tipo: TipoCanal, valor: String): ResultadoInstituicao {
        val erros = ValidacaoInstituicao.validarCanal(rotulo, tipo, valor)
        if (erros.temErro) return ResultadoInstituicao.CanalInvalido(erros)
        val normalizado = ValidacaoInstituicao.normalizarValor(tipo, valor)
            ?: return ResultadoInstituicao.CanalInvalido(ErrosDoCanal(valor = "Valor inválido."))
        val atual = buscarCanal(id) ?: return ResultadoInstituicao.NaoEncontrada
        val rotuloLimpo = ValidacaoInstituicao.limparTexto(rotulo)
        val continuaOficial = atual.oficial &&
                atual.tipo == tipo && atual.valor == normalizado && atual.rotulo == rotuloLimpo
        val valores = ContentValues().apply {
            put(Canais.ROTULO, rotuloLimpo)
            put(Canais.TIPO, tipo.name)
            put(Canais.VALOR, normalizado)
            put(Canais.OFICIAL, if (continuaOficial) 1 else 0)
            put(Canais.ATUALIZADO_EM, System.currentTimeMillis())
        }
        banco.writableDatabase.update(Canais.TABELA, valores, "${Canais.ID} = ?", arrayOf(id.toString()))
        tocarInstituicao(atual.instituicaoId)
        return ResultadoInstituicao.Sucesso(id)
    }

    fun excluirCanal(id: Long): Boolean {
        val atual = buscarCanal(id) ?: return false
        val linhas = banco.writableDatabase.delete(
            Canais.TABELA, "${Canais.ID} = ?", arrayOf(id.toString())
        )
        tocarInstituicao(atual.instituicaoId)
        return linhas > 0
    }

    fun restaurarCanaisOficiais(instituicaoId: Long): Int {
        val instituicao = buscarPorId(instituicaoId) ?: return 0
        val modelo = CatalogoInstituicoes.porCodigo(instituicao.codigo) ?: return 0
        val db = banco.writableDatabase
        var restaurados = 0
        db.beginTransaction()
        try {
            var ordem = (instituicao.canais.maxOfOrNull { it.ordem } ?: -1) + 1
            for (canal in modelo.canais) {
                val jaExiste = instituicao.canais.any { it.tipo == canal.tipo && it.valor == canal.valor }
                if (jaExiste) continue
                if (instituicao.canais.size + restaurados >= ValidacaoInstituicao.LIMITE_DE_CANAIS) break
                inserirCanal(db, instituicaoId, canal.rotulo, canal.tipo, canal.valor, oficial = true, ordem = ordem++)
                restaurados++
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        if (restaurados > 0) tocarInstituicao(instituicaoId)
        return restaurados
    }

    fun limparTudo() {
        val db = banco.writableDatabase
        db.beginTransaction()
        try {
            db.delete(Canais.TABELA, null, null)
            db.delete(Instituicoes.TABELA, null, null)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun buscarCanal(id: Long): CanalOficial? =
        banco.readableDatabase.query(
            Canais.TABELA, null, "${Canais.ID} = ?", arrayOf(id.toString()), null, null, null
        ).use { cursor -> if (cursor.moveToFirst()) cursor.paraCanal() else null }

    private fun existeNome(nome: String, ignorarId: Long?): Boolean {
        val selecao = if (ignorarId == null) {
            "${Instituicoes.NOME} = ? COLLATE NOCASE"
        } else {
            "${Instituicoes.NOME} = ? COLLATE NOCASE AND ${Instituicoes.ID} != ?"
        }
        val argumentos = if (ignorarId == null) arrayOf(nome) else arrayOf(nome, ignorarId.toString())
        return DatabaseUtils.queryNumEntries(
            banco.readableDatabase, Instituicoes.TABELA, selecao, argumentos
        ) > 0
    }

    private fun inserirInstituicao(db: SQLiteDatabase, codigo: String?, nome: String): Long {
        val agora = System.currentTimeMillis()
        val valores = ContentValues().apply {
            put(Instituicoes.CODIGO, codigo)
            put(Instituicoes.NOME, nome)
            put(Instituicoes.CRIADO_EM, agora)
            put(Instituicoes.ATUALIZADO_EM, agora)
        }
        return db.insertOrThrow(Instituicoes.TABELA, null, valores)
    }

    private fun inserirCanal(
        db: SQLiteDatabase,
        instituicaoId: Long,
        rotulo: String,
        tipo: TipoCanal,
        valor: String,
        oficial: Boolean,
        ordem: Int
    ): Long {
        val agora = System.currentTimeMillis()
        val valores = ContentValues().apply {
            put(Canais.INSTITUICAO_ID, instituicaoId)
            put(Canais.ROTULO, rotulo)
            put(Canais.TIPO, tipo.name)
            put(Canais.VALOR, valor)
            put(Canais.OFICIAL, if (oficial) 1 else 0)
            put(Canais.ORDEM, ordem)
            put(Canais.CRIADO_EM, agora)
            put(Canais.ATUALIZADO_EM, agora)
        }
        return db.insertOrThrow(Canais.TABELA, null, valores)
    }

    private fun tocarInstituicao(id: Long) {
        val valores = ContentValues().apply { put(Instituicoes.ATUALIZADO_EM, System.currentTimeMillis()) }
        banco.writableDatabase.update(
            Instituicoes.TABELA, valores, "${Instituicoes.ID} = ?", arrayOf(id.toString())
        )
    }

    private fun Cursor.paraInstituicao(): Instituicao {
        val indiceCodigo = getColumnIndexOrThrow(Instituicoes.CODIGO)
        return Instituicao(
            id = getLong(getColumnIndexOrThrow(Instituicoes.ID)),
            codigo = if (isNull(indiceCodigo)) null else getString(indiceCodigo),
            nome = getString(getColumnIndexOrThrow(Instituicoes.NOME))
        )
    }

    private fun Cursor.paraCanal(): CanalOficial = CanalOficial(
        id = getLong(getColumnIndexOrThrow(Canais.ID)),
        instituicaoId = getLong(getColumnIndexOrThrow(Canais.INSTITUICAO_ID)),
        rotulo = getString(getColumnIndexOrThrow(Canais.ROTULO)),
        tipo = TipoCanal.deTexto(getString(getColumnIndexOrThrow(Canais.TIPO))),
        valor = getString(getColumnIndexOrThrow(Canais.VALOR)),
        oficial = getInt(getColumnIndexOrThrow(Canais.OFICIAL)) == 1,
        ordem = getInt(getColumnIndexOrThrow(Canais.ORDEM))
    )
}

object AcoesDeCanal {

    fun intentPara(canal: CanalOficial): android.content.Intent {
        val uri = if (canal.tipo == TipoCanal.TELEFONE) {
            android.net.Uri.parse(NumeroOficial.uriDeLigacao(canal.valor))
        } else {
            android.net.Uri.parse(canal.valor)
        }
        val acao = if (canal.tipo == TipoCanal.TELEFONE) {
            android.content.Intent.ACTION_DIAL
        } else {
            android.content.Intent.ACTION_VIEW
        }
        return android.content.Intent(acao, uri).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun abrir(context: Context, canal: CanalOficial) {
        try {
            context.startActivity(intentPara(canal))
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                context,
                if (canal.tipo == TipoCanal.TELEFONE) {
                    "Não foi possível abrir o telefone."
                } else {
                    "Não foi possível abrir o site."
                },
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    fun canaisDoCatalogo(modelo: InstituicaoDoCatalogo): List<CanalOficial> =
        modelo.canais.mapIndexed { indice, canal ->
            CanalOficial(
                id = -(indice + 1).toLong(),
                instituicaoId = 0,
                rotulo = canal.rotulo,
                tipo = canal.tipo,
                valor = canal.valor,
                oficial = true,
                ordem = indice
            )
        }
}