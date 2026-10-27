package com.example.protege_idoso

import com.example.protege_idoso.contatos.ContatoDaAgenda
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContatoDaAgendaTest {

    @Test
    fun numeroComMaisCinquentaECincoViraFormatoComDdd() {
        val contato = ContatoDaAgenda("Maria Souza", "+55 69 99999-8888")
        assertEquals("69999998888", contato.telefoneNormalizado)
        assertEquals("(69) 99999-8888", contato.telefoneParaFormulario)
    }

    @Test
    fun numeroComZeroDeDiscagemTambemEhAceito() {
        val contato = ContatoDaAgenda("João", "069 3216-8940")
        assertEquals("(69) 3216-8940", contato.telefoneParaFormulario)
    }

    @Test
    fun numeroSemDddFicaComoVeioParaAPessoaCompletar() {
        val contato = ContatoDaAgenda("Ana", "99999-8888")
        assertNull(contato.telefoneNormalizado)
        assertEquals("99999-8888", contato.telefoneParaFormulario)
    }

    @Test
    fun nomeTemEspacosRepetidosRemovidosELimiteDeTamanho() {
        assertEquals("Maria da Silva", ContatoDaAgenda("  Maria   da  Silva ", "69999998888").nomeParaFormulario)
        assertEquals(50, ContatoDaAgenda("A".repeat(80), "69999998888").nomeParaFormulario.length)
    }
}
