package cl.oursour.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Carbon,
    onPrimary = Crema,
    primaryContainer = CarbonSuave,
    onPrimaryContainer = Crema,
    secondary = Dorado,
    onSecondary = Carbon,
    secondaryContainer = DoradoClaro,
    onSecondaryContainer = DoradoOscuro,
    tertiary = DoradoOscuro,
    background = Crema,
    onBackground = Carbon,
    surface = Crema,
    onSurface = Carbon,
    surfaceVariant = CremaOscura,
    onSurfaceVariant = CarbonSuave,
    surfaceContainer = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainerHigh = CremaOscura,
    outline = Color(0xFFB8A98C),
    error = Rojo
)

private val DarkColorScheme = darkColorScheme(
    primary = DoradoNoche,
    onPrimary = Carbon,
    primaryContainer = DoradoOscuro,
    onPrimaryContainer = DoradoClaro,
    secondary = DoradoNoche,
    onSecondary = Carbon,
    secondaryContainer = DoradoOscuro,
    onSecondaryContainer = DoradoClaro,
    tertiary = DoradoClaro,
    background = FondoNoche,
    onBackground = Crema,
    surface = FondoNoche,
    onSurface = Crema,
    surfaceVariant = SuperficieNoche,
    onSurfaceVariant = CremaOscura,
    surfaceContainer = SuperficieNoche,
    surfaceContainerLow = SuperficieNoche,
    surfaceContainerHigh = CarbonSuave,
    error = Color(0xFFEF9A9A)
)

@Composable
fun OursourTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
