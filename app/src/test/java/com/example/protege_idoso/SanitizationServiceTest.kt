package com.example.protege_idoso

import org.junit.Test
import org.junit.Assert.assertEquals

class SanitizationServiceTest {

    @Test
    fun deveSanitizarDadosPessoais() {

        val mensagem = """
            Olá João!
            Seu CPF é 123.456.789-00.
            Seu telefone é (69) 99999-9999.
            Seu e-mail é joao@gmail.com.
            Acesse https://site-suspeito.com/login
        """.trimIndent()

        val resultado = SanitizationService.sanitizeMessage(mensagem)

        println("===== ORIGINAL =====")
        println(mensagem)

        println("===== SANITIZADA =====")
        println(resultado)

        assertEquals(
            """
            Olá João!
            Seu CPF é [CPF].
            Seu telefone é [TELEFONE].
            Seu e-mail é [EMAIL].
            Acesse [LINK]
            """.trimIndent(),
            resultado
        )
    }
    @Test
    fun deveSanitizarCpfComEPontuacao() {
        val texto = "Meu CPF é 047.284.442-36 ou 04728444236"

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Meu CPF é [CPF] ou [CPF]",
            resultado
        )
    }
    @Test
    fun deveSanitizarTelefoneSemMascara() {
        val texto = "Meu telefone é 69999999999 e (69)98135-0326."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Meu telefone é [TELEFONE] e [TELEFONE].",
            resultado
        )
    }
    @Test
    fun deveSanitizarEmailComCaracteresEspeciais() {
        val texto = "Entre em contato: joao.silva123+teste@gmail.com."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Entre em contato: [EMAIL].",
            resultado
        )
    }
    @Test
    fun deveSanitizarLink() {
        val texto = "Acesse https://www.google.com para mais informações."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Acesse [LINK] para mais informações.",
            resultado
        )
    }
    @Test
    fun deveSanitizarLinkComWww() {
        val texto = "Entre no site www.exemplo.com.br para consultar."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Entre no site [LINK] para consultar.",
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarLinkComCaminhoEParametros() {
        val texto = "Clique aqui: https://exemplo.com.br/pagamento?id=12345&user=teste."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Clique aqui: [LINK]",
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarChavePixAleatoria() {
        val texto = "Minha chave Pix é 123e4567-e89b-12d3-a456-426614174000."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Minha chave Pix é [CHAVE_PIX].",
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarChavePixCpf() {
        val texto = "Minha chave Pix é 529.982.247-25."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Minha chave Pix é [CPF].",
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarChavePixTelefone() {
        val texto = "\nMinha chave Pix é +5569999999999.\n"

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "\nMinha chave Pix é [TELEFONE].\n",
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarChavePixEmail() {
        val texto = "Minha chave Pix é joao.silva@gmail.com."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Minha chave Pix é [EMAIL].",
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarChavePixCnpj() {
        val texto = "Minha chave Pix: 12345678000195."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Minha chave Pix: [CHAVE_PIX].",
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarMensagemCompleta() {
        val texto = """
        Olá João, seus dados são:
        CPF: 529.982.247-25
        Telefone: (69) 99999-9999
        Email: joao@gmail.com
        Site: https://exemplo.com.br/pagamento?id=123
        Pix: 12345678000195
    """.trimIndent()

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            """
        Olá João, seus dados são:
        CPF: [CPF]
        Telefone: [TELEFONE]
        Email: [EMAIL]
        Site: [LINK]
        Pix: [CHAVE_PIX]
        """.trimIndent(),
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarChavePixSemDoisPontos() {
        val texto = "Minha chave Pix é 12345678000195."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Minha chave Pix é [CHAVE_PIX].",
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarChavePixSemSeparador() {
        val texto = "Pix 12345678000195."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Pix [CHAVE_PIX].",
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarChavePixComTextoChave() {
        val texto = "Chave Pix 12345678000195."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Chave Pix [CHAVE_PIX].",
            resultado
        )
        println(resultado)
    }
    @Test
    fun deveSanitizarChavePixComDoisPontos() {
        val texto = "Pix: 12345678000195."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Pix: [CHAVE_PIX].",
            resultado
        )
        println(resultado)
    }
    @Test
    fun naoDeveSanitizarNumeroDe14DigitosSemContextoPix() {
        val texto = "O código da operação é 12345678000195."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "O código da operação é 12345678000195.",
            resultado
        )
        println(resultado)
    }
    @Test
    fun naoDeveSanitizarPixSemChave() {
        val texto = "Hoje vou fazer um pagamento via Pix para a loja."

        val resultado = SanitizationService.sanitizeMessage(texto)

        assertEquals(
            "Hoje vou fazer um pagamento via Pix para a loja.",
            resultado
        )
        println(resultado)
    }
}
