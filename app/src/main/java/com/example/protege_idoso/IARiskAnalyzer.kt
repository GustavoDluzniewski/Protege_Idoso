package com.example.protege_idoso

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object OpenAIRiskAnalyzer {

    private const val TAG = "ProtegeIdoso"

    // URL pública do backend FastAPI, não do LiteLLM.
    private const val URL_SERVIDOR =
        "https://understand-attached-garmin-spouse.trycloudflare.com/analisar"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(70, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    private data class RespostaServidor(
        val classificacao: String?,
        val explicacao: String?,
        val orientacao: String?
    )

    suspend fun analisarMensagem(mensagem: String): RiskResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Iniciando análise pelo backend")

                val resultado = enviarParaBackend(mensagem)

                converterRespostaParaRiskResult(resultado)

            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Falha na análise remota (${e.javaClass.simpleName}). " +
                            "Usando análise local."
                )

                LocalRiskAnalyzer.analisarMensagem(mensagem)
            }
        }
    }

    private fun enviarParaBackend(mensagem: String): String {
        val jsonBody = gson.toJson(
            mapOf("mensagem" to mensagem)
        )

        val body = jsonBody.toRequestBody(
            "application/json; charset=utf-8".toMediaType()
        )

        val request = Request.Builder()
            .url(URL_SERVIDOR)
            .header("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            Log.d(TAG, "Resposta HTTP do backend: ${response.code}")

            val respostaCompleta = response.body?.string()
                ?: throw Exception("Resposta vazia do backend")

            if (!response.isSuccessful) {
                // Não registrar o corpo da resposta.
                throw Exception("Erro HTTP ${response.code}")
            }

            // O backend já devolve os campos diretamente,
            // sem o formato choices usado pelo LiteLLM.
            val json = JsonParser.parseString(
                respostaCompleta
            ).asJsonObject

            val resposta = gson.fromJson(
                json,
                RespostaServidor::class.java
            )

            val classificacao = resposta.classificacao
                ?: throw Exception("Classificação não encontrada")

            val explicacao = resposta.explicacao
                ?: throw Exception("Explicação não encontrada")

            val orientacao = resposta.orientacao
                ?: throw Exception("Orientação não encontrada")

            return gson.toJson(
                mapOf(
                    "classificacao" to classificacao,
                    "explicacao" to explicacao,
                    "orientacao" to orientacao
                )
            )
        }
    }

    private fun converterRespostaParaRiskResult(
        respostaTexto: String
    ): RiskResult {
        val json = JsonParser.parseString(
            respostaTexto.trim()
        ).asJsonObject

        val classificacao = json.get("classificacao")?.asString
            ?: throw Exception("Classificação não encontrada")

        val explicacao = json.get("explicacao")?.asString
            ?: throw Exception("Explicação não encontrada")

        val orientacao = json.get("orientacao")?.asString
            ?: throw Exception("Orientação não encontrada")

        val nivel = when (classificacao.uppercase()) {
            "SEGURO" -> RiskLevel.SEGURO
            "SUSPEITO" -> RiskLevel.SUSPEITO
            "POSSIVEL_GOLPE", "POSSÍVEL_GOLPE" ->
                RiskLevel.POSSIVEL_GOLPE

            else -> throw Exception("Classificação inválida")
        }

        return RiskResult(
            nivel = nivel,
            explicacao = explicacao,
            orientacao = orientacao
        )
    }
}
