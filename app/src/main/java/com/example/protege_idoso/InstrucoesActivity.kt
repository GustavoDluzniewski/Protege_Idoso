package com.example.protege_idoso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme

class InstrucoesActivity : ComponentActivity() {

    private fun abrirDisqueDenuncia() {

        val numero = "556932168940"

        val mensagem = """
            Olá. Estou recebendo mensagens que podem ser uma tentativa de golpe.
            
            Gostaria de orientação sobre como proceder e, se necessário, realizar uma denúncia.
            
            Tenho as mensagens recebidas e outras informações que podem ajudar na denúncia.
        """.trimIndent()

        val url = "https://wa.me/$numero?text=${Uri.encode(mensagem)}"

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Não foi possível abrir o WhatsApp.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Protege_IdosoTheme {
                InstrucoesScreen(
                    onDenunciar = {
                        abrirDisqueDenuncia()
                    }
                )
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
    onDenunciar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            CabecalhoInstrucoes()

            Text(
                text = "O que fazer?",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics {
                    heading()
                }
            )

            CartaoDePassos(passos = passosDeSeguranca)

            Text(
                text = "Já fez o pagamento ou passou dados:",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics {
                    heading()
                }
            )

            CartaoDePassos(passos = passosAposGolpe)

            BotaoDenunciar(
                onClick = onDenunciar
            )
        }
    }
}

@Composable
private fun CabecalhoInstrucoes() {
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
                painter = androidx.compose.ui.res.painterResource(R.drawable.ic_stat_shield),
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
                text = "Veja o que fazer agora",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun CartaoDePassos(
    passos: List<String>
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

            passos.forEachIndexed { indice, passo ->

                if (indice > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                LinhaDePasso(texto = passo)
            }
        }
    }
}

@Composable
private fun LinhaDePasso(
    texto: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BotaoDenunciar(
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
                text = "Denuncie Aqui!",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Text(
            text = "Abre o WhatsApp para pedir orientação ou fazer uma denúncia!.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
