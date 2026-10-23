package com.example.protege_idoso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.example.protege_idoso.ui.BotaoSecundario
import com.example.protege_idoso.ui.CabecalhoDeTela
import com.example.protege_idoso.ui.CartaoBranco
import com.example.protege_idoso.ui.CartaoColorido
import com.example.protege_idoso.ui.DialogoConfirmacao
import com.example.protege_idoso.ui.DivisorSuave
import com.example.protege_idoso.ui.TituloDeSecao
import com.example.protege_idoso.ui.theme.ProtegeTema
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme

class PrivacidadeActivity : ComponentActivity() {

    private var acessoNotificacoes by mutableStateOf(false)
    private var avisosPermitidos by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        atualizarEstados()

        setContent {
            Protege_IdosoTheme {
                PrivacidadeScreen(
                    acessoNotificacoes = acessoNotificacoes,
                    avisosPermitidos = avisosPermitidos,
                    onVoltar = { finish() },
                    onAbrirAcessoNotificacoes = {
                        abrirConfiguracao(
                            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        )
                    },
                    onAbrirAvisos = {
                        abrirConfiguracao(
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        )
                    },
                    onAbrirConfiguracoesDoApp = {
                        abrirConfiguracao(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            )
                        )
                    },
                    onLerTermos = {
                        startActivity(
                            DocumentoLegalActivity.criarIntent(
                                this,
                                DocumentoLegalActivity.TIPO_TERMOS
                            )
                        )
                    },
                    onLerPolitica = {
                        startActivity(
                            DocumentoLegalActivity.criarIntent(
                                this,
                                DocumentoLegalActivity.TIPO_PRIVACIDADE
                            )
                        )
                    },
                    onRevogarConsentimento = {
                        revogarConsentimento()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        atualizarEstados()
    }

    private fun atualizarEstados() {
        acessoNotificacoes = NotificationManagerCompat
            .getEnabledListenerPackages(this)
            .contains(packageName)

        avisosPermitidos = NotificationManagerCompat
            .from(this)
            .areNotificationsEnabled()
    }

    private fun revogarConsentimento() {
        Preferencias(this).revogarAceite()

        try {
            com.example.protege_idoso.historico.HistoricoRepository(applicationContext)
                .limparTudo()
            com.example.protege_idoso.contatos.ContatosRepository(applicationContext)
                .limparTudo()
            com.example.protege_idoso.quiz.QuizRepository(applicationContext)
                .apagarTudo()
        } catch (e: Exception) {
            android.util.Log.e(
                "ProtegeIdoso",
                "Não foi possível apagar os dados locais: ${e.javaClass.simpleName}"
            )
        }

        Toast.makeText(
            this,
            "Consentimento revogado.",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }

    private fun abrirConfiguracao(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Não foi possível abrir as configurações.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

@Composable
fun PrivacidadeScreen(
    acessoNotificacoes: Boolean,
    avisosPermitidos: Boolean,
    onVoltar: () -> Unit,
    onAbrirAcessoNotificacoes: () -> Unit,
    onAbrirAvisos: () -> Unit,
    onAbrirConfiguracoesDoApp: () -> Unit,
    onLerTermos: () -> Unit,
    onLerPolitica: () -> Unit,
    onRevogarConsentimento: () -> Unit
) {
    var confirmarRevogacao by remember { mutableStateOf(false) }

    if (confirmarRevogacao) {
        DialogoConfirmacao(
            titulo = "Revogar consentimento?",
            texto = "O ProtegeIdoso vai parar de analisar as suas mensagens e " +
                    "apagar, neste celular, o registro do seu aceite, as suas " +
                    "escolhas, o histórico de alertas, os contatos de confiança " +
                    "e o placar do desafio do dia. " +
                    "Para usar de novo, será preciso aceitar os Termos " +
                    "de Uso e a Política de Privacidade outra vez.\n\n" +
                    "Para também retirar a permissão de acesso às notificações, " +
                    "use as configurações do celular.",
            rotuloConfirmar = "Revogar",
            onConfirmar = {
                confirmarRevogacao = false
                onRevogarConsentimento()
            },
            onCancelar = { confirmarRevogacao = false }
        )
    }

    val iconeCadeado = rememberVectorPainter(Icons.Filled.Lock)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            CabecalhoDeTela(
                titulo = "Privacidade e segurança",
                subtitulo = "Veja como cuidamos dos seus dados",
                icone = iconeCadeado,
                onVoltar = onVoltar
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                CartaoResumo()

                TituloDeSecao("O que o aplicativo precisa")

                CartaoDePermissoes(
                    acessoNotificacoes = acessoNotificacoes,
                    avisosPermitidos = avisosPermitidos,
                    onAbrirAcessoNotificacoes = onAbrirAcessoNotificacoes,
                    onAbrirAvisos = onAbrirAvisos
                )

                TituloDeSecao("O que acontece com cada mensagem")

                CartaoDePassosDaMensagem()

                TituloDeSecao("Nossos compromissos com você")

                CartaoDeGarantias()

                CartaoDeLimites()

                TituloDeSecao("Seus direitos e seu consentimento")

                CartaoDeDireitos(
                    onLerTermos = onLerTermos,
                    onLerPolitica = onLerPolitica,
                    onRevogar = { confirmarRevogacao = true }
                )

                TituloDeSecao("Você está no controle")

                CartaoDeControle(
                    onAbrirConfiguracoesDoApp = onAbrirConfiguracoesDoApp
                )
            }
        }
    }
}

@Composable
private fun CartaoResumo() {
    CartaoColorido(
        fundo = MaterialTheme.colorScheme.primaryContainer,
        texto = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Text(
            text = "Suas conversas continuam suas",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "O ProtegeIdoso só observa os avisos de mensagens do WhatsApp " +
                    "para procurar sinais de golpe. Ele não responde, não envia " +
                    "mensagens e não guarda o texto do que você recebe. No histórico " +
                    "fica só o resultado dos avisos.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun CartaoDePermissoes(
    acessoNotificacoes: Boolean,
    avisosPermitidos: Boolean,
    onAbrirAcessoNotificacoes: () -> Unit,
    onAbrirAvisos: () -> Unit
) {
    CartaoBranco {

        LinhaDePermissao(
            titulo = "Acesso às notificações",
            descricao = "Necessário. Permite ler o aviso das mensagens que chegam " +
                    "no WhatsApp. Avisos de outros aplicativos são ignorados.",
            ativo = acessoNotificacoes,
            textoAtivo = "Ativado",
            textoInativo = "Desativado",
            textoBotaoAtivo = "Gerenciar nas configurações",
            textoBotaoInativo = "Ativar nas configurações",
            onClick = onAbrirAcessoNotificacoes
        )

        DivisorSuave()

        LinhaDePermissao(
            titulo = "Mostrar avisos",
            descricao = "Necessário. Permite que o aplicativo mostre o aviso de " +
                    "mensagem suspeita ou de possível golpe.",
            ativo = avisosPermitidos,
            textoAtivo = "Permitido",
            textoInativo = "Bloqueado",
            textoBotaoAtivo = "Gerenciar nas configurações",
            textoBotaoInativo = "Permitir nas configurações",
            onClick = onAbrirAvisos
        )

        DivisorSuave()

        LinhaDePermissao(
            titulo = "Internet",
            descricao = "Necessária. Usada para enviar para análise apenas o texto " +
                    "das mensagens que tiverem sinais de risco, com seus dados " +
                    "pessoais já escondidos.",
            ativo = null,
            textoAtivo = "",
            textoInativo = "",
            textoBotaoAtivo = "",
            textoBotaoInativo = "",
            onClick = {}
        )

        DivisorSuave()

        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Não pedimos acesso a",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Contatos, fotos e arquivos, câmera, microfone, localização, " +
                        "SMS e ligações.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Os contatos de confiança são digitados por você e ficam só " +
                        "neste celular. O aplicativo não lê a sua agenda.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LinhaDePermissao(
    titulo: String,
    descricao: String,
    ativo: Boolean?,
    textoAtivo: String,
    textoInativo: String,
    textoBotaoAtivo: String,
    textoBotaoInativo: String,
    onClick: () -> Unit
) {
    val cores = ProtegeTema.cores

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = descricao,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (ativo != null) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (ativo) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = null,
                    tint = if (ativo) cores.seguroTexto else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = if (ativo) textoAtivo else textoInativo,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (ativo) cores.seguroTexto else MaterialTheme.colorScheme.error
                )
            }

            BotaoSecundario(
                texto = if (ativo) textoBotaoAtivo else textoBotaoInativo,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun CartaoDePassosDaMensagem() {
    val passos = listOf(
        "Uma mensagem chega no WhatsApp e o aplicativo lê o aviso que aparece no celular.",
        "A primeira análise é feita no próprio celular. Mensagens sem nenhum sinal de risco não saem do aparelho.",
        "Se houver sinais de risco, o aplicativo esconde e-mails, telefones, CPF, links e chaves Pix antes de enviar o texto.",
        "O texto protegido vai por conexão segura (HTTPS) para a análise com inteligência artificial. Se ela não responder, a análise é feita só no celular.",
        "Você recebe o aviso, de acordo com as opções da tela inicial em “Quando devo avisar você?”."
    )

    CartaoBranco {
        passos.forEachIndexed { indice, passo ->

            if (indice > 0) {
                DivisorSuave()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    ),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (indice + 1).toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = passo,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CartaoDeGarantias() {
    val garantias = listOf(
        "Não responde nem envia mensagens por você.",
        "Não apaga nem bloqueia suas mensagens.",
        "Ignora na hora as notificações de outros aplicativos.",
        "Não guarda suas mensagens no celular.",
        "Não envia o nome nem o número de quem escreveu.",
        "Não envia o histórico nem os contatos de confiança para lugar nenhum."
    )

    CartaoBranco {
        garantias.forEachIndexed { indice, garantia ->

            if (indice > 0) {
                DivisorSuave()
            }

            ItemComIcone(
                icone = Icons.Filled.CheckCircle,
                texto = garantia,
                corDoIcone = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CartaoDeLimites() {
    val cores = ProtegeTema.cores

    CartaoColorido(
        fundo = cores.alertaFundo,
        texto = cores.alertaTexto
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

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                Text(
                    text = "Importante saber",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "O aplicativo esconde automaticamente e-mails, telefones, " +
                            "CPF, links e chaves Pix. Outras informações escritas na " +
                            "mensagem, como nomes ou endereços, podem seguir no texto " +
                            "enviado para análise.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Nenhum aviso é 100% certo. Na dúvida, não responda e " +
                            "peça ajuda a uma pessoa de confiança.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun CartaoDeControle(
    onAbrirConfiguracoesDoApp: () -> Unit
) {
    CartaoBranco {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Para parar a leitura a qualquer momento, desligue " +
                        "“Analisar mensagens” na tela inicial ou retire a permissão " +
                        "de acesso às notificações nas configurações do celular.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            BotaoSecundario(
                texto = "Abrir configurações do aplicativo",
                onClick = onAbrirConfiguracoesDoApp
            )
        }
    }
}

@Composable
private fun CartaoDeDireitos(
    onLerTermos: () -> Unit,
    onLerPolitica: () -> Unit,
    onRevogar: () -> Unit
) {
    CartaoBranco {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Pela Lei Geral de Proteção de Dados (LGPD), você pode pedir " +
                        "acesso, correção ou eliminação dos seus dados, informações " +
                        "sobre o compartilhamento e a revogação do consentimento. " +
                        "Para isso, escreva para: " + DadosLegais.EMAIL_CONTATO,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            BotaoSecundario(
                texto = "Ler os Termos de Uso",
                onClick = onLerTermos
            )

            BotaoSecundario(
                texto = "Ler a Política de Privacidade",
                onClick = onLerPolitica
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Text(
                text = "Mudou de ideia? Você pode retirar o consentimento a qualquer " +
                        "momento. O aplicativo para de analisar mensagens até você " +
                        "aceitar de novo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            BotaoSecundario(
                texto = DadosLegais.ROTULO_REVOGAR,
                onClick = onRevogar,
                cor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ItemComIcone(
    icone: ImageVector,
    texto: String,
    corDoIcone: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = corDoIcone,
            modifier = Modifier.size(28.dp)
        )

        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}
