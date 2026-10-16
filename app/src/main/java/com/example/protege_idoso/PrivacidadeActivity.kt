package com.example.protege_idoso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme

class PrivacidadeActivity : ComponentActivity() {

    private var acessoNotificacoes by mutableStateOf(false)
    private var avisosPermitidos by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
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
        AlertDialog(
            onDismissRequest = { confirmarRevogacao = false },
            title = {
                Text(text = "Revogar consentimento?")
            },
            text = {
                Text(
                    text = "O ProtegeIdoso vai parar de analisar as suas mensagens e " +
                            "apagar, neste celular, o registro do seu aceite e as suas " +
                            "escolhas. Para usar de novo, será preciso aceitar os Termos " +
                            "de Uso e a Política de Privacidade outra vez.\n\n" +
                            "Para também retirar a permissão de acesso às notificações, " +
                            "use as configurações do celular.",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarRevogacao = false
                        onRevogarConsentimento()
                    },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text(
                        text = "Revogar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmarRevogacao = false },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                FilledTonalButton(
                    onClick = onVoltar,
                    modifier = Modifier.heightIn(min = 56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Voltar",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                CabecalhoPrivacidade()

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
private fun CabecalhoPrivacidade() {
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
                text = "Privacidade e segurança",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics {
                    heading()
                }
            )

            Text(
                text = "Veja como cuidamos dos seus dados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TituloDeSecao(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.semantics {
            heading()
        }
    )
}

@Composable
private fun CartaoResumo() {
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Suas conversas continuam suas",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "O ProtegeIdoso só observa os avisos de mensagens do WhatsApp " +
                        "para procurar sinais de golpe. Ele não responde, não envia " +
                        "mensagens e não guarda o que você recebe.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CartaoDePermissoes(
    acessoNotificacoes: Boolean,
    avisosPermitidos: Boolean,
    onAbrirAcessoNotificacoes: () -> Unit,
    onAbrirAvisos: () -> Unit
) {
    CartaoBase {

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

        Divisor()

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

        Divisor()

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

        Divisor()

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
                    tint = if (ativo) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = if (ativo) textoAtivo else textoInativo,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (ativo) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            OutlinedButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (ativo) textoBotaoAtivo else textoBotaoInativo,
                    style = MaterialTheme.typography.labelLarge
                )
            }
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

    CartaoBase {
        passos.forEachIndexed { indice, passo ->

            if (indice > 0) {
                Divisor()
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
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (indice + 1).toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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
        "Não envia o nome nem o número de quem escreveu."
    )

    CartaoBase {
        garantias.forEachIndexed { indice, garantia ->

            if (indice > 0) {
                Divisor()
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
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
    CartaoBase {

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

            OutlinedButton(
                onClick = onAbrirConfiguracoesDoApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Abrir configurações do aplicativo",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun CartaoDeDireitos(
    onLerTermos: () -> Unit,
    onLerPolitica: () -> Unit,
    onRevogar: () -> Unit
) {
    CartaoBase {

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

            OutlinedButton(
                onClick = onLerTermos,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Ler os Termos de Uso",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = onLerPolitica,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Ler a Política de Privacidade",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Text(
                text = "Mudou de ideia? Você pode retirar o consentimento a qualquer " +
                        "momento. O aplicativo para de analisar mensagens até você " +
                        "aceitar de novo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = onRevogar,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = DadosLegais.ROTULO_REVOGAR,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ItemComIcone(
    icone: ImageVector,
    texto: String,
    corDoIcone: androidx.compose.ui.graphics.Color
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

@Composable
private fun CartaoBase(
    conteudo: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {
        Column {
            conteudo()
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
