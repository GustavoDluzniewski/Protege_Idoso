package com.example.protege_idoso

object LocalRiskAnalyzer {

    fun analisarMensagem(mensagem: String): RiskResult {
        val texto = mensagem.lowercase()

        val sinais = contarSinaisDeRisco(texto)

        return when {
            temSinalGrave(texto) || sinais >= 3 -> {
                RiskResult(
                    nivel = RiskLevel.POSSIVEL_GOLPE,
                    explicacao = "A mensagem possui sinais fortes de golpe, como pedido de senha, código, PIX, prêmio ou urgência.",
                    orientacao = "Não clique em links, não envie dinheiro e não informe senhas ou códigos. Confirme com alguém de confiança antes de responder."
                )
            }

            sinais >= 1 -> {
                RiskResult(
                    nivel = RiskLevel.SUSPEITO,
                    explicacao = "A mensagem possui sinais de alerta, como link, urgência, pedido de dados ou remetente desconhecido.",
                    orientacao = "Verifique com cuidado antes de responder. Evite clicar em links desconhecidos."
                )
            }

            else -> {
                RiskResult(
                    nivel = RiskLevel.SEGURO,
                    explicacao = "Nenhum sinal claro de golpe foi identificado.",
                    orientacao = "A mensagem parece segura."
                )
            }
        }
    }

    fun deveEnviarParaIA(mensagem: String): Boolean {
        val texto = mensagem.lowercase()

        val sinais = contarSinaisDeRisco(texto)

        return sinais >= 1 || temSinalGrave(texto)
    }

    private fun contarSinaisDeRisco(texto: String): Int {
        val temPix = texto.contains("pix")
        val temSenha = texto.contains("senha")
        val temCodigo = texto.contains("código") || texto.contains("codigo")
        val temPremio = texto.contains("prêmio") ||
                texto.contains("premio") ||
                texto.contains("sorteio") ||
                texto.contains("ganhou") ||
                texto.contains("parabéns") ||
                texto.contains("parabens")

        val temUrgencia = texto.contains("urgente") ||
                texto.contains("agora") ||
                texto.contains("imediatamente") ||
                texto.contains("última chance") ||
                texto.contains("ultima chance") ||
                texto.contains("bloqueado") ||
                texto.contains("bloqueada") ||
                texto.contains("suspenso") ||
                texto.contains("suspensa")

        val temLink = texto.contains("http") ||
                texto.contains("www.") ||
                texto.contains(".com") ||
                texto.contains(".net") ||
                texto.contains(".br") ||
                texto.contains("bit.ly") ||
                texto.contains("tinyurl") ||
                texto.contains("encurtador")

        val pedeDados = texto.contains("confirme seus dados") ||
                texto.contains("dados pessoais") ||
                texto.contains("cpf") ||
                texto.contains("rg") ||
                texto.contains("cartão") ||
                texto.contains("cartao")

        val imitaInstituicao = texto.contains("banco") ||
                texto.contains("gov") ||
                texto.contains("governo") ||
                texto.contains("receita federal") ||
                texto.contains("caixa") ||
                texto.contains("nubank") ||
                texto.contains("bradesco") ||
                texto.contains("itau") ||
                texto.contains("itaú") ||
                texto.contains("santander")

        val fingeFamiliar = texto.contains("troquei de número") ||
                texto.contains("troquei de numero") ||
                texto.contains("meu celular quebrou") ||
                texto.contains("preciso de dinheiro") ||
                texto.contains("me ajuda com um pix") ||
                texto.contains("sou seu filho") ||
                texto.contains("sou sua filha")

        return listOf(
            temPix,
            temSenha,
            temCodigo,
            temPremio,
            temUrgencia,
            temLink,
            pedeDados,
            imitaInstituicao,
            fingeFamiliar
        ).count { it }
    }

    private fun temSinalGrave(texto: String): Boolean {
        return texto.contains("senha") ||
                texto.contains("código") ||
                texto.contains("codigo") ||
                texto.contains("pix") ||
                texto.contains("me ajuda com um pix") ||
                texto.contains("confirme seus dados") ||
                texto.contains("troquei de número") ||
                texto.contains("troquei de numero")
    }
}