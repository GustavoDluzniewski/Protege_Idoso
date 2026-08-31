package com.example.protege_idoso

data class RiskResult(
    val nivel: RiskLevel,
    val explicacao: String,
    val orientacao: String
)