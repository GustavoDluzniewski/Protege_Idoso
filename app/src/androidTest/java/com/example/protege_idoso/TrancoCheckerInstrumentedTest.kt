package com.example.protege_idoso

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class TrancoCheckerInstrumentedTest {
    @Test
    fun deveRetornarFalseParaDominioForaDoRanking() {
        val context =
            InstrumentationRegistry.getInstrumentation().targetContext

        TrancoChecker.carregarLista(context)

        assertEquals(
            false,
            TrancoChecker.consultarUrl("https://dominio-inexistente.invalid/pagina")
        )
    }


    @Test
    fun deveConsultarDominioGoogleSemWww() {
        val context =
            InstrumentationRegistry.getInstrumentation().targetContext

        TrancoChecker.carregarLista(context)

        assertEquals(
            true,
            TrancoChecker.consultarUrl("https://google.com/pagina")
        )
    }


    @Test
    fun deveReconhecerUrlSemWwwNaMensagem() {
        val mensagem = "Confira este link: https://google.com/pagina"

        val urlEncontrada = Regex(
            """https?://[^\s]+|www\.[^\s]+""",
            RegexOption.IGNORE_CASE
        ).find(mensagem)?.value

        assertEquals("https://google.com/pagina", urlEncontrada)
    }


    @Test
    fun deveExtrairDominioSemWww() {
        val dominio = TrancoChecker.extrairDominio("https://google.com/pagina")
        assertEquals("google.com", dominio)
    }


    @Test
    fun deveConsultarUrlNoRankingTranco() {
        val context =
            InstrumentationRegistry.getInstrumentation().targetContext

        TrancoChecker.carregarLista(context)

        assertEquals(
            true,
            TrancoChecker.consultarUrl(
                "https://www.google.com/pagina"
            )
        )
    }

    @Test
    fun deveCarregarListaDoTranco() {
        val context =
            InstrumentationRegistry.getInstrumentation().targetContext

        TrancoChecker.carregarLista(context)

        assertTrue(
            "google.com deveria estar na lista do Tranco",
            TrancoChecker.dominioEstaNoRanking("google.com")
        )
    }


    @Test
    fun deveNormalizarUrlAntesDeConsultarRanking() {
        TrancoChecker.carregarLista(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        assertTrue(
            TrancoChecker.dominioEstaNoRanking(
                "https://www.google.com/pagina"
            )
        )
    }

    @Test
    fun deveExtrairDominioDeUrlCompleta() {
        val dominio = TrancoChecker.extrairDominio(
            "https://www.google.com/pagina?origem=teste"
        )

        assertEquals("google.com", dominio)
    }

    @Test
    fun deveReconhecerSubdominioDeSiteDoRanking() {
        TrancoChecker.carregarLista(
            InstrumentationRegistry.getInstrumentation().targetContext
        )

        assertEquals(
            true,
            TrancoChecker.consultarUrl("https://login.nubank.com.br/entrar")
        )
    }

    @Test
    fun siteDeHospedagemAbertaNaoContaComoConhecido() {
        TrancoChecker.carregarLista(
            InstrumentationRegistry.getInstrumentation().targetContext
        )

        assertEquals(
            false,
            TrancoChecker.consultarUrl("https://premio-caixa.blogspot.com/resgate")
        )
    }
}
