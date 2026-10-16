package com.example.protege_idoso.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.R


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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                CabecalhoAceite()

                Text(
                    text = "Antes de começar, veja como o aplicativo cuida dos seus dados:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                CartaoDeResumoDoAceite()

                BotaoDeLeitura(
                    texto = "Ler os Termos de Uso",
                    onClick = onLerTermos
                )

                BotaoDeLeitura(
                    texto = "Ler a Política de Privacidade",
                    onClick = onLerPolitica
                )

                Text(
                    text = "Para continuar, marque as duas opções:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics {
                        heading()
                    }
                )

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

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (!podeContinuar) {
                    Text(
                        text = "Marque as duas opções acima para continuar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onAceitar,
                    enabled = podeContinuar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Aceitar e continuar",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                OutlinedButton(
                    onClick = onRecusar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Não aceito (sair)",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun CabecalhoAceite() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_stat_shield),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = "Bem-vindo ao ProtegeIdoso",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics {
                    heading()
                }
            )

            Text(
                text = "Termos de Uso e Privacidade",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CartaoDeResumoDoAceite() {
    val pontos = listOf(
        "Lemos apenas os avisos de mensagens do WhatsApp, para procurar sinais de golpe.",
        "Mensagens sem sinal de risco ficam no seu celular.",
        "Mensagens com sinal de risco vão para análise, com e-mails, telefones, CPF, links e chaves Pix escondidos.",
        "Não respondemos, não apagamos e não guardamos suas mensagens.",
        "Você pode revogar o consentimento quando quiser, na tela “Privacidade e segurança”."
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
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
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = ponto,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BotaoDeLeitura(
    texto: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge
        )
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
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
