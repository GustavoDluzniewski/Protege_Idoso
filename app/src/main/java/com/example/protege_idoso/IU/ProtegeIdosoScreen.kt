package com.example.protege_idoso.ui

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.R
import androidx.compose.ui.semantics.stateDescription
import com.example.protege_idoso.InstrucoesActivity

@Composable
fun ProtegeIdosoScreen(
    protecaoAtiva: Boolean,
    analisarWhatsApp: Boolean,
    avisoSuspeito: Boolean,
    avisoGolpe: Boolean,
    onAnalisarWhatsAppChange: (Boolean) -> Unit,
    onAvisoSuspeitoChange: (Boolean) -> Unit,
    onAvisoGolpeChange: (Boolean) -> Unit,
    onAtivarProtecao: () -> Unit,
    onPedirAjuda: () -> Unit,
    onAbrirInstrucoes: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Cabecalho()

            CartaoDeStatus(
                protecaoAtiva = protecaoAtiva,
                onAtivarProtecao = onAtivarProtecao
            )

            BotaoPrecisoDeAjuda(
                onClick = onPedirAjuda
            )

            BotaoInstrucoes(
                onClick = onAbrirInstrucoes
            )

            Text(
                text = "Quando devo avisar você?",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics {
                    heading()
                }
            )

            CartaoDeOpcoes(
                analisarWhatsApp = analisarWhatsApp,
                avisoSuspeito = avisoSuspeito,
                avisoGolpe = avisoGolpe,
                onAnalisarWhatsAppChange = onAnalisarWhatsAppChange,
                onAvisoSuspeitoChange = onAvisoSuspeitoChange,
                onAvisoGolpeChange = onAvisoGolpeChange
            )

            NotaDePrivacidade()
        }
    }
}

@Composable
private fun BotaoInstrucoes(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "Instruções de Segurança",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun Cabecalho() {
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
                contentDescription = "ProtegeIdoso",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Column {

            Text(
                text = "ProtegeIdoso",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics {
                    heading()
                }
            )

            Text(
                text = "Proteção contra golpes no WhatsApp",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CartaoDeStatus(
    protecaoAtiva: Boolean,
    onAtivarProtecao: () -> Unit
) {

    val fundo =
        if (protecaoAtiva)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.secondaryContainer

    val texto =
        if (protecaoAtiva)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSecondaryContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = fundo,
            contentColor = texto
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Icon(
                    imageVector =
                        if (protecaoAtiva)
                            Icons.Filled.CheckCircle
                        else
                            Icons.Filled.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )

                Text(
                    text =
                        if (protecaoAtiva)
                            "Proteção ativada"
                        else
                            "Proteção desativada",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Text(
                text =
                    if (protecaoAtiva) {
                        "Estamos de olho nas suas mensagens do WhatsApp."
                    } else {
                        "Para o aplicativo funcionar, é preciso liberar uma permissão. Leva só um minuto."
                    },
                style = MaterialTheme.typography.bodyLarge
            )

            if (!protecaoAtiva) {

                Button(
                    onClick = onAtivarProtecao,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = texto,
                        contentColor = fundo
                    )
                ) {
                    Text(
                        text = "Ligar a proteção",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Text(
                    text = "Na tela que abrir, toque em ProtegeIdoso e ative a opção.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun BotaoPrecisoDeAjuda(
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {

            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Text(
                text = "Preciso de ajuda",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Text(
            text = "Abre o WhatsApp para você pedir orientação ou denunciar um golpe.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CartaoDeOpcoes(
    analisarWhatsApp: Boolean,
    avisoSuspeito: Boolean,
    avisoGolpe: Boolean,
    onAnalisarWhatsAppChange: (Boolean) -> Unit,
    onAvisoSuspeitoChange: (Boolean) -> Unit,
    onAvisoGolpeChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {

        Column {

            LinhaDeOpcao(
                titulo = "Analisar mensagens",
                descricao = "Procura sinais de golpe nas mensagens do WhatsApp.",
                marcado = analisarWhatsApp,
                onMudar = onAnalisarWhatsAppChange
            )

            Divisor()

            LinhaDeOpcao(
                titulo = "Aviso de mensagem suspeita",
                descricao = "Mostra um aviso quando algo parece estranho.",
                marcado = avisoSuspeito,
                habilitado = analisarWhatsApp,
                onMudar = onAvisoSuspeitoChange
            )

            Divisor()

            LinhaDeOpcao(
                titulo = "Aviso de possível golpe",
                descricao = "Mostra um aviso forte quando há grande chance de golpe.",
                marcado = avisoGolpe,
                habilitado = analisarWhatsApp,
                onMudar = onAvisoGolpeChange
            )
        }
    }
}

@Composable
private fun Divisor() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun LinhaDeOpcao(
    titulo: String,
    descricao: String,
    marcado: Boolean,
    onMudar: (Boolean) -> Unit,
    habilitado: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(
                if (habilitado) 1f else 0.5f
            )
            .semantics {
                stateDescription = if (marcado) {
                    "Ativado"
                } else {
                    "Desativado"
                }
            }
            .toggleable(
                value = marcado,
                enabled = habilitado,
                role = Role.Switch,
                onValueChange = onMudar
            )
            .heightIn(min = 72.dp)
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Switch(
            checked = marcado,
            onCheckedChange = null,
            enabled = habilitado
        )
    }
}

@Composable
private fun NotaDePrivacidade() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {

        Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = "Privacidade",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "Antes de analisar uma mensagem com inteligência artificial, " +
                    "o aplicativo esconde e-mails, telefones, CPF, links e chaves Pix.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}