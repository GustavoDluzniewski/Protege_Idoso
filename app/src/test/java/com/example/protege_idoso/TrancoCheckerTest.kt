package com.example.protege_idoso

import org.junit.Assert.assertFalse
import org.junit.Test

class TrancoCheckerTest {

    @Test
    fun dominioNaoEncontradoDeveRetornarFalse() {
        assertFalse(
            TrancoChecker.dominioEstaNoRanking("dominio-que-nao-existe-xyz.invalid")
        )
    }
}