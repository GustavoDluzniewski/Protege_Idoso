package com.example.protege_idoso

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.contatos.Telefone
import com.example.protege_idoso.instituicoes.AcoesDeCanal
import com.example.protege_idoso.instituicoes.CanalOficial
import com.example.protege_idoso.instituicoes.CatalogoInstituicoes
import com.example.protege_idoso.instituicoes.ErrosDoCanal
import com.example.protege_idoso.instituicoes.Instituicao
import com.example.protege_idoso.instituicoes.InstituicoesRepository
import com.example.protege_idoso.instituicoes.ResultadoInstituicao
import com.example.protege_idoso.instituicoes.TipoCanal
import com.example.protege_idoso.instituicoes.ValidacaoInstituicao
import com.example.protege_idoso.ui.BotaoPilula
import com.example.protege_idoso.ui.BotaoPrincipal
import com.example.protege_idoso.ui.BotaoSecundario
import com.example.protege_idoso.ui.CabecalhoDeTela
import com.example.protege_idoso.ui.CartaoBranco
import com.example.protege_idoso.ui.CartaoColorido
import com.example.protege_idoso.ui.DialogoConfirmacao
import com.example.protege_idoso.ui.DivisorSuave
import com.example.protege_idoso.ui.IconeEmCirculo
import com.example.protege_idoso.ui.IconeInstituicao
import com.example.protege_idoso.ui.TituloDeSecao
import com.example.protege_idoso.ui.theme.AzulClaro
import com.example.protege_idoso.ui.theme.AzulPrincipal
import com.example.protege_idoso.ui.theme.ProtegeTema
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InstituicoesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val repositorio = InstituicoesRepository(applicationContext)
        val abrirEscolha = intent.getBooleanExtra(EXTRA_ABRIR_ESCOLHA, false)

        setContent {
            Protege_IdosoTheme {
                InstituicoesScreen(
                    repositorio = repositorio,
                    abrirEscolha = abrirEscolha,
                    onVoltar = { finish() }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_ABRIR_ESCOLHA = "abrir_escolha"

        fun criarIntent(context: Context, abrirEscolha: Boolean = false): Intent =
            Intent(context, InstituicoesActivity::class.java)
                .putExtra(EXTRA_ABRIR_ESCOLHA, abrirEscolha)
    }
}

private const val TELA_LISTA = "lista"
private const val TELA_ESCOLHA = "escolha"
private const val TELA_DETALHE = "detalhe"
private const val TELA_CANAL = "canal"
private const val TELA_NOME = "nome"

@Composable
fun InstituicoesScreen(
    repositorio: InstituicoesRepository,
    abrirEscolha: Boolean,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var instituicoes by remember { mutableStateOf<List<Instituicao>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    var tela by rememberSaveable { mutableStateOf(if (abrirEscolha) TELA_ESCOLHA else TELA_LISTA) }
    var instituicaoId by rememberSaveable { mutableStateOf(0L) }
    var canalId by rememberSaveable { mutableStateOf(0L) }

    var confirmarExclusaoInstituicao by remember { mutableStateOf<Instituicao?>(null) }
    var confirmarExclusaoCanal by remember { mutableStateOf<CanalOficial?>(null) }

    fun avisar(texto: String) {
        Toast.makeText(context, texto, Toast.LENGTH_LONG).show()
    }

    suspend fun carregar() {
        instituicoes = withContext(Dispatchers.IO) { repositorio.listar() }
        carregando = false
    }

    LaunchedEffect(Unit) { carregar() }

    val instituicaoAtual = instituicoes.firstOrNull { it.id == instituicaoId }
    val canalAtual = instituicaoAtual?.canais?.firstOrNull { it.id == canalId }

    fun voltarUmaTela() {
        tela = when (tela) {
            TELA_CANAL -> TELA_DETALHE
            TELA_NOME -> if (instituicaoId > 0) TELA_DETALHE else TELA_ESCOLHA
            else -> TELA_LISTA
        }
    }

    BackHandler(enabled = tela != TELA_LISTA) { voltarUmaTela() }

    confirmarExclusaoInstituicao?.let { alvo ->
        DialogoConfirmacao(
            titulo = "Remover ${alvo.nome}?",
            texto = "A instituição e todos os canais dela serão apagados deste celular.",
            rotuloConfirmar = "Remover",
            onConfirmar = {
                confirmarExclusaoInstituicao = null
                scope.launch {
                    withContext(Dispatchers.IO) { repositorio.excluirInstituicao(alvo.id) }
                    tela = TELA_LISTA
                    instituicaoId = 0L
                    carregar()
                    avisar("${alvo.nome} removido.")
                }
            },
            onCancelar = { confirmarExclusaoInstituicao = null }
        )
    }

    confirmarExclusaoCanal?.let { alvo ->
        DialogoConfirmacao(
            titulo = "Remover este canal?",
            texto = "\"${alvo.rotulo}\" (${alvo.valorFormatado}) será apagado.",
            rotuloConfirmar = "Remover",
            onConfirmar = {
                confirmarExclusaoCanal = null
                scope.launch {
                    withContext(Dispatchers.IO) { repositorio.excluirCanal(alvo.id) }
                    tela = TELA_DETALHE
                    carregar()
                    avisar("Canal removido.")
                }
            },
            onCancelar = { confirmarExclusaoCanal = null }
        )
    }

    val iconeInstituicao = rememberVectorPainter(IconeInstituicao)

    val (titulo, subtitulo) = when (tela) {
        TELA_ESCOLHA -> "Quais instituições você utiliza?" to
                "Toque nas que você usa. Os canais oficiais entram sozinhos."
        TELA_DETALHE -> (instituicaoAtual?.nome ?: "Instituição") to "Veja e organize os canais oficiais."
        TELA_CANAL -> (if (canalAtual == null) "Novo canal" else "Editar canal") to
                (instituicaoAtual?.nome ?: "")
        TELA_NOME -> (if (instituicaoId > 0) "Mudar nome" else "Outra instituição") to
                "Escreva o nome do banco, cooperativa ou órgão."
        else -> "Minhas instituições" to "Bancos e órgãos que você usa, com os canais oficiais."
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            CabecalhoDeTela(
                titulo = titulo,
                subtitulo = subtitulo,
                icone = iconeInstituicao,
                onVoltar = { if (tela == TELA_LISTA) onVoltar() else voltarUmaTela() }
            )

            when {
                carregando -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                tela == TELA_ESCOLHA -> EscolhaDeInstituicoes(
                    jaCadastradas = instituicoes.mapNotNull { it.codigo }.toSet(),
                    limiteAtingido = instituicoes.size >= ValidacaoInstituicao.LIMITE_DE_INSTITUICOES,
                    onOutra = {
                        instituicaoId = 0L
                        tela = TELA_NOME
                    },
                    onSalvar = { codigos ->
                        scope.launch {
                            val adicionadas = withContext(Dispatchers.IO) {
                                repositorio.adicionarDoCatalogo(codigos)
                            }
                            carregar()
                            tela = TELA_LISTA
                            avisar(
                                when (adicionadas) {
                                    0 -> "Nenhuma instituição nova foi adicionada."
                                    1 -> "1 instituição adicionada com os canais oficiais."
                                    else -> "$adicionadas instituições adicionadas com os canais oficiais."
                                }
                            )
                        }
                    }
                )

                (tela == TELA_DETALHE || tela == TELA_CANAL) && instituicaoAtual == null -> {
                    LaunchedEffect(tela, instituicaoId) { tela = TELA_LISTA }
                }

                tela == TELA_DETALHE && instituicaoAtual != null -> DetalheDaInstituicao(
                    instituicao = instituicaoAtual,
                    onAbrirCanal = { AcoesDeCanal.abrir(context, it) },
                    onAdicionarCanal = {
                        canalId = 0L
                        tela = TELA_CANAL
                    },
                    onEditarCanal = {
                        canalId = it.id
                        tela = TELA_CANAL
                    },
                    onRemoverCanal = { confirmarExclusaoCanal = it },
                    onRenomear = { tela = TELA_NOME },
                    onRestaurar = {
                        scope.launch {
                            val restaurados = withContext(Dispatchers.IO) {
                                repositorio.restaurarCanaisOficiais(instituicaoAtual.id)
                            }
                            carregar()
                            avisar(
                                if (restaurados == 0) {
                                    "Todos os canais oficiais já estavam na lista."
                                } else {
                                    "Canais oficiais restaurados."
                                }
                            )
                        }
                    },
                    onRemover = { confirmarExclusaoInstituicao = instituicaoAtual }
                )

                tela == TELA_CANAL && instituicaoAtual != null -> FormularioDeCanal(
                    canal = canalAtual,
                    onSalvar = { rotulo, tipo, valor, aoTerminar ->
                        scope.launch {
                            val resultado = withContext(Dispatchers.IO) {
                                if (canalAtual == null) {
                                    repositorio.adicionarCanal(instituicaoAtual.id, rotulo, tipo, valor)
                                } else {
                                    repositorio.atualizarCanal(canalAtual.id, rotulo, tipo, valor)
                                }
                            }
                            when (resultado) {
                                is ResultadoInstituicao.Sucesso -> {
                                    carregar()
                                    tela = TELA_DETALHE
                                    avisar(if (canalAtual == null) "Canal salvo!" else "Alterações salvas!")
                                }
                                is ResultadoInstituicao.CanalInvalido -> aoTerminar(resultado.erros)
                                ResultadoInstituicao.LimiteAtingido -> {
                                    tela = TELA_DETALHE
                                    avisar(
                                        "Cada instituição pode ter até " +
                                                "${ValidacaoInstituicao.LIMITE_DE_CANAIS} canais."
                                    )
                                }
                                else -> {
                                    carregar()
                                    tela = TELA_DETALHE
                                    avisar("Esse canal não existe mais.")
                                }
                            }
                        }
                    },
                    onCancelar = { tela = TELA_DETALHE },
                    onRemover = canalAtual?.let { alvo -> { confirmarExclusaoCanal = alvo } }
                )

                tela == TELA_NOME -> FormularioDeNome(
                    nomeAtual = if (instituicaoId > 0) instituicaoAtual?.nome.orEmpty() else "",
                    onSalvar = { nome, aoErrar ->
                        scope.launch {
                            val editando = instituicaoId > 0
                            val resultado = withContext(Dispatchers.IO) {
                                if (editando) {
                                    repositorio.renomear(instituicaoId, nome)
                                } else {
                                    repositorio.criarPersonalizada(nome)
                                }
                            }
                            when (resultado) {
                                is ResultadoInstituicao.Sucesso -> {
                                    instituicaoId = resultado.id
                                    carregar()
                                    tela = TELA_DETALHE
                                    avisar(
                                        if (editando) {
                                            "Nome alterado."
                                        } else {
                                            "Instituição criada. Agora adicione os canais dela."
                                        }
                                    )
                                }
                                is ResultadoInstituicao.NomeInvalido -> aoErrar(resultado.mensagem)
                                ResultadoInstituicao.JaCadastrada ->
                                    aoErrar("Já existe uma instituição com esse nome.")
                                ResultadoInstituicao.LimiteAtingido -> {
                                    tela = TELA_LISTA
                                    avisar(
                                        "Você chegou ao limite de " +
                                                "${ValidacaoInstituicao.LIMITE_DE_INSTITUICOES} instituições."
                                    )
                                }
                                else -> {
                                    carregar()
                                    tela = TELA_LISTA
                                }
                            }
                        }
                    },
                    onCancelar = { voltarUmaTela() }
                )

                else -> ListaDeInstituicoes(
                    instituicoes = instituicoes,
                    onAbrirCanal = { AcoesDeCanal.abrir(context, it) },
                    onGerenciar = {
                        instituicaoId = it.id
                        tela = TELA_DETALHE
                    },
                    onAdicionar = { tela = TELA_ESCOLHA }
                )
            }
        }
    }
}

@Composable
private fun AvatarDaInstituicao(
    nome: String,
    iniciais: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(AzulClaro, AzulPrincipal)))
            .semantics { contentDescription = nome },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciais,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}

@Composable
private fun BarraDeBaixo(conteudo: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            conteudo()
        }
    }
}

@Composable
private fun AvisoDeConferencia() {
    val cores = ProtegeTema.cores
    CartaoColorido(fundo = cores.alertaFundo, texto = cores.alertaTexto) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = "Os números foram conferidos nos sites oficiais em " +
                        "${CatalogoInstituicoes.DATA_DA_CONFERENCIA}. Eles podem mudar: " +
                        "confira também no verso do seu cartão ou no aplicativo do banco.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LinhaDeCanal(
    canal: CanalOficial,
    onAbrir: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .semantics(mergeDescendants = true) {
                    contentDescription = if (canal.tipo == TipoCanal.TELEFONE) {
                        "${canal.rotulo}, telefone ${Telefone.paraLeitorDeTela(canal.valor)}"
                    } else {
                        "${canal.rotulo}, ${canal.valor}"
                    }
                }
        ) {
            Text(
                text = canal.rotulo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (canal.tipo == TipoCanal.TELEFONE) {
                    canal.valorFormatado
                } else {
                    canal.valor.removePrefix("https://")
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        BotaoPilula(
            texto = if (canal.tipo == TipoCanal.TELEFONE) "Ligar" else "Abrir",
            onClick = onAbrir
        )
    }
}

@Composable
private fun ListaDeInstituicoes(
    instituicoes: List<Instituicao>,
    onAbrirCanal: (CanalOficial) -> Unit,
    onGerenciar: (Instituicao) -> Unit,
    onAdicionar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CartaoColorido(
                    fundo = MaterialTheme.colorScheme.primaryContainer,
                    texto = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Recebeu ligação ou mensagem em nome de um banco? " +
                                    "Desligue e ligue você mesmo para um número desta lista. " +
                                    "Nunca use o número ou o link que veio na mensagem.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (instituicoes.isEmpty()) {
                item {
                    CartaoBranco {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconeEmCirculo(icone = IconeInstituicao, tamanho = 80.dp)
                            Text(
                                text = "Nenhuma instituição ainda",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Escolha os bancos e órgãos que você usa. " +
                                        "Os telefones oficiais já entram prontos.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(instituicoes, key = { it.id }) { instituicao ->
                    CartaoDaInstituicao(
                        instituicao = instituicao,
                        onAbrirCanal = onAbrirCanal,
                        onGerenciar = { onGerenciar(instituicao) }
                    )
                }
            }
        }

        BarraDeBaixo {
            BotaoPrincipal(
                texto = if (instituicoes.isEmpty()) "Escolher instituições" else "Adicionar instituição",
                onClick = onAdicionar,
                icone = Icons.Filled.Add,
                habilitado = instituicoes.size < ValidacaoInstituicao.LIMITE_DE_INSTITUICOES
            )
        }
    }
}

@Composable
private fun CartaoDaInstituicao(
    instituicao: Instituicao,
    onAbrirCanal: (CanalOficial) -> Unit,
    onGerenciar: () -> Unit
) {
    CartaoBranco {
        Row(
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AvatarDaInstituicao(nome = instituicao.nome, iniciais = instituicao.iniciais)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = instituicao.nome,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    text = if (instituicao.doCatalogo) "Canais oficiais" else "Criada por você",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (instituicao.canais.isEmpty()) {
            Text(
                text = "Nenhum canal cadastrado. Toque em Gerenciar para adicionar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            )
        } else {
            instituicao.canais.forEachIndexed { indice, canal ->
                if (indice > 0) DivisorSuave()
                LinhaDeCanal(canal = canal, onAbrir = { onAbrirCanal(canal) })
            }
        }

        TextButton(
            onClick = onGerenciar,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .padding(horizontal = 8.dp)
        ) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Gerenciar ${instituicao.nome}", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun EscolhaDeInstituicoes(
    jaCadastradas: Set<String>,
    limiteAtingido: Boolean,
    onOutra: () -> Unit,
    onSalvar: (List<String>) -> Unit
) {
    var selecionadasTexto by rememberSaveable { mutableStateOf("") }
    val selecionadas = selecionadasTexto.split(",").filter { it.isNotBlank() }
    var salvando by remember { mutableStateOf(false) }

    val disponiveis = CatalogoInstituicoes.todas.filter { it.codigo !in jaCadastradas }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (disponiveis.isEmpty()) {
                item {
                    CartaoBranco {
                        Text(
                            text = "Você já adicionou todas as instituições da lista. " +
                                    "Use \"Outra instituição\" para cadastrar uma diferente.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            } else {
                items(disponiveis.chunked(2), key = { linha -> linha.first().codigo }) { linha ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        linha.forEach { modelo ->
                            val marcada = modelo.codigo in selecionadas
                            OpcaoDeInstituicao(
                                nome = modelo.nome,
                                iniciais = Instituicao(codigo = modelo.codigo, nome = modelo.nome).iniciais,
                                selecionada = marcada,
                                onClick = {
                                    selecionadasTexto = if (marcada) {
                                        (selecionadas - modelo.codigo).joinToString(",")
                                    } else {
                                        (selecionadas + modelo.codigo).joinToString(",")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (linha.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                BotaoSecundario(
                    texto = "Outra instituição",
                    onClick = onOutra,
                    icone = Icons.Filled.Add
                )
            }

            item { AvisoDeConferencia() }
        }

        BarraDeBaixo {
            if (limiteAtingido) {
                Text(
                    text = "Você chegou ao limite de ${ValidacaoInstituicao.LIMITE_DE_INSTITUICOES} instituições.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            BotaoPrincipal(
                texto = when (selecionadas.size) {
                    0 -> "Escolha pelo menos uma"
                    1 -> "Salvar 1 instituição"
                    else -> "Salvar ${selecionadas.size} instituições"
                },
                onClick = {
                    if (!salvando) {
                        salvando = true
                        onSalvar(selecionadas)
                    }
                },
                icone = Icons.Filled.Check,
                habilitado = selecionadas.isNotEmpty() && !salvando && !limiteAtingido
            )
        }
    }
}

@Composable
private fun OpcaoDeInstituicao(
    nome: String,
    iniciais: String,
    selecionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 120.dp)
            .semantics { selected = selecionada },
        shape = RoundedCornerShape(22.dp),
        color = if (selecionada) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            if (selecionada) 2.5.dp else 1.dp,
            if (selecionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        shadowElevation = if (selecionada || ProtegeTema.cores.escuro) 0.dp else 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box {
                AvatarDaInstituicao(nome = nome, iniciais = iniciais)
                if (selecionada) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Text(
                text = nome,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun DetalheDaInstituicao(
    instituicao: Instituicao,
    onAbrirCanal: (CanalOficial) -> Unit,
    onAdicionarCanal: () -> Unit,
    onEditarCanal: (CanalOficial) -> Unit,
    onRemoverCanal: (CanalOficial) -> Unit,
    onRenomear: () -> Unit,
    onRestaurar: () -> Unit,
    onRemover: () -> Unit
) {
    val modelo = CatalogoInstituicoes.porCodigo(instituicao.codigo)
    val faltamOficiais = modelo != null && modelo.canais.any { oficial ->
        instituicao.canais.none { it.tipo == oficial.tipo && it.valor == oficial.valor }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (modelo != null) {
                    "Canais conferidos nos sites oficiais em ${CatalogoInstituicoes.DATA_DA_CONFERENCIA}."
                } else {
                    "Instituição criada por você. Cadastre só números que você conferiu."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (instituicao.canais.isEmpty()) {
            item {
                CartaoBranco {
                    Text(
                        text = "Nenhum canal ainda. Toque em \"Adicionar canal\".",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }

        items(instituicao.canais, key = { it.id }) { canal ->
            CartaoBranco {
                LinhaDeCanal(canal = canal, onAbrir = { onAbrirCanal(canal) })
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (canal.oficial) "Oficial" else "Editado por você",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (canal.oficial) {
                            ProtegeTema.cores.seguroTexto
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Row {
                        TextButton(
                            onClick = { onEditarCanal(canal) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Editar", style = MaterialTheme.typography.labelLarge)
                        }
                        TextButton(
                            onClick = { onRemoverCanal(canal) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Remover",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        item {
            BotaoPrincipal(
                texto = "Adicionar canal",
                onClick = onAdicionarCanal,
                icone = Icons.Filled.Add,
                habilitado = instituicao.canais.size < ValidacaoInstituicao.LIMITE_DE_CANAIS
            )
        }

        if (faltamOficiais) {
            item {
                BotaoSecundario(
                    texto = "Restaurar canais oficiais",
                    onClick = onRestaurar,
                    icone = Icons.Filled.Refresh
                )
            }
        }

        item {
            BotaoSecundario(
                texto = "Mudar nome",
                onClick = onRenomear,
                icone = Icons.Filled.Edit
            )
        }

        item {
            TextButton(
                onClick = onRemover,
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
                    text = "Remover ${instituicao.nome}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun OpcaoSelecionavel(
    texto: String,
    selecionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 52.dp)
            .semantics { selected = selecionada },
        shape = RoundedCornerShape(50),
        color = if (selecionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selecionada) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            1.5.dp,
            if (selecionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selecionada) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text = texto, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun coresDosCampos() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    errorContainerColor = MaterialTheme.colorScheme.surface
)

@Composable
private fun FormularioDeCanal(
    canal: CanalOficial?,
    onSalvar: (
        rotulo: String,
        tipo: TipoCanal,
        valor: String,
        aoTerminar: (ErrosDoCanal) -> Unit
    ) -> Unit,
    onCancelar: () -> Unit,
    onRemover: (() -> Unit)?
) {
    val focusManager = LocalFocusManager.current

    var tipoTexto by rememberSaveable(canal?.id) { mutableStateOf((canal?.tipo ?: TipoCanal.TELEFONE).name) }
    val tipo = TipoCanal.deTexto(tipoTexto)
    var rotulo by rememberSaveable(canal?.id) { mutableStateOf(canal?.rotulo.orEmpty()) }
    var valor by rememberSaveable(canal?.id) {
        mutableStateOf(
            when {
                canal == null -> ""
                canal.tipo == TipoCanal.TELEFONE -> canal.valorFormatado
                else -> canal.valor
            }
        )
    }
    var erros by remember(canal?.id) { mutableStateOf(ErrosDoCanal()) }
    var salvando by remember { mutableStateOf(false) }

    val previa = ValidacaoInstituicao.normalizarValor(tipo, valor)?.let { normalizado ->
        if (tipo == TipoCanal.TELEFONE) {
            CanalOficial(instituicaoId = 0, rotulo = "", tipo = tipo, valor = normalizado, oficial = false)
                .valorFormatado
        } else {
            normalizado
        }
    }

    fun salvar() {
        focusManager.clearFocus()
        val validacao = ValidacaoInstituicao.validarCanal(rotulo, tipo, valor)
        erros = validacao
        if (validacao.temErro || salvando) return
        salvando = true
        onSalvar(rotulo, tipo, valor) { errosDoBanco ->
            erros = errosDoBanco
            salvando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        TituloDeSecao(texto = "Que tipo de canal?")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TipoCanal.entries.forEach { opcao ->
                OpcaoSelecionavel(
                    texto = opcao.rotulo,
                    selecionada = tipo == opcao,
                    onClick = {
                        if (tipo != opcao) {
                            tipoTexto = opcao.name
                            valor = ""
                            erros = ErrosDoCanal()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        OutlinedTextField(
            value = rotulo,
            onValueChange = {
                rotulo = it.take(ValidacaoInstituicao.TAMANHO_MAXIMO_ROTULO + 10)
                if (erros.rotulo != null) erros = erros.copy(rotulo = null)
            },
            label = { Text("Para que serve?") },
            placeholder = { Text("Ex.: Central de atendimento") },
            singleLine = true,
            isError = erros.rotulo != null,
            supportingText = {
                Text(erros.rotulo ?: "Escolha abaixo ou escreva.")
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(16.dp),
            colors = coresDosCampos(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val sugestoes = if (tipo == TipoCanal.SITE) {
                listOf("Site oficial", "Aplicativo")
            } else {
                ValidacaoInstituicao.sugestoesDeRotulo
            }
            sugestoes.chunked(2).forEach { linha ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    linha.forEach { sugestao ->
                        OpcaoSelecionavel(
                            texto = sugestao,
                            selecionada = rotulo.equals(sugestao, ignoreCase = true),
                            onClick = {
                                rotulo = sugestao
                                if (erros.rotulo != null) erros = erros.copy(rotulo = null)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (linha.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        OutlinedTextField(
            value = valor,
            onValueChange = { novo ->
                valor = if (tipo == TipoCanal.TELEFONE) {
                    novo.filter { it.isDigit() || it in " ()-+" }.take(20)
                } else {
                    novo.take(120)
                }
                if (erros.valor != null) erros = erros.copy(valor = null)
            },
            label = { Text(if (tipo == TipoCanal.TELEFONE) "Número de telefone" else "Endereço do site") },
            placeholder = {
                Text(if (tipo == TipoCanal.TELEFONE) "Ex.: 0800 729 0001" else "Ex.: www.bb.com.br")
            },
            singleLine = true,
            isError = erros.valor != null,
            supportingText = {
                Text(
                    when {
                        erros.valor != null -> erros.valor.orEmpty()
                        previa != null -> "Vai ficar assim: $previa"
                        tipo == TipoCanal.TELEFONE -> "Pode ser 0800, 4004, 135 ou um número com DDD."
                        else -> "Use só o endereço que você conferiu."
                    }
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(16.dp),
            colors = coresDosCampos(),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (tipo == TipoCanal.TELEFONE) KeyboardType.Phone else KeyboardType.Uri,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { salvar() }),
            modifier = Modifier.fillMaxWidth()
        )

        val cores = ProtegeTema.cores
        CartaoColorido(fundo = cores.alertaFundo, texto = cores.alertaTexto) {
            Text(
                text = "Copie o número de telefone do verso do cartão, do aplicativo ou do site oficial. " +
                        "Nunca use números ou links que chegaram por mensagem.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        BotaoPrincipal(
            texto = if (salvando) "Salvando..." else "Salvar canal",
            onClick = { salvar() },
            habilitado = !salvando
        )

        BotaoSecundario(texto = "Cancelar", onClick = onCancelar)

        if (onRemover != null) {
            TextButton(
                onClick = onRemover,
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
                    text = "Remover este canal",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun FormularioDeNome(
    nomeAtual: String,
    onSalvar: (nome: String, aoErrar: (String) -> Unit) -> Unit,
    onCancelar: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var nome by rememberSaveable { mutableStateOf(nomeAtual) }
    var erro by remember { mutableStateOf<String?>(null) }
    var salvando by remember { mutableStateOf(false) }

    fun salvar() {
        focusManager.clearFocus()
        val validacao = ValidacaoInstituicao.validarNome(nome)
        erro = validacao
        if (validacao != null || salvando) return
        salvando = true
        onSalvar(nome) { mensagem ->
            erro = mensagem
            salvando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        OutlinedTextField(
            value = nome,
            onValueChange = {
                nome = it.take(ValidacaoInstituicao.TAMANHO_MAXIMO_NOME + 10)
                if (erro != null) erro = null
            },
            label = { Text("Nome da instituição") },
            placeholder = { Text("Ex.: Mercado Pago") },
            singleLine = true,
            isError = erro != null,
            supportingText = { Text(erro ?: "Depois você adiciona os telefones e o site.") },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(16.dp),
            colors = coresDosCampos(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { salvar() }),
            modifier = Modifier.fillMaxWidth()
        )

        BotaoPrincipal(
            texto = if (salvando) "Salvando..." else "Salvar",
            onClick = { salvar() },
            habilitado = !salvando
        )

        BotaoSecundario(texto = "Cancelar", onClick = onCancelar)
    }
}

@Composable
fun CartaoCanaisOficiais(
    nomeInstituicao: String,
    canais: List<CanalOficial>,
    cadastrada: Boolean,
    onAbrirCanal: (CanalOficial) -> Unit,
    onGerenciar: () -> Unit
) {
    val cores = ProtegeTema.cores
    val telefones = canais.filter { it.tipo == TipoCanal.TELEFONE }.take(2)
    val site = canais.firstOrNull { it.tipo == TipoCanal.SITE }

    CartaoBranco {
        Column(
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconeEmCirculo(
                    icone = Icons.Filled.Call,
                    cor = cores.seguroTexto,
                    fundo = cores.seguroFundo
                )
                Text(
                    text = "Fale direto com $nomeInstituicao",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() }
                )
            }
            Text(
                text = "Esta mensagem fala em nome de $nomeInstituicao. Não use números ou links " +
                        "que vieram nela: ligue você mesmo para o canal oficial.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            telefones.forEach { canal ->
                LinhaDeCanal(canal = canal, onAbrir = { onAbrirCanal(canal) })
            }
            if (site != null) {
                LinhaDeCanal(canal = site, onAbrir = { onAbrirCanal(site) })
            }
            TextButton(
                onClick = onGerenciar,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = if (cadastrada) "Ver todos os canais" else "Adicionar às minhas instituições",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}