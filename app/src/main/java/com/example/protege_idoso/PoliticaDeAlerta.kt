package com.example.protege_idoso

object PoliticaDeAlerta {

    fun deveAnalisar(
        termosAceitos: Boolean,
        analisarWhatsApp: Boolean,
        avisoSuspeito: Boolean,
        avisoGolpe: Boolean
    ): Boolean {
        return termosAceitos && analisarWhatsApp && (avisoSuspeito || avisoGolpe)
    }


    fun deveAvisar(
        nivel: RiskLevel,
        termosAceitos: Boolean,
        analisarWhatsApp: Boolean,
        avisoSuspeito: Boolean,
        avisoGolpe: Boolean
    ): Boolean {
        if (!termosAceitos || !analisarWhatsApp) return false

        return when (nivel) {
            RiskLevel.SEGURO -> false
            RiskLevel.SUSPEITO -> avisoSuspeito
            RiskLevel.POSSIVEL_GOLPE -> avisoGolpe
        }
    }
}
