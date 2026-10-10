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
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextAlign
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme
import com.example.protege_idoso.ui.ProtegeIdosoScreen
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner

class MainActivity : ComponentActivity() {

    private lateinit var preferencias: Preferencias

    private fun abrirDisqueDenuncia(context: android.content.Context) {

        val numero = "556932168940"

        val mensagem = """
        Olá. Estou recebendo mensagens que podem ser uma tentativa de golpe.
        
        Gostaria de orientações sobre como proceder e, se necessário, realizar uma denúncia.
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

    private fun verificarProtecaoAtiva(): Boolean {
        return NotificationManagerCompat
            .getEnabledListenerPackages(this)
            .contains(packageName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pedirPermissaoParaMostrarNotificacoes()

        preferencias = Preferencias(this)

        setContent {

            var analisarWhatsApp by remember {
                mutableStateOf(preferencias.analisarWhatsApp)
            }
            var avisoSuspeito by remember {
                mutableStateOf(preferencias.avisoSuspeito)
            }
            var avisoGolpe by remember {
                mutableStateOf(preferencias.avisoGolpe)
            }
            var protecaoAtiva by remember {
                mutableStateOf(verificarProtecaoAtiva())
            }

            val lifecycleOwner = LocalLifecycleOwner.current

            DisposableEffect(Unit) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        protecaoAtiva = verificarProtecaoAtiva()
                    }
                }

                lifecycle.addObserver(observer)

                onDispose {
                    lifecycle.removeObserver(observer)
                }
            }

            Protege_IdosoTheme {
                ProtegeIdosoScreen(
                    protecaoAtiva = protecaoAtiva,
                    analisarWhatsApp = analisarWhatsApp,
                    avisoSuspeito = avisoSuspeito,
                    avisoGolpe = avisoGolpe,
                    onAnalisarWhatsAppChange = {
                        analisarWhatsApp = it
                        preferencias.analisarWhatsApp = it
                    },
                    onAvisoSuspeitoChange = {
                        avisoSuspeito = it
                        preferencias.avisoSuspeito = it
                    },
                    onAvisoGolpeChange = {
                        avisoGolpe = it
                        preferencias.avisoGolpe = it
                    },
                    onAtivarProtecao = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        startActivity(intent)
                    },
                    onPedirAjuda = {
                        abrirDisqueDenuncia(this)
                    },
                    onAbrirInstrucoes = {
                        val intent = Intent(this, InstrucoesActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
    onAbrirConfiguracoes: () -> Unit,
    onAbrirDisqueDenuncia: () -> Unit,
    onAbrirInstrucoes: () -> Unit
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
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAbrirConfiguracoes
            ) {
                Text("⚠\uFE0FAtivar leitura de notificações⚠\uFE0F")
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

            LargeFloatingActionButton(
                onClick = onAbrirDisqueDenuncia,
                modifier = Modifier.size(120.dp),
                shape = CircleShape
            ) {
                Text(
                    text = "\uD83D\uDEA8\nDENUNCIE AQUI!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "A permissão de leitura de notificações precisa ser ativada manualmente por segurança do dispositivo.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = onAbrirInstrucoes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Ver orientações de segurança.⚠\uFE0F",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}