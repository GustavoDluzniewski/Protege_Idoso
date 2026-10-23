package com.example.protege_idoso

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SanitizacaoIntegracaoTest {
    @Test
    fun mensagemSanitizadaNaoDeveConterDadosOriginais() {
        val mensagemOriginal = """
            Seu CPF é 529.982.247-25.
            Seu telefone é (69) 99999-9999.
            Seu e-mail é joao.silva@gmail.com.
            Sua chave Pix é 12345678000195.
            Acesse https://exemplo.com.br/premio
        """.trimIndent()

        val mensagemSanitizada =
            SanitizationService.sanitizeMessage(mensagemOriginal)

        assertTrue(mensagemSanitizada.contains("[CPF]"))
        assertTrue(mensagemSanitizada.contains("[TELEFONE]"))
        assertTrue(mensagemSanitizada.contains("[EMAIL]"))
        assertTrue(mensagemSanitizada.contains("[CHAVE_PIX]"))
        assertTrue(mensagemSanitizada.contains("[LINK]"))

        assertFalse(mensagemSanitizada.contains("529.982.247-25"))
        assertFalse(mensagemSanitizada.contains("(69) 99999-9999"))
        assertFalse(mensagemSanitizada.contains("joao.silva@gmail.com"))
        assertFalse(mensagemSanitizada.contains("12345678000195"))
        assertFalse(mensagemSanitizada.contains("exemplo.com.br"))
    }
}
