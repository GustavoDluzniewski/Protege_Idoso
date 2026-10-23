package com.example.protege_idoso

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.protege_idoso.ui.CabecalhoDeTela
import com.example.protege_idoso.ui.CartaoBranco
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
        enableEdgeToEdge()
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
    val iconeDocumento = rememberVectorPainter(Icons.Filled.Done)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            CabecalhoDeTela(
                titulo = titulo,
                subtitulo = "Versão ${DadosLegais.VERSAO} · Atualizado em ${DadosLegais.DATA_ATUALIZACAO}",
                icone = iconeDocumento,
                onVoltar = onVoltar
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                secoes.forEach { secao ->
                    SecaoDoDocumento(secao)
                }
            }
        }
    }
}

@Composable
private fun SecaoDoDocumento(secao: SecaoLegal) {
    CartaoBranco {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = secao.titulo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
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
                            color = MaterialTheme.colorScheme.primary
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
}
