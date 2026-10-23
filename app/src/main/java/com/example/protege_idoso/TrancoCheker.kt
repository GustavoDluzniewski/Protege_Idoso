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

    fun dominioEstaNoRanking(dominio: String): Boolean {
        val dominioNormalizado = dominio
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

        return dominios.contains(dominioNormalizado)
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