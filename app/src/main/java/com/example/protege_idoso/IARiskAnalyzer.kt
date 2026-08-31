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

object OpenAIRiskAnalyzer {

    private val API_KEY = BuildConfig.OPENAI_API_KEY
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun analisarMensagem(mensagem: String): RiskResult {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = criarPrompt(mensagem)

                Log.d("ProtegeIdoso", "Preparando análise com OpenAI...")

                val respostaTexto = enviarParaOpenAI(prompt)

                Log.d("ProtegeIdoso", "Texto retornado pela OpenAI: $respostaTexto")

                converterRespostaParaRiskResult(respostaTexto)

            } catch (e: Exception) {
                Log.e("ProtegeIdoso", "Erro ao analisar com OpenAI", e)

                Log.d("ProtegeIdoso", "Usando análise local como fallback...")

                LocalRiskAnalyzer.analisarMensagem(mensagem)
            }
        }
    }

    private fun criarPrompt(mensagem: String): String {
        return """
            Classifique esta mensagem de WhatsApp quanto ao risco de golpe contra idosos.

            Responda somente em JSON válido.
            Não use markdown.
            Não escreva nada antes ou depois do JSON.

            Use somente uma destas classificações:
            SEGURO
            SUSPEITO
            POSSIVEL_GOLPE

            Critérios:

            SEGURO:
            Conversa comum, sem link suspeito, sem urgência e sem pedido de dados pessoais.

            SUSPEITO:
            Mensagem com link externo, pedido de dados, urgência moderada, remetente desconhecido ou linguagem estranha.

            POSSIVEL_GOLPE:
            Mensagem com pedido de PIX, senha, código, prêmio, ameaça, imitação de banco, governo, loja ou familiar, link encurtado ou múltiplos sinais de fraude.

            Responda exatamente neste formato:

            {
              "classificacao": "SEGURO",
              "explicacao": "explicação curta e simples para um idoso",
              "orientacao": "orientação simples sobre o que fazer"
            }

            Mensagem recebida:
            "$mensagem"
        """.trimIndent()
    }

    private fun enviarParaOpenAI(prompt: String): String {
        val url = "https://api.openai.com/v1/responses"

        val jsonBody = """
            {
              "model": "gpt-4.1-nano",
              "input": ${gson.toJson(prompt)},
              "temperature": 0.2
            }
        """.trimIndent()

        val mediaType = "application/json".toMediaType()
        val body = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        Log.d("ProtegeIdoso", "REQUISIÇÃO OPENAI INICIADA")

        client.newCall(request).execute().use { response ->

            val respostaCompleta = response.body?.string()
                ?: throw Exception("Resposta vazia da OpenAI")

            Log.d("ProtegeIdoso", "REQUISIÇÃO OPENAI FINALIZADA")
            Log.d("ProtegeIdoso", "Código da resposta OpenAI: ${response.code}")

            if (!response.isSuccessful) {
                Log.e("ProtegeIdoso", "Erro completo da OpenAI: $respostaCompleta")
                throw Exception("Erro na OpenAI: ${response.code} - $respostaCompleta")
            }

            Log.d("ProtegeIdoso", "Resposta completa OpenAI: $respostaCompleta")

            val json = JsonParser.parseString(respostaCompleta).asJsonObject

            val output = json.getAsJsonArray("output")
                ?: throw Exception("Campo 'output' não encontrado na resposta")

            if (output.size() == 0) {
                throw Exception("Campo 'output' veio vazio")
            }

            val primeiroOutput = output[0].asJsonObject

            val content = primeiroOutput.getAsJsonArray("content")
                ?: throw Exception("Campo 'content' não encontrado na resposta")

            if (content.size() == 0) {
                throw Exception("Campo 'content' veio vazio")
            }

            val primeiroContent = content[0].asJsonObject

            val texto = primeiroContent.get("text")?.asString
                ?: throw Exception("Campo 'text' não encontrado na resposta")

            return texto
        }
    }

    private fun converterRespostaParaRiskResult(respostaTexto: String): RiskResult {
        val textoLimpo = respostaTexto
            .replace("```json", "")
            .replace("```", "")
            .trim()

        Log.d("ProtegeIdoso", "JSON limpo para conversão: $textoLimpo")

        val json = JsonParser.parseString(textoLimpo).asJsonObject

        val classificacao = json.get("classificacao")?.asString
            ?: throw Exception("Campo 'classificacao' não encontrado")

        val explicacao = json.get("explicacao")?.asString
            ?: "A mensagem foi analisada, mas a explicação não foi retornada."

        val orientacao = json.get("orientacao")?.asString
            ?: "Verifique com cuidado antes de responder."

        val nivel = when (classificacao.uppercase()) {
            "SEGURO" -> RiskLevel.SEGURO
            "SUSPEITO" -> RiskLevel.SUSPEITO
            "POSSIVEL_GOLPE", "POSSÍVEL_GOLPE" -> RiskLevel.POSSIVEL_GOLPE
            else -> RiskLevel.SUSPEITO
        }

        return RiskResult(
            nivel = nivel,
            explicacao = explicacao,
            orientacao = orientacao
        )
    }
}