package com.example.protege_idoso

import com.example.protege_idoso.contatos.ContatoConfianca
import com.example.protege_idoso.contatos.Telefone
import com.example.protege_idoso.contatos.ValidacaoContato
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContatosDeConfiancaTest {
    @Test
    fun aceitaCelularEmVariosFormatos() {
        val esperado = "69999998888"
        assertEquals(esperado, Telefone.normalizar("69999998888"))
        assertEquals(esperado, Telefone.normalizar("(69) 99999-8888"))
        assertEquals(esperado, Telefone.normalizar("69 9 9999 8888"))
        assertEquals(esperado, Telefone.normalizar("+55 (69) 99999-8888"))
        assertEquals(esperado, Telefone.normalizar("5569999998888"))
        assertEquals(esperado, Telefone.normalizar("069999998888"))
    }

    @Test
    fun aceitaTelefoneFixo() {
        assertEquals("6932168940", Telefone.normalizar("(69) 3216-8940"))
        assertEquals("1133334444", Telefone.normalizar("+55 11 3333-4444"))
    }

    @Test
    fun mantemDddCinquentaECinco() {
        assertEquals("55999998888", Telefone.normalizar("(55) 99999-8888"))
        assertEquals("5532221111", Telefone.normalizar("(55) 3222-1111"))
    }

    @Test
    fun recusaTelefonesInvalidos() {
        assertNull(Telefone.normalizar(""))
        assertNull(Telefone.normalizar("99999-8888"))
        assertNull(Telefone.normalizar("(10) 99999-8888"))
        assertNull(Telefone.normalizar("(20) 99999-8888"))
        assertNull(Telefone.normalizar("(69) 89999-8888"))
        assertNull(Telefone.normalizar("(69) 1216-8940"))
        assertNull(Telefone.normalizar("(69) 99999-99999"))
        assertNull(Telefone.normalizar("(69) 9999-9999"))
        assertNull(Telefone.normalizar("abc"))
    }

    @Test
    fun formataParaMostrar() {
        assertEquals("(69) 99999-8888", Telefone.formatar("69999998888"))
        assertEquals("(69) 3216-8940", Telefone.formatar("6932168940"))
        assertEquals("123", Telefone.formatar("123"))
    }

    @Test
    fun montaEnderecosDeLigacaoEWhatsApp() {
        assertEquals("tel:+5569999998888", Telefone.uriDeLigacao("69999998888"))
        assertEquals("5569999998888", Telefone.paraWhatsApp("69999998888"))
        assertTrue(Telefone.ehCelular("69999998888"))
        assertFalse(Telefone.ehCelular("6932168940"))
    }

    @Test
    fun validaFormularioCompleto() {
        val erros = ValidacaoContato.validar("Maria Souza", "(69) 99999-8888", "Filho(a)")
        assertFalse(erros.temErro)
    }

    @Test
    fun parentescoEhOpcional() {
        val erros = ValidacaoContato.validar("João", "69999998888", "")
        assertFalse(erros.temErro)
    }

    @Test
    fun apontaCadaErroNoSeuCampo() {
        val vazio = ValidacaoContato.validar("  ", "", "")
        assertNotNull(vazio.nome)
        assertNotNull(vazio.telefone)
        assertNull(vazio.parentesco)

        val curto = ValidacaoContato.validar("A", "69999998888", "")
        assertNotNull(curto.nome)
        assertNull(curto.telefone)

        val semLetras = ValidacaoContato.validar("12345", "69999998888", "")
        assertNotNull(semLetras.nome)

        val longo = ValidacaoContato.validar("A".repeat(51), "69999998888", "")
        assertNotNull(longo.nome)

        val telefoneRuim = ValidacaoContato.validar("Ana", "1234", "")
        assertNull(telefoneRuim.nome)
        assertNotNull(telefoneRuim.telefone)

        val parentescoLongo = ValidacaoContato.validar("Ana", "69999998888", "x".repeat(31))
        assertNotNull(parentescoLongo.parentesco)
    }

    @Test
    fun limpaEspacosDoNome() {
        assertEquals("Maria da Silva", ValidacaoContato.limparNome("  Maria   da  Silva "))
    }

    @Test
    fun calculaIniciais() {
        assertEquals("MS", ContatoConfianca(nome = "Maria da Silva", telefone = "69999998888").iniciais)
        assertEquals("J", ContatoConfianca(nome = "joão", telefone = "69999998888").iniciais)
        assertEquals("?", ContatoConfianca(nome = "  ", telefone = "69999998888").iniciais)
    }

    @Test
    fun primeiroNomeParaOBotaoDeLigar() {
        assertEquals("Maria", primeiroNome(ContatoConfianca(nome = "Maria da Silva", telefone = "69999998888")))
    }
}
