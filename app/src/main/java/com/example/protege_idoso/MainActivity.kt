package com.example.protege_idoso

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class MainActivity : ComponentActivity() {

    private fun abrirDisqueDenuncia(context: android.content.Context) {

        val numero = "556932168940"

        val mensagem = """
        Olá. Estou recebendo mensagens que podem ser uma tentativa de golpe.
        
        Gostaria de orientação sobre como proceder e, se necessário, realizar uma denúncia.
        
        Tenho as mensagens recebidas e outras informações que podem ajudar na denúncia.
    """.trimIndent()

        val url = "https://wa.me/$numero?text=${Uri.encode(mensagem)}"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Não foi possível abrir o WhatsApp.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val pedirPermissaoNotificacao =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permitido ->
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pedirPermissaoParaMostrarNotificacoes()

        setContent{
            Button(
                onClick = {
                    abrirDisqueDenuncia(this)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Preciso de ajuda")
            }
            ProtegeIdosoScreen(
                onAbrirConfiguracoes = {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    startActivity(intent)
                }
            )
        }
    }

    private fun pedirPermissaoParaMostrarNotificacoes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissao = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissao != PackageManager.PERMISSION_GRANTED) {
                pedirPermissaoNotificacao.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun ProtegeIdosoScreen(
    onAbrirConfiguracoes: () -> Unit
) {
    var analisarWhatsApp by remember { mutableStateOf(true) }
    var alertaSuspeito by remember { mutableStateOf(true) }
    var alertaGolpe by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "ProtegeIdoso",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Proteção contra possíveis golpes em mensagens recebidas pelo WhatsApp.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAbrirConfiguracoes
            ) {
                Text("Ativar leitura de notificações")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = analisarWhatsApp,
                    onCheckedChange = { analisarWhatsApp = it }
                )
                Text("Analisar mensagens do WhatsApp")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = alertaSuspeito,
                    onCheckedChange = { alertaSuspeito = it }
                )
                Text("Mostrar alerta para mensagem suspeita")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = alertaGolpe,
                    onCheckedChange = { alertaGolpe = it }
                )
                Text("Mostrar alerta para possível golpe")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "A permissão de leitura de notificações precisa ser ativada manualmente por segurança do Android.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}