package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldHover,
    onPrimary = Color.Black,
    primaryContainer = GoldPrimary,
    onPrimaryContainer = Color.White,
    secondary = NeonEmerald,
    onSecondary = Color.Black,
    secondaryContainer = SlateSurfaceDark,
    onSecondaryContainer = TextPrimary,
    tertiary = RubyRed,
    onTertiary = Color.White,
    background = SlateCanvasDark,
    onBackground = TextPrimary,
    surface = SlateSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SlateCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = SlateCardBorder,
    outlineVariant = SlateCardBorderSubtle
)

@Composable
fun LeaseGuardTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
