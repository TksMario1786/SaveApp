package com.example.saveapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SaveAppGreenPrimaryDark,
    onPrimary = SaveAppGreenOnPrimaryDark,
    primaryContainer = SaveAppGreenPrimaryContainerDark,
    onPrimaryContainer = SaveAppGreenOnPrimaryContainerDark,
    secondary = SaveAppGreenSecondary,
    onSecondary = SaveAppGreenOnSecondary,
    secondaryContainer = SaveAppGreenSecondaryContainer,
    onSecondaryContainer = SaveAppGreenOnSecondaryContainer,
    tertiary = SaveAppGreenTertiary,
    onTertiary = SaveAppGreenOnTertiary,
    background = SaveAppOnBackground,
    onBackground = SaveAppBackground,
    surface = SaveAppOnSurface,
    onSurface = SaveAppSurface,
    surfaceVariant = SaveAppOnSurfaceVariant,
    onSurfaceVariant = SaveAppSurfaceVariant,
    outline = SaveAppOutline,
)

private val LightColorScheme = lightColorScheme(
    primary = SaveAppGreenPrimary,
    onPrimary = SaveAppGreenOnPrimary,
    primaryContainer = SaveAppGreenPrimaryContainer,
    onPrimaryContainer = SaveAppGreenOnPrimaryContainer,
    secondary = SaveAppGreenSecondary,
    onSecondary = SaveAppGreenOnSecondary,
    secondaryContainer = SaveAppGreenSecondaryContainer,
    onSecondaryContainer = SaveAppGreenOnSecondaryContainer,
    tertiary = SaveAppGreenTertiary,
    onTertiary = SaveAppGreenOnTertiary,
    tertiaryContainer = SaveAppGreenTertiaryContainer,
    onTertiaryContainer = SaveAppGreenOnTertiaryContainer,
    background = SaveAppBackground,
    onBackground = SaveAppOnBackground,
    surface = SaveAppSurface,
    onSurface = SaveAppOnSurface,
    surfaceVariant = SaveAppSurfaceVariant,
    onSurfaceVariant = SaveAppOnSurfaceVariant,
    outline = SaveAppOutline,
    outlineVariant = SaveAppOutlineVariant
)

@Composable
fun SaveAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Se establece en false por defecto para forzar la paleta verde de SaveApp
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
