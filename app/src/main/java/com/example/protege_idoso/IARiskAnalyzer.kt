package com.example.protege_idoso

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object IARiskAnalyzer {
    private const val TAG = "ProtegeIdoso"

    private const val TAMANHO_MAXIMO_TEXTO = 300

    private val URL_BASE = BuildConfig.URL_SERVIDOR

    private val URL_REGISTRAR =
        "$URL_BASE/instalacoes/registrar"

    private val URL_ANALISAR =
        "$URL_BASE/analisar"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(70, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    private data class RespostaRegistro(
        val token: String?
    )

    private data class RespostaServidor(
        val classificacao: String?,
        val explicacao: String?,
        val orientacao: String?
    )

    suspend fun analisarMensagem(
        context: Context,
        mensagem: String,
        urls: List<String> = emptyList(),
        dominioForaDoRanking: Boolean = false
    ): RiskResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Iniciando análise pelo backend")

                val resposta = enviarParaBackend(
                    context.applicationContext,
                    mensagem,
                    urls
                )

                converterRespostaParaRiskResult(resposta)

            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Falha na análise remota (${e.javaClass.simpleName}). " +
                            "Usando análise local."
                )

                LocalRiskAnalyzer.analisarMensagem(
                    mensagem = mensagem,
                    dominioForaDoRanking = dominioForaDoRanking
                )
            }
        }
    }

    private fun obterOuRegistrarToken(context: Context): String {
        val tokenSalvo = TokenStorage.obterToken(context)

        if (!tokenSalvo.isNullOrBlank()) {
            return tokenSalvo
        }

        val requisicao = Request.Builder()
            .url(URL_REGISTRAR)
            .post(ByteArray(0).toRequestBody(null))
            .build()

        client.newCall(requisicao).execute().use { resposta ->
            if (!resposta.isSuccessful) {
                throw IOException(
                    "Falha ao registrar instalação: HTTP ${resposta.code}"
                )
            }

            val corpo = resposta.body?.string()
                ?: throw IOException("Resposta de registro vazia")

            val registro = gson.fromJson(
                corpo,
                RespostaRegistro::class.java
            )

            val token = registro.token
                ?.takeIf { it.isNotBlank() }
                ?: throw IOException("Token não recebido do servidor")

            TokenStorage.salvarToken(context, token)

            Log.d(TAG, "Instalação registrada com sucesso.")
            return token
        }
    }

    private fun enviarParaBackend(
        context: Context,
        mensagem: String,
        urls: List<String>
    ): String {
        var token = obterOuRegistrarToken(context)

        for (tentativa in 0..1) {
            val jsonBody = gson.toJson(
                mapOf(
                    "mensagem" to mensagem,
                    "urls" to urls
                )
            )

            val body = jsonBody.toRequestBody(
                "application/json; charset=utf-8".toMediaType()
            )

            val requisicao = Request.Builder()
                .url(URL_ANALISAR)
                .header("Authorization", "Bearer $token")
                .post(body)
                .build()

            client.newCall(requisicao).execute().use { resposta ->
                Log.d(
                    TAG,
                    "Resposta HTTP do backend: ${resposta.code}"
                )

                if (resposta.code == 401 && tentativa == 0) {
                    TokenStorage.apagarToken(context)
                    token = obterOuRegistrarToken(context)
                    return@use
                }

                val respostaCompleta = resposta.body?.string()
                    ?: throw IOException("Resposta vazia do backend")

                if (!resposta.isSuccessful) {
                    throw IOException("Erro HTTP ${resposta.code}")
                }

                return respostaCompleta
            }
        }

        throw IOException("Não foi possível autenticar a instalação.")
    }

    private fun converterRespostaParaRiskResult(
        respostaTexto: String
    ): RiskResult {
        val resposta = gson.fromJson(
            respostaTexto.trim(),
            RespostaServidor::class.java
        ) ?: throw IOException("Resposta vazia do backend")

        val classificacao = resposta.classificacao
            ?: throw IOException("Classificação não encontrada")

        val explicacao = resposta.explicacao
            ?: throw IOException("Explicação não encontrada")

        val orientacao = resposta.orientacao
            ?: throw IOException("Orientação não encontrada")

        val nivel = when (classificacao.uppercase()) {
            "SEGURO" -> RiskLevel.SEGURO
            "SUSPEITO" -> RiskLevel.SUSPEITO
            "POSSIVEL_GOLPE", "POSSÍVEL_GOLPE" ->
                RiskLevel.POSSIVEL_GOLPE

            else -> throw IOException("Classificação inválida")
        }
        return RiskResult(
            nivel = nivel,
            explicacao = limparTextoDaIA(explicacao),
            orientacao = limparTextoDaIA(orientacao)
        )
    }

    private fun limparTextoDaIA(texto: String): String {
        val limpo = SanitizationService.sanitizeMessage(texto.trim())

        return if (limpo.length <= TAMANHO_MAXIMO_TEXTO) {
            limpo
        } else {
            limpo.take(TAMANHO_MAXIMO_TEXTO).trimEnd() + "…"
        }
    }
}
