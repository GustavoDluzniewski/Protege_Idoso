package com.example.protege_idoso.quiz

import android.content.Context
import java.time.LocalDate

data class ResultadoDaResposta(
    val pontos: Int,
    val rodadaConcluida: Boolean,
    val acertosDaRodada: Int,
    val pontosDaRodada: Int,
    val sequencia: Int = 0,
    val bonusDeSequencia: Int = 0
)
class QuizRepository(context: Context) {

    private val preferencias = context.getSharedPreferences(
        "protege_idoso_quiz",
        Context.MODE_PRIVATE
    )

    val rodadasConcluidas: Int get() = preferencias.getInt(CHAVE_RODADAS, 0)
    val ultimaConclusao: Long get() = preferencias.getLong(CHAVE_ULTIMA_CONCLUSAO, 0L)
    val sequenciaDeDias: Int get() = preferencias.getInt(CHAVE_SEQUENCIA, 0)
    val acertosTotais: Int get() = preferencias.getInt(CHAVE_ACERTOS_TOTAIS, 0)
    val pontosTotais: Int get() = preferencias.getInt(CHAVE_PONTOS_TOTAIS, 0)
    val melhorSequencia: Int get() = preferencias.getInt(CHAVE_MELHOR_SEQUENCIA, 0)
    val diasJogados: Set<Long>
        get() = preferencias.getString(CHAVE_DIAS_JOGADOS, "")
            .orEmpty()
            .split(',')
            .mapNotNull { it.toLongOrNull() }
            .toSet()

    fun sequenciaVigente(agora: Long = System.currentTimeMillis()): Int =
        RegrasDoQuiz.sequenciaVigente(ultimaConclusao, agora, sequenciaDeDias)

    val rodadaEmAndamento: Boolean get() = preferencias.getBoolean(CHAVE_EM_ANDAMENTO, false)
    val indicePerguntaAtual: Int get() = preferencias.getInt(CHAVE_INDICE, 0)
    val inicioPerguntaAtual: Long get() = preferencias.getLong(CHAVE_INICIO_PERGUNTA, 0L)
    val acertosDaRodada: Int get() = preferencias.getInt(CHAVE_ACERTOS_RODADA, 0)
    val pontosDaRodada: Int get() = preferencias.getInt(CHAVE_PONTOS_RODADA, 0)

    fun proximaLiberacao(): Long = RegrasDoQuiz.proximaLiberacao(ultimaConclusao)

    fun estaLiberado(agora: Long = System.currentTimeMillis()): Boolean =
        rodadaEmAndamento || RegrasDoQuiz.estaLiberado(agora, ultimaConclusao)

    fun iniciarRodada(agora: Long) {
        preferencias.edit()
            .putBoolean(CHAVE_EM_ANDAMENTO, true)
            .putInt(CHAVE_INDICE, 0)
            .putLong(CHAVE_INICIO_PERGUNTA, agora)
            .putInt(CHAVE_ACERTOS_RODADA, 0)
            .putInt(CHAVE_PONTOS_RODADA, 0)
            .apply()
    }
    fun iniciarPerguntaAtual(agora: Long) {
        if (inicioPerguntaAtual <= 0L) {
            preferencias.edit().putLong(CHAVE_INICIO_PERGUNTA, agora).apply()
        }
    }

    fun registrarResposta(acertou: Boolean, agora: Long): ResultadoDaResposta {
        val restante = RegrasDoQuiz.tempoRestanteDaPergunta(inicioPerguntaAtual, agora)
        val pontos = RegrasDoQuiz.pontosDaResposta(acertou, restante)
        val acertos = acertosDaRodada + if (acertou) 1 else 0
        val proximoIndice = indicePerguntaAtual + 1
        val concluiu = proximoIndice >= RegrasDoQuiz.PERGUNTAS_POR_RODADA
        val sequencia = if (concluiu) {
            RegrasDoQuiz.novaSequencia(ultimaConclusao, agora, sequenciaDeDias)
        } else {
            sequenciaVigente(agora)
        }
        val bonusSequencia = if (concluiu) RegrasDoQuiz.bonusDeSequencia(sequencia) else 0
        val pontosRodada = pontosDaRodada + pontos + bonusSequencia

        val edicao = preferencias.edit()
            .putInt(CHAVE_INDICE, proximoIndice)
            .putLong(CHAVE_INICIO_PERGUNTA, 0L)
            .putInt(CHAVE_ACERTOS_RODADA, acertos)
            .putInt(CHAVE_PONTOS_RODADA, pontosRodada)

        if (concluiu) {
            edicao
                .putBoolean(CHAVE_EM_ANDAMENTO, false)
                .putInt(CHAVE_RODADAS, rodadasConcluidas + 1)
                .putInt(CHAVE_SEQUENCIA, sequencia)
                .putInt(CHAVE_MELHOR_SEQUENCIA, maxOf(melhorSequencia, sequencia))
                .putString(CHAVE_DIAS_JOGADOS, diasComHoje().joinToString(","))
                .putLong(CHAVE_ULTIMA_CONCLUSAO, agora)
                .putInt(CHAVE_ACERTOS_TOTAIS, acertosTotais + acertos)
                .putInt(CHAVE_PONTOS_TOTAIS, pontosTotais + pontosRodada)
        }
        edicao.apply()

        return ResultadoDaResposta(
            pontos = pontos,
            rodadaConcluida = concluiu,
            acertosDaRodada = acertos,
            pontosDaRodada = pontosRodada,
            sequencia = sequencia,
            bonusDeSequencia = bonusSequencia
        )
    }

    private fun diasComHoje(): List<Long> {
        val hoje = LocalDate.now().toEpochDay()
        return (diasJogados + hoje).filter { it > hoje - 60 }.sorted()
    }

    fun apagarTudo() {
        preferencias.edit().clear().apply()
    }

    private companion object {
        const val CHAVE_RODADAS = "rodadas_concluidas"
        const val CHAVE_ULTIMA_CONCLUSAO = "ultima_conclusao"
        const val CHAVE_SEQUENCIA = "sequencia_de_dias"
        const val CHAVE_ACERTOS_TOTAIS = "acertos_totais"
        const val CHAVE_PONTOS_TOTAIS = "pontos_totais"
        const val CHAVE_MELHOR_SEQUENCIA = "melhor_sequencia"
        const val CHAVE_DIAS_JOGADOS = "dias_jogados"
        const val CHAVE_EM_ANDAMENTO = "rodada_em_andamento"
        const val CHAVE_INDICE = "indice_pergunta_atual"
        const val CHAVE_INICIO_PERGUNTA = "inicio_pergunta_atual"
        const val CHAVE_ACERTOS_RODADA = "acertos_da_rodada"
        const val CHAVE_PONTOS_RODADA = "pontos_da_rodada"
    }
}
