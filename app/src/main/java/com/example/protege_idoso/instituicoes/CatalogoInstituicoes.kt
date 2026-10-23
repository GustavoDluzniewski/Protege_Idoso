package com.example.protege_idoso.instituicoes

import java.text.Normalizer

data class CanalDoCatalogo(
    val rotulo: String,
    val tipo: TipoCanal,
    val valor: String
)

data class InstituicaoDoCatalogo(
    val codigo: String,
    val nome: String,
    val nomesNaMensagem: List<String>,
    val canais: List<CanalDoCatalogo>
)

object CatalogoInstituicoes {

    const val DATA_DA_CONFERENCIA = "22/10/2026"

    private fun telefone(rotulo: String, numero: String) =
        CanalDoCatalogo(rotulo, TipoCanal.TELEFONE, numero.filter { it.isDigit() })

    private fun site(endereco: String) =
        CanalDoCatalogo("Site oficial", TipoCanal.SITE, endereco)

    val todas: List<InstituicaoDoCatalogo> = listOf(
        InstituicaoDoCatalogo(
            codigo = "banco_do_brasil",
            nome = "Banco do Brasil",
            nomesNaMensagem = listOf("banco do brasil"),
            canais = listOf(
                telefone("Central de atendimento (capitais)", "4004 0001"),
                telefone("Central de atendimento (demais cidades)", "0800 729 0001"),
                telefone("SAC", "0800 729 0722"),
                telefone("Ouvidoria", "0800 729 5678"),
                site("https://www.bb.com.br")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "caixa",
            nome = "Caixa",
            nomesNaMensagem = listOf("caixa economica", "caixa tem", "alo caixa"),
            canais = listOf(
                telefone("Alô Caixa (capitais)", "4004 0104"),
                telefone("Alô Caixa (demais cidades)", "0800 104 0104"),
                telefone("SAC", "0800 726 0101"),
                telefone("Ouvidoria", "0800 725 7474"),
                site("https://www.caixa.gov.br")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "bradesco",
            nome = "Bradesco",
            nomesNaMensagem = listOf("bradesco"),
            canais = listOf(
                telefone("Fone Fácil (capitais)", "4002 0022"),
                telefone("Fone Fácil (demais cidades)", "0800 570 0022"),
                telefone("SAC", "0800 704 8383"),
                telefone("Ouvidoria", "0800 727 9933"),
                site("https://banco.bradesco")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "itau",
            nome = "Itaú",
            nomesNaMensagem = listOf("itau"),
            canais = listOf(
                telefone("Central de atendimento (capitais)", "4004 4828"),
                telefone("Central de atendimento (demais cidades)", "0800 970 4828"),
                telefone("SAC", "0800 728 0728"),
                telefone("Ouvidoria", "0800 570 0011"),
                site("https://www.itau.com.br")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "santander",
            nome = "Santander",
            nomesNaMensagem = listOf("santander"),
            canais = listOf(
                telefone("Central de atendimento (capitais)", "4004 3535"),
                telefone("Central de atendimento (demais cidades)", "0800 702 3535"),
                telefone("SAC", "0800 762 7777"),
                telefone("Ouvidoria", "0800 726 0322"),
                site("https://www.santander.com.br")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "nubank",
            nome = "Nubank",
            nomesNaMensagem = listOf("nubank", "nu bank"),
            canais = listOf(
                telefone("Atendimento (capitais)", "4020 0185"),
                telefone("Atendimento (demais cidades)", "0800 591 2117"),
                telefone("Ouvidoria", "0800 887 0463"),
                site("https://nubank.com.br")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "inter",
            nome = "Banco Inter",
            nomesNaMensagem = listOf("banco inter"),
            canais = listOf(
                telefone("Central de atendimento (capitais)", "3003 4070"),
                telefone("Central de atendimento (demais cidades)", "0800 940 0007"),
                telefone("SAC", "0800 940 9999"),
                telefone("Ouvidoria", "0800 940 7772"),
                site("https://inter.co")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "sicredi",
            nome = "Sicredi",
            nomesNaMensagem = listOf("sicredi"),
            canais = listOf(
                telefone("Central de atendimento", "0800 724 4770"),
                telefone("SAC", "0800 724 7220"),
                telefone("Ouvidoria", "0800 646 2519"),
                site("https://www.sicredi.com.br")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "sicoob",
            nome = "Sicoob",
            nomesNaMensagem = listOf("sicoob"),
            canais = listOf(
                telefone("Central de atendimento (capitais)", "4000 1111"),
                telefone("Central de atendimento (demais cidades)", "0800 642 0000"),
                telefone("SAC", "0800 724 4420"),
                telefone("Ouvidoria", "0800 725 0996"),
                site("https://www.sicoob.com.br")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "pan",
            nome = "Banco Pan",
            nomesNaMensagem = listOf("banco pan", "bancopan"),
            canais = listOf(
                telefone("Cartão e conta (capitais)", "4003 0101"),
                telefone("Cartão e conta (demais cidades)", "0800 888 0101"),
                telefone("Empréstimos e financiamentos (capitais)", "4002 1687"),
                telefone("Empréstimos e financiamentos (demais cidades)", "0800 775 8686"),
                telefone("SAC", "0800 776 8000"),
                telefone("Ouvidoria", "0800 776 9595"),
                site("https://www.bancopan.com.br")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "bmg",
            nome = "Banco BMG",
            nomesNaMensagem = listOf("bmg"),
            canais = listOf(
                telefone("Central de relacionamento", "0800 770 1790"),
                telefone("SAC", "0800 979 9099"),
                telefone("Ouvidoria", "0800 723 2044"),
                site("https://www.bancobmg.com.br")
            )
        ),
        InstituicaoDoCatalogo(
            codigo = "inss",
            nome = "INSS",
            nomesNaMensagem = listOf("inss", "meu inss", "previdencia social"),
            canais = listOf(
                telefone("Central 135", "135"),
                site("https://www.gov.br/inss")
            )
        )
    )

    fun porCodigo(codigo: String?): InstituicaoDoCatalogo? =
        if (codigo == null) null else todas.firstOrNull { it.codigo == codigo }
}

object DetectorDeInstituicao {

    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")

    private val padroes: List<Pair<InstituicaoDoCatalogo, Regex>> =
        CatalogoInstituicoes.todas.flatMap { instituicao ->
            instituicao.nomesNaMensagem.map { nome ->
                instituicao to Regex("(?<![a-z0-9])" + Regex.escape(normalizar(nome)) + "(?![a-z0-9])")
            }
        }

    fun detectar(texto: String): InstituicaoDoCatalogo? {
        if (texto.isBlank()) return null
        val normalizado = normalizar(texto)
        return padroes
            .mapNotNull { (instituicao, padrao) ->
                padrao.find(normalizado)?.let { achado -> instituicao to achado.range.first }
            }
            .minByOrNull { it.second }
            ?.first
    }
}