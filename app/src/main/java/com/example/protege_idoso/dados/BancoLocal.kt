package com.example.protege_idoso.dados

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoLocal private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    NOME_BANCO,
    null,
    VERSAO_BANCO
) {
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        criarTabelaAnalises(db)
        criarTabelaContatos(db)
        criarTabelasInstituicoes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            criarTabelaContatos(db)
        }
        if (oldVersion < 3) {
            criarTabelasInstituicoes(db)
            db.execSQL("ALTER TABLE ${Analises.TABELA} ADD COLUMN ${Analises.INSTITUICAO} TEXT")
        }
    }

    private fun criarTabelaAnalises(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ${Analises.TABELA} (
                ${Analises.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Analises.DATA_HORA} INTEGER NOT NULL,
                ${Analises.NIVEL} TEXT NOT NULL,
                ${Analises.EXPLICACAO} TEXT NOT NULL,
                ${Analises.ORIENTACAO} TEXT NOT NULL,
                ${Analises.ORIGEM} TEXT NOT NULL,
                ${Analises.STATUS} TEXT NOT NULL,
                ${Analises.ATUALIZADO_EM} INTEGER NOT NULL,
                ${Analises.INSTITUICAO} TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_analises_data " +
                    "ON ${Analises.TABELA}(${Analises.DATA_HORA} DESC)"
        )
    }

    private fun criarTabelaContatos(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ${Contatos.TABELA} (
                ${Contatos.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Contatos.NOME} TEXT NOT NULL,
                ${Contatos.TELEFONE} TEXT NOT NULL UNIQUE,
                ${Contatos.PARENTESCO} TEXT NOT NULL DEFAULT '',
                ${Contatos.CRIADO_EM} INTEGER NOT NULL,
                ${Contatos.ATUALIZADO_EM} INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    private fun criarTabelasInstituicoes(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ${Instituicoes.TABELA} (
                ${Instituicoes.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Instituicoes.CODIGO} TEXT UNIQUE,
                ${Instituicoes.NOME} TEXT NOT NULL,
                ${Instituicoes.CRIADO_EM} INTEGER NOT NULL,
                ${Instituicoes.ATUALIZADO_EM} INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ${Canais.TABELA} (
                ${Canais.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Canais.INSTITUICAO_ID} INTEGER NOT NULL
                    REFERENCES ${Instituicoes.TABELA}(${Instituicoes.ID}) ON DELETE CASCADE,
                ${Canais.ROTULO} TEXT NOT NULL,
                ${Canais.TIPO} TEXT NOT NULL,
                ${Canais.VALOR} TEXT NOT NULL,
                ${Canais.OFICIAL} INTEGER NOT NULL DEFAULT 0,
                ${Canais.ORDEM} INTEGER NOT NULL DEFAULT 0,
                ${Canais.CRIADO_EM} INTEGER NOT NULL,
                ${Canais.ATUALIZADO_EM} INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_canais_instituicao " +
                    "ON ${Canais.TABELA}(${Canais.INSTITUICAO_ID})"
        )
    }

    object Analises {
        const val TABELA = "analises"
        const val ID = "id"
        const val DATA_HORA = "data_hora"
        const val NIVEL = "nivel"
        const val EXPLICACAO = "explicacao"
        const val ORIENTACAO = "orientacao"
        const val ORIGEM = "origem"
        const val STATUS = "status"
        const val ATUALIZADO_EM = "atualizado_em"
        const val INSTITUICAO = "instituicao"
    }

    object Contatos {
        const val TABELA = "contatos_confianca"
        const val ID = "id"
        const val NOME = "nome"
        const val TELEFONE = "telefone"
        const val PARENTESCO = "parentesco"
        const val CRIADO_EM = "criado_em"
        const val ATUALIZADO_EM = "atualizado_em"
    }

    object Instituicoes {
        const val TABELA = "instituicoes"
        const val ID = "id"
        const val CODIGO = "codigo"
        const val NOME = "nome"
        const val CRIADO_EM = "criado_em"
        const val ATUALIZADO_EM = "atualizado_em"
    }

    object Canais {
        const val TABELA = "canais_oficiais"
        const val ID = "id"
        const val INSTITUICAO_ID = "instituicao_id"
        const val ROTULO = "rotulo"
        const val TIPO = "tipo"
        const val VALOR = "valor"
        const val OFICIAL = "oficial"
        const val ORDEM = "ordem"
        const val CRIADO_EM = "criado_em"
        const val ATUALIZADO_EM = "atualizado_em"
    }

    companion object {
        const val NOME_BANCO = "protege_idoso_historico.db"
        const val VERSAO_BANCO = 3

        @Volatile
        private var instancia: BancoLocal? = null

        fun obter(context: Context): BancoLocal =
            instancia ?: synchronized(this) {
                instancia ?: BancoLocal(context).also { instancia = it }
            }
    }
}