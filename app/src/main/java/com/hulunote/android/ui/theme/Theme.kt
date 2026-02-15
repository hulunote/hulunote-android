package com.hulunote.android.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val HulunoteColorScheme = lightColorScheme(
    primary = PurpleStart,
    onPrimary = CardWhite,
    primaryContainer = AccentPurpleLight,
    onPrimaryContainer = DarkNavy,
    secondary = PurpleEnd,
    onSecondary = CardWhite,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = LightBackground,
    onSurfaceVariant = TextSecondary,
    outline = BorderColor,
    outlineVariant = BorderColor,
)

private val HulunoteShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(30.dp),
)

@Composable
fun HulunoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HulunoteColorScheme,
        typography = HulunoteTypography,
        shapes = HulunoteShapes,
        content = content,
    )
}
