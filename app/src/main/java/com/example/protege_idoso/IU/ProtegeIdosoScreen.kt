package com.example.protege_idoso.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.R
import kotlin.math.roundToInt
import kotlinx.coroutines.launch


private class Ancoras {
    var viewport: LayoutCoordinates? = null
    var opcoes: LayoutCoordinates? = null
}

@Composable
fun ProtegeIdosoScreen(
    protecaoAtiva: Boolean,
    notificacoesPermitidas: Boolean,
    analisarWhatsApp: Boolean,
    avisoSuspeito: Boolean,
    avisoGolpe: Boolean,
    versao: String,
    onAnalisarWhatsAppChange: (Boolean) -> Unit,
    onAvisoSuspeitoChange: (Boolean) -> Unit,
    onAvisoGolpeChange: (Boolean) -> Unit,
    onAtivarProtecao: () -> Unit,
    onPermitirAvisos: () -> Unit,
    onPedirAjuda: () -> Unit,
    onAbrirInstrucoes: () -> Unit,
    onAbrirPrivacidade: () -> Unit,
    onAbrirTermos: () -> Unit,
    onAbrirPolitica: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val ancoras = remember { Ancoras() }
    val margemPx = with(LocalDensity.current) { 16.dp.toPx() }


    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ConteudoDoMenu(
                protecaoAtiva = protecaoAtiva,
                versao = versao,
                onInicio = {
                    scope.launch {
                        drawerState.close()
                        scrollState.animateScrollTo(0)
                    }
                },
                onProtecao = {
                    scope.launch { drawerState.close() }
                    onAtivarProtecao()
                },
                onOpcoes = {
                    scope.launch {
                        drawerState.close()
                        rolarParaOpcoes(ancoras, scrollState, margemPx)
                    }
                },
                onInstrucoes = {
                    scope.launch { drawerState.close() }
                    onAbrirInstrucoes()
                },
                onAjuda = {
                    scope.launch { drawerState.close() }
                    onPedirAjuda()
                },
                onPrivacidade = {
                    scope.launch { drawerState.close() }
                    onAbrirPrivacidade()
                },
                onTermos = {
                    scope.launch { drawerState.close() }
                    onAbrirTermos()
                },
                onPolitica = {
                    scope.launch { drawerState.close() }
                    onAbrirPolitica()
                }
            )
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {

                BarraSuperior(
                    onAbrirMenu = {
                        scope.launch { drawerState.open() }
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { ancoras.viewport = it }
                        .verticalScroll(scrollState)
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

                    if (!notificacoesPermitidas) {
                        CartaoAvisosBloqueados(
                            onPermitirAvisos = onPermitirAvisos
                        )
                    }

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
                        modifier = Modifier
                            .onGloballyPositioned { ancoras.opcoes = it }
                            .semantics {
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

                    NotaDePrivacidade(
                        onAbrirPrivacidade = onAbrirPrivacidade
                    )
                }
            }
        }
    }
}

private suspend fun rolarParaOpcoes(
    ancoras: Ancoras,
    scrollState: ScrollState,
    margemPx: Float
) {
    val viewport = ancoras.viewport
    val opcoes = ancoras.opcoes

    if (viewport == null || opcoes == null) return
    if (!viewport.isAttached || !opcoes.isAttached) return

    val diferenca = opcoes.positionInRoot().y - viewport.positionInRoot().y
    val alvo = (scrollState.value + diferenca - margemPx)
        .roundToInt()
        .coerceAtLeast(0)

    scrollState.animateScrollTo(alvo)
}

@Composable
private fun BarraSuperior(
    onAbrirMenu: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            )
    ) {

        FilledTonalButton(
            onClick = onAbrirMenu,
            modifier = Modifier.heightIn(min = 56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Menu",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun ConteudoDoMenu(
    protecaoAtiva: Boolean,
    versao: String,
    onInicio: () -> Unit,
    onProtecao: () -> Unit,
    onOpcoes: () -> Unit,
    onInstrucoes: () -> Unit,
    onAjuda: () -> Unit,
    onPrivacidade: () -> Unit,
    onTermos: () -> Unit,
    onPolitica: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 24.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_stat_shield),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column {
                    Text(
                        text = "ProtegeIdoso",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = if (protecaoAtiva) "Proteção ativada" else "Proteção desativada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(12.dp))

            ItemDoMenu(
                texto = "Início",
                icone = Icons.Filled.Home,
                selecionado = true,
                onClick = onInicio
            )

            ItemDoMenu(
                texto = if (protecaoAtiva) "Proteção ativada" else "Ligar a proteção",
                icone = if (protecaoAtiva) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                onClick = onProtecao
            )

            ItemDoMenu(
                texto = "Quando devo avisar você?",
                icone = Icons.Filled.Notifications,
                onClick = onOpcoes
            )

            ItemDoMenu(
                texto = "Instruções de segurança",
                icone = Icons.Filled.Info,
                onClick = onInstrucoes
            )

            ItemDoMenu(
                texto = "Preciso de ajuda",
                icone = Icons.Filled.Phone,
                onClick = onAjuda
            )

            ItemDoMenu(
                texto = "Privacidade e segurança",
                icone = Icons.Filled.Lock,
                onClick = onPrivacidade
            )

            ItemDoMenu(
                texto = "Termos de Uso",
                icone = Icons.Filled.Done,
                onClick = onTermos
            )

            ItemDoMenu(
                texto = "Política de Privacidade",
                icone = Icons.Filled.Done,
                onClick = onPolitica
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = "Versão $versao",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = 28.dp,
                    vertical = 16.dp
                )
            )
        }
    }
}

@Composable
private fun ItemDoMenu(
    texto: String,
    icone: ImageVector,
    onClick: () -> Unit,
    selecionado: Boolean = false
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = texto,
                style = MaterialTheme.typography.titleMedium
            )
        },
        selected = selecionado,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icone,
                contentDescription = null
            )
        },
        modifier = Modifier
            .padding(NavigationDrawerItemDefaults.ItemPadding)
            .heightIn(min = 64.dp)
    )
}

@Composable
private fun CartaoAvisosBloqueados(
    onPermitirAvisos: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
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
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = "Os avisos estão bloqueados",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Text(
                text = "O celular não está deixando o ProtegeIdoso mostrar avisos. " +
                        "Sem isso, você não será avisado quando chegar uma mensagem suspeita.",
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = onPermitirAvisos,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(
                    text = "Permitir avisos",
                    style = MaterialTheme.typography.labelLarge
                )
            }
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
private fun NotaDePrivacidade(
    onAbrirPrivacidade: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

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

        TextButton(
            onClick = onAbrirPrivacidade,
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text(
                text = "Ver privacidade e segurança",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
