package com.example.protege_idoso

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.protege_idoso.contatos.AcoesDeContato
import com.example.protege_idoso.contatos.ContatoConfianca
import com.example.protege_idoso.contatos.ContatosRepository
import com.example.protege_idoso.historico.AnaliseSalva
import com.example.protege_idoso.historico.HistoricoRepository
import com.example.protege_idoso.historico.StatusAnalise
import com.example.protege_idoso.ui.BotaoPrincipal
import com.example.protege_idoso.ui.BotaoSecundario
import com.example.protege_idoso.ui.CabecalhoDeTela
import com.example.protege_idoso.ui.CartaoBranco
import com.example.protege_idoso.ui.CartaoDeNumeros
import com.example.protege_idoso.ui.DialogoConfirmacao
import com.example.protege_idoso.ui.Estatistica
import com.example.protege_idoso.ui.IconeEmCirculo
import com.example.protege_idoso.ui.TituloDeSecao
import com.example.protege_idoso.ui.theme.ProtegeTema
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.protege_idoso.instituicoes.AcoesDeCanal
import com.example.protege_idoso.instituicoes.CanalOficial
import com.example.protege_idoso.instituicoes.CatalogoInstituicoes
import com.example.protege_idoso.instituicoes.Instituicao
import com.example.protege_idoso.instituicoes.InstituicoesRepository

class HistoricoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val repositorio = HistoricoRepository(applicationContext)
        val repositorioContatos = ContatosRepository(applicationContext)
        val repositorioInstituicoes = InstituicoesRepository(applicationContext)
        val idInicial = intent.getLongExtra(EXTRA_ID_ANALISE, -1L).takeIf { it > 0 }

        setContent {
            Protege_IdosoTheme {
                HistoricoScreen(
                    repositorio = repositorio,
                    repositorioContatos = repositorioContatos,
                    repositorioInstituicoes = repositorioInstituicoes,
                    idInicial = idInicial,
                    onVoltar = { finish() },
                    onAbrirInstrucoes = {
                        startActivity(Intent(this, InstrucoesActivity::class.java))
                    },
                    onAbrirContatos = {
                        startActivity(ContatosActivity.criarIntent(this))
                    },
                    onAbrirInstituicoes = { cadastrada ->
                        startActivity(InstituicoesActivity.criarIntent(this, abrirEscolha = !cadastrada))
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_ID_ANALISE = "id_analise"

        fun criarIntent(context: Context, idAnalise: Long? = null): Intent =
            Intent(context, HistoricoActivity::class.java).apply {
                if (idAnalise != null) putExtra(EXTRA_ID_ANALISE, idAnalise)
            }
    }
}

private fun formatarData(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR")).format(Date(millis))

@Composable
fun HistoricoScreen(
    repositorio: HistoricoRepository,
    repositorioContatos: ContatosRepository,
    repositorioInstituicoes: InstituicoesRepository,
    idInicial: Long?,
    onVoltar: () -> Unit,
    onAbrirInstrucoes: () -> Unit,
    onAbrirContatos: () -> Unit,
    onAbrirInstituicoes: (cadastrada: Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var analises by remember { mutableStateOf<List<AnaliseSalva>>(emptyList()) }
    var contatos by remember { mutableStateOf<List<ContatoConfianca>>(emptyList()) }
    var instituicoes by remember { mutableStateOf<List<Instituicao>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var idSelecionado by rememberSaveable { mutableStateOf(idInicial ?: -1L) }
    var confirmarLimpeza by remember { mutableStateOf(false) }
    var confirmarExclusao by remember { mutableStateOf<AnaliseSalva?>(null) }

    fun recarregar() {
        scope.launch {
            val (novasAnalises, novosContatos) = withContext(Dispatchers.IO) {
                repositorio.listar() to repositorioContatos.listar()
            }
            val novasInstituicoes = withContext(Dispatchers.IO) { repositorioInstituicoes.listar() }
            analises = novasAnalises
            contatos = novosContatos
            instituicoes = novasInstituicoes
            carregando = false
        }
    }

    fun avisar(texto: String) {
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
    }

    LifecycleResumeEffect(Unit) {
        recarregar()
        onPauseOrDispose { }
    }

    val selecionada = analises.firstOrNull { it.id == idSelecionado }

    BackHandler(enabled = selecionada != null) {
        idSelecionado = -1L
    }

    if (confirmarLimpeza) {
        DialogoConfirmacao(
            titulo = "Apagar todo o histórico?",
            texto = "Todos os alertas guardados serão apagados deste celular. " +
                    "Isso não pode ser desfeito.",
            rotuloConfirmar = "Apagar tudo",
            onConfirmar = {
                confirmarLimpeza = false
                scope.launch {
                    withContext(Dispatchers.IO) { repositorio.limparTudo() }
                    idSelecionado = -1L
                    recarregar()
                    avisar("Histórico apagado.")
                }
            },
            onCancelar = { confirmarLimpeza = false }
        )
    }

    confirmarExclusao?.let { analise ->
        DialogoConfirmacao(
            titulo = "Apagar este alerta?",
            texto = "O alerta de ${formatarData(analise.dataHora)} será apagado do histórico.",
            rotuloConfirmar = "Apagar",
            onConfirmar = {
                confirmarExclusao = null
                scope.launch {
                    withContext(Dispatchers.IO) { repositorio.excluir(analise.id) }
                    idSelecionado = -1L
                    recarregar()
                    avisar("Alerta apagado.")
                }
            },
            onCancelar = { confirmarExclusao = null }
        )
    }

    val iconeLista = rememberVectorPainter(Icons.AutoMirrored.Filled.List)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CabecalhoDeTela(
                titulo = if (selecionada != null) "Detalhe do alerta" else "Histórico de alertas",
                subtitulo = if (selecionada != null) {
                    formatarData(selecionada.dataHora)
                } else {
                    "Guardamos só o resultado. O texto das mensagens não é salvo."
                },
                icone = iconeLista,
                onVoltar = {
                    if (selecionada != null) idSelecionado = -1L else onVoltar()
                }
            )

            when {
                carregando -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                selecionada != null -> DetalheDaAnalise(
                    analise = selecionada,
                    contatos = contatos,
                    instituicoes = instituicoes,
                    onMudarStatus = { status ->
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                repositorio.atualizarStatus(selecionada.id, status)
                            }
                            recarregar()
                            avisar("Pronto! Marcado como: ${status.rotulo}.")
                        }
                    },
                    onExcluir = { confirmarExclusao = selecionada },
                    onAbrirInstrucoes = onAbrirInstrucoes,
                    onLigar = { AcoesDeContato.ligar(context, it) },
                    onAbrirContatos = onAbrirContatos,
                    onAbrirCanal = { AcoesDeCanal.abrir(context, it) },
                    onAbrirInstituicoes = onAbrirInstituicoes
                )

                else -> ListaDeAnalises(
                    analises = analises,
                    onAbrir = { idSelecionado = it.id },
                    onLimparTudo = { confirmarLimpeza = true }
                )
            }
        }
    }
}

@Composable
private fun ListaDeAnalises(
    analises: List<AnaliseSalva>,
    onAbrir: (AnaliseSalva) -> Unit,
    onLimparTudo: () -> Unit
) {
    val pendentes = analises.count { it.status == StatusAnalise.PENDENTE }
    val golpes = analises.count { it.nivel == RiskLevel.POSSIVEL_GOLPE }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (analises.isEmpty()) {
            item { CartaoVazio() }
        } else {
            item {
                CartaoDeNumeros(
                    itens = listOf(
                        Estatistica(analises.size.toString(), "alertas guardados"),
                        Estatistica(pendentes.toString(), "para conferir"),
                        Estatistica(golpes.toString(), "possíveis golpes")
                    )
                )
            }

            item {
                TituloDeSecao(
                    texto = when (pendentes) {
                        0 -> "Tudo conferido"
                        1 -> "1 alerta para conferir"
                        else -> "$pendentes alertas para conferir"
                    }
                )
            }

            items(analises, key = { it.id }) { analise ->
                CartaoResumo(analise = analise, onClick = { onAbrir(analise) })
            }

            item {
                BotaoSecundario(
                    texto = "Apagar todo o histórico",
                    onClick = onLimparTudo,
                    icone = Icons.Filled.Delete,
                    cor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CartaoVazio() {
    val cores = ProtegeTema.cores
    CartaoBranco {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconeEmCirculo(
                icone = Icons.Filled.CheckCircle,
                cor = cores.seguroTexto,
                fundo = cores.seguroFundo,
                tamanho = 80.dp
            )
            Text(
                text = "Nenhum alerta por aqui",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Quando o aplicativo avisar sobre uma mensagem suspeita, " +
                        "o aviso aparece nesta lista para você consultar depois.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class CoresNivel(val fundo: Color, val texto: Color)

@Composable
private fun coresDoNivel(nivel: RiskLevel): CoresNivel {
    val cores = ProtegeTema.cores
    return when (nivel) {
        RiskLevel.POSSIVEL_GOLPE -> CoresNivel(cores.perigoFundo, cores.perigoTexto)
        RiskLevel.SUSPEITO -> CoresNivel(cores.alertaFundo, cores.alertaTexto)
        RiskLevel.SEGURO -> CoresNivel(cores.seguroFundo, cores.seguroTexto)
    }
}

private fun tituloDoNivel(nivel: RiskLevel): String = when (nivel) {
    RiskLevel.POSSIVEL_GOLPE -> "Possível golpe"
    RiskLevel.SUSPEITO -> "Mensagem suspeita"
    RiskLevel.SEGURO -> "Mensagem segura"
}

@Composable
private fun EtiquetaNivel(nivel: RiskLevel) {
    val cores = coresDoNivel(nivel)
    Surface(
        shape = RoundedCornerShape(50),
        color = cores.fundo,
        contentColor = cores.texto
    ) {
        Text(
            text = tituloDoNivel(nivel),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun EtiquetaStatus(status: StatusAnalise) {
    val verificado = status != StatusAnalise.PENDENTE
    Surface(
        shape = RoundedCornerShape(50),
        color = if (verificado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
        contentColor = if (verificado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (verificado) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(text = status.rotulo, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun CartaoResumo(
    analise: AnaliseSalva,
    onClick: () -> Unit
) {
    val cores = coresDoNivel(analise.nivel)

    CartaoBranco(onClick = onClick) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconeEmCirculo(
                icone = Icons.Filled.Warning,
                cor = cores.texto,
                fundo = cores.fundo
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = tituloDoNivel(analise.nivel),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${formatarData(analise.dataHora)} · ${analise.origem}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = analise.explicacao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                EtiquetaStatus(status = analise.status)
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(28.dp)
            )
        }
    }
}

@Composable
private fun DetalheDaAnalise(
    analise: AnaliseSalva,
    contatos: List<ContatoConfianca>,
    instituicoes: List<Instituicao>,
    onMudarStatus: (StatusAnalise) -> Unit,
    onExcluir: () -> Unit,
    onAbrirInstrucoes: () -> Unit,
    onLigar: (ContatoConfianca) -> Unit,
    onAbrirContatos: () -> Unit,
    onAbrirCanal: (CanalOficial) -> Unit,
    onAbrirInstituicoes: (cadastrada: Boolean) -> Unit
) {

    val cores = coresDoNivel(analise.nivel)

    val modeloCitado = CatalogoInstituicoes.porCodigo(analise.instituicao)

    val instituicaoCitada = instituicoes.firstOrNull { it.codigo != null && it.codigo == analise.instituicao
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CartaoBranco {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    IconeEmCirculo(
                        icone = Icons.Filled.Warning,
                        cor = cores.texto,
                        fundo = cores.fundo,
                        tamanho = 56.dp
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = tituloDoNivel(analise.nivel),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.semantics { heading() }
                        )
                        Text(
                            text = "Recebida pelo ${analise.origem}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EtiquetaNivel(nivel = analise.nivel)
                    EtiquetaStatus(status = analise.status)
                }
            }
        }

        SecaoTexto(titulo = "Por que avisamos", texto = analise.explicacao)
        SecaoTexto(titulo = "O que fazer", texto = analise.orientacao)

        if (modeloCitado != null) {
            val canaisParaMostrar = instituicaoCitada?.canais?.takeIf { it.isNotEmpty() }
                ?: AcoesDeCanal.canaisDoCatalogo(modeloCitado)
            CartaoCanaisOficiais(
                nomeInstituicao = instituicaoCitada?.nome ?: modeloCitado.nome,
                canais = canaisParaMostrar,
                cadastrada = instituicaoCitada != null,
                onAbrirCanal = onAbrirCanal,
                onGerenciar = { onAbrirInstituicoes(instituicaoCitada != null) }
            )
        }

        com.example.protege_idoso.CartaoPecaAjuda(
            contatos = contatos,
            onLigar = onLigar,
            onGerenciar = onAbrirContatos
        )

        TituloDeSecao(texto = "Você já conferiu esta mensagem?")

        if (analise.status != StatusAnalise.VERIFICADA) {
            BotaoPrincipal(
                texto = "Já verifiquei",
                onClick = { onMudarStatus(StatusAnalise.VERIFICADA) },
                icone = Icons.Filled.Check
            )
        }

        if (analise.status != StatusAnalise.FALSO_POSITIVO) {
            BotaoSecundario(
                texto = "Era uma mensagem segura",
                onClick = { onMudarStatus(StatusAnalise.FALSO_POSITIVO) },
                icone = Icons.Filled.CheckCircle
            )
        }

        if (analise.status != StatusAnalise.PENDENTE) {
            TextButton(
                onClick = { onMudarStatus(StatusAnalise.PENDENTE) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Marcar como não verificado", style = MaterialTheme.typography.labelLarge)
            }
        }

        BotaoSecundario(
            texto = "Instruções de segurança",
            onClick = onAbrirInstrucoes,
            icone = Icons.Filled.Info
        )

        TextButton(
            onClick = onExcluir,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Apagar este alerta",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SecaoTexto(titulo: String, texto: String) {
    CartaoBranco {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
