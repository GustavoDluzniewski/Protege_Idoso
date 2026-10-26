package com.example.protege_idoso


object DadosLegais {

    const val VERSAO = 2

    const val DATA_ATUALIZACAO = "24 de outubro de 2026"


    const val ROTULO_REVOGAR = "Revogar meu consentimento"

    const val NOME_CONTROLADOR =
        "ProtegeIdoso"

    const val EMAIL_CONTATO =
        "protegeidosoapp@gmail.com"

    const val RETENCAO_NO_SERVIDOR =
        "o aplicativo não grava o conteúdo das mensagens no servidor."
}

data class SecaoLegal(
    val titulo: String,
    val paragrafos: List<String>
)


object TextosLegais {

    val TERMOS_DE_USO: List<SecaoLegal> = listOf(

        SecaoLegal(
            "1. O que são estes Termos",
            listOf(
                "Estes Termos de Uso explicam as regras para usar o aplicativo ProtegeIdoso.",
                "Ao marcar a caixa de aceite e tocar em “Aceitar e continuar”, você declara que leu e concorda com eles. Se não concordar, não use o aplicativo."
            )
        ),

        SecaoLegal(
            "2. O que o ProtegeIdoso faz",
            listOf(
                "O ProtegeIdoso ajuda a identificar mensagens do WhatsApp que possam ser golpes. Ele lê o aviso (notificação) de cada mensagem recebida, procura sinais de risco e, quando necessário, mostra um aviso no seu celular.",
                "O aplicativo não responde, não apaga, não bloqueia e não envia mensagens por você, e não altera nada no WhatsApp.",
                "Ele também guarda, só no seu celular, um histórico dos avisos mostrados e uma lista de contatos de confiança que você mesmo cadastra, para ligar ou mandar mensagem quando tiver dúvida."
            )
        ),

        SecaoLegal(
            "3. Quem pode usar",
            listOf(
                "• O aplicativo é destinado a pessoas maiores de 18 anos.",
                "• Se um familiar ou cuidador instalar o aplicativo no celular de outra pessoa, isso só pode ser feito com o conhecimento e a concordância dela. O aceite destes documentos deve ser dado pela própria pessoa dona do celular ou por quem a represente legalmente."
            )
        ),

        SecaoLegal(
            "4. Permissões necessárias",
            listOf(
                "Para funcionar, o aplicativo precisa de:",
                "• Acesso às notificações: para ler os avisos de mensagens do WhatsApp. Notificações de outros aplicativos são ignoradas.",
                "• Permissão para mostrar avisos: para alertar você sobre mensagens suspeitas ou possíveis golpes.",
                "• Internet: para enviar para análise o texto das mensagens que tiverem sinais de risco.",
                "Você pode retirar qualquer permissão a qualquer momento nas configurações do celular. Sem elas, o aplicativo deixa de funcionar, total ou parcialmente."
            )
        ),

        SecaoLegal(
            "5. Limites do serviço",
            listOf(
                "Nenhum sistema acerta sempre. O ProtegeIdoso pode classificar como segura uma mensagem que é golpe e pode avisar sobre uma mensagem que é legítima.",
                "Os avisos são uma ajuda e não substituem o seu cuidado. A decisão de responder, clicar em links, informar dados ou fazer pagamentos continua sendo sua. Na dúvida, não responda e peça ajuda a uma pessoa de confiança.",
                "O aplicativo depende da internet e de serviços de terceiros, como o WhatsApp e o servidor de análise. Se eles ficarem indisponíveis, a análise pode ser feita apenas no celular, com menos precisão, ou deixar de funcionar.",
                "Mudanças no WhatsApp ou no Android podem fazer o aplicativo deixar de ler as notificações."
            )
        ),

        SecaoLegal(
            "6. Responsabilidades",
            listOf(
                "Dentro dos limites permitidos pela lei, o responsável pelo aplicativo não responde por prejuízos decorrentes de golpes que não tenham sido identificados, de avisos incorretos ou da indisponibilidade do serviço. Isso não afasta os direitos que a lei garante a você, inclusive os do Código de Defesa do Consumidor, quando aplicável.",
                "Você se compromete a usar o aplicativo apenas para proteger a si mesmo, ou a quem tenha autorizado, e nunca para vigiar ou ler mensagens de outras pessoas sem autorização."
            )
        ),

        SecaoLegal(
            "7. Botão “Preciso de ajuda”",
            listOf(
                "Esse botão abre o WhatsApp com uma mensagem já escrita, para você pedir orientação ou denunciar um golpe. A conversa acontece diretamente entre você e o contato indicado, pelo WhatsApp, e segue as regras desse serviço. Você pode alterar ou apagar o texto antes de enviar."
            )
        ),

        SecaoLegal(
            "8. Privacidade e proteção de dados",
            listOf(
                "O tratamento dos seus dados pessoais está descrito na Política de Privacidade, que faz parte destes Termos."
            )
        ),

        SecaoLegal(
            "9. Mudanças nos Termos",
            listOf(
                "Os Termos podem ser atualizados. Se a mudança for relevante, o aplicativo pedirá um novo aceite antes de continuar a funcionar. A versão e a data de atualização aparecem no início deste documento."
            )
        ),

        SecaoLegal(
            "10. Parar de usar",
            listOf(
                "Você pode parar de usar o aplicativo quando quiser: revogue o consentimento na tela “Privacidade e segurança”, retire as permissões nas configurações do celular ou desinstale o aplicativo.",
                "O uso pode ser suspenso em caso de uso indevido ou se o serviço for encerrado, com aviso sempre que possível."
            )
        ),

        SecaoLegal(
            "11. Lei aplicável e contato",
            listOf(
                "Estes Termos seguem as leis do Brasil. Fica eleito o foro do domicílio do usuário para resolver eventuais conflitos.",
                "Responsável: " + DadosLegais.NOME_CONTROLADOR,
                "Contato: " + DadosLegais.EMAIL_CONTATO
            )
        )
    )

    val POLITICA_DE_PRIVACIDADE: List<SecaoLegal> = listOf(

        SecaoLegal(
            "1. Quem é o responsável pelos seus dados",
            listOf(
                "A Lei Geral de Proteção de Dados (LGPD, Lei nº 13.709/2018) chama de controlador quem decide como os dados pessoais são usados. No ProtegeIdoso, o controlador é:",
                DadosLegais.NOME_CONTROLADOR,
                "Contato para assuntos de privacidade, inclusive para falar com o encarregado pelo tratamento de dados:",
                DadosLegais.EMAIL_CONTATO
            )
        ),

        SecaoLegal(
            "2. Quais dados tratamos",
            listOf(
                "• Conteúdo das notificações de mensagens do WhatsApp: o texto que aparece no aviso do celular. O nome de quem enviou é usado apenas para não analisar a mesma mensagem duas vezes. Ele não é enviado e não é gravado.",
                "• Texto da mensagem que tiver sinais de risco, depois de esconder e-mails, telefones, CPF, links e chaves Pix (trocados por marcações como [CPF] ou [LINK]).",
                "• Endereços dos links da mensagem: enviados à parte, sem o resto do texto, apenas para verificar se o site é conhecido ou perigoso. Um link pode conter dados de quem o criou (por exemplo, um código de rastreio); por isso ele é usado só para essa verificação.",
                "• Suas escolhas no aplicativo: se a análise e cada tipo de aviso estão ligados.",
                "• Registro do seu aceite: qual versão destes documentos você aceitou e quando. Fica somente no seu celular.",
                "• Histórico de alertas: para cada aviso mostrado, o nível de risco, a explicação, a orientação, a data e a hora. Não inclui o texto da mensagem, o nome nem o número de quem enviou. Fica somente no seu celular.",
                "• Contatos de confiança: nome, telefone e, se você quiser, o parentesco das pessoas que você cadastrar. Esses dados são digitados por você, ficam somente no seu celular e nunca são enviados ao servidor. Cadastre apenas pessoas que concordem em ajudar você.",
                "• Dados de conexão: ao enviar o texto para análise, o servidor recebe o endereço de rede (IP) do seu aparelho, como acontece em qualquer conexão com a internet.",
                "Não coletamos seu nome, telefone, e-mail, localização, fotos, arquivos, áudio, câmera nem identificadores de publicidade, e não lemos a agenda de contatos do celular.",
                "As mensagens podem conter, por acaso, dados pessoais sensíveis (como informações de saúde) ou dados de quem escreveu para você. O aplicativo não procura esses dados, não os usa para outra finalidade e trata o texto somente para classificar o risco de golpe. Ao aceitar esta Política, você autoriza esse tratamento."
            )
        ),

        SecaoLegal(
            "3. Para que usamos os dados e em que base legal",
            listOf(
                "Usamos os dados para uma única finalidade: identificar sinais de golpe nas mensagens que você recebe e avisar você. Não usamos para publicidade, venda, perfil de consumo ou qualquer outra finalidade.",
                "A base legal é o seu consentimento (art. 7º, inciso I, da LGPD), dado quando você marca as duas caixas e toca em “Aceitar e continuar”. Você pode revogá-lo quando quiser."
            )
        ),

        SecaoLegal(
            "4. Como o texto é tratado, passo a passo",
            listOf(
                "• 1) O aplicativo lê o aviso da mensagem no seu celular.",
                "• 2) Uma primeira análise é feita no próprio aparelho. Se não houver nenhum sinal de risco, a mensagem não sai do celular.",
                "• 3) Se houver sinais de risco, o aplicativo esconde e-mails, telefones, CPF, links e chaves Pix no texto. Os endereços dos links são separados do texto e enviados à parte, só para verificar se o site é perigoso.",
                "• 4) O texto protegido é enviado, por conexão segura (HTTPS), ao servidor de análise, que usa inteligência artificial para classificar a mensagem como segura, suspeita ou possível golpe e devolve o resultado.",
                "• 5) Se o servidor não responder, a análise é feita só no celular.",
                "• 6) O aplicativo mostra o aviso, conforme as opções que você escolheu.",
                "O aplicativo esconde apenas os tipos de dado listados acima. Outras informações escritas na mensagem, como nomes e endereços, podem seguir no texto enviado."
            )
        ),

        SecaoLegal(
            "5. Com quem compartilhamos",
            listOf(
                "• Servidor de análise: mantido pelo responsável pelo aplicativo. Recebe o texto protegido apenas para devolver a classificação ao aplicativo.",
                "• Provedor de inteligência artificial: o servidor pode enviar o texto protegido a um serviço de IA de terceiros, que o trata apenas para classificar a mensagem (como operador, nos termos da LGPD).",
                "Não vendemos nem cedemos seus dados para anunciantes ou outras empresas. Os dados só serão informados a autoridades se a lei exigir."
            )
        ),

        SecaoLegal(
            "6. Transferência para fora do Brasil",
            listOf(
                "Serviços de inteligência artificial costumam funcionar em outros países. Por isso, o texto protegido pode ser tratado fora do Brasil. O responsável se compromete a escolher provedores que ofereçam grau de proteção compatível com a LGPD (art. 33). Ao aceitar esta Política, você concorda com essa possibilidade."
            )
        ),

        SecaoLegal(
            "7. Por quanto tempo guardamos",
            listOf(
                "• No celular: o aplicativo não grava o conteúdo das mensagens. Ele guarda apenas as suas opções, o registro do aceite, o histórico de alertas (só o nível de risco, a explicação, a orientação e a data e hora do aviso — sem o texto da mensagem, o nome ou o número de quem enviou) e os contatos de confiança que você cadastrar, até você revogar o consentimento, limpar os dados do aplicativo ou desinstalá-lo. Você também pode apagar alertas a qualquer momento na tela “Histórico de alertas” e remover contatos na tela “Contatos de confiança”.",
                "• No servidor de análise: " + DadosLegais.RETENCAO_NO_SERVIDOR
            )
        ),

        SecaoLegal(
            "8. Seus direitos (art. 18 da LGPD)",
            listOf(
                "Você pode pedir, a qualquer momento e sem custo:",
                "• confirmação de que tratamos dados seus;",
                "• acesso aos dados;",
                "• correção de dados incompletos, inexatos ou desatualizados;",
                "• anonimização, bloqueio ou eliminação de dados desnecessários ou tratados em desacordo com a lei;",
                "• portabilidade dos dados;",
                "• eliminação dos dados tratados com o seu consentimento;",
                "• informação sobre com quem compartilhamos os dados;",
                "• informação sobre a possibilidade de não dar consentimento e sobre as consequências da recusa;",
                "• revogação do consentimento.",
                "Para exercer seus direitos, escreva para " + DadosLegais.EMAIL_CONTATO + ". Como o aplicativo não guarda seu nome, telefone ou e-mail, pode não ser possível localizar no servidor dados de uma pessoa específica. Nesse caso, explicaremos o motivo.",
                "Você também pode fazer reclamação à Autoridade Nacional de Proteção de Dados (ANPD), em gov.br/anpd."
            )
        ),

        SecaoLegal(
            "9. Como protegemos seus dados",
            listOf(
                "• A primeira análise é feita no celular: mensagens sem sinal de risco não são enviadas.",
                "• E-mails, telefones, CPF, links e chaves Pix são escondidos no texto antes do envio. Os endereços dos links seguem à parte, só para verificar o site.",
                "• O envio usa conexão segura (HTTPS) e o aplicativo bloqueia conexões sem criptografia.",
                "• O conteúdo das mensagens não é gravado no celular.",
                "• O histórico de alertas e os contatos de confiança ficam só no celular e não entram no backup na nuvem.",
                "• O aplicativo pede só as permissões de que precisa e ignora na hora as notificações de outros aplicativos.",
                "Nenhum sistema é totalmente seguro. Se ocorrer um incidente que possa causar risco ou dano relevante a você, comunicaremos você e a ANPD, conforme a LGPD (art. 48)."
            )
        ),

        SecaoLegal(
            "10. Consentimento, recusa e revogação",
            listOf(
                "O consentimento é livre: você só aceita se quiser. Se não aceitar, o aplicativo não analisa mensagens e não pode ser usado, porque essa análise é a sua única função.",
                "Para revogar, abra “Privacidade e segurança” e toque em “" + DadosLegais.ROTULO_REVOGAR + "”. É tão fácil quanto aceitar. A revogação não afeta o que já foi feito antes dela. Depois de revogar, o aplicativo para de analisar mensagens."
            )
        ),

        SecaoLegal(
            "11. Crianças e adolescentes",
            listOf(
                "O aplicativo não é destinado a menores de 18 anos e não coleta dados de crianças ou adolescentes de propósito. Se um menor de idade for usar o aplicativo, o aceite deve ser dado por pai, mãe ou responsável legal (art. 14 da LGPD)."
            )
        ),

        SecaoLegal(
            "12. Mudanças nesta Política",
            listOf(
                "Esta Política pode ser atualizada. Mudanças relevantes pedirão um novo aceite. A versão e a data de atualização aparecem no início do documento."
            )
        ),

        SecaoLegal(
            "13. Contato",
            listOf(
                "Dúvidas sobre privacidade: " + DadosLegais.EMAIL_CONTATO
            )
        )
    )
}
