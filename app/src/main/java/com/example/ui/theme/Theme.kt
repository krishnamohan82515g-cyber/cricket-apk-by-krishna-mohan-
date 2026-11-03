package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = CricketGreenLight,
    onPrimary = Color.Black,
    primaryContainer = CricketGreenDark,
    onPrimaryContainer = CricketGreenContainer,
    secondary = CricketGoldLight,
    onSecondary = Color.Black,
    secondaryContainer = CricketOnGoldContainer,
    onSecondaryContainer = CricketGoldContainer,
    tertiary = CricketBallRedLight,
    onTertiary = Color.White,
    tertiaryContainer = CricketBallRed,
    onTertiaryContainer = CricketBallRedContainer,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC2CBC1)
)

private val LightColorScheme = lightColorScheme(
    primary = CricketGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = CricketGreenContainer,
    onPrimaryContainer = CricketOnGreenContainer,
    secondary = CricketGoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = CricketGoldContainer,
    onSecondaryContainer = CricketOnGoldContainer,
    tertiary = CricketBallRed,
    onTertiary = Color.White,
    tertiaryContainer = CricketBallRedContainer,
    onTertiaryContainer = Color(0xFF410002),
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF424941)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep cricket club branding consistent
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
        typography = Typography,
        content = content
    )
}
