package com.example.protege_idoso.contatos

data class ContatoConfianca(
    val id: Long = 0,
    val nome: String,
    val telefone: String,
    val parentesco: String = "",
    val criadoEm: Long = System.currentTimeMillis(),
    val atualizadoEm: Long = criadoEm
) {
    val telefoneFormatado: String get() = Telefone.formatar(telefone)

    val iniciais: String
        get() {
            val partes = nome.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            return when {
                partes.isEmpty() -> "?"
                partes.size == 1 -> partes[0].take(1).uppercase()
                else -> (partes.first().take(1) + partes.last().take(1)).uppercase()
            }
        }
}

data class ErrosDoContato(
    val nome: String? = null,
    val telefone: String? = null,
    val parentesco: String? = null
) {
    val temErro: Boolean get() = nome != null || telefone != null || parentesco != null
}

object ValidacaoContato {
    const val TAMANHO_MAXIMO_NOME = 50
    const val TAMANHO_MAXIMO_PARENTESCO = 30
    const val LIMITE_DE_CONTATOS = 10

    val sugestoesDeParentesco = listOf(
        "Filho(a)", "Neto(a)", "Irmão(ã)", "Cônjuge", "Amigo(a)", "Vizinho(a)", "Cuidador(a)"
    )

    fun validar(nome: String, telefone: String, parentesco: String): ErrosDoContato {
        val nomeLimpo = nome.trim()
        val erroNome = when {
            nomeLimpo.isEmpty() -> "Escreva o nome da pessoa."
            nomeLimpo.length < 2 -> "O nome precisa ter pelo menos 2 letras."
            nomeLimpo.length > TAMANHO_MAXIMO_NOME -> "Use no máximo $TAMANHO_MAXIMO_NOME letras."
            nomeLimpo.none { it.isLetter() } -> "O nome precisa ter letras."
            else -> null
        }

        val erroTelefone = when {
            Telefone.somenteDigitos(telefone).isEmpty() -> "Escreva o telefone com DDD."
            Telefone.normalizar(telefone) == null ->
                "Telefone inválido. Use o DDD e o número, por exemplo: (69) 99999-8888."
            else -> null
        }

        val erroParentesco =
            if (parentesco.trim().length > TAMANHO_MAXIMO_PARENTESCO) {
                "Use no máximo $TAMANHO_MAXIMO_PARENTESCO letras."
            } else {
                null
            }

        return ErrosDoContato(erroNome, erroTelefone, erroParentesco)
    }

    fun limparNome(nome: String): String = nome.trim().replace(Regex("\\s+"), " ")
}
