package com.example.protege_idoso.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerValue
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.CartaoDesafioDoDia
import com.example.protege_idoso.R
import com.example.protege_idoso.ui.theme.ProtegeTema
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
    alertasPendentes: Int,
    totalContatos: Int,
    totalInstituicoes: Int,
    desafioLiberadoEm: Long,
    desafioEmAndamento: Boolean,
    onAnalisarWhatsAppChange: (Boolean) -> Unit,
    onAvisoSuspeitoChange: (Boolean) -> Unit,
    onAvisoGolpeChange: (Boolean) -> Unit,
    onAtivarProtecao: () -> Unit,
    onPermitirAvisos: () -> Unit,
    onPedirAjuda: () -> Unit,
    onAbrirInstrucoes: () -> Unit,
    onAbrirHistorico: () -> Unit,
    onAbrirDesafio: () -> Unit,
    onAbrirContatos: () -> Unit,
    onCadastrarContato: () -> Unit,
    onAbrirInstituicoes: () -> Unit,
    onEscolherInstituicoes: () -> Unit,
    onAbrirPrivacidade: () -> Unit,
    onAbrirTermos: () -> Unit,
    onAbrirPolitica: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val ancoras = remember { Ancoras() }
    val margemPx = with(LocalDensity.current) { 16.dp.toPx() }

    val irParaOpcoes: () -> Unit = {
        scope.launch { rolarParaOpcoes(ancoras, scrollState, margemPx) }
    }

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
                onHistorico = {
                    scope.launch { drawerState.close() }
                    onAbrirHistorico()
                },
                onDesafio = {
                    scope.launch { drawerState.close() }
                    onAbrirDesafio()
                },
                onContatos = {
                    scope.launch { drawerState.close() }
                    onAbrirContatos()
                },
                onInstituicoes = {
                    scope.launch { drawerState.close() }
                    onAbrirInstituicoes()
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
                    .onGloballyPositioned { ancoras.viewport = it }
                    .verticalScroll(scrollState)
                    .navigationBarsPadding()
            ) {
                CabecalhoInicio(
                    protecaoAtiva = protecaoAtiva,
                    onAbrirMenu = { scope.launch { drawerState.open() } },
                    onAtivarProtecao = onAtivarProtecao
                )

                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (!notificacoesPermitidas) {
                        CartaoAvisosBloqueados(onPermitirAvisos = onPermitirAvisos)
                    }

                    BotaoPrecisoDeAjuda(onClick = onPedirAjuda)

                    CartaoDesafioDoDia(
                        liberadoEm = desafioLiberadoEm,
                        emAndamento = desafioEmAndamento,
                        onClick = onAbrirDesafio
                    )

                    PainelDeAtalhos(
                        alertasPendentes = alertasPendentes,
                        onHistorico = onAbrirHistorico,
                        onContatos = onAbrirContatos,
                        onInstrucoes = onAbrirInstrucoes,
                        onOpcoes = irParaOpcoes,
                        onPrivacidade = onAbrirPrivacidade,
                        onInstituicoes = onAbrirInstituicoes
                    )

                    CartaoDeSugestoes(
                        alertasPendentes = alertasPendentes,
                        totalContatos = totalContatos,
                        totalInstituicoes = totalInstituicoes,
                        analisarWhatsApp = analisarWhatsApp,
                        onAbrirHistorico = onAbrirHistorico,
                        onCadastrarContato = onCadastrarContato,
                        onEscolherInstituicoes = onEscolherInstituicoes,
                        onLigarAnalise = { onAnalisarWhatsAppChange(true) }
                    )

                    TituloDeSecao(
                        texto = "Quando devo avisar você?",
                        modifier = Modifier.onGloballyPositioned { ancoras.opcoes = it }
                    )

                    CartaoDeOpcoes(
                        analisarWhatsApp = analisarWhatsApp,
                        avisoSuspeito = avisoSuspeito,
                        avisoGolpe = avisoGolpe,
                        onAnalisarWhatsAppChange = onAnalisarWhatsAppChange,
                        onAvisoSuspeitoChange = onAvisoSuspeitoChange,
                        onAvisoGolpeChange = onAvisoGolpeChange
                    )

                    NotaDePrivacidade(onAbrirPrivacidade = onAbrirPrivacidade)
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
private fun CabecalhoInicio(
    protecaoAtiva: Boolean,
    onAbrirMenu: () -> Unit,
    onAtivarProtecao: () -> Unit
) {
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onAbrirMenu,
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.18f),
                    contentColor = cores.sobreGradiente,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 12.dp, end = 18.dp, top = 10.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Menu", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_stat_shield),
                        contentDescription = null,
                        tint = cores.sobreGradiente,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "ProtegeIdoso",
                        style = MaterialTheme.typography.titleMedium,
                        color = cores.sobreGradiente,
                        modifier = Modifier.semantics { heading() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        enabled = !protecaoAtiva,
                        role = Role.Button,
                        onClickLabel = "Ligar a proteção",
                        onClick = onAtivarProtecao
                    )
                    .semantics {
                        contentDescription = "Escudo de proteção"
                        stateDescription = if (protecaoAtiva) "Proteção ativada" else "Proteção desativada"
                    }
            ) {
                EscudoDeProtecao(ativo = protecaoAtiva)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (protecaoAtiva) "Proteção ativada" else "Proteção desligada",
                style = MaterialTheme.typography.headlineMedium,
                color = cores.sobreGradiente,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (protecaoAtiva) {
                    "Estamos de olho nas suas mensagens do WhatsApp."
                } else {
                    "Para o aplicativo funcionar, é preciso liberar uma permissão. Leva só um minuto."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = cores.sobreGradiente,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            if (!protecaoAtiva) {
                Spacer(modifier = Modifier.height(18.dp))

                BotaoPrincipal(
                    texto = "Ligar a proteção",
                    onClick = onAtivarProtecao,
                    corFundo = Color.White,
                    corTexto = cores.gradienteTopo,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Na tela que abrir, toque em ProtegeIdoso e ative a opção.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = cores.sobreGradiente,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        OndaInferior(altura = 36.dp)
    }
}


@Composable
private fun PainelDeAtalhos(
    alertasPendentes: Int,
    onHistorico: () -> Unit,
    onContatos: () -> Unit,
    onInstrucoes: () -> Unit,
    onOpcoes: () -> Unit,
    onPrivacidade: () -> Unit,
    onInstituicoes: () -> Unit
) {
    CartaoBranco {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                BotaoCircular(
                    rotulo = "Histórico de alertas",
                    icone = Icons.AutoMirrored.Filled.List,
                    onClick = onHistorico,
                    contador = alertasPendentes,
                    descricaoContador = "$alertasPendentes para conferir",
                    modifier = Modifier.weight(1f)
                )
                BotaoCircular(
                    rotulo = "Contatos de confiança",
                    icone = Icons.Filled.Person,
                    onClick = onContatos,
                    modifier = Modifier.weight(1f)
                )
                BotaoCircular(
                    rotulo = "Dicas de segurança",
                    icone = Icons.Filled.Info,
                    onClick = onInstrucoes,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                BotaoCircular(
                    rotulo = "Quando avisar",
                    icone = Icons.Filled.Notifications,
                    onClick = onOpcoes,
                    modifier = Modifier.weight(1f)
                )
                BotaoCircular(
                    rotulo = "Privacidade",
                    icone = Icons.Filled.Lock,
                    onClick = onPrivacidade,
                    modifier = Modifier.weight(1f)
                )
                BotaoCircular(
                    rotulo = "Minhas instituições",
                    icone = IconeInstituicao,
                    onClick = onInstituicoes,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CartaoDeSugestoes(
    alertasPendentes: Int,
    totalContatos: Int,
    totalInstituicoes: Int,
    analisarWhatsApp: Boolean,
    onAbrirHistorico: () -> Unit,
    onCadastrarContato: () -> Unit,
    onEscolherInstituicoes: () -> Unit,
    onLigarAnalise: () -> Unit
) {
    val cores = ProtegeTema.cores
    val sugestoes = listOf(
        !analisarWhatsApp,
        alertasPendentes > 0,
        totalContatos == 0,
        totalInstituicoes == 0
    )
    val quantidade = sugestoes.count { it }
    if (quantidade == 0) return

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TituloDeSecao(
            texto = if (quantidade == 1) "1 sugestão para você" else "$quantidade sugestões para você"
        )

        CartaoBranco {
            var mostrouAlguma = false

            if (!analisarWhatsApp) {
                ItemDeSugestao(
                    icone = Icons.Filled.Warning,
                    titulo = "Análise desligada",
                    descricao = "As mensagens do WhatsApp não estão sendo verificadas.",
                    rotuloBotao = "Ligar",
                    onClick = onLigarAnalise,
                    corIcone = cores.alertaTexto,
                    fundoIcone = cores.alertaFundo
                )
                mostrouAlguma = true
            }

            if (alertasPendentes > 0) {
                if (mostrouAlguma) DivisorSuave()
                ItemDeSugestao(
                    icone = Icons.AutoMirrored.Filled.List,
                    titulo = if (alertasPendentes == 1) {
                        "1 alerta para conferir"
                    } else {
                        "$alertasPendentes alertas para conferir"
                    },
                    descricao = "Veja os avisos e marque os que você já verificou.",
                    rotuloBotao = "Ver",
                    onClick = onAbrirHistorico,
                    corIcone = cores.perigoTexto,
                    fundoIcone = cores.perigoFundo
                )
                mostrouAlguma = true
            }

            if (totalContatos == 0) {
                if (mostrouAlguma) DivisorSuave()
                ItemDeSugestao(
                    icone = Icons.Filled.Person,
                    titulo = "Cadastre alguém de confiança",
                    descricao = "Na dúvida sobre uma mensagem, ligue para essa pessoa com um toque.",
                    rotuloBotao = "Cadastrar",
                    onClick = onCadastrarContato
                )
                mostrouAlguma = true
            }

            if (totalInstituicoes == 0) {
                if (mostrouAlguma) DivisorSuave()
                ItemDeSugestao(
                    icone = IconeInstituicao,
                    titulo = "Quais instituições você utiliza?",
                    descricao = "Escolha seus bancos e tenha os números oficiais à mão.",
                    rotuloBotao = "Escolher",
                    onClick = onEscolherInstituicoes
                )
            }
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
    onHistorico: () -> Unit,
    onDesafio: () -> Unit,
    onContatos: () -> Unit,
    onInstituicoes: () -> Unit,
    onInstrucoes: () -> Unit,
    onAjuda: () -> Unit,
    onPrivacidade: () -> Unit,
    onTermos: () -> Unit,
    onPolitica: () -> Unit
) {
    val cores = ProtegeTema.cores

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradienteDoCabecalho())
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_stat_shield),
                        contentDescription = null,
                        tint = cores.sobreGradiente,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Column {
                    Text(
                        text = "ProtegeIdoso",
                        style = MaterialTheme.typography.titleLarge,
                        color = cores.sobreGradiente
                    )
                    Text(
                        text = if (protecaoAtiva) "Proteção ativada" else "Proteção desativada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = cores.sobreGradiente
                    )
                }
            }

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
                texto = "Histórico de alertas",
                icone = Icons.AutoMirrored.Filled.List,
                onClick = onHistorico
            )

            ItemDoMenu(
                texto = "Desafio do dia",
                icone = Icons.Filled.Star,
                onClick = onDesafio
            )

            ItemDoMenu(
                texto = "Contatos de confiança",
                icone = Icons.Filled.Person,
                onClick = onContatos
            )

            ItemDoMenu(
                texto = "Minhas instituições",
                icone = IconeInstituicao,
                onClick = onInstituicoes
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
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 28.dp, vertical = 16.dp)
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
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .padding(NavigationDrawerItemDefaults.ItemPadding)
            .heightIn(min = 60.dp)
    )
}


@Composable
private fun CartaoAvisosBloqueados(
    onPermitirAvisos: () -> Unit
) {
    val cores = ProtegeTema.cores
    CartaoColorido(
        fundo = cores.perigoFundo,
        texto = cores.perigoTexto
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

        BotaoPrincipal(
            texto = "Permitir avisos",
            onClick = onPermitirAvisos,
            corFundo = cores.botaoPerigo,
            corTexto = cores.sobreBotaoPerigo
        )
    }
}

@Composable
private fun BotaoPrecisoDeAjuda(
    onClick: () -> Unit
) {
    val cores = ProtegeTema.cores
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BotaoPrincipal(
            texto = "Preciso de ajuda",
            onClick = onClick,
            icone = Icons.Filled.Phone,
            corFundo = cores.botaoPerigo,
            corTexto = cores.sobreBotaoPerigo,
            alturaMinima = 68.dp
        )

        Text(
            text = "Abre o WhatsApp para você pedir orientação ou denunciar um golpe.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
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
    val cores = ProtegeTema.cores

    CartaoBranco {
        LinhaDeOpcao(
            icone = Icons.Filled.CheckCircle,
            corIcone = MaterialTheme.colorScheme.primary,
            fundoIcone = MaterialTheme.colorScheme.primaryContainer,
            titulo = "Analisar mensagens",
            descricao = "Procura sinais de golpe nas mensagens do WhatsApp.",
            marcado = analisarWhatsApp,
            onMudar = onAnalisarWhatsAppChange
        )

        DivisorSuave()

        LinhaDeOpcao(
            icone = Icons.Filled.Info,
            corIcone = cores.alertaTexto,
            fundoIcone = cores.alertaFundo,
            titulo = "Aviso de mensagem suspeita",
            descricao = "Mostra um aviso quando algo parece estranho.",
            marcado = avisoSuspeito,
            habilitado = analisarWhatsApp,
            onMudar = onAvisoSuspeitoChange
        )

        DivisorSuave()

        LinhaDeOpcao(
            icone = Icons.Filled.Warning,
            corIcone = cores.perigoTexto,
            fundoIcone = cores.perigoFundo,
            titulo = "Aviso de possível golpe",
            descricao = "Mostra um aviso forte quando há grande chance de golpe.",
            marcado = avisoGolpe,
            habilitado = analisarWhatsApp,
            onMudar = onAvisoGolpeChange
        )
    }
}

@Composable
private fun LinhaDeOpcao(
    icone: ImageVector,
    corIcone: Color,
    fundoIcone: Color,
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
            .heightIn(min = 76.dp)
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconeEmCirculo(icone = icone, cor = corIcone, fundo = fundoIcone, tamanho = 44.dp)

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
                        "o aplicativo esconde e-mails, telefones, CPF, links e chaves Pix." +
                        " O endereço dos links vai à parte, só para verificar o site.",
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
