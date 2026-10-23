package com.example.protege_idoso.contatos

object Telefone {
    private val dddsValidos = setOf(
        11, 12, 13, 14, 15, 16, 17, 18, 19,
        21, 22, 24, 27, 28,
        31, 32, 33, 34, 35, 37, 38,
        41, 42, 43, 44, 45, 46, 47, 48, 49,
        51, 53, 54, 55,
        61, 62, 63, 64, 65, 66, 67, 68, 69,
        71, 73, 74, 75, 77, 79,
        81, 82, 83, 84, 85, 86, 87, 88, 89,
        91, 92, 93, 94, 95, 96, 97, 98, 99
    )

    fun somenteDigitos(texto: String): String = texto.filter { it.isDigit() }

    fun normalizar(texto: String): String? {
        var digitos = somenteDigitos(texto)

        if (digitos.length in 12..13 && digitos.startsWith("55")) {
            digitos = digitos.substring(2)
        } else if (digitos.length in 11..12 && digitos.startsWith("0")) {
            digitos = digitos.substring(1)
        }

        return if (ehValido(digitos)) digitos else null
    }

    fun ehValido(digitos: String): Boolean {
        if (digitos.length != 10 && digitos.length != 11) return false
        if (!digitos.all { it.isDigit() }) return false

        val ddd = digitos.substring(0, 2).toInt()
        if (ddd !in dddsValidos) return false

        val numero = digitos.substring(2)
        if (numero.toSet().size == 1) return false

        return if (numero.length == 9) {
            numero[0] == '9'
        } else {
            numero[0] in '2'..'5'
        }
    }

    fun formatar(digitos: String): String {
        if (digitos.length != 10 && digitos.length != 11) return digitos
        val ddd = digitos.substring(0, 2)
        val numero = digitos.substring(2)
        val corte = numero.length - 4
        return "($ddd) ${numero.substring(0, corte)}-${numero.substring(corte)}"
    }

    fun paraLeitorDeTela(digitos: String): String = digitos.toCharArray().joinToString(" ")

    fun uriDeLigacao(digitos: String): String = "tel:+55$digitos"

    fun paraWhatsApp(digitos: String): String = "55$digitos"

    fun ehCelular(digitos: String): Boolean = digitos.length == 11
}
