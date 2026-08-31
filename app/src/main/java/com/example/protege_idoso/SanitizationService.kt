package com.example.protege_idoso

object SanitizationService {

    fun sanitizeMessage(mensagem: String): String {

        var texto = mensagem

        texto = sanitizarEmails(texto)
        texto = sanitizarTelefones(texto)
        texto = sanitizarCpfs(texto)
        texto = sanitizarLinks(texto)
        texto = sanitizarChavesPix(texto)

        return texto

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

    private fun sanitizarCpfs(texto: String): String {
        val regexComMascara = Regex("(?<!\\d)\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}(?!\\d)")
        val regexSemMascara = Regex("(?<!\\d)\\d{11}(?!\\d)")

        var resultado = texto.replace(regexComMascara, "[CPF]")
        resultado = resultado.replace(regexSemMascara, "[CPF]")

        return resultado
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
           "(?i)(pix\\s*[:\\-]?\\s*)([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}|\\+?\\d{10,13}|[0-9]{11}|[0-9]{14})"

       )

       var resultado = texto.replace(regexEvp, "[CHAVE_PIX")
       resultado = resultado.replace(regexPixComPrefixo, "$1[CHAVE_PIX]")

       return resultado
    }
}