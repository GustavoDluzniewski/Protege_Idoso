package com.example.protege_idoso.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.ui.theme.ProtegeTema


@Composable
fun AceiteScreen(
    onLerTermos: () -> Unit,
    onLerPolitica: () -> Unit,
    onAceitar: () -> Unit,
    onRecusar: () -> Unit
) {
    var aceitouTermos by rememberSaveable { mutableStateOf(false) }
    var aceitouPrivacidade by rememberSaveable { mutableStateOf(false) }

    val podeContinuar = aceitouTermos && aceitouPrivacidade

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {

                CabecalhoAceite()

                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {

                    Text(
                        text = "Antes de começar, veja como o aplicativo cuida dos seus dados:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    CartaoDeResumoDoAceite()

                    BotaoSecundario(
                        texto = "Ler os Termos de Uso",
                        onClick = onLerTermos
                    )

                    BotaoSecundario(
                        texto = "Ler a Política de Privacidade",
                        onClick = onLerPolitica
                    )

                    TituloDeSecao(texto = "Para continuar, marque as duas opções:")

                    CaixaDeAceite(
                        marcado = aceitouTermos,
                        texto = "Li e concordo com os Termos de Uso.",
                        onMudar = { aceitouTermos = it }
                    )

                    CaixaDeAceite(
                        marcado = aceitouPrivacidade,
                        texto = "Li a Política de Privacidade e autorizo o tratamento dos meus " +
                                "dados como descrito nela, inclusive o envio, para análise, do " +
                                "texto protegido das mensagens que tiverem sinais de risco. " +
                                "Esse texto pode ser tratado em outros países.",
                        onMudar = { aceitouPrivacidade = it }
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    if (!podeContinuar) {
                        Text(
                            text = "Marque as duas opções acima para continuar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    BotaoPrincipal(
                        texto = "Aceitar e continuar",
                        onClick = onAceitar,
                        habilitado = podeContinuar
                    )

                    BotaoSecundario(
                        texto = "Não aceito (sair)",
                        onClick = onRecusar
                    )
                }
            }
        }
    }
}

@Composable
private fun CabecalhoAceite() {
    val cores = ProtegeTema.cores

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradienteDoCabecalho())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EscudoDeProtecao(ativo = true, tamanho = 170.dp)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Bem-vindo ao ProtegeIdoso",
                style = MaterialTheme.typography.headlineMedium,
                color = cores.sobreGradiente,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics {
                    heading()
                }
            )

            Text(
                text = "Termos de Uso e Privacidade",
                style = MaterialTheme.typography.bodyLarge,
                color = cores.sobreGradiente,
                textAlign = TextAlign.Center
            )
        }

        OndaInferior()
    }
}

@Composable
private fun CartaoDeResumoDoAceite() {
    val pontos = listOf(
        "Lemos apenas os avisos de mensagens do WhatsApp, para procurar sinais de golpe.",
        "Mensagens sem sinal de risco ficam no seu celular.",
        "Mensagens com sinal de risco vão para análise, com e-mails, telefones, CPF, RG, cartões, agência e conta, CEP, links e chaves Pix escondidos no texto. O endereço dos links vai à parte, só para verificar o site.",
        "Não respondemos, não apagamos e não guardamos suas mensagens.",
        "Você pode revogar o consentimento quando quiser, na tela “Privacidade e segurança”."
    )

    CartaoBranco {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            pontos.forEach { ponto ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )

                    Text(
                        text = ponto,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CaixaDeAceite(
    marcado: Boolean,
    texto: String,
    onMudar: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (marcado) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            if (marcado) 2.dp else 1.dp,
            if (marcado) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = marcado,
                    role = Role.Checkbox,
                    onValueChange = onMudar
                )
                .heightIn(min = 72.dp)
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = marcado,
                onCheckedChange = null
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
