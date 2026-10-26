package com.example.protege_idoso.quiz

import kotlin.random.Random

object RegrasDoQuiz {
    const val PERGUNTAS_POR_RODADA = 4
    const val TEMPO_POR_PERGUNTA_MS = 4 * 60 * 1000L
    const val INTERVALO_ENTRE_RODADAS_MS = 24 * 60 * 60 * 1000L

    private const val PONTOS_POR_ACERTO = 100
    private const val BONUS_MAXIMO_DE_RAPIDEZ = 50
    const val PRAZO_PARA_MANTER_SEQUENCIA_MS = 2 * INTERVALO_ENTRE_RODADAS_MS
    private const val BONUS_POR_DIA_SEGUIDO = 10
    private const val LIMITE_DE_DIAS_COM_BONUS = 10

    val MARCOS_DE_SEQUENCIA = listOf(3, 7, 15, 30, 60, 100, 365)
    fun proximaLiberacao(ultimaConclusao: Long): Long =
        if (ultimaConclusao <= 0L) 0L else ultimaConclusao + INTERVALO_ENTRE_RODADAS_MS

    fun estaLiberado(agora: Long, ultimaConclusao: Long): Boolean =
        agora >= proximaLiberacao(ultimaConclusao)

    fun tempoRestanteDaPergunta(inicioDaPergunta: Long, agora: Long): Long =
        (TEMPO_POR_PERGUNTA_MS - (agora - inicioDaPergunta)).coerceIn(0L, TEMPO_POR_PERGUNTA_MS)

    fun pontosDaResposta(acertou: Boolean, tempoRestante: Long): Int {
        if (!acertou) return 0
        val bonus = tempoRestante.coerceIn(0L, TEMPO_POR_PERGUNTA_MS) *
                BONUS_MAXIMO_DE_RAPIDEZ / TEMPO_POR_PERGUNTA_MS
        return PONTOS_POR_ACERTO + bonus.toInt()
    }
    fun novaSequencia(ultimaConclusao: Long, agora: Long, sequenciaAtual: Int): Int =
        if (ultimaConclusao > 0L && agora - ultimaConclusao <= PRAZO_PARA_MANTER_SEQUENCIA_MS) {
            sequenciaAtual + 1
        } else {
            1
        }
    fun sequenciaVigente(ultimaConclusao: Long, agora: Long, sequenciaSalva: Int): Int =
        if (ultimaConclusao > 0L && agora - ultimaConclusao <= PRAZO_PARA_MANTER_SEQUENCIA_MS) {
            sequenciaSalva
        } else {
            0
        }

    fun prazoDaSequencia(ultimaConclusao: Long): Long =
        ultimaConclusao + PRAZO_PARA_MANTER_SEQUENCIA_MS
    fun bonusDeSequencia(sequencia: Int): Int =
        if (sequencia <= 1) 0 else BONUS_POR_DIA_SEGUIDO * sequencia.coerceAtMost(LIMITE_DE_DIAS_COM_BONUS)

    fun ehMarco(sequencia: Int): Boolean = sequencia in MARCOS_DE_SEQUENCIA

    fun proximoMarco(sequencia: Int): Int? = MARCOS_DE_SEQUENCIA.firstOrNull { it > sequencia }

    fun nomeDoMarco(sequencia: Int): String = when (sequencia) {
        3 -> "Bom hábito: 3 dias seguidos!"
        7 -> "Uma semana inteira de treino!"
        15 -> "Quinze dias sem cair em golpe!"
        30 -> "Um mês inteiro de atenção!"
        60 -> "Dois meses de proteção!"
        100 -> "Cem dias! Você é exemplo para a família!"
        365 -> "Um ano inteiro! Que orgulho!"
        else -> "$sequencia dias seguidos!"
    }
    fun perguntasDaRodada(
        numeroDaRodada: Int,
        banco: List<PerguntaQuiz> = BancoDePerguntas.todas
    ): List<PerguntaQuiz> {
        val rodadasPorCiclo = banco.size / PERGUNTAS_POR_RODADA
        val ciclo = numeroDaRodada / rodadasPorCiclo
        val posicao = numeroDaRodada % rodadasPorCiclo
        val inicio = posicao * PERGUNTAS_POR_RODADA
        return banco
            .shuffled(Random(ciclo))
            .subList(inicio, inicio + PERGUNTAS_POR_RODADA)
    }

    fun tituloDoJogador(acertosTotais: Int): String = when {
        acertosTotais >= 100 -> "Guardião contra golpes"
        acertosTotais >= 50 -> "Protetor experiente"
        acertosTotais >= 20 -> "Olho atento"
        acertosTotais >= 5 -> "Aprendiz cuidadoso"
        else -> "Iniciante"
    }
    fun formatarContagem(milissegundos: Long): String {
        val totalSegundos = (milissegundos.coerceAtLeast(0L) + 999L) / 1000L
        val horas = totalSegundos / 3600
        val minutos = (totalSegundos % 3600) / 60
        val segundos = totalSegundos % 60
        return "%02d:%02d:%02d".format(horas, minutos, segundos)
    }
    fun formatarCronometro(milissegundos: Long): String {
        val totalSegundos = (milissegundos.coerceAtLeast(0L) + 999L) / 1000L
        return "%d:%02d".format(totalSegundos / 60, totalSegundos % 60)
    }
    fun descreverTempo(milissegundos: Long): String {
        val totalSegundos = (milissegundos.coerceAtLeast(0L) + 999L) / 1000L
        val minutos = totalSegundos / 60
        val segundos = totalSegundos % 60
        val partes = buildList {
            if (minutos > 0) add(if (minutos == 1L) "1 minuto" else "$minutos minutos")
            if (segundos > 0 || minutos == 0L) add(if (segundos == 1L) "1 segundo" else "$segundos segundos")
        }
        return partes.joinToString(" e ")
    }
}
