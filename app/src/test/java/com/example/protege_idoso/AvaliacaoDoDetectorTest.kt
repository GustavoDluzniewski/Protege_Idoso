package com.example.protege_idoso

import org.junit.Test

/**
 * Mede o acerto do filtro local (sanitização + LocalRiskAnalyzer) em um conjunto
 * de mensagens rotuladas. Os exemplos abaixo são um ponto de partida: troque e
 * complete com golpes reais (notícias, Procon, Febraban) e mensagens do dia a dia.
 *
 * Rode com ./gradlew test e veja o resultado na saída do teste
 * (app/build/reports/tests/testDebugUnitTest/index.html, aba "Standard output").
 */
class AvaliacaoDoDetectorTest {

    private val golpes = listOf(
        "Oi mãe, troquei de número. Me manda um pix urgente que depois te explico",
        "Pai, meu celular quebrou, estou com o número novo. Preciso de dinheiro pra pagar uma conta hoje",
        "Parabéns! Você ganhou um prêmio de R\$ 5.000. Acesse https://premio-sorteio.xyz para resgatar",
        "Sua conta foi bloqueada. Clique no link e informe sua senha para desbloquear: http://seguranca-banco.top",
        "Olá, aqui é do suporte do WhatsApp. Me passe o código de 6 dígitos que chegou por SMS",
        "Receita Federal: seu CPF está irregular. Regularize agora em www.receita-regulariza.com",
        "Vó, sou seu neto. Faz um pix de 800 reais pra mim, é urgente",
        "Seu cartão foi suspenso por segurança. Confirme seus dados pelo link bit.ly/cartao-seguro",
        "Última chance! Sua encomenda está retida. Pague a taxa de R\$ 19,90 via pix para liberar",
        "Caixa Econômica: você tem um valor a receber do governo. Informe seu CPF para liberar",
        "Oi, troquei de numero. Salva esse aqui. Consegue me ajudar com um pix?",
        "Banco do Brasil informa: compra de R\$ 2.300 não reconhecida? Responda agora com o código recebido",
        "Seu nome foi sorteado na promoção do mês. Envie seu RG para receber o prêmio",
        "Nubank: detectamos acesso suspeito. Digite sua senha no link para não ter a conta bloqueada",
        "Mãe, preciso de dinheiro urgente, depois te devolvo. Transfira para essa chave pix"
    )

    private val normais = listOf(
        "Chego agora para o almoço, pode ir esquecendo a panela no fogo",
        "Bom dia! Como a senhora dormiu?",
        "Mãe, amanhã passo aí para levar a senhora no médico",
        "Já fui no banco e paguei a conta de luz",
        "Comprei uma caixa de bombom pra senhora",
        "Feliz aniversário, vó! Muita saúde e paz",
        "O culto hoje é às 19h, quer carona?",
        "Vi aquela receita de bolo que a senhora pediu, depois te mostro",
        "Te mandei o pix do aluguel, confere aí depois",
        "Parabéns pelo seu dia, tia!",
        "Estou indo agora pra casa, chego em 20 minutos",
        "A reunião do condomínio foi remarcada para sábado",
        "Lembra de tomar o remédio da pressão",
        "O neto ganhou o campeonato de futebol da escola!",
        "Vou passar no mercado, precisa de alguma coisa?"
    )

    private fun alertaria(mensagem: String): Boolean {
        val sanitizada = SanitizationService.sanitizeMessage(mensagem)
        return LocalRiskAnalyzer.analisarMensagem(sanitizada).nivel != RiskLevel.SEGURO
    }

    @Test
    fun matrizDeConfusaoDoFiltroLocal() {
        val verdadeirosPositivos = golpes.count { alertaria(it) }
        val falsosNegativos = golpes.size - verdadeirosPositivos
        val falsosPositivos = normais.count { alertaria(it) }
        val verdadeirosNegativos = normais.size - falsosPositivos

        val precisao = verdadeirosPositivos.toDouble() /
                (verdadeirosPositivos + falsosPositivos).coerceAtLeast(1)
        val recall = verdadeirosPositivos.toDouble() / golpes.size
        val acuracia = (verdadeirosPositivos + verdadeirosNegativos).toDouble() /
                (golpes.size + normais.size)

        println("=== Avaliação do filtro local ===")
        println("Golpes: ${golpes.size} | Normais: ${normais.size}")
        println("VP=$verdadeirosPositivos FN=$falsosNegativos FP=$falsosPositivos VN=$verdadeirosNegativos")
        println("Precisão=%.2f Recall=%.2f Acurácia=%.2f".format(precisao, recall, acuracia))

        golpes.filterNot { alertaria(it) }.forEach { println("Golpe não detectado: $it") }
        normais.filter { alertaria(it) }.forEach { println("Falso positivo: $it") }
    }
}
