package com.example.protege_idoso

object SanitizationService {
    fun sanitizeMessage(mensagem: String): String {
        var texto = mensagem

        texto = sanitizarEmails(texto)
        texto = sanitizarRgs(texto)
        texto = sanitizarCartoes(texto)
        texto = sanitizarCpfs(texto)
        texto = sanitizarCeps(texto)
        texto = sanitizarContasBancarias(texto)
        texto = sanitizarTelefones(texto)
        texto = sanitizarLinks(texto)
        texto = sanitizarChavesPix(texto)

        return texto

    }
    fun sanitizarUrl(url: String): String {
        val inicio = url.indexOfFirst { it == '?' || it == '#' }
        if (inicio < 0) return url

        var parametros = url.substring(inicio)

        parametros = parametros.replace(regexEmailEmUrl, MARCA_URL)
        parametros = parametros.replace(regexCpfComMascara, MARCA_URL)
        parametros = regexCpfSemMascara.replace(parametros) { match ->
            if (cpfValido(match.value)) MARCA_URL else match.value
        }

        return url.substring(0, inicio) + parametros
    }

    private const val MARCA_URL = "REMOVIDO"
    private val regexEmailEmUrl =
        Regex("[A-Za-z0-9._%+-]+(?:@|%40)[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
    private val regexCpfComMascara =
        Regex("(?<!\\d)\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}(?!\\d)")
    private val regexCpfSemMascara =
        Regex("(?<!\\d)\\d{11}(?!\\d)")
    private fun sanitizarRgs(texto: String): String {
        val regex = Regex(
            "(?i)(\\b(?:rg|r\\.g\\.)(?![a-z])\\s*(?:n[º°o.]\\s*)?:?\\s*)([0-9][0-9xX.\\-]{4,13}[0-9xX])"
        )
        return texto.replace(regex) { match -> match.groupValues[1] + "[RG]" }
    }
    private fun sanitizarCartoes(texto: String): String {
        val regexContinuo = Regex("(?<![\\d+])\\d{15,16}(?!\\d)")
        val regexEmGrupos = Regex(
            "(?<![\\d+])(?:\\d{4}([ -])\\d{4}\\1\\d{4}\\1\\d{4}(?:\\1\\d{3})?|\\d{4}([ -])\\d{6}\\2\\d{5})(?!\\d)"
        )

        val trocar: (MatchResult) -> String = { match ->
            if (luhnValido(match.value.filter { it.isDigit() })) "[CARTAO]" else match.value
        }

        return regexContinuo.replace(regexEmGrupos.replace(texto, trocar), trocar)
    }

    private fun luhnValido(digitos: String): Boolean {
        var soma = 0
        digitos.reversed().forEachIndexed { indice, caractere ->
            var valor = caractere.digitToInt()
            if (indice % 2 == 1) {
                valor *= 2
                if (valor > 9) valor -= 9
            }
            soma += valor
        }
        return soma % 10 == 0
    }

    private fun sanitizarCeps(texto: String): String {
        val regexComTraco = Regex("(?<!\\d)\\d{5}-\\d{3}(?!\\d)")
        val regexComPrefixo = Regex("(?i)(\\bcep\\s*:?\\s*)\\d{8}(?!\\d)")

        val resultado = texto.replace(regexComTraco, "[CEP]")
        return resultado.replace(regexComPrefixo) { match -> match.groupValues[1] + "[CEP]" }
    }
    private fun sanitizarContasBancarias(texto: String): String {
        val regexAgencia = Regex(
            "(?i)(\\b(?:ag[eê]ncia|ag\\.?)(?![a-zà-ú])\\s*(?:n[º°o.]\\s*)?:?\\s*)\\d{3,5}(?:-[0-9xX])?(?!\\d)"
        )
        val regexConta = Regex(
            "(?i)(\\b(?:conta(?:\\s+corrente|\\s+poupan[cç]a)?|c/c)\\s*(?:n[º°o.]\\s*)?:?\\s*)\\d{4,13}(?:-[0-9xX])?(?!\\d)"
        )

        val resultado = texto.replace(regexAgencia) { match -> match.groupValues[1] + "[AGENCIA]" }
        return resultado.replace(regexConta) { match -> match.groupValues[1] + "[CONTA]" }
    }
    private fun sanitizarEmails(texto: String): String {
        val regex = Regex(
            "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
            RegexOption.IGNORE_CASE
        )
        return texto.replace(regex, "[EMAIL]")
    }
    private fun sanitizarTelefones(texto: String): String {
        val regex = Regex(
            "(?<!\\d)(?:\\+55\\s?)?(?:\\(?\\d{2}\\)?\\s?)?\\d{4,5}[-\\s]?\\d{4}(?!\\d)"
        )
        return texto.replace(regex, "[TELEFONE]")
    }

    private fun sanitizarCpfs (texto: String) : String {
        val regexComMascara =
            Regex("(?<!\\d)\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}(?!\\d)")

        val regexSemMascara =
            Regex ("(?<!\\d)\\d{11}(?!\\d)")

        var resultado = texto.replace(regexComMascara, "[CPF]")

        resultado = regexSemMascara.replace(resultado) { match ->
            val cpf = match.value

            if (cpfValido(cpf)){
                "[CPF]"
            } else {
                cpf
            }
        }
        return  resultado
    }

    private fun cpfValido (cpf: String): Boolean{
        if(cpf.length !=11) return false
        if(cpf.all {it == cpf[0] }) return false
        var soma = 0
        for (i in 0 until 9){
            soma += cpf[i].digitToInt() * (10 - i)
        }
        var resto = soma % 11
        val primeiroDigito = if (resto < 2) 0 else 11 - resto
        if (cpf[9].digitToInt() != primeiroDigito){
            return false
        }
        soma = 0
        for (i in 0 until 10){
            soma += cpf[i].digitToInt() * (11 - i)
        }
        resto = soma % 11
        val segundoDigito = if (resto < 2) 0 else 11 - resto
        return cpf[10].digitToInt() == segundoDigito
    }

    private fun sanitizarLinks(texto: String): String {
        val regex = Regex(
            "(https?://\\S+|www\\.\\S+|[a-zA-Z0-9.-]+\\.(com|br|net|org|gov|app|xyz)/?\\S*)",
            RegexOption.IGNORE_CASE
        )
        return texto.replace(regex, "[LINK]")
    }
    private fun sanitizarChavesPix(texto: String): String {
        val regexEvp = Regex(
            "(?i)[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"
        )

        val regexPixComPrefixo = Regex(
            "(?i)(\\b(?:minha\\s+)?chave\\s+pix\\s*(?:é|e|:|-)?\\s*|\\bpix\\s*(?:é|e|:|-)?\\s*)(" +
                    "[0-9]{14}|" +
                    "\\+?\\d{10,13}|" +
                    "[0-9]{11}" +
                    ")"
        )

        var resultado = texto.replace(regexEvp, "[CHAVE_PIX]")

        resultado = resultado.replace(regexPixComPrefixo) { match ->
            val prefixo = match.groupValues[1]
            "$prefixo[CHAVE_PIX]"
        }

        return resultado
    }
}