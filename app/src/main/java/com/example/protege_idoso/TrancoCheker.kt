package com.example.protege_idoso

import android.content.Context

object TrancoChecker {
    private val dominios = mutableSetOf<String>()

    private var carregado = false

    fun carregarLista(context: Context) {
        if (carregado) return

        context.assets.open("tranco_top_50k.csv")
            .bufferedReader()
            .useLines { linhas ->
                linhas.forEach { linha ->
                    val partes = linha.split(",", limit = 2)

                    if (partes.size == 2) {
                        val dominio = partes[1].trim().lowercase()

                        if (dominio.isNotBlank()) {
                            dominios.add(dominio)
                        }
                    }
                }
            }

        carregado = true
    }

    // Sites famosos onde qualquer pessoa publica uma página ou encurta um link.
    // Estar no ranking não diz nada sobre quem criou o link, então contam como desconhecidos.
    private val hospedagemAberta = setOf(
        "bit.ly", "tinyurl.com", "goo.gl", "is.gd", "cutt.ly", "t.co",
        "forms.gle", "linktr.ee",
        "sites.google.com", "docs.google.com",
        "blogspot.com", "github.io", "wixsite.com", "weebly.com",
        "web.app", "firebaseapp.com", "vercel.app", "netlify.app",
        "pages.dev", "herokuapp.com", "000webhostapp.com", "glitch.me",
        "trycloudflare.com", "ngrok.io", "ngrok-free.app"
    )

    private fun normalizar(dominio: String): String = dominio
        .trim()
        .lowercase()
        .removePrefix("https://")
        .removePrefix("http://")
        .removePrefix("www.")
        .substringBefore("/")
        .substringBefore(":")
        .substringBefore("?")
        .substringBefore("#")
        .trimEnd('.')

    fun ehHospedagemAberta(dominio: String): Boolean {
        val normalizado = normalizar(dominio)

        return hospedagemAberta.any {
            normalizado == it || normalizado.endsWith(".$it")
        }
    }

    /**
     * Procura o domínio e, se não achar, os domínios "pais":
     * login.nubank.com.br -> nubank.com.br -> com.br.
     */
    fun dominioEstaNoRanking(dominio: String): Boolean {
        var atual = normalizar(dominio)

        if (ehHospedagemAberta(atual)) return false

        while (atual.contains('.')) {
            if (dominios.contains(atual)) return true
            atual = atual.substringAfter('.')
        }

        return false
    }


    fun extrairDominio(url: String): String? {
        val urlCompleta = url.trim()
            .let {
                if (it.startsWith("http://", ignoreCase = true) ||
                    it.startsWith("https://", ignoreCase = true)
                ) {
                    it
                } else {
                    "https://$it"
                }
            }

        return try {
            val uri = android.net.Uri.parse(urlCompleta)
            uri.host
                ?.lowercase()
                ?.removePrefix("www.")
        } catch (e: Exception) {
            null
        }
    }

    fun consultarUrl(url: String): Boolean? {
        val dominio = extrairDominio(url) ?: return null

        return dominioEstaNoRanking(dominio)
    }
}