package com.example.protege_idoso.quiz

data class PerguntaQuiz(
    val id: Int,
    val cenario: String,
    val alternativas: List<String>,
    val indiceCorreto: Int,
    val explicacao: String
) {
    val respostaCorreta: String get() = alternativas[indiceCorreto]
}
object BancoDePerguntas {

    val todas: List<PerguntaQuiz> = listOf(
        PerguntaQuiz(
            id = 1,
            cenario = "Chega uma mensagem: \"Oi, mãe! Troquei de número, salva esse. " +
                    "Preciso pagar uma conta urgente, você me faz um Pix?\"",
            alternativas = listOf(
                "Faço o Pix logo para ajudar.",
                "Ligo para o número antigo do meu filho para confirmar."
            ),
            indiceCorreto = 1,
            explicacao = "Esse é o golpe do falso parente. Antes de mandar dinheiro, " +
                    "ligue para o número que você já conhece ou fale com outra pessoa da família."
        ),
        PerguntaQuiz(
            id = 2,
            cenario = "Alguém liga dizendo ser da central do seu banco. Fala que houve uma " +
                    "compra estranha e pede a sua senha para cancelar.",
            alternativas = listOf(
                "Desligo e ligo eu mesmo para o número que está atrás do cartão.",
                "Falo a senha para cancelar a compra rápido."
            ),
            indiceCorreto = 0,
            explicacao = "O banco nunca pede a sua senha por telefone. Desligue e ligue você " +
                    "mesmo para o número oficial, de preferência de outro aparelho."
        ),
        PerguntaQuiz(
            id = 3,
            cenario = "\"Parabéns! Você ganhou um vale-compras de R$ 500. " +
                    "Clique no link para resgatar hoje.\"",
            alternativas = listOf(
                "Clico no link só para ver se é verdade.",
                "Não clico e apago a mensagem."
            ),
            indiceCorreto = 1,
            explicacao = "Prêmio que você não pediu é isca. O link pode roubar seus dados " +
                    "ou instalar um programa no celular."
        ),
        PerguntaQuiz(
            id = 4,
            cenario = "Você recebeu um código de 6 números por SMS. Logo depois, alguém no " +
                    "WhatsApp pede: \"Te mandei um código por engano, me passa?\"",
            alternativas = listOf(
                "Não passo o código para ninguém.",
                "Passo o código, já que a pessoa pediu com educação."
            ),
            indiceCorreto = 0,
            explicacao = "Com esse código, o golpista entra no seu WhatsApp e pede dinheiro " +
                    "aos seus contatos. Esse código é só seu."
        ),
        PerguntaQuiz(
            id = 5,
            cenario = "Ligam dizendo que o seu cartão foi clonado e que um motoboy do banco " +
                    "vai passar na sua casa para buscar o cartão.",
            alternativas = listOf(
                "Corto o cartão ao meio e entrego ao motoboy.",
                "Não entrego. O banco nunca manda buscar cartão em casa."
            ),
            indiceCorreto = 1,
            explicacao = "Nenhum banco busca cartão em casa, nem cortado. Mesmo partido, o chip " +
                    "pode ser usado. Ligue para o banco pelo número oficial."
        ),
        PerguntaQuiz(
            id = 6,
            cenario = "\"Seu benefício do INSS será bloqueado. Atualize seus dados " +
                    "neste link até hoje.\"",
            alternativas = listOf(
                "Confiro pelo aplicativo Meu INSS ou pelo telefone 135.",
                "Atualizo os dados pelo link para não perder o benefício."
            ),
            indiceCorreto = 0,
            explicacao = "O INSS não manda links por mensagem. Consulte sempre pelo Meu INSS, " +
                    "pelo 135 ou numa agência."
        ),
        PerguntaQuiz(
            id = 7,
            cenario = "Uma loja manda um boleto pelo WhatsApp com 40% de desconto, " +
                    "mas só se você pagar hoje.",
            alternativas = listOf(
                "Pago hoje para garantir o desconto.",
                "Confiro com a loja pelo telefone ou site oficial antes de pagar."
            ),
            indiceCorreto = 1,
            explicacao = "Pressa e desconto grande são truques comuns. Boleto falso parece " +
                    "verdadeiro, então confirme pelos canais oficiais da empresa."
        ),
        PerguntaQuiz(
            id = 8,
            cenario = "O telefone toca e uma voz chorando diz: \"Me sequestraram! " +
                    "Faz o depósito que eles estão pedindo!\"",
            alternativas = listOf(
                "Desligo e ligo para o meu familiar ou para alguém próximo dele.",
                "Faço o depósito o mais rápido possível."
            ),
            indiceCorreto = 0,
            explicacao = "O falso sequestro usa o susto para você não pensar. Desligue, " +
                    "ligue para a pessoa e, se precisar, chame a polícia no 190."
        ),
        PerguntaQuiz(
            id = 9,
            cenario = "Oferecem um empréstimo já aprovado, mas pedem que você pague uma " +
                    "taxa antes para liberar o dinheiro.",
            alternativas = listOf(
                "Pago a taxa, porque o empréstimo já está aprovado.",
                "Recuso. Empréstimo de verdade não cobra taxa adiantada."
            ),
            indiceCorreto = 1,
            explicacao = "Cobrar taxa antes de liberar empréstimo é golpe. Procure o seu " +
                    "banco pessoalmente se precisar de crédito."
        ),
        PerguntaQuiz(
            id = 10,
            cenario = "Um amigo, com a foto de sempre no WhatsApp, pede R$ 800 emprestados " +
                    "e diz que devolve amanhã.",
            alternativas = listOf(
                "Ligo para o meu amigo, por voz ou vídeo, antes de mandar.",
                "Mando o dinheiro, porque é a foto dele."
            ),
            indiceCorreto = 0,
            explicacao = "Golpistas copiam a foto e o nome de pessoas conhecidas. Só uma " +
                    "ligação ou uma conversa pessoalmente confirma quem é."
        ),
        PerguntaQuiz(
            id = 11,
            cenario = "\"Ganhe R$ 300 por dia só curtindo vídeos! Para começar, pague " +
                    "R$ 50 de inscrição.\"",
            alternativas = listOf(
                "Pago a inscrição para começar a ganhar.",
                "Ignoro. Ninguém paga tanto por tão pouco."
            ),
            indiceCorreto = 1,
            explicacao = "Trabalho fácil com ganho alto e taxa de entrada é golpe. " +
                    "O dinheiro da inscrição nunca volta."
        ),
        PerguntaQuiz(
            id = 12,
            cenario = "Um \"técnico do banco\" pede que você instale um aplicativo para ele " +
                    "\"proteger\" o seu celular de longe.",
            alternativas = listOf(
                "Não instalo e conto para alguém da família.",
                "Instalo, já que é para a minha segurança."
            ),
            indiceCorreto = 0,
            explicacao = "Esses aplicativos dão controle do seu celular ao golpista, " +
                    "inclusive do aplicativo do banco. Banco não pede isso."
        ),
        PerguntaQuiz(
            id = 13,
            cenario = "\"Mandei um Pix para você por engano, pode devolver?\" A pessoa até " +
                    "manda a foto do comprovante.",
            alternativas = listOf(
                "Devolvo na hora, coitada da pessoa.",
                "Confiro no aplicativo do meu banco se o dinheiro entrou mesmo."
            ),
            indiceCorreto = 1,
            explicacao = "Comprovante em foto pode ser falso. Veja no seu extrato. Se o dinheiro " +
                    "entrou, peça ao seu banco a devolução pelo próprio Pix."
        ),
        PerguntaQuiz(
            id = 14,
            cenario = "\"Sua luz será cortada hoje por conta atrasada. Pague por este " +
                    "QR Code para evitar o corte.\"",
            alternativas = listOf(
                "Confiro no aplicativo ou na conta da companhia de energia.",
                "Pago pelo QR Code para não ficar sem luz."
            ),
            indiceCorreto = 0,
            explicacao = "Ameaça de corte imediato é para causar pressa. Confira a sua conta " +
                    "pelos canais oficiais da companhia."
        ),
        PerguntaQuiz(
            id = 15,
            cenario = "\"Sua encomenda está retida nos Correios. Pague R$ 12,90 de taxa " +
                    "neste link para liberar.\"",
            alternativas = listOf(
                "Pago, é um valor pequeno.",
                "Confiro o rastreio no site oficial dos Correios."
            ),
            indiceCorreto = 1,
            explicacao = "O valor baixo serve para você não desconfiar, e o link rouba os dados " +
                    "do cartão. Confira sempre no site oficial."
        ),
        PerguntaQuiz(
            id = 16,
            cenario = "Uma gravação diz que você ganhou um prêmio e pede para digitar " +
                    "1 e depois os números do seu CPF.",
            alternativas = listOf(
                "Desligo sem digitar nada.",
                "Sigo as instruções para receber o prêmio."
            ),
            indiceCorreto = 0,
            explicacao = "Ninguém dá prêmio por ligação automática. Seus dados podem ser usados " +
                    "para abrir contas e fazer compras no seu nome."
        ),
        PerguntaQuiz(
            id = 17,
            cenario = "Uma pessoa que você conheceu pela internet diz que está apaixonada " +
                    "e pede dinheiro para a passagem para vir te ver.",
            alternativas = listOf(
                "Mando o dinheiro, ela parece sincera.",
                "Desconfio e converso com alguém da família."
            ),
            indiceCorreto = 1,
            explicacao = "O golpe do amor conquista a confiança aos poucos para depois pedir " +
                    "dinheiro. Quem gosta de você não pede dinheiro assim."
        ),
        PerguntaQuiz(
            id = 18,
            cenario = "Oferecem um investimento que rende 10% por mês, com lucro garantido " +
                    "e sem risco.",
            alternativas = listOf(
                "Desconfio. Lucro alto e garantido é sinal de golpe.",
                "Invisto um pouco para testar."
            ),
            indiceCorreto = 0,
            explicacao = "Nenhum investimento sério garante lucro tão alto. Converse com o seu " +
                    "banco ou com alguém de confiança antes de investir."
        ),
        PerguntaQuiz(
            id = 19,
            cenario = "\"Receita Federal: seu CPF está irregular. Pague a multa hoje " +
                    "para não ser bloqueado.\"",
            alternativas = listOf(
                "Pago a multa para resolver logo.",
                "Consulto a situação do meu CPF no site gov.br."
            ),
            indiceCorreto = 1,
            explicacao = "A Receita não cobra multa por mensagem. A situação do CPF é " +
                    "consultada de graça no site oficial gov.br."
        ),
        PerguntaQuiz(
            id = 20,
            cenario = "Alguém se apresenta como \"Suporte do WhatsApp\" e pede para você " +
                    "confirmar seus dados para não perder a conta.",
            alternativas = listOf(
                "Não respondo e bloqueio o contato.",
                "Confirmo os dados para não perder o WhatsApp."
            ),
            indiceCorreto = 0,
            explicacao = "O WhatsApp não manda mensagem pedindo dados ou códigos. " +
                    "É golpe para roubar a sua conta."
        ),
        PerguntaQuiz(
            id = 21,
            cenario = "Uma pessoa diz ser do banco e pede para você ligar para um número " +
                    "que ela mesma passa, para \"falar com o gerente\".",
            alternativas = listOf(
                "Ligo para o número que a pessoa me passou.",
                "Ligo para o número oficial que está atrás do meu cartão."
            ),
            indiceCorreto = 1,
            explicacao = "O número passado pelo golpista leva a outro golpista. Use sempre o " +
                    "número do cartão ou do site oficial do banco."
        ),
        PerguntaQuiz(
            id = 22,
            cenario = "Seu neto pede dinheiro por mensagem e diz: \"Não conta pra ninguém, " +
                    "tá? É segredo nosso.\"",
            alternativas = listOf(
                "Converso com alguém de confiança antes de mandar.",
                "Guardo o segredo e mando o dinheiro."
            ),
            indiceCorreto = 0,
            explicacao = "Pedir segredo é um sinal clássico de golpe, para ninguém te alertar. " +
                    "Contar para alguém sempre protege você."
        ),
        PerguntaQuiz(
            id = 23,
            cenario = "Uma pessoa sem crachá toca a campainha dizendo ser da companhia " +
                    "de água e pede para entrar.",
            alternativas = listOf(
                "Deixo entrar, deve ser uma vistoria.",
                "Não deixo entrar e ligo para a companhia para confirmar."
            ),
            indiceCorreto = 1,
            explicacao = "Falsos funcionários usam a visita para roubar. Confirme pelo telefone " +
                    "da empresa antes de abrir a porta."
        ),
        PerguntaQuiz(
            id = 24,
            cenario = "Chega um link de \"comprovante.pdf\" de um número que você " +
                    "não conhece.",
            alternativas = listOf(
                "Não abro e apago a mensagem.",
                "Abro para ver de quem é."
            ),
            indiceCorreto = 0,
            explicacao = "Arquivos e links de desconhecidos podem instalar programas que " +
                    "roubam senhas. Na dúvida, não abra."
        ),
        PerguntaQuiz(
            id = 25,
            cenario = "Chega um áudio com a voz parecida com a do seu gerente pedindo uma " +
                    "transferência urgente.",
            alternativas = listOf(
                "Transfiro, a voz é igual à dele.",
                "Confirmo ligando para a agência pelo número oficial."
            ),
            indiceCorreto = 1,
            explicacao = "Hoje é possível imitar vozes com computador. Pedido de dinheiro " +
                    "urgente sempre precisa de confirmação por outro caminho."
        ),
        PerguntaQuiz(
            id = 26,
            cenario = "Uma corrente pede doações para vítimas de enchente e passa uma chave " +
                    "Pix no nome de uma pessoa.",
            alternativas = listOf(
                "Doo por meio de instituições conhecidas.",
                "Doo pela chave Pix da corrente."
            ),
            indiceCorreto = 0,
            explicacao = "Golpistas aproveitam tragédias. Prefira instituições conhecidas e " +
                    "confira o nome de quem recebe antes de confirmar o Pix."
        ),
        PerguntaQuiz(
            id = 27,
            cenario = "\"Você tem uma dívida em atraso. Pague hoje ou seu nome será " +
                    "negativado!\" Você não reconhece essa dívida.",
            alternativas = listOf(
                "Pago para evitar problemas.",
                "Consulto o meu CPF nos sites oficiais e falo com a família."
            ),
            indiceCorreto = 1,
            explicacao = "Cobrança de dívida que você não conhece, com ameaça e pressa, " +
                    "é golpe comum. Confirme antes de pagar qualquer coisa."
        ),
        PerguntaQuiz(
            id = 28,
            cenario = "Você recebe uma mensagem pedindo uma senha ou código, e fica " +
                    "na dúvida se é golpe.",
            alternativas = listOf(
                "Paro, não respondo e peço ajuda a alguém de confiança.",
                "Respondo rápido para não perder o prazo."
            ),
            indiceCorreto = 0,
            explicacao = "Na dúvida, pare. Golpista tem pressa, você não. Use o botão " +
                    "\"Preciso de ajuda\" ou ligue para um contato de confiança."
        )
    )
}
