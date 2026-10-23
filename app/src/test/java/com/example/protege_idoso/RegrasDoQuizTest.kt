package com.example.protege_idoso

import com.example.protege_idoso.quiz.BancoDePerguntas
import com.example.protege_idoso.quiz.RegrasDoQuiz
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegrasDoQuizTest {
    private val hora = 60 * 60 * 1000L
    private val dia = 24 * hora

    @Test
    fun bancoTemPerguntasValidas() {
        val banco = BancoDePerguntas.todas
        assertEquals(0, banco.size % RegrasDoQuiz.PERGUNTAS_POR_RODADA)
        assertEquals(banco.size, banco.map { it.id }.toSet().size)
        banco.forEach {
            assertEquals("Pergunta ${it.id}", 2, it.alternativas.size)
            assertTrue("Pergunta ${it.id}", it.indiceCorreto in it.alternativas.indices)
        }
    }

    @Test
    fun primeiroDesafioJaVemLiberado() {
        assertTrue(RegrasDoQuiz.estaLiberado(agora = 1_000L, ultimaConclusao = 0L))
    }

    @Test
    fun proximoDesafioSoDepoisDe24Horas() {
        val concluiu = 10 * dia
        assertFalse(RegrasDoQuiz.estaLiberado(concluiu + dia - 1, concluiu))
        assertTrue(RegrasDoQuiz.estaLiberado(concluiu + dia, concluiu))
        assertEquals(concluiu + dia, RegrasDoQuiz.proximaLiberacao(concluiu))
    }

    @Test
    fun relogioAtrasadoNaoLiberaAntes() {
        val concluiu = 10 * dia
        assertFalse(RegrasDoQuiz.estaLiberado(concluiu - 5 * dia, concluiu))
    }

    @Test
    fun cronometroDe4MinutosPorPergunta() {
        val inicio = 1_000_000L
        assertEquals(4 * 60_000L, RegrasDoQuiz.tempoRestanteDaPergunta(inicio, inicio))
        assertEquals(60_000L, RegrasDoQuiz.tempoRestanteDaPergunta(inicio, inicio + 3 * 60_000L))
        assertEquals(0L, RegrasDoQuiz.tempoRestanteDaPergunta(inicio, inicio + 10 * 60_000L))
        assertEquals(4 * 60_000L, RegrasDoQuiz.tempoRestanteDaPergunta(inicio, inicio - 60_000L))
    }

    @Test
    fun pontosComBonusDeRapidez() {
        assertEquals(0, RegrasDoQuiz.pontosDaResposta(false, 4 * 60_000L))
        assertEquals(150, RegrasDoQuiz.pontosDaResposta(true, 4 * 60_000L))
        assertEquals(125, RegrasDoQuiz.pontosDaResposta(true, 2 * 60_000L))
        assertEquals(100, RegrasDoQuiz.pontosDaResposta(true, 0L))
    }

    @Test
    fun sequenciaDeDias() {
        assertEquals(1, RegrasDoQuiz.novaSequencia(0L, dia, 0))
        assertEquals(4, RegrasDoQuiz.novaSequencia(dia, 2 * dia + hora, 3))
        assertEquals(1, RegrasDoQuiz.novaSequencia(dia, 4 * dia, 3))
    }

    @Test
    fun sequenciaVenceDepoisDe48Horas() {
        val concluiu = 10 * dia
        assertEquals(5, RegrasDoQuiz.sequenciaVigente(concluiu, concluiu + 47 * hora, 5))
        assertEquals(0, RegrasDoQuiz.sequenciaVigente(concluiu, concluiu + 49 * hora, 5))
        assertEquals(0, RegrasDoQuiz.sequenciaVigente(0L, concluiu, 5))
        assertEquals(concluiu + 2 * dia, RegrasDoQuiz.prazoDaSequencia(concluiu))
    }

    @Test
    fun bonusDeSequenciaCresceAte100() {
        assertEquals(0, RegrasDoQuiz.bonusDeSequencia(1))
        assertEquals(20, RegrasDoQuiz.bonusDeSequencia(2))
        assertEquals(70, RegrasDoQuiz.bonusDeSequencia(7))
        assertEquals(100, RegrasDoQuiz.bonusDeSequencia(30))
    }

    @Test
    fun conquistasDeSequencia() {
        assertTrue(RegrasDoQuiz.ehMarco(7))
        assertFalse(RegrasDoQuiz.ehMarco(8))
        assertEquals(3, RegrasDoQuiz.proximoMarco(0))
        assertEquals(15, RegrasDoQuiz.proximoMarco(7))
        assertEquals(null, RegrasDoQuiz.proximoMarco(365))
    }

    @Test
    fun rodadasNaoRepetemPerguntasDentroDoCiclo() {
        val banco = BancoDePerguntas.todas
        val rodadasPorCiclo = banco.size / RegrasDoQuiz.PERGUNTAS_POR_RODADA
        val ids = (0 until rodadasPorCiclo).flatMap { rodada ->
            RegrasDoQuiz.perguntasDaRodada(rodada).also {
                assertEquals(RegrasDoQuiz.PERGUNTAS_POR_RODADA, it.size)
            }.map { it.id }
        }
        assertEquals(banco.size, ids.toSet().size)
        assertEquals(RegrasDoQuiz.perguntasDaRodada(3), RegrasDoQuiz.perguntasDaRodada(3))
        assertEquals(4, RegrasDoQuiz.perguntasDaRodada(rodadasPorCiclo * 5 + 2).size)
    }

    @Test
    fun formataContagemECronometro() {
        assertEquals("23:59:59", RegrasDoQuiz.formatarContagem(dia - 1_000L))
        assertEquals("00:00:00", RegrasDoQuiz.formatarContagem(-5L))
        assertEquals("4:00", RegrasDoQuiz.formatarCronometro(4 * 60_000L))
        assertEquals("0:09", RegrasDoQuiz.formatarCronometro(8_500L))
        assertEquals("3 minutos e 20 segundos", RegrasDoQuiz.descreverTempo(200_000L))
        assertEquals("1 minuto", RegrasDoQuiz.descreverTempo(60_000L))
    }
}
