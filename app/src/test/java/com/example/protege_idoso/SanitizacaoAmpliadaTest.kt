package com.example.protege_idoso

import org.junit.Assert.assertEquals
import org.junit.Test

class SanitizacaoAmpliadaTest {

    private fun limpar(texto: String) = SanitizationService.sanitizeMessage(texto)

    @Test
    fun deveEsconderCartaoComEspacos() {
        assertEquals("Cartão: [CARTAO].", limpar("Cartão: 4111 1111 1111 1111."))
    }

    @Test
    fun deveEsconderCartaoSemEspacos() {
        assertEquals("Número [CARTAO]", limpar("Número 5555555555554444"))
    }

    @Test
    fun deveEsconderCartaoAmex() {
        assertEquals("[CARTAO]", limpar("3782 822463 10005"))
    }

    @Test
    fun naoDeveEsconderNumeroQueNaoEhCartao() {
        assertEquals("Pedido 4111111111111112 confirmado", limpar("Pedido 4111111111111112 confirmado"))
    }

    @Test
    fun deveEsconderCep() {
        assertEquals("CEP [CEP], Cuiabá", limpar("CEP 78000-000, Cuiabá"))
        assertEquals("cep: [CEP]", limpar("cep: 78000000"))
    }

    @Test
    fun deveEsconderAgenciaEConta() {
        assertEquals(
            "Agência [AGENCIA], conta [CONTA]",
            limpar("Agência 1234-5, conta 98765-4")
        )
        assertEquals(
            "conta corrente nº [CONTA]",
            limpar("conta corrente nº 123456-7")
        )
    }

    @Test
    fun naoDeveMexerEmContaSemNumero() {
        assertEquals("Sua conta foi bloqueada", limpar("Sua conta foi bloqueada"))
        assertEquals("Vou agora 1234 vezes", limpar("Vou agora 1234 vezes"))
    }

    @Test
    fun deveEsconderRg() {
        assertEquals("RG: [RG]", limpar("RG: 12.345.678-9"))
        assertEquals("meu rg [RG]", limpar("meu rg 1234567"))
    }

    @Test
    fun chavePixCnpjContinuaComoAntes() {
        assertEquals("Pix: [CHAVE_PIX].", limpar("Pix: 12345678000195."))
    }

    @Test
    fun urlSemParametrosNaoMuda() {
        assertEquals(
            "https://golpe.com/login",
            SanitizationService.sanitizarUrl("https://golpe.com/login")
        )
    }

    @Test
    fun urlTemEmailECpfRetiradosDosParametros() {
        assertEquals(
            "https://golpe.com/login?email=REMOVIDO&id=123",
            SanitizationService.sanitizarUrl("https://golpe.com/login?email=joao%40gmail.com&id=123")
        )
        assertEquals(
            "https://golpe.com/a?cpf=REMOVIDO",
            SanitizationService.sanitizarUrl("https://golpe.com/a?cpf=529.982.247-25")
        )
        assertEquals(
            "https://golpe.com/a#REMOVIDO",
            SanitizationService.sanitizarUrl("https://golpe.com/a#52998224725")
        )
    }

    @Test
    fun urlMantemNumerosQueNaoSaoCpf() {
        assertEquals(
            "https://loja.com/p?pedido=12345678901",
            SanitizationService.sanitizarUrl("https://loja.com/p?pedido=12345678901")
        )
    }
}
