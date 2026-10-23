package com.example.protege_idoso

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.quiz.PerguntaQuiz
import com.example.protege_idoso.quiz.QuizRepository
import com.example.protege_idoso.quiz.RegrasDoQuiz
import com.example.protege_idoso.ui.BotaoPrincipal
import com.example.protege_idoso.ui.BotaoSecundario
import com.example.protege_idoso.ui.CabecalhoDeTela
import com.example.protege_idoso.ui.CartaoBranco
import com.example.protege_idoso.ui.CartaoColorido
import com.example.protege_idoso.ui.CartaoDeNumeros
import com.example.protege_idoso.ui.DialogoConfirmacao
import com.example.protege_idoso.ui.Estatistica
import com.example.protege_idoso.ui.IconeEmCirculo
import com.example.protege_idoso.ui.TituloDeSecao
import com.example.protege_idoso.ui.theme.ProtegeTema
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val repositorio = QuizRepository(applicationContext)

        setContent {
            Protege_IdosoTheme {
                QuizScreen(
                    repositorio = repositorio,
                    onVoltar = { finish() },
                    onAbrirInstrucoes = {
                        startActivity(Intent(this, InstrucoesActivity::class.java))
                    }
                )
            }
        }
    }

    companion object {
        fun criarIntent(context: Context): Intent = Intent(context, QuizActivity::class.java)
    }
}

enum class EtapaQuiz { BLOQUEADO, APRESENTACAO, PERGUNTA, RESPOSTA, RESULTADO }

private const val TEMPO_ESGOTADO = -1

private val Dourado = Color(0xFFF5B301)
private val LaranjaChama = Color(0xFFF57C00)

private const val CAMINHO_CHAMA =
    "M19.48,12.35c-1.57,-4.08 -7.16,-4.3 -5.81,-10.23 0.1,-0.44 -0.37,-0.78 -0.75,-0.55" +
            "C9.29,3.71 6.68,8 8.87,13.62c0.18,0.46 -0.36,0.89 -0.75,0.59 -1.81,-1.37 -2,-3.34 " +
            "-1.84,-4.75 0.06,-0.52 -0.62,-0.77 -0.91,-0.34C4.69,10.16 4,11.84 4,14.37" +
            "c0.38,5.6 5.11,7.32 6.81,7.54 2.43,0.31 5.06,-0.14 6.95,-1.87 2.08,-1.93 2.84,-5.01 " +
            "1.72,-7.69zM10.2,17.38c1.44,-0.35 2.18,-1.39 2.38,-2.31 0.33,-1.43 -0.96,-2.83 " +
            "-0.09,-5.09 0.33,1.87 3.27,3.04 3.27,5.08 0.08,2.53 -2.66,4.7 -5.56,2.32z"

private val IconeChama: ImageVector by lazy {
    ImageVector.Builder(
        name = "Chama",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    )
        .addPath(pathData = addPathNodes(CAMINHO_CHAMA), fill = SolidColor(Color.Black))
        .build()
}

private fun etapaInicial(repositorio: QuizRepository): EtapaQuiz = when {
    repositorio.rodadaEmAndamento -> EtapaQuiz.PERGUNTA
    repositorio.estaLiberado() -> EtapaQuiz.APRESENTACAO
    else -> EtapaQuiz.BLOQUEADO
}

/** "hoje às 20:15", "amanhã às 08:00" ou "25/10 às 08:00". */
internal fun descreverLiberacao(momento: Long, agora: Long = System.currentTimeMillis()): String {
    val hora = SimpleDateFormat("HH:mm", Locale.forLanguageTag("pt-BR")).format(Date(momento))
    val dia = Calendar.getInstance().apply { timeInMillis = momento }
    val hoje = Calendar.getInstance().apply { timeInMillis = agora }
    val mesmoAno = dia.get(Calendar.YEAR) == hoje.get(Calendar.YEAR)
    val diferencaDeDias = dia.get(Calendar.DAY_OF_YEAR) - hoje.get(Calendar.DAY_OF_YEAR)
    return when {
        mesmoAno && diferencaDeDias == 0 -> "hoje às $hora"
        mesmoAno && diferencaDeDias == 1 -> "amanhã às $hora"
        else -> SimpleDateFormat("dd/MM 'às' HH:mm", Locale.forLanguageTag("pt-BR")).format(Date(momento))
    }
}

/** Relógio que atualiza a cada segundo, sempre pelo horário real do celular. */
@Composable
internal fun relogioPorSegundo(): Long {
    var agora by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L - System.currentTimeMillis() % 1000L)
            agora = System.currentTimeMillis()
        }
    }
    return agora
}

@Composable
fun QuizScreen(
    repositorio: QuizRepository,
    onVoltar: () -> Unit,
    onAbrirInstrucoes: () -> Unit
) {
    val haptico = LocalHapticFeedback.current
    val agora = relogioPorSegundo()

    var etapa by rememberSaveable { mutableStateOf(etapaInicial(repositorio)) }
    var rodada by rememberSaveable { mutableIntStateOf(repositorio.rodadasConcluidas) }
    var indice by rememberSaveable { mutableIntStateOf(repositorio.indicePerguntaAtual) }
    var inicioDaPergunta by rememberSaveable { mutableLongStateOf(repositorio.inicioPerguntaAtual) }
    var escolha by rememberSaveable { mutableIntStateOf(TEMPO_ESGOTADO) }
    var pontosGanhos by rememberSaveable { mutableIntStateOf(0) }
    var acertosDaRodada by rememberSaveable { mutableIntStateOf(repositorio.acertosDaRodada) }
    var pontosDaRodada by rememberSaveable { mutableIntStateOf(repositorio.pontosDaRodada) }
    var sequenciaDaRodada by rememberSaveable { mutableIntStateOf(repositorio.sequenciaVigente()) }
    var bonusDaRodada by rememberSaveable { mutableIntStateOf(0) }
    var confirmarSaida by remember { mutableStateOf(false) }
    val rolagem = rememberScrollState()

    val perguntas = remember(rodada) { RegrasDoQuiz.perguntasDaRodada(rodada) }
    val perguntaAtual = perguntas[indice.coerceIn(0, perguntas.lastIndex)]
    val tempoRestante = if (inicioDaPergunta <= 0L) {
        RegrasDoQuiz.TEMPO_POR_PERGUNTA_MS
    } else {
        RegrasDoQuiz.tempoRestanteDaPergunta(inicioDaPergunta, agora)
    }

    fun responder(alternativa: Int?) {
        if (etapa != EtapaQuiz.PERGUNTA) return
        val acertou = alternativa == perguntaAtual.indiceCorreto
        val resultado = repositorio.registrarResposta(acertou, System.currentTimeMillis())
        escolha = alternativa ?: TEMPO_ESGOTADO
        pontosGanhos = resultado.pontos
        acertosDaRodada = resultado.acertosDaRodada
        pontosDaRodada = resultado.pontosDaRodada
        sequenciaDaRodada = resultado.sequencia
        bonusDaRodada = resultado.bonusDeSequencia
        if (!acertou) haptico.performHapticFeedback(HapticFeedbackType.LongPress)
        etapa = EtapaQuiz.RESPOSTA
    }

    fun comecar() {
        val momento = System.currentTimeMillis()
        rodada = repositorio.rodadasConcluidas
        repositorio.iniciarRodada(momento)
        indice = 0
        inicioDaPergunta = momento
        acertosDaRodada = 0
        pontosDaRodada = 0
        bonusDaRodada = 0
        etapa = EtapaQuiz.PERGUNTA
    }

    fun avancar() {
        if (etapa != EtapaQuiz.RESPOSTA) return
        if (indice + 1 >= RegrasDoQuiz.PERGUNTAS_POR_RODADA) {
            etapa = EtapaQuiz.RESULTADO
        } else {
            indice += 1
            inicioDaPergunta = 0L
            etapa = EtapaQuiz.PERGUNTA
        }
    }

    LaunchedEffect(etapa, indice) {
        if (etapa == EtapaQuiz.PERGUNTA && inicioDaPergunta <= 0L) {
            repositorio.iniciarPerguntaAtual(System.currentTimeMillis())
            inicioDaPergunta = repositorio.inicioPerguntaAtual
        }
    }

    LaunchedEffect(etapa, tempoRestante) {
        if (etapa == EtapaQuiz.PERGUNTA && inicioDaPergunta > 0L && tempoRestante <= 0L) {
            responder(null)
        }
    }

    LaunchedEffect(etapa, agora) {
        if (etapa == EtapaQuiz.BLOQUEADO && repositorio.estaLiberado(agora)) {
            etapa = EtapaQuiz.APRESENTACAO
        }
    }

    LaunchedEffect(etapa, indice) {
        rolagem.scrollTo(0)
    }

    BackHandler(enabled = etapa == EtapaQuiz.PERGUNTA) {
        confirmarSaida = true
    }

    if (confirmarSaida) {
        DialogoConfirmacao(
            titulo = "Sair do desafio?",
            texto = "Você pode voltar depois, mas o cronômetro desta pergunta continua contando.",
            rotuloConfirmar = "Sair",
            onConfirmar = {
                confirmarSaida = false
                onVoltar()
            },
            onCancelar = { confirmarSaida = false },
            acaoPerigosa = false
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CabecalhoDeTela(
                titulo = "Desafio do dia",
                subtitulo = when (etapa) {
                    EtapaQuiz.PERGUNTA, EtapaQuiz.RESPOSTA ->
                        "Pergunta ${indice + 1} de ${RegrasDoQuiz.PERGUNTAS_POR_RODADA}"
                    EtapaQuiz.RESULTADO -> "Desafio concluído"
                    else -> "Treine para reconhecer golpes"
                },
                icone = rememberVectorPainter(Icons.Filled.Star),
                onVoltar = {
                    if (etapa == EtapaQuiz.PERGUNTA) confirmarSaida = true else onVoltar()
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rolagem)
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (etapa) {
                    EtapaQuiz.BLOQUEADO -> TelaDeEspera(
                        repositorio = repositorio,
                        agora = agora,
                        onVoltar = onVoltar,
                        onAbrirInstrucoes = onAbrirInstrucoes
                    )

                    EtapaQuiz.APRESENTACAO -> TelaDeApresentacao(
                        repositorio = repositorio,
                        agora = agora,
                        onComecar = { comecar() }
                    )

                    EtapaQuiz.PERGUNTA -> TelaDePergunta(
                        pergunta = perguntaAtual,
                        indice = indice,
                        tempoRestante = tempoRestante,
                        pontosDaRodada = pontosDaRodada,
                        onResponder = { responder(it) }
                    )

                    EtapaQuiz.RESPOSTA -> TelaDeResposta(
                        pergunta = perguntaAtual,
                        escolha = escolha,
                        pontosGanhos = pontosGanhos,
                        ultimaPergunta = indice + 1 >= RegrasDoQuiz.PERGUNTAS_POR_RODADA,
                        onAvancar = { avancar() }
                    )

                    EtapaQuiz.RESULTADO -> TelaDeResultado(
                        repositorio = repositorio,
                        agora = agora,
                        acertos = acertosDaRodada,
                        pontos = pontosDaRodada,
                        sequencia = sequenciaDaRodada,
                        bonusDeSequencia = bonusDaRodada,
                        onComecar = { comecar() },
                        onVoltar = onVoltar,
                        onAbrirInstrucoes = onAbrirInstrucoes
                    )
                }
            }
        }
    }
}

@Composable
private fun TelaDeApresentacao(
    repositorio: QuizRepository,
    agora: Long,
    onComecar: () -> Unit
) {
    CartaoBranco {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconeEmCirculo(
                icone = Icons.Filled.Star,
                cor = Dourado,
                fundo = ProtegeTema.cores.alertaFundo,
                tamanho = 88.dp
            )
            Text(
                text = "O desafio de hoje está pronto!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = "Veja algumas situações do dia a dia e escolha o que você faria. " +
                        "Cada acerto vale pontos, e quem responde mais rápido ganha um bônus.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }

    CartaoBranco {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LinhaDeRegra(Icons.Filled.Info, "${RegrasDoQuiz.PERGUNTAS_POR_RODADA} perguntas por dia")
            LinhaDeRegra(Icons.Filled.CheckCircle, "2 alternativas em cada pergunta")
            LinhaDeRegra(Icons.Filled.Warning, "4 minutos para responder cada uma")
            LinhaDeRegra(Icons.Filled.DateRange, "Um novo desafio a cada 24 horas")
        }
    }

    if (repositorio.rodadasConcluidas > 0) {
        CartaoDeSequencia(repositorio = repositorio, agora = agora)
        CartaoDePlacar(repositorio = repositorio)
    }

    BotaoPrincipal(
        texto = if (repositorio.rodadasConcluidas > 0) "Começar o desafio de hoje" else "Começar o desafio",
        onClick = onComecar,
        icone = Icons.Filled.PlayArrow
    )
}

@Composable
private fun LinhaDeRegra(icone: ImageVector, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconeEmCirculo(icone = icone, tamanho = 40.dp)
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CartaoDePlacar(repositorio: QuizRepository) {
    TituloDeSecao(texto = "Seu placar: ${RegrasDoQuiz.tituloDoJogador(repositorio.acertosTotais)}")
    CartaoDeNumeros(
        itens = listOf(
            Estatistica(repositorio.rodadasConcluidas.toString(), "desafios feitos"),
            Estatistica(repositorio.acertosTotais.toString(), "acertos"),
            Estatistica(repositorio.pontosTotais.toString(), "pontos")
        )
    )
}

@Composable
private fun TelaDePergunta(
    pergunta: PerguntaQuiz,
    indice: Int,
    tempoRestante: Long,
    pontosDaRodada: Int,
    onResponder: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IndicadorDeProgresso(atual = indice, modifier = Modifier.weight(1f))
        Surface(
            shape = RoundedCornerShape(50),
            color = ProtegeTema.cores.alertaFundo,
            contentColor = ProtegeTema.cores.alertaTexto
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Dourado,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = " $pontosDaRodada pontos",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }

    Cronometro(tempoRestante = tempoRestante)

    CartaoBranco {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "A situação",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = pergunta.cenario,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    TituloDeSecao(texto = "O que você faz?")

    pergunta.alternativas.forEachIndexed { posicao, texto ->
        BotaoAlternativa(
            letra = ('A' + posicao).toString(),
            texto = texto,
            onClick = { onResponder(posicao) }
        )
    }
}

@Composable
private fun IndicadorDeProgresso(atual: Int, modifier: Modifier = Modifier) {
    val descricao = "Pergunta ${atual + 1} de ${RegrasDoQuiz.PERGUNTAS_POR_RODADA}"
    Row(
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = descricao },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(RegrasDoQuiz.PERGUNTAS_POR_RODADA) { posicao ->
            val cor = when {
                posicao < atual -> MaterialTheme.colorScheme.primary
                posicao == atual -> Dourado
                else -> MaterialTheme.colorScheme.outline
            }
            Box(
                modifier = Modifier
                    .size(if (posicao == atual) 18.dp else 14.dp)
                    .clip(CircleShape)
                    .background(cor)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun Cronometro(tempoRestante: Long) {
    val cores = ProtegeTema.cores
    val fracao = tempoRestante.toFloat() / RegrasDoQuiz.TEMPO_POR_PERGUNTA_MS
    val cor = when {
        tempoRestante <= 30_000L -> cores.perigoTexto
        tempoRestante <= 60_000L -> cores.alertaTexto
        else -> MaterialTheme.colorScheme.primary
    }
    val descricao = "Faltam ${RegrasDoQuiz.descreverTempo(tempoRestante)} para responder"

    CartaoBranco {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .semantics(mergeDescendants = true) { contentDescription = descricao },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { fracao },
                    modifier = Modifier.size(84.dp),
                    color = cor,
                    trackColor = MaterialTheme.colorScheme.outline,
                    strokeWidth = 8.dp
                )
                Text(
                    text = RegrasDoQuiz.formatarCronometro(tempoRestante),
                    style = MaterialTheme.typography.titleLarge,
                    color = cor
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Tempo para responder",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (tempoRestante <= 60_000L) {
                        "Falta pouco! Escolha uma alternativa."
                    } else {
                        "Leia com calma. Responder rápido vale bônus."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BotaoAlternativa(
    letra: String,
    texto: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        shadowElevation = if (ProtegeTema.cores.escuro) 0.dp else 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letra,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TelaDeResposta(
    pergunta: PerguntaQuiz,
    escolha: Int,
    pontosGanhos: Int,
    ultimaPergunta: Boolean,
    onAvancar: () -> Unit
) {
    val cores = ProtegeTema.cores
    val acertou = escolha == pergunta.indiceCorreto
    val tempoEsgotado = escolha == TEMPO_ESGOTADO

    val escala = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    CartaoColorido(
        fundo = if (acertou) cores.seguroFundo else cores.perigoFundo,
        texto = if (acertou) cores.seguroTexto else cores.perigoTexto
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = if (acertou) Icons.Filled.CheckCircle else Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        scaleX = escala.value
                        scaleY = escala.value
                    }
            )
            Text(
                text = when {
                    acertou -> "Muito bem! Você acertou!"
                    tempoEsgotado -> "O tempo acabou!"
                    else -> "Quase! Essa era uma armadilha."
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = if (acertou) "+$pontosGanhos pontos" else "Desta vez não valeu pontos.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }

    if (!acertou) {
        CartaoBranco {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "O mais seguro é",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    text = pergunta.respostaCorreta,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    CartaoBranco {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Por quê?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = pergunta.explicacao,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    BotaoPrincipal(
        texto = if (ultimaPergunta) "Ver meu resultado" else "Próxima pergunta",
        onClick = onAvancar,
        icone = if (ultimaPergunta) Icons.Filled.Star else Icons.AutoMirrored.Filled.ArrowForward
    )
}

@Composable
private fun TelaDeResultado(
    repositorio: QuizRepository,
    agora: Long,
    acertos: Int,
    pontos: Int,
    sequencia: Int,
    bonusDeSequencia: Int,
    onComecar: () -> Unit,
    onVoltar: () -> Unit,
    onAbrirInstrucoes: () -> Unit
) {
    val total = RegrasDoQuiz.PERGUNTAS_POR_RODADA

    CartaoBranco {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Estrelas(acertos = acertos, total = total)
            Text(
                text = when {
                    acertos == total -> "Perfeito! Nenhum golpe passa por você!"
                    acertos >= total - 1 -> "Muito bom! Você está atento."
                    acertos >= total / 2 -> "Bom trabalho! Continue treinando."
                    else -> "Não desanime! Amanhã tem mais."
                },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = "Você acertou $acertos de $total e ganhou $pontos pontos.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }

    CartaoDeSequenciaGanha(sequencia = sequencia, bonus = bonusDeSequencia)

    CartaoDeContagem(
        proximaLiberacao = repositorio.proximaLiberacao(),
        agora = agora,
        onComecar = onComecar
    )

    CartaoDeSequencia(repositorio = repositorio, agora = agora)

    CartaoDePlacar(repositorio = repositorio)

    BotaoSecundario(
        texto = "Ver dicas de segurança",
        onClick = onAbrirInstrucoes,
        icone = Icons.Filled.Info
    )
    BotaoSecundario(
        texto = "Voltar ao início",
        onClick = onVoltar,
        icone = Icons.Filled.Home
    )
}

@Composable
private fun Estrelas(acertos: Int, total: Int) {
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "$acertos de $total estrelas"
        },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(total) { posicao ->
            val ganhou = posicao < acertos
            val escala = remember { Animatable(if (ganhou) 0f else 1f) }
            LaunchedEffect(ganhou) {
                if (ganhou) {
                    delay(300L * posicao)
                    escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
            }
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (ganhou) Dourado else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(56.dp)
                    .graphicsLayer {
                        scaleX = escala.value
                        scaleY = escala.value
                    }
            )
        }
    }
}

@Composable
private fun TelaDeEspera(
    repositorio: QuizRepository,
    agora: Long,
    onVoltar: () -> Unit,
    onAbrirInstrucoes: () -> Unit
) {
    CartaoDeContagem(
        proximaLiberacao = repositorio.proximaLiberacao(),
        agora = agora,
        onComecar = null
    )

    if (repositorio.rodadasConcluidas > 0) {
        CartaoDeSequencia(repositorio = repositorio, agora = agora)
        CartaoDePlacar(repositorio = repositorio)
    }

    BotaoSecundario(
        texto = "Ver dicas de segurança",
        onClick = onAbrirInstrucoes,
        icone = Icons.Filled.Info
    )
    BotaoSecundario(
        texto = "Voltar ao início",
        onClick = onVoltar,
        icone = Icons.Filled.Home
    )
}

@Composable
private fun CartaoDeContagem(
    proximaLiberacao: Long,
    agora: Long,
    onComecar: (() -> Unit)?
) {
    val liberado = agora >= proximaLiberacao
    val restante = proximaLiberacao - agora

    CartaoBranco {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconeEmCirculo(
                icone = if (liberado) Icons.Filled.Star else Icons.Filled.Lock,
                cor = if (liberado) Dourado else MaterialTheme.colorScheme.primary,
                tamanho = 64.dp
            )
            if (liberado) {
                Text(
                    text = "Um novo desafio já está liberado!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                if (onComecar != null) {
                    BotaoPrincipal(
                        texto = "Começar agora",
                        onClick = onComecar,
                        icone = Icons.Filled.PlayArrow
                    )
                }
            } else {
                Text(
                    text = "Próximo desafio em",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = RegrasDoQuiz.formatarContagem(restante),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics {
                        contentDescription = "Próximo desafio ${descreverLiberacao(proximaLiberacao, agora)}"
                    }
                )
                Text(
                    text = "Libera ${descreverLiberacao(proximaLiberacao, agora)}. " +
                            "Volte para ganhar mais pontos!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Atalho da tela inicial: mostra se o desafio está liberado ou quanto falta. */
@Composable
fun CartaoDesafioDoDia(
    liberadoEm: Long,
    emAndamento: Boolean,
    onClick: () -> Unit
) {
    val agora = relogioPorSegundo()
    val liberado = emAndamento || agora >= liberadoEm
    val context = LocalContext.current
    val sequencia = remember(liberadoEm, emAndamento, agora / 60_000L) {
        QuizRepository(context.applicationContext).sequenciaVigente(agora)
    }

    CartaoBranco(onClick = onClick) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconeEmCirculo(
                icone = if (liberado) Icons.Filled.Star else Icons.Filled.Lock,
                cor = if (liberado) Dourado else MaterialTheme.colorScheme.primary,
                fundo = if (liberado) ProtegeTema.cores.alertaFundo else MaterialTheme.colorScheme.primaryContainer,
                tamanho = 56.dp
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Desafio do dia",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when {
                        emAndamento -> "Você tem um desafio pela metade. Toque para continuar."
                        liberado -> "Novo desafio liberado! Teste se você reconhece golpes."
                        else -> "Próximo desafio em ${RegrasDoQuiz.formatarContagem(liberadoEm - agora)}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (sequencia > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = IconeChama,
                            contentDescription = null,
                            tint = LaranjaChama,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = textoDeDias(sequencia) + " seguidos",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun textoDeDias(dias: Int): String = if (dias == 1) "1 dia" else "$dias dias"

private fun abreviarDia(dia: DayOfWeek): String = when (dia) {
    DayOfWeek.MONDAY -> "seg"
    DayOfWeek.TUESDAY -> "ter"
    DayOfWeek.WEDNESDAY -> "qua"
    DayOfWeek.THURSDAY -> "qui"
    DayOfWeek.FRIDAY -> "sex"
    DayOfWeek.SATURDAY -> "sáb"
    DayOfWeek.SUNDAY -> "dom"
}

/** Mostra a sequência de dias seguidos, o recorde, os últimos 7 dias e a próxima conquista. */
@Composable
private fun CartaoDeSequencia(repositorio: QuizRepository, agora: Long) {
    val cores = ProtegeTema.cores
    val sequencia = repositorio.sequenciaVigente(agora)
    val recorde = repositorio.melhorSequencia
    val diasJogados = repositorio.diasJogados
    val hoje = LocalDate.now().toEpochDay()
    val proximoMarco = RegrasDoQuiz.proximoMarco(sequencia)
    val desafioLiberado = repositorio.estaLiberado(agora)
    val prazo = RegrasDoQuiz.prazoDaSequencia(repositorio.ultimaConclusao)

    CartaoBranco {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (sequencia > 0) {
                                Brush.verticalGradient(listOf(Dourado, LaranjaChama))
                            } else {
                                SolidColor(MaterialTheme.colorScheme.primaryContainer)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = IconeChama,
                        contentDescription = null,
                        tint = if (sequencia > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(38.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .semantics(mergeDescendants = true) {},
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = if (sequencia > 0) "${textoDeDias(sequencia)} seguidos" else "Sem sequência ainda",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text(
                        text = "Seu recorde: ${textoDeDias(recorde)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                for (diasAtras in 6 downTo 0) {
                    val dia = hoje - diasAtras
                    val jogou = dia in diasJogados
                    val ehHoje = diasAtras == 0
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .semantics(mergeDescendants = true) {
                                contentDescription = (if (ehHoje) "Hoje" else abreviarDia(LocalDate.ofEpochDay(dia).dayOfWeek)) +
                                        if (jogou) ", jogou" else ", não jogou"
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (ehHoje) "hoje" else abreviarDia(LocalDate.ofEpochDay(dia).dayOfWeek),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (ehHoje) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (jogou) LaranjaChama else MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (jogou) {
                                Icon(
                                    imageVector = IconeChama,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            val aviso = when {
                sequencia > 0 && desafioLiberado ->
                    "Jogue o desafio de hoje até ${descreverLiberacao(prazo, agora)} para não perder a sequência!"
                sequencia > 0 ->
                    "Volte quando o próximo desafio liberar para chegar a ${textoDeDias(sequencia + 1)} seguidos."
                recorde > 0 ->
                    "A sequência recomeçou. Jogue todo dia para bater o seu recorde!"
                else ->
                    "Jogue todos os dias para criar uma sequência e ganhar pontos extras."
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (sequencia > 0 && desafioLiberado) cores.alertaFundo else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (sequencia > 0 && desafioLiberado) cores.alertaTexto else MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(
                    text = aviso,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }

            if (proximoMarco != null) {
                val faltam = proximoMarco - sequencia
                val anterior = RegrasDoQuiz.MARCOS_DE_SEQUENCIA.lastOrNull { it <= sequencia } ?: 0
                val progresso = (sequencia - anterior).toFloat() / (proximoMarco - anterior)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Próxima conquista: $proximoMarco dias " +
                                "(${if (faltam == 1) "falta 1 dia" else "faltam $faltam dias"})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.outline)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progresso.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(50))
                                .background(LaranjaChama)
                        )
                    }
                }
            }
        }
    }
}

/** Comemoração no fim do desafio: dias seguidos, bônus ganho e conquista nova. */
@Composable
private fun CartaoDeSequenciaGanha(sequencia: Int, bonus: Int) {
    if (sequencia <= 0) return
    val cores = ProtegeTema.cores
    val novoMarco = RegrasDoQuiz.ehMarco(sequencia)

    val escala = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        delay(1200L)
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    CartaoColorido(fundo = cores.alertaFundo, texto = cores.alertaTexto) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = IconeChama,
                contentDescription = null,
                tint = LaranjaChama,
                modifier = Modifier
                    .size(56.dp)
                    .graphicsLayer {
                        scaleX = escala.value
                        scaleY = escala.value
                    }
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (novoMarco) "Conquista: ${RegrasDoQuiz.nomeDoMarco(sequencia)}" else
                        if (sequencia == 1) "Sequência começou: 1 dia!" else "${textoDeDias(sequencia)} seguidos!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    text = if (bonus > 0) {
                        "Você ganhou +$bonus pontos de bônus por jogar dias seguidos."
                    } else {
                        "Jogue amanhã de novo para começar a ganhar pontos extras."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
