package com.example.protege_idoso

import android.os.Bundle
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.protege_idoso.contatos.AcoesDeContato
import com.example.protege_idoso.contatos.ContatoConfianca
import com.example.protege_idoso.contatos.ContatosRepository
import com.example.protege_idoso.ui.BotaoPrincipal
import com.example.protege_idoso.ui.CabecalhoDeTela
import com.example.protege_idoso.ui.CartaoBranco
import com.example.protege_idoso.ui.DivisorSuave
import com.example.protege_idoso.ui.TituloDeSecao
import com.example.protege_idoso.ui.theme.ProtegeTema
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InstrucoesActivity : ComponentActivity() {
    private fun abrirDisqueDenuncia() {
        val mensagem = """
            Olá. Estou recebendo mensagens que podem ser uma tentativa de golpe.
            
            Gostaria de orientação sobre como proceder e, se necessário, realizar uma denúncia.
            
            Tenho as mensagens recebidas e outras informações que podem ajudar na denúncia.
        """.trimIndent()

        DisqueDenuncia.abrir(this, mensagem)
    }

    private var contatos by mutableStateOf<List<ContatoConfianca>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            Protege_IdosoTheme {
                InstrucoesScreen(
                    contatos = contatos,
                    onVoltar = { finish() },
                    onLigar = { AcoesDeContato.ligar(this, it) },
                    onGerenciarContatos = {
                        startActivity(
                            ContatosActivity.criarIntent(this, novoContato = contatos.isEmpty())
                        )
                    },
                    onDenunciar = {
                        abrirDisqueDenuncia()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            contatos = try {
                withContext(Dispatchers.IO) {
                    ContatosRepository(applicationContext).listar()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

private val passosDeSeguranca = listOf(
    "Não responda à mensagem.",
    "Não clique em links suspeitos.",
    "Não informe senhas, códigos ou dados pessoais.",
    "Não faça pagamentos ou transferências.",
    "Confirme a informação por um canal oficial.",
    "Se tiver dúvidas, peça ajuda a uma pessoa de confiança.",
    "Se a mensagem diz ser de um filho ou parente com número novo, ligue para o número antigo dele antes de responder.",
    "Bloqueie o contato e guarde prints da conversa como prova. Não apague."
)

private val passosAposGolpe = listOf(
    "Pare de conversar com o golpista.",

    "Ligue agora para o seu banco e peça o bloqueio da conta ou do Pix.",
    "Troque as senhas do banco.",
    "Avise um familiar de confiança.",
    "Registre um boletim de ocorrência na delegacia ou pela internet.",
    "Se necessário, procure ajuda pelo botão “Denuncie Aqui!”."
)


@Composable
fun InstrucoesScreen(
    contatos: List<ContatoConfianca>,
    onVoltar: () -> Unit,
    onLigar: (ContatoConfianca) -> Unit,
    onGerenciarContatos: () -> Unit,
    onDenunciar: () -> Unit
) {
    val cores = ProtegeTema.cores

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CabecalhoDeTela(
                titulo = "Instruções de segurança",
                subtitulo = "Veja o que fazer agora",
                icone = painterResource(R.drawable.ic_stat_shield),
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
                TituloDeSecao(texto = "O que fazer?")

                CartaoDePassos(
                    passos = passosDeSeguranca,
                    corNumero = MaterialTheme.colorScheme.primary,
                    fundoNumero = MaterialTheme.colorScheme.primaryContainer
                )

                CartaoPecaAjuda(
                    contatos = contatos,
                    onLigar = onLigar,
                    onGerenciar = onGerenciarContatos,
                    titulo = "Ligue para quem você confia"
                )

                TituloDeSecao(texto = "Já fez o pagamento ou passou dados:")

                CartaoDePassos(
                    passos = passosAposGolpe,
                    corNumero = cores.perigoTexto,
                    fundoNumero = cores.perigoFundo
                )

                BotaoDenunciar(
                    onClick = onDenunciar
                )
            }
        }
    }
}

@Composable
private fun CartaoDePassos(
    passos: List<String>,
    corNumero: Color,
    fundoNumero: Color
) {
    CartaoBranco {
        passos.forEachIndexed { indice, passo ->

            if (indice > 0) {
                DivisorSuave()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
                    .padding(
                        horizontal = 18.dp,
                        vertical = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(fundoNumero),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (indice + 1).toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = corNumero
                    )
                }

                Text(
                    text = passo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BotaoDenunciar(
    onClick: () -> Unit
) {
    val cores = ProtegeTema.cores

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BotaoPrincipal(
            texto = "Denuncie Aqui!",
            onClick = onClick,
            icone = Icons.Filled.Phone,
            corFundo = cores.botaoPerigo,
            corTexto = cores.sobreBotaoPerigo,
            alturaMinima = 68.dp
        )

        Text(
            text = "Abre o WhatsApp para pedir orientação ou fazer uma denúncia.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
