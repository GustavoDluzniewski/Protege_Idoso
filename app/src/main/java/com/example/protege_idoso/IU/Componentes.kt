package com.example.protege_idoso.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.ui.theme.AzulClaro
import com.example.protege_idoso.ui.theme.AzulGelo
import com.example.protege_idoso.ui.theme.AzulPrincipal
import com.example.protege_idoso.ui.theme.ProtegeTema


@Composable
fun gradienteDoCabecalho(): Brush {
    val cores = ProtegeTema.cores
    return Brush.verticalGradient(listOf(cores.gradienteTopo, cores.gradienteBase))
}

@Composable
fun OndaInferior(
    modifier: Modifier = Modifier,
    cor: Color = MaterialTheme.colorScheme.background,
    altura: Dp = 30.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(altura)
    ) {
        val w = size.width
        val h = size.height

        val ondaDeTras = Path().apply {
            moveTo(0f, h * 0.55f)
            cubicTo(w * 0.22f, h * 0.05f, w * 0.48f, h * 0.95f, w * 0.72f, h * 0.45f)
            cubicTo(w * 0.86f, h * 0.18f, w * 0.94f, h * 0.22f, w, h * 0.32f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(ondaDeTras, cor.copy(alpha = 0.45f))

        val ondaDaFrente = Path().apply {
            moveTo(0f, h * 0.82f)
            cubicTo(w * 0.30f, h * 0.30f, w * 0.62f, h * 1.05f, w, h * 0.52f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(ondaDaFrente, cor)
    }
}

@Composable
fun BotaoVoltar(
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cores = ProtegeTema.cores
    Surface(
        onClick = onVoltar,
        modifier = modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.18f),
        contentColor = cores.sobreGradiente
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 18.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Voltar", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun CabecalhoDeTela(
    titulo: String,
    subtitulo: String? = null,
    icone: Painter? = null,
    onVoltar: (() -> Unit)? = null,
    conteudoExtra: @Composable ColumnScope.() -> Unit = {}
) {
    val cores = ProtegeTema.cores

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradienteDoCabecalho())
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (onVoltar != null) {
                BotaoVoltar(onVoltar = onVoltar)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (icone != null) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = icone,
                            contentDescription = null,
                            tint = cores.sobreGradiente,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.headlineSmall,
                        color = cores.sobreGradiente,
                        modifier = Modifier.semantics { heading() }
                    )
                    if (subtitulo != null) {
                        Text(
                            text = subtitulo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = cores.sobreGradiente
                        )
                    }
                }
            }

            conteudoExtra()
        }

        OndaInferior(modifier = Modifier.padding(top = 6.dp))
    }
}


private const val CAMINHO_ESCUDO =
    "M12,1L3,5v6c0,5.55 3.84,10.74 9,12 5.16,-1.26 9,-6.45 9,-12L21,5l-9,-4z"
private const val CAMINHO_METADE_ESQUERDA =
    "M12,1L3,5v6c0,5.55 3.84,10.74 9,12z"
private const val CAMINHO_CONFERIDO =
    "M10,17l-4,-4 1.41,-1.41L10,14.17l6.59,-6.59L18,9l-8,8z"
private const val CAMINHO_EXCLAMACAO =
    "M11,6h2v8h-2zM11,16h2v2h-2z"

private fun criarEscudo(ativo: Boolean): ImageVector {
    val topo = if (ativo) AzulClaro else Color(0xFFF6A93B)
    val base = if (ativo) AzulPrincipal else Color(0xFFB45309)

    return ImageVector.Builder(
        name = if (ativo) "EscudoAtivo" else "EscudoInativo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    )
        .addPath(
            pathData = addPathNodes(CAMINHO_ESCUDO),
            fill = Brush.linearGradient(
                colors = listOf(topo, base),
                start = Offset(12f, 1f),
                end = Offset(12f, 23f)
            )
        )
        .addPath(
            pathData = addPathNodes(CAMINHO_METADE_ESQUERDA),
            fill = SolidColor(Color.White),
            fillAlpha = 0.16f
        )
        .addPath(
            pathData = addPathNodes(if (ativo) CAMINHO_CONFERIDO else CAMINHO_EXCLAMACAO),
            fill = SolidColor(Color.White)
        )
        .build()
}

@Composable
private fun escalaPulsando(): Float {
    val transicao = rememberInfiniteTransition(label = "pulso")
    val escala by transicao.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )
    return escala
}

@Composable
fun EscudoDeProtecao(
    ativo: Boolean,
    modifier: Modifier = Modifier,
    tamanho: Dp = 220.dp
) {
    val escudo = remember(ativo) { criarEscudo(ativo) }
    val escala = if (ativo) escalaPulsando() else 1f
    val cores = ProtegeTema.cores

    Box(
        modifier = modifier.size(tamanho),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = escala
                    scaleY = escala
                }
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.10f))
        )
        Box(
            modifier = Modifier
                .fillMaxSize(0.84f)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f))
        )
        Box(
            modifier = Modifier
                .fillMaxSize(0.68f)
                .shadow(
                    elevation = 18.dp,
                    shape = CircleShape,
                    ambientColor = cores.sombra.copy(alpha = 0.35f),
                    spotColor = cores.sombra.copy(alpha = 0.45f)
                )
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color.White, AzulGelo))),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberVectorPainter(escudo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.62f)
            )
        }
    }
}


@Composable
fun CartaoBranco(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    conteudo: @Composable ColumnScope.() -> Unit
) {
    val cores = ProtegeTema.cores
    val forma = RoundedCornerShape(24.dp)
    val coresDoCartao = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
    val elevacao = CardDefaults.cardElevation(defaultElevation = if (cores.escuro) 0.dp else 3.dp)
    val borda = if (cores.escuro) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = forma,
            colors = coresDoCartao,
            elevation = elevacao,
            border = borda
        ) {
            Column(content = conteudo)
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = forma,
            colors = coresDoCartao,
            elevation = elevacao,
            border = borda
        ) {
            Column(content = conteudo)
        }
    }
}

@Composable
fun CartaoColorido(
    fundo: Color,
    texto: Color,
    modifier: Modifier = Modifier,
    conteudo: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = fundo, contentColor = texto)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = conteudo
        )
    }
}

@Composable
fun DivisorSuave(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
fun TituloDeSecao(
    texto: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .padding(top = 4.dp)
            .semantics { heading() }
    )
}

@Composable
fun IconeEmCirculo(
    icone: ImageVector,
    modifier: Modifier = Modifier,
    cor: Color = MaterialTheme.colorScheme.primary,
    fundo: Color = MaterialTheme.colorScheme.primaryContainer,
    tamanho: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(tamanho)
            .clip(CircleShape)
            .background(fundo),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = cor,
            modifier = Modifier.size(tamanho * 0.55f)
        )
    }
}


@Composable
fun BotaoPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icone: ImageVector? = null,
    corFundo: Color = MaterialTheme.colorScheme.primary,
    corTexto: Color = MaterialTheme.colorScheme.onPrimary,
    habilitado: Boolean = true,
    alturaMinima: Dp = 60.dp
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = alturaMinima),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = corFundo,
            contentColor = corTexto
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        if (icone != null) {
            Icon(imageVector = icone, contentDescription = null, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(text = texto, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}

@Composable
fun BotaoSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icone: ImageVector? = null,
    cor: Color = MaterialTheme.colorScheme.primary,
    alturaMinima: Dp = 56.dp
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = alturaMinima),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = cor),
        border = BorderStroke(1.5.dp, cor),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        if (icone != null) {
            Icon(imageVector = icone, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(text = texto, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}

@Composable
fun BotaoPilula(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    corFundo: Color = MaterialTheme.colorScheme.primary,
    corTexto: Color = MaterialTheme.colorScheme.onPrimary
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = corFundo, contentColor = corTexto),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(text = texto, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun BotaoCircular(
    rotulo: String,
    icone: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    corIcone: Color = MaterialTheme.colorScheme.primary,
    contador: Int = 0,
    descricaoContador: String? = null
) {
    val cores = ProtegeTema.cores

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                if (descricaoContador != null && contador > 0) {
                    stateDescription = descricaoContador
                }
            }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(
                        elevation = if (cores.escuro) 0.dp else 8.dp,
                        shape = CircleShape,
                        ambientColor = cores.sombra.copy(alpha = 0.25f),
                        spotColor = cores.sombra.copy(alpha = 0.35f)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icone,
                    contentDescription = null,
                    tint = corIcone,
                    modifier = Modifier.size(32.dp)
                )
            }

            if (contador > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-4).dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(cores.botaoPerigo),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (contador > 9) "9+" else contador.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = cores.sobreBotaoPerigo
                    )
                }
            }
        }

        Text(
            text = rotulo,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 3
        )
    }
}


@Composable
fun ItemDeSugestao(
    icone: ImageVector,
    titulo: String,
    descricao: String,
    rotuloBotao: String,
    onClick: () -> Unit,
    corIcone: Color = MaterialTheme.colorScheme.primary,
    fundoIcone: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconeEmCirculo(icone = icone, cor = corIcone, fundo = fundoIcone)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
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

        BotaoPilula(texto = rotuloBotao, onClick = onClick)
    }
}

data class Estatistica(
    val valor: String,
    val rotulo: String
)

@Composable
fun CartaoDeNumeros(
    itens: List<Estatistica>,
    modifier: Modifier = Modifier
) {
    CartaoBranco(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(vertical = 18.dp)
        ) {
            itens.forEachIndexed { indice, item ->
                if (indice > 0) {
                    VerticalDivider(color = MaterialTheme.colorScheme.outline)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                        .semantics(mergeDescendants = true) {},
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = item.valor,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = item.rotulo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun DialogoConfirmacao(
    titulo: String,
    texto: String,
    rotuloConfirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    acaoPerigosa: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(text = titulo, style = MaterialTheme.typography.titleLarge) },
        text = { Text(text = texto, style = MaterialTheme.typography.bodyLarge) },
        confirmButton = {
            TextButton(onClick = onConfirmar, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(
                    text = rotuloConfirmar,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (acaoPerigosa) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(text = "Cancelar", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}
