package com.example.protege_idoso

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrancoCheckerTest {

    @Test
    fun dominioNaoEncontradoDeveRetornarFalse() {
        assertFalse(
            TrancoChecker.dominioEstaNoRanking("dominio-que-nao-existe-xyz.invalid")
        )
    }

    @Test
    fun deveReconhecerSitesDeHospedagemAberta() {
        assertTrue(TrancoChecker.ehHospedagemAberta("bit.ly"))
        assertTrue(TrancoChecker.ehHospedagemAberta("premio-caixa.blogspot.com"))
        assertTrue(TrancoChecker.ehHospedagemAberta("https://sites.google.com/view/golpe"))
        assertTrue(TrancoChecker.ehHospedagemAberta("www.meu-golpe.github.io/pagina"))
    }

    @Test
    fun siteComumNaoEhHospedagemAberta() {
        assertFalse(TrancoChecker.ehHospedagemAberta("nubank.com.br"))
        assertFalse(TrancoChecker.ehHospedagemAberta("google.com"))
        assertFalse(TrancoChecker.ehHospedagemAberta("notbit.ly"))
    }

    @Test
    fun hospedagemAbertaNuncaContaComoNoRanking() {
        assertFalse(TrancoChecker.dominioEstaNoRanking("premio-caixa.blogspot.com"))
    }
}
