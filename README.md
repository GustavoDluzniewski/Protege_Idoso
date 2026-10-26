ProtegeIdoso é um aplicativo Android desenvolvido em Kotlin projetado para proteger idosos contra golpes, 
fraudes e engenharia social em mensagens do WhatsApp. 
O sistema opera em tempo real capturando notificações, aplicando técnicas locais de privacidade/segurança 
e utilizando Inteligência Artificial para análise preditiva de risco.

O servidor que faz a análise das mensagens está em:
[Protege_Idoso_backend](https://github.com/GustavoDluzniewski/Protege_Idoso_backend)

## Configuração

O endereço do servidor de análise é obrigatório. Adicione ao arquivo `local.properties` (que não vai para o git):

```
URL_SERVIDOR=https://endereco-do-seu-servidor
```

Sem essa linha, o build para com uma mensagem explicando o que falta.
