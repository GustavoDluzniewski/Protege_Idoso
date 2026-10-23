package com.example.protege_idoso

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.protege_idoso.contatos.ContatosRepository
import com.example.protege_idoso.historico.HistoricoRepository
import com.example.protege_idoso.quiz.QuizRepository
import com.example.protege_idoso.ui.AceiteScreen
import com.example.protege_idoso.ui.ProtegeIdosoScreen
import com.example.protege_idoso.ui.theme.Protege_IdosoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var preferencias: Preferencias


    private var protecaoAtiva by mutableStateOf(false)
    private var notificacoesPermitidas by mutableStateOf(true)


    private var termosAceitos by mutableStateOf(false)

    private var alertasPendentes by mutableStateOf(0)
    private var totalContatos by mutableStateOf(0)
    private var desafioLiberadoEm by mutableStateOf(0L)
    private var desafioEmAndamento by mutableStateOf(false)

    private fun abrirDisqueDenuncia(context: Context) {
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

    private fun atualizarContadores() {
        lifecycleScope.launch {
            try {
                val (pendentes, contatos) = withContext(Dispatchers.IO) {
                    HistoricoRepository(applicationContext).contarPendentes() to
                            ContatosRepository(applicationContext).contar()
                }
                alertasPendentes = pendentes
                totalContatos = contatos
            } catch (e: Exception) {
                android.util.Log.e(
                    "ProtegeIdoso",
                    "Não foi possível ler os contadores: ${e.javaClass.simpleName}"
                )
            }
        }
    }

    private fun atualizarDesafio() {
        val quiz = QuizRepository(applicationContext)
        desafioLiberadoEm = quiz.proximaLiberacao()
        desafioEmAndamento = quiz.rodadaEmAndamento
    }

    private val pedirPermissaoNotificacao =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            atualizarEstados()
        }

    private fun verificarProtecaoAtiva(): Boolean {
        return NotificationManagerCompat
            .getEnabledListenerPackages(this)
            .contains(packageName)
    }

    private fun atualizarEstados() {
        termosAceitos = preferencias.aceitouTermosAtuais()
        protecaoAtiva = verificarProtecaoAtiva()
        notificacoesPermitidas = NotificationManagerCompat
            .from(this)
            .areNotificationsEnabled()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        preferencias = Preferencias(this)

        atualizarEstados()


        if (termosAceitos) {
            pedirPermissaoParaMostrarNotificacoes()
        }

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

            Protege_IdosoTheme {
                if (!termosAceitos) {
                    AceiteScreen(
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
                        onAceitar = {
                            preferencias.registrarAceite()
                            termosAceitos = true
                            pedirPermissaoParaMostrarNotificacoes()
                        },
                        onRecusar = {
                            finish()
                        }
                    )
                } else ProtegeIdosoScreen(
                    protecaoAtiva = protecaoAtiva,
                    notificacoesPermitidas = notificacoesPermitidas,
                    analisarWhatsApp = analisarWhatsApp,
                    avisoSuspeito = avisoSuspeito,
                    avisoGolpe = avisoGolpe,
                    versao = BuildConfig.VERSION_NAME,
                    alertasPendentes = alertasPendentes,
                    totalContatos = totalContatos,
                    desafioLiberadoEm = desafioLiberadoEm,
                    desafioEmAndamento = desafioEmAndamento,
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
                        abrirConfiguracao(
                            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        )
                    },
                    onPermitirAvisos = {
                        abrirConfiguracao(
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        )
                    },
                    onPedirAjuda = {
                        abrirDisqueDenuncia(this)
                    },
                    onAbrirInstrucoes = {
                        startActivity(Intent(this, InstrucoesActivity::class.java))
                    },
                    onAbrirHistorico = {
                        startActivity(HistoricoActivity.criarIntent(this))
                    },
                    onAbrirDesafio = {
                        startActivity(QuizActivity.criarIntent(this))
                    },
                    onAbrirContatos = {
                        startActivity(ContatosActivity.criarIntent(this))
                    },
                    onCadastrarContato = {
                        startActivity(ContatosActivity.criarIntent(this, novoContato = true))
                    },
                    onAbrirPrivacidade = {
                        startActivity(Intent(this, PrivacidadeActivity::class.java))
                    },
                    onAbrirTermos = {
                        startActivity(
                            DocumentoLegalActivity.criarIntent(
                                this,
                                DocumentoLegalActivity.TIPO_TERMOS
                            )
                        )
                    },
                    onAbrirPolitica = {
                        startActivity(
                            DocumentoLegalActivity.criarIntent(
                                this,
                                DocumentoLegalActivity.TIPO_PRIVACIDADE
                            )
                        )
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarEstados()
        atualizarContadores()
        atualizarDesafio()
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
