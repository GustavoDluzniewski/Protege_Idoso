package com.example.protege_idoso.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Color.White,
    primaryContainer = AzulGelo,
    onPrimaryContainer = AzulProfundo,
    secondary = AzulVivo,
    onSecondary = Color.White,
    secondaryContainer = AzulGelo,
    onSecondaryContainer = AzulTinta,
    tertiary = VerdeSeguro,
    onTertiary = Color.White,
    tertiaryContainer = VerdeSeguroFundo,
    onTertiaryContainer = VerdeSeguro,
    background = AzulNevoa,
    onBackground = AzulTinta,
    surface = Color.White,
    onSurface = AzulTinta,
    surfaceVariant = AzulGelo,
    onSurfaceVariant = AzulCinza,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = AzulGelo,
    outline = AzulContorno,
    outlineVariant = AzulContorno,
    error = VermelhoPerigo,
    onError = Color.White,
    errorContainer = VermelhoPerigoFundo,
    onErrorContainer = VermelhoPerigoTexto
)

private val EsquemaEscuro = darkColorScheme(
    primary = NoiteAzul,
    onPrimary = NoiteAzulEscuro,
    primaryContainer = NoiteSuperficieAlta,
    onPrimaryContainer = NoiteTexto,
    secondary = NoiteAzul,
    onSecondary = NoiteAzulEscuro,
    secondaryContainer = NoiteSuperficieAlta,
    onSecondaryContainer = NoiteTexto,
    tertiary = NoiteSeguroTexto,
    onTertiary = NoiteSeguroFundo,
    tertiaryContainer = NoiteSeguroFundo,
    onTertiaryContainer = NoiteSeguroTexto,
    background = NoiteFundo,
    onBackground = NoiteTexto,
    surface = NoiteSuperficie,
    onSurface = NoiteTexto,
    surfaceVariant = NoiteSuperficieAlta,
    onSurfaceVariant = NoiteTextoSecundario,
    surfaceContainerLowest = NoiteSuperficie,
    surfaceContainerLow = NoiteSuperficie,
    surfaceContainer = NoiteSuperficie,
    surfaceContainerHigh = NoiteSuperficieAlta,
    surfaceContainerHighest = NoiteSuperficieAlta,
    outline = NoiteContorno,
    outlineVariant = NoiteContorno,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = NoitePerigoFundo,
    onErrorContainer = NoitePerigoTexto
)

@Immutable
data class CoresProtege(
    val escuro: Boolean,
    val gradienteTopo: Color,
    val gradienteBase: Color,
    val sobreGradiente: Color,
    val alertaFundo: Color,
    val alertaTexto: Color,
    val perigoFundo: Color,
    val perigoTexto: Color,
    val seguroFundo: Color,
    val seguroTexto: Color,
    val sombra: Color,
    val botaoPerigo: Color,
    val sobreBotaoPerigo: Color
)

private val CoresClaras = CoresProtege(
    escuro = false,
    gradienteTopo = AzulProfundo,
    gradienteBase = AzulVivo,
    sobreGradiente = Color.White,
    alertaFundo = LaranjaAlertaFundo,
    alertaTexto = LaranjaAlertaTexto,
    perigoFundo = VermelhoPerigoFundo,
    perigoTexto = VermelhoPerigoTexto,
    seguroFundo = VerdeSeguroFundo,
    seguroTexto = VerdeSeguro,
    sombra = AzulPrincipal,
    botaoPerigo = VermelhoPerigo,
    sobreBotaoPerigo = Color.White
)

private val CoresEscuras = CoresProtege(
    escuro = true,
    gradienteTopo = NoiteGradienteTopo,
    gradienteBase = NoiteGradienteBase,
    sobreGradiente = Color.White,
    alertaFundo = NoiteAlertaFundo,
    alertaTexto = NoiteAlertaTexto,
    perigoFundo = NoitePerigoFundo,
    perigoTexto = NoitePerigoTexto,
    seguroFundo = NoiteSeguroFundo,
    seguroTexto = NoiteSeguroTexto,
    sombra = Color.Black,
    botaoPerigo = Color(0xFFB3261E),
    sobreBotaoPerigo = Color.White
)

private val LocalCoresProtege = staticCompositionLocalOf { CoresClaras }

object ProtegeTema {
    val cores: CoresProtege
        @Composable
        @ReadOnlyComposable
        get() = LocalCoresProtege.current
}

@Composable
fun Protege_IdosoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EsquemaEscuro else EsquemaClaro
    val coresExtras = if (darkTheme) CoresEscuras else CoresClaras

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)

            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = !darkTheme

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }

    CompositionLocalProvider(LocalCoresProtege provides coresExtras) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
