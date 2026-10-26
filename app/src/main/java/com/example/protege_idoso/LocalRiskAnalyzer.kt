package com.example.protege_idoso

object LocalRiskAnalyzer {

    fun analisarMensagem(
        mensagem: String,
        dominioForaDoRanking: Boolean = false
    ): RiskResult {
        val texto = mensagem.lowercase()

        val sinais = contarSinaisDeRisco(texto) +
                if (dominioForaDoRanking) 1 else 0

        return when {
            temSinalGrave(texto) || sinais >= 3 -> {
                RiskResult(
                    nivel = RiskLevel.POSSIVEL_GOLPE,
                    explicacao = "A mensagem possui sinais fortes de golpe, como pedido de senha, código, PIX, prêmio ou urgência.",
                    orientacao = "Não clique em links, não envie dinheiro e não informe senhas ou códigos. Confirme com uma pessoa de confiança antes de responder."
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

    fun deveEnviarParaIA(
        mensagem: String,
        dominioForaDoRanking: Boolean = false
    ): Boolean {
        val texto = mensagem.lowercase()

        val sinais = contarSinaisDeRisco(texto) +
                if (dominioForaDoRanking) 1 else 0

        return sinais >= 1 || temSinalGrave(texto)
    }

    private fun contarSinaisDeRisco(texto: String): Int {
        val temPix = texto.contains("pix")
        val temSenha = texto.contains("senha")
        val temCodigo = texto.contains("código") || texto.contains("codigo")
        val temPremio = texto.contains("prêmio") ||
                texto.contains("premio") ||
                texto.contains("sorteio") ||
                texto.contains("sorteado") ||
                texto.contains("você ganhou") ||
                texto.contains("voce ganhou")

        val temUrgencia = texto.contains("urgente") ||
                texto.contains("responda agora") ||
                texto.contains("pague agora") ||
                texto.contains("clique agora") ||
                texto.contains("acesse agora") ||
                texto.contains("imediatamente") ||
                texto.contains("última chance") ||
                texto.contains("ultima chance") ||
                texto.contains("bloqueado") ||
                texto.contains("bloqueada") ||
                texto.contains("suspenso") ||
                texto.contains("suspensa")

        val temLink = texto.contains("[link]") ||
                texto.contains("http") ||
                texto.contains("www.") ||
                texto.contains("bit.ly") ||
                texto.contains("tinyurl") ||
                texto.contains("encurtador")

        val pedeDados = texto.contains("confirme seus dados") ||
                texto.contains("dados pessoais") ||
                texto.contains("envie seu cpf") ||
                texto.contains("mande seu cpf") ||
                texto.contains("informe seu cpf") ||
                texto.contains("digite seu cpf") ||
                texto.contains("envie seu rg") ||
                texto.contains("mande seu rg") ||
                texto.contains("informe seu rg") ||
                texto.contains("envie seu cartão") ||
                texto.contains("mande seu cartão") ||
                texto.contains("informe seu cartão") ||
                texto.contains("digite seu cartão") ||
                texto.contains("envie seu cartao") ||
                texto.contains("mande seu cartao") ||
                texto.contains("informe seu cartao") ||
                texto.contains("digite seu cartao")

        val imitaInstituicao = texto.contains("seu banco") ||
                texto.contains("banco do brasil") ||
                texto.contains("banco central") ||
                texto.contains("gov.br") ||
                texto.contains("governo") ||
                texto.contains("receita federal") ||
                texto.contains("caixa econômica") ||
                texto.contains("caixa economica") ||
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
        val pedeSenha =
            (texto.contains("senha") || texto.contains("palavra-passe")) &&
                    listOf(
                        "informe", "digite", "envie", "compartilhe",
                        "forneça", "forneca", "passe sua"
                    ).any { texto.contains(it) }

        val pedeCodigo =
            (texto.contains("código") || texto.contains("codigo")) &&
                    listOf(
                        "informe", "digite", "envie", "compartilhe",
                        "forneça", "forneca", "me passe"
                    ).any { texto.contains(it) }


        val pedeTransferencia =
            Regex(
                """\b(faça|faca|envie|transfira|pague|deposite|mande|manda|realize|efetue)\b"""
            ).containsMatchIn(texto) &&
                    Regex(
                        """\b(pix|transferência|transferencia|depósito|deposito)\b"""
                    ).containsMatchIn(texto)


        val pedeDinheiroComoFamiliar =
            (texto.contains("troquei de número") ||
                    texto.contains("troquei de numero") ||
                    texto.contains("meu celular quebrou") ||
                    texto.contains("sou seu filho") ||
                    texto.contains("sou sua filha") ||
                    texto.contains("sou seu neto") ||
                    texto.contains("sou sua neta")) &&
                    listOf(
                        "dinheiro", "pix", "transferência",
                        "transferencia", "depósito", "deposito"
                    ).any { texto.contains(it) }

        val ameacaComBloqueio =
            (texto.contains("bloqueio") ||
                    texto.contains("bloqueada") ||
                    texto.contains("bloqueado")) &&
                    (texto.contains("senha") ||
                            texto.contains("código") ||
                            texto.contains("codigo") ||
                            texto.contains("pix") ||
                            texto.contains("clique no link"))

        val pedeDadosPorLink =
            (texto.contains("[link]") ||
                    texto.contains("http") ||
                    texto.contains("www.")) &&
                    listOf(
                        "confirme seus dados", "atualize seus dados",
                        "dados pessoais", "seu cpf", "seu cartão", "seu cartao"
                    ).any { texto.contains(it) }

        return pedeSenha ||
                pedeCodigo ||
                pedeDadosPorLink ||
                pedeTransferencia ||
                pedeDinheiroComoFamiliar ||
                ameacaComBloqueio
    }

}