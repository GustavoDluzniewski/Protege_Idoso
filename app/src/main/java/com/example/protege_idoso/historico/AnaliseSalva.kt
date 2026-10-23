package com.example.protege_idoso.historico

import com.example.protege_idoso.RiskLevel

data class AnaliseSalva(
    val id: Long = 0,
    val dataHora: Long,
    val nivel: RiskLevel,
    val explicacao: String,
    val orientacao: String,
    val origem: String,
    val status: StatusAnalise = StatusAnalise.PENDENTE,
    val atualizadoEm: Long = dataHora
)

enum class StatusAnalise(val rotulo: String) {
    PENDENTE("Ainda não verificado"),
    VERIFICADA("Já verifiquei"),
    FALSO_POSITIVO("Era uma mensagem segura");

    companion object {
        fun deTexto(valor: String?): StatusAnalise =
            entries.firstOrNull { it.name == valor } ?: PENDENTE
    }
}

fun nivelDeTexto(valor: String?): RiskLevel =
    RiskLevel.entries.firstOrNull { it.name == valor } ?: RiskLevel.SUSPEITO
