package com.ash.flowr.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = InkDeep,
    onPrimary = Color.White,
    primaryContainer = InkMint,
    onPrimaryContainer = Color(0xFF00200F),
    secondary = Color(0xFF4D6B5A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFE9D9),
    onSecondaryContainer = Color(0xFF092019),
    background = Paper,
    onBackground = InkBlack,
    surface = PaperCard,
    onSurface = InkBlack,
    surfaceVariant = PaperSubtle,
    onSurfaceVariant = InkGray,
    outline = InkFaint,
    outlineVariant = InkLine,
    error = Color(0xFF9B2020),
    onError = Color.White,
    errorContainer = Color(0xFFF8DDD8),
    onErrorContainer = Color(0xFF3B0A0A),
    inverseSurface = Color(0xFF2E2C28),
    inverseOnSurface = Paper,
    inversePrimary = InkForest,
)

private val DarkColorScheme = darkColorScheme(
    primary = InkForest,
    onPrimary = Color(0xFF00351E),
    primaryContainer = Color(0xFF004D2D),
    onPrimaryContainer = InkMint,
    secondary = Color(0xFF9BCCB5),
    onSecondary = Color(0xFF1A3529),
    secondaryContainer = Color(0xFF2E4D3D),
    onSecondaryContainer = Color(0xFFCFE9D9),
    background = NightSurface,
    onBackground = NightText,
    surface = NightCard,
    onSurface = NightText,
    surfaceVariant = NightSubtle,
    onSurfaceVariant = NightMuted,
    outline = Color(0xFF706C65),
    outlineVariant = NightSubtle,
    error = Color(0xFFCF7070),
    onError = Color(0xFF5A0A0A),
    errorContainer = Color(0xFF4A1A1A),
    onErrorContainer = Color(0xFFF8DDD8),
    inverseSurface = PaperSubtle,
    inverseOnSurface = InkBlack,
    inversePrimary = InkDeep,
)

@Composable
fun FlowrTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FlowrTypography,
        content = content
    )
}
