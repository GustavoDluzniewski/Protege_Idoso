package com.example.protege_idoso.instituicoes

import com.example.protege_idoso.contatos.Telefone

enum class TipoCanal(val rotulo: String) {
    TELEFONE("Telefone"),
    SITE("Site");

    companion object {
        fun deTexto(valor: String?): TipoCanal =
            entries.firstOrNull { it.name == valor } ?: TELEFONE
    }
}

data class CanalOficial(
    val id: Long = 0,
    val instituicaoId: Long,
    val rotulo: String,
    val tipo: TipoCanal,
    val valor: String,
    val oficial: Boolean,
    val ordem: Int = 0
) {
    val valorFormatado: String
        get() = if (tipo == TipoCanal.TELEFONE) NumeroOficial.formatar(valor) else valor
}

data class Instituicao(
    val id: Long = 0,
    val codigo: String?,
    val nome: String,
    val canais: List<CanalOficial> = emptyList()
) {
    val doCatalogo: Boolean get() = codigo != null

    val telefones: List<CanalOficial> get() = canais.filter { it.tipo == TipoCanal.TELEFONE }

    val sites: List<CanalOficial> get() = canais.filter { it.tipo == TipoCanal.SITE }

    val iniciais: String
        get() {
            val partes = nome.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            return when {
                partes.isEmpty() -> "?"
                partes.size == 1 -> partes[0].take(2).uppercase()
                else -> (partes.first().take(1) + partes.last().take(1)).uppercase()
            }
        }
}

object NumeroOficial {

    private val prefixosGratuitos = listOf("0800", "0300", "0500")

    fun somenteDigitos(texto: String): String = texto.filter { it.isDigit() }

    fun normalizar(texto: String): String? {
        val digitos = somenteDigitos(texto)
        return when {
            digitos.length in 3..5 && !digitos.startsWith("0") -> digitos
            digitos.length == 8 && (digitos[0] == '3' || digitos[0] == '4') -> digitos
            digitos.length == 11 && prefixosGratuitos.any { digitos.startsWith(it) } -> digitos
            digitos.startsWith("0900") -> null
            else -> Telefone.normalizar(texto)
        }
    }

    fun ehTarifado(texto: String): Boolean = somenteDigitos(texto).startsWith("0900")

    fun formatar(digitos: String): String = when {
        digitos.length == 11 && prefixosGratuitos.any { digitos.startsWith(it) } ->
            "${digitos.substring(0, 4)} ${digitos.substring(4, 7)} ${digitos.substring(7)}"
        digitos.length == 8 && (digitos[0] == '3' || digitos[0] == '4') ->
            "${digitos.substring(0, 4)} ${digitos.substring(4)}"
        digitos.length == 10 || digitos.length == 11 -> Telefone.formatar(digitos)
        else -> digitos
    }

    fun uriDeLigacao(digitos: String): String = when {
        digitos.length <= 5 -> "tel:$digitos"
        digitos.length == 8 -> "tel:$digitos"
        prefixosGratuitos.any { digitos.startsWith(it) } -> "tel:$digitos"
        else -> Telefone.uriDeLigacao(digitos)
    }
}

object EnderecoOficial {

    fun normalizar(texto: String): String? {
        var endereco = texto.trim()
        if (endereco.isEmpty() || endereco.any { it.isWhitespace() }) return null
        if (endereco.startsWith("http://", ignoreCase = true)) {
            endereco = "https://" + endereco.substring(7)
        } else if (!endereco.startsWith("https://", ignoreCase = true)) {
            endereco = "https://$endereco"
        }
        val semProtocolo = endereco.substring(8)
        val dominio = semProtocolo.substringBefore('/').substringBefore('?').lowercase()
        if (dominio.length < 4 || !dominio.contains('.')) return null
        if (dominio.startsWith('.') || dominio.endsWith('.') || dominio.contains("..")) return null
        if (!dominio.all { it.isLetterOrDigit() || it == '.' || it == '-' }) return null
        return "https://" + dominio + semProtocolo.substring(dominio.length)
    }
}

data class ErrosDoCanal(
    val rotulo: String? = null,
    val valor: String? = null
) {
    val temErro: Boolean get() = rotulo != null || valor != null
}

object ValidacaoInstituicao {

    const val TAMANHO_MAXIMO_NOME = 40
    const val TAMANHO_MAXIMO_ROTULO = 50
    const val LIMITE_DE_INSTITUICOES = 20
    const val LIMITE_DE_CANAIS = 15

    val sugestoesDeRotulo = listOf(
        "Central de atendimento", "SAC", "Ouvidoria", "Gerente", "Agência", "Cartão de crédito"
    )

    fun validarNome(nome: String): String? {
        val limpo = nome.trim()
        return when {
            limpo.isEmpty() -> "Escreva o nome da instituição."
            limpo.length < 2 -> "O nome precisa ter pelo menos 2 letras."
            limpo.length > TAMANHO_MAXIMO_NOME -> "Use no máximo $TAMANHO_MAXIMO_NOME letras."
            limpo.none { it.isLetter() } -> "O nome precisa ter letras."
            else -> null
        }
    }

    fun validarCanal(rotulo: String, tipo: TipoCanal, valor: String): ErrosDoCanal {
        val rotuloLimpo = rotulo.trim()
        val erroRotulo = when {
            rotuloLimpo.isEmpty() -> "Escreva para que serve este canal."
            rotuloLimpo.length > TAMANHO_MAXIMO_ROTULO -> "Use no máximo $TAMANHO_MAXIMO_ROTULO letras."
            else -> null
        }
        val erroValor = when (tipo) {
            TipoCanal.TELEFONE -> when {
                NumeroOficial.somenteDigitos(valor).isEmpty() -> "Escreva o número de telefone."
                NumeroOficial.ehTarifado(valor) ->
                    "Números 0900 são pagos e não são usados por bancos. Confira o número."
                NumeroOficial.normalizar(valor) == null ->
                    "Número inválido. Exemplos: 4004 0001, 0800 729 0001 ou (69) 3216-8940."
                else -> null
            }
            TipoCanal.SITE -> when {
                valor.isBlank() -> "Escreva o endereço do site."
                EnderecoOficial.normalizar(valor) == null ->
                    "Endereço inválido. Exemplo: www.bb.com.br"
                else -> null
            }
        }
        return ErrosDoCanal(erroRotulo, erroValor)
    }

    fun normalizarValor(tipo: TipoCanal, valor: String): String? = when (tipo) {
        TipoCanal.TELEFONE -> NumeroOficial.normalizar(valor)
        TipoCanal.SITE -> EnderecoOficial.normalizar(valor)
    }

    fun limparTexto(texto: String): String = texto.trim().replace(Regex("\\s+"), " ")
}