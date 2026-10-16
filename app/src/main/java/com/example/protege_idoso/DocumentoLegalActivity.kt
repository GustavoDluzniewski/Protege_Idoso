package com.example.protege_idoso

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme


class DocumentoLegalActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_TIPO = "tipo_do_documento"
        const val TIPO_TERMOS = "termos"
        const val TIPO_PRIVACIDADE = "privacidade"

        fun criarIntent(context: Context, tipo: String): Intent {
            return Intent(context, DocumentoLegalActivity::class.java)
                .putExtra(EXTRA_TIPO, tipo)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tipo = intent.getStringExtra(EXTRA_TIPO) ?: TIPO_TERMOS
        val politica = tipo == TIPO_PRIVACIDADE

        val titulo = if (politica) "Política de Privacidade" else "Termos de Uso"
        val secoes = if (politica) {
            TextosLegais.POLITICA_DE_PRIVACIDADE
        } else {
            TextosLegais.TERMOS_DE_USO
        }

        setContent {
            Protege_IdosoTheme {
                DocumentoLegalScreen(
                    titulo = titulo,
                    secoes = secoes,
                    onVoltar = { finish() }
                )
            }
        }
    }
}

@Composable
fun DocumentoLegalScreen(
    titulo: String,
    secoes: List<SecaoLegal>,
    onVoltar: () -> Unit
) {
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.semantics {
                            heading()
                        }
                    )

                    Text(
                        text = "Versão ${DadosLegais.VERSAO} · Atualizado em ${DadosLegais.DATA_ATUALIZACAO}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                secoes.forEach { secao ->
                    SecaoDoDocumento(secao)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SecaoDoDocumento(secao: SecaoLegal) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Text(
            text = secao.titulo,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics {
                heading()
            }
        )

        secao.paragrafos.forEach { paragrafo ->
            if (paragrafo.startsWith("• ")) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = paragrafo.removePrefix("• "),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Text(
                    text = paragrafo,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
