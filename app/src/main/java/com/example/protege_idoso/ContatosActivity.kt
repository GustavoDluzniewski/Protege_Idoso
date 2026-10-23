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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.contatos.AcoesDeContato
import com.example.protege_idoso.contatos.ContatoConfianca
import com.example.protege_idoso.contatos.ContatosRepository
import com.example.protege_idoso.contatos.ErrosDoContato
import com.example.protege_idoso.contatos.ResultadoContato
import com.example.protege_idoso.contatos.Telefone
import com.example.protege_idoso.contatos.ValidacaoContato
import com.example.protege_idoso.ui.BotaoPrincipal
import com.example.protege_idoso.ui.BotaoSecundario
import com.example.protege_idoso.ui.CabecalhoDeTela
import com.example.protege_idoso.ui.CartaoBranco
import com.example.protege_idoso.ui.CartaoColorido
import com.example.protege_idoso.ui.DialogoConfirmacao
import com.example.protege_idoso.ui.IconeEmCirculo
import com.example.protege_idoso.ui.theme.AzulClaro
import com.example.protege_idoso.ui.theme.AzulPrincipal
import com.example.protege_idoso.ui.theme.ProtegeTema
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContatosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val repositorio = ContatosRepository(applicationContext)
        val abrirFormulario = intent.getBooleanExtra(EXTRA_NOVO_CONTATO, false)

        setContent {
            Protege_IdosoTheme {
                ContatosScreen(
                    repositorio = repositorio,
                    abrirFormularioNovo = abrirFormulario,
                    onVoltar = { finish() }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_NOVO_CONTATO = "novo_contato"

        fun criarIntent(context: Context, novoContato: Boolean = false): Intent =
            Intent(context, ContatosActivity::class.java)
                .putExtra(EXTRA_NOVO_CONTATO, novoContato)
    }
}

private const val SEM_EDICAO = -1L
private const val NOVO_CONTATO = 0L

@Composable
fun ContatosScreen(
    repositorio: ContatosRepository,
    abrirFormularioNovo: Boolean,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var contatos by remember { mutableStateOf<List<ContatoConfianca>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var confirmarExclusao by remember { mutableStateOf<ContatoConfianca?>(null) }

    var idEmEdicao by rememberSaveable {
        mutableStateOf(if (abrirFormularioNovo) NOVO_CONTATO else SEM_EDICAO)
    }

    fun avisar(texto: String) {
        Toast.makeText(context, texto, Toast.LENGTH_LONG).show()
    }

    fun recarregar() {
        scope.launch {
            contatos = withContext(Dispatchers.IO) { repositorio.listar() }
            carregando = false
        }
    }

    LaunchedEffect(Unit) { recarregar() }

    BackHandler(enabled = idEmEdicao != SEM_EDICAO) {
        idEmEdicao = SEM_EDICAO
    }

    confirmarExclusao?.let { contato ->
        DialogoConfirmacao(
            titulo = "Remover ${contato.nome}?",
            texto = "O contato será apagado da sua lista de confiança. " +
                    "Isso não apaga a pessoa da agenda do celular.",
            rotuloConfirmar = "Remover",
            onConfirmar = {
                confirmarExclusao = null
                scope.launch {
                    val apagou = withContext(Dispatchers.IO) { repositorio.excluir(contato.id) }
                    idEmEdicao = SEM_EDICAO
                    recarregar()
                    avisar(if (apagou) "Contato removido." else "Esse contato já tinha sido removido.")
                }
            },
            onCancelar = { confirmarExclusao = null }
        )
    }

    val editando = idEmEdicao != SEM_EDICAO
    val contatoEmEdicao = contatos.firstOrNull { it.id == idEmEdicao }
    val iconePessoa = rememberVectorPainter(Icons.Filled.Person)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CabecalhoDeTela(
                titulo = when {
                    !editando -> "Contatos de confiança"
                    idEmEdicao == NOVO_CONTATO -> "Novo contato"
                    else -> "Editar contato"
                },
                subtitulo = if (editando) {
                    "Preencha os dados de quem pode ajudar você."
                } else {
                    "Pessoas para ligar quando uma mensagem parecer estranha."
                },
                icone = iconePessoa,
                onVoltar = {
                    if (editando) idEmEdicao = SEM_EDICAO else onVoltar()
                }
            )

            when {
                carregando -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                editando && idEmEdicao != NOVO_CONTATO && contatoEmEdicao == null -> {
                    LaunchedEffect(idEmEdicao) { idEmEdicao = SEM_EDICAO }
                }

                editando -> FormularioDeContato(
                    contato = contatoEmEdicao,
                    onSalvar = { nome, telefone, parentesco, aoTerminar ->
                        scope.launch {
                            val resultado = withContext(Dispatchers.IO) {
                                if (contatoEmEdicao == null) {
                                    repositorio.cadastrar(nome, telefone, parentesco)
                                } else {
                                    repositorio.atualizar(contatoEmEdicao.id, nome, telefone, parentesco)
                                }
                            }
                            when (resultado) {
                                is ResultadoContato.Sucesso -> {
                                    avisar(
                                        if (contatoEmEdicao == null) "Contato salvo!" else "Alterações salvas!"
                                    )
                                    idEmEdicao = SEM_EDICAO
                                    recarregar()
                                }
                                is ResultadoContato.Invalido -> aoTerminar(resultado.erros)
                                ResultadoContato.TelefoneRepetido -> aoTerminar(
                                    ErrosDoContato(telefone = "Esse telefone já está na sua lista.")
                                )
                                ResultadoContato.LimiteAtingido -> {
                                    avisar(
                                        "Você já tem ${ValidacaoContato.LIMITE_DE_CONTATOS} contatos. " +
                                                "Remova um para cadastrar outro."
                                    )
                                    idEmEdicao = SEM_EDICAO
                                }
                                ResultadoContato.NaoEncontrado -> {
                                    avisar("Esse contato não existe mais.")
                                    idEmEdicao = SEM_EDICAO
                                    recarregar()
                                }
                            }
                        }
                    },
                    onCancelar = { idEmEdicao = SEM_EDICAO },
                    onRemover = contatoEmEdicao?.let { c -> { confirmarExclusao = c } }
                )

                else -> ListaDeContatos(
                    contatos = contatos,
                    onAdicionar = { idEmEdicao = NOVO_CONTATO },
                    onEditar = { idEmEdicao = it.id },
                    onRemover = { confirmarExclusao = it },
                    onLigar = { AcoesDeContato.ligar(context, it) },
                    onWhatsApp = { AcoesDeContato.abrirWhatsApp(context, it) }
                )
            }
        }
    }
}


@Composable
private fun ListaDeContatos(
    contatos: List<ContatoConfianca>,
    onAdicionar: () -> Unit,
    onEditar: (ContatoConfianca) -> Unit,
    onRemover: (ContatoConfianca) -> Unit,
    onLigar: (ContatoConfianca) -> Unit,
    onWhatsApp: (ContatoConfianca) -> Unit
) {
    val limiteAtingido = contatos.size >= ValidacaoContato.LIMITE_DE_CONTATOS

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
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Recebeu uma mensagem estranha? Antes de responder, " +
                                    "pagar ou clicar em algo, ligue para uma destas pessoas.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (contatos.isEmpty()) {
                item { CartaoSemContatos() }
            } else {
                item {
                    Text(
                        text = "${contatos.size} de ${ValidacaoContato.LIMITE_DE_CONTATOS} contatos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.semantics { heading() }
                    )
                }
                items(contatos, key = { it.id }) { contato ->
                    CartaoDeContato(
                        contato = contato,
                        onLigar = { onLigar(contato) },
                        onWhatsApp = { onWhatsApp(contato) },
                        onEditar = { onEditar(contato) },
                        onRemover = { onRemover(contato) }
                    )
                }
            }
        }

        BarraInferior {
            if (limiteAtingido) {
                Text(
                    text = "Você chegou ao limite de ${ValidacaoContato.LIMITE_DE_CONTATOS} contatos. " +
                            "Remova um para cadastrar outro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            BotaoPrincipal(
                texto = "Adicionar contato",
                onClick = onAdicionar,
                icone = Icons.Filled.Add,
                habilitado = !limiteAtingido
            )
        }
    }
}

@Composable
private fun BarraInferior(conteudo: @Composable () -> Unit) {
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
private fun CartaoSemContatos() {
    CartaoBranco {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconeEmCirculo(icone = Icons.Filled.Person, tamanho = 80.dp)
            Text(
                text = "Nenhum contato ainda",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Cadastre um filho, neto, amigo ou vizinho. " +
                        "Assim, na dúvida, você tem para quem ligar com um toque.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AvatarDoContato(
    contato: ContatoConfianca,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(AzulClaro, AzulPrincipal))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = contato.iniciais,
            style = MaterialTheme.typography.titleLarge,
            color = androidx.compose.ui.graphics.Color.White
        )
    }
}

@Composable
private fun CartaoDeContato(
    contato: ContatoConfianca,
    onLigar: () -> Unit,
    onWhatsApp: () -> Unit,
    onEditar: () -> Unit,
    onRemover: () -> Unit
) {
    CartaoBranco {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.semantics(mergeDescendants = true) {
                    contentDescription = buildString {
                        append(contato.nome)
                        if (contato.parentesco.isNotBlank()) append(", ").append(contato.parentesco)
                        append(", telefone ").append(Telefone.paraLeitorDeTela(contato.telefone))
                    }
                }
            ) {
                AvatarDoContato(contato)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contato.nome,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (contato.parentesco.isNotBlank()) {
                        Text(
                            text = contato.parentesco,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = contato.telefoneFormatado,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            BotaoPrincipal(
                texto = "Ligar para ${primeiroNome(contato)}",
                onClick = onLigar,
                icone = Icons.Filled.Call
            )

            if (Telefone.ehCelular(contato.telefone)) {
                BotaoSecundario(
                    texto = "Mandar WhatsApp",
                    onClick = onWhatsApp,
                    icone = Icons.AutoMirrored.Filled.Send
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onEditar, modifier = Modifier.heightIn(min = 48.dp)) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Editar", style = MaterialTheme.typography.labelLarge)
                }
                TextButton(onClick = onRemover, modifier = Modifier.heightIn(min = 48.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
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

@Composable
private fun OpcaoDeParentesco(
    texto: String,
    selecionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 52.dp)
            .semantics { selected = selecionado },
        shape = RoundedCornerShape(50),
        color = if (selecionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selecionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            1.5.dp,
            if (selecionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selecionado) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text = texto, style = MaterialTheme.typography.labelLarge)
        }
    }
}

fun primeiroNome(contato: ContatoConfianca): String =
    contato.nome.trim().split(Regex("\\s+")).firstOrNull().orEmpty().ifEmpty { contato.nome }


@Composable
private fun FormularioDeContato(
    contato: ContatoConfianca?,
    onSalvar: (
        nome: String,
        telefone: String,
        parentesco: String,
        aoTerminar: (ErrosDoContato) -> Unit
    ) -> Unit,
    onCancelar: () -> Unit,
    onRemover: (() -> Unit)?
) {
    val focusManager = LocalFocusManager.current

    var nome by rememberSaveable(contato?.id) { mutableStateOf(contato?.nome.orEmpty()) }
    var telefone by rememberSaveable(contato?.id) {
        mutableStateOf(contato?.telefoneFormatado.orEmpty())
    }
    var parentesco by rememberSaveable(contato?.id) { mutableStateOf(contato?.parentesco.orEmpty()) }
    var erros by remember(contato?.id) { mutableStateOf(ErrosDoContato()) }
    var salvando by remember { mutableStateOf(false) }

    val telefoneNormalizado = Telefone.normalizar(telefone)

    fun salvar() {
        focusManager.clearFocus()
        val validacao = ValidacaoContato.validar(nome, telefone, parentesco)
        erros = validacao
        if (validacao.temErro || salvando) return
        salvando = true
        onSalvar(nome, telefone, parentesco) { errosDoBanco ->
            erros = errosDoBanco
            salvando = false
        }
    }

    val coresDoCampo = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        errorContainerColor = MaterialTheme.colorScheme.surface
    )

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
                nome = it.take(ValidacaoContato.TAMANHO_MAXIMO_NOME + 10)
                if (erros.nome != null) erros = erros.copy(nome = null)
            },
            label = { Text("Nome") },
            placeholder = { Text("Ex.: Maria Souza") },
            singleLine = true,
            isError = erros.nome != null,
            supportingText = erros.nome?.let { mensagem -> { Text(mensagem) } },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(16.dp),
            colors = coresDoCampo,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefone,
            onValueChange = { novo ->
                telefone = novo.filter { it.isDigit() || it in " ()-+" }.take(20)
                if (erros.telefone != null) erros = erros.copy(telefone = null)
            },
            label = { Text("Telefone com DDD") },
            placeholder = { Text("Ex.: (69) 99999-8888") },
            singleLine = true,
            isError = erros.telefone != null,
            supportingText = {
                when {
                    erros.telefone != null -> Text(erros.telefone.orEmpty())
                    telefoneNormalizado != null ->
                        Text("Vai ficar assim: ${Telefone.formatar(telefoneNormalizado)}")
                    else -> Text("Digite o DDD e o número.")
                }
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(16.dp),
            colors = coresDoCampo,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Quem é essa pessoa? (opcional)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            ValidacaoContato.sugestoesDeParentesco.chunked(2).forEach { linha ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    linha.forEach { opcao ->
                        val selecionado = parentesco.equals(opcao, ignoreCase = true)
                        OpcaoDeParentesco(
                            texto = opcao,
                            selecionado = selecionado,
                            onClick = {
                                parentesco = if (selecionado) "" else opcao
                                if (erros.parentesco != null) erros = erros.copy(parentesco = null)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (linha.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            OutlinedTextField(
                value = parentesco,
                onValueChange = {
                    parentesco = it.take(ValidacaoContato.TAMANHO_MAXIMO_PARENTESCO + 10)
                    if (erros.parentesco != null) erros = erros.copy(parentesco = null)
                },
                label = { Text("Ou escreva aqui") },
                placeholder = { Text("Ex.: Sobrinha") },
                singleLine = true,
                isError = erros.parentesco != null,
                supportingText = erros.parentesco?.let { mensagem -> { Text(mensagem) } },
                textStyle = MaterialTheme.typography.bodyLarge,
                shape = RoundedCornerShape(16.dp),
                colors = coresDoCampo,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { salvar() }),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        BotaoPrincipal(
            texto = if (salvando) "Salvando..." else "Salvar contato",
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
                    text = "Remover este contato",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Text(
            text = "Os contatos ficam guardados só neste celular. " +
                    "O aplicativo não lê a sua agenda e não envia esses dados para lugar nenhum.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}


@Composable
fun CartaoPecaAjuda(
    contatos: List<ContatoConfianca>,
    onLigar: (ContatoConfianca) -> Unit,
    onGerenciar: () -> Unit,
    titulo: String = "Confirme com quem você confia"
) {
    val cores = ProtegeTema.cores
    CartaoBranco {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )

            if (contatos.isEmpty()) {
                Text(
                    text = "Você ainda não cadastrou ninguém. Cadastre um familiar ou amigo " +
                            "para ligar com um toque quando tiver dúvida.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BotaoPrincipal(
                    texto = "Cadastrar contato",
                    onClick = onGerenciar,
                    icone = Icons.Filled.Add
                )
            } else {
                Text(
                    text = "Antes de responder ou pagar, ligue e pergunte se a história é verdadeira.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                contatos.take(3).forEach { contato ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AvatarDoContato(contato)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contato.nome,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (contato.parentesco.isNotBlank()) {
                                Text(
                                    text = contato.parentesco,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    BotaoPrincipal(
                        texto = "Ligar para ${primeiroNome(contato)}",
                        onClick = { onLigar(contato) },
                        icone = Icons.Filled.Call,
                        corFundo = cores.gradienteTopo,
                        corTexto = cores.sobreGradiente
                    )
                }

                TextButton(
                    onClick = onGerenciar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text(
                        text = if (contatos.size > 3) "Ver todos os contatos" else "Gerenciar contatos",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}