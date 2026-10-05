package com.example.protege_idoso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment


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
            InstrucoesScreen(
                onDenunciar = {
                    abrirDisqueDenuncia()
                }
            )
        }
    }
}


@androidx.compose.runtime.Composable
fun InstrucoesScreen(
    onDenunciar: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "⚠️ Possível golpe identificado",

                style = MaterialTheme.typography.headlineMedium,

                fontWeight = FontWeight.Bold,

                textAlign = TextAlign.Center,

                modifier = Modifier.fillMaxWidth()
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            Text(
                text = "Esta mensagem apresenta características que podem indicar uma tentativa de golpe.",

                style = MaterialTheme.typography.bodyLarge
            )


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            Text(
                text = "O que fazer?",

                style = MaterialTheme.typography.titleLarge,

                fontWeight = FontWeight.Bold
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            Text(
                text =
                    "✓ Não responda à mensagem.\n" +
                            "✓ Não clique em links suspeitos.\n" +
                            "✓ Não informe senhas, códigos ou dados pessoais.\n" +
                            "✓ Não faça pagamentos ou transferências.\n" +
                            "✓ Confirme a informação por um canal oficial.\n" +
                            "✓ Se tiver dúvidas, peça ajuda a uma pessoa de confiança.",

                style = MaterialTheme.typography.bodyLarge
            )


            Spacer(
                modifier = Modifier.height(32.dp)
            )


            LargeFloatingActionButton(
                onClick = onDenunciar,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\uD83D\uDEA8\nDenuncie\nAqui!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
