package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SleekColorScheme = lightColorScheme(
    primary = BrandPrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = SurfaceSleekLight,
    onPrimaryContainer = BrandDarkPurple,
    secondary = MatterBarBlue,
    onSecondary = Color.White,
    secondaryContainer = MatterTileBg,
    onSecondaryContainer = MatterTextDeep,
    tertiary = AntimatterBarRed,
    onTertiary = Color.White,
    tertiaryContainer = AntimatterTileBg,
    onTertiaryContainer = AntimatterTextDeep,
    background = CanvasBackground,
    onBackground = TextPrimaryDark,
    surface = SurfacePureWhite,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceSleekLight,
    onSurfaceVariant = TextSecondaryMedium,
    outline = SurfaceBorderLight
)

@Composable
fun ZeroSumTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SleekColorScheme,
        typography = Typography,
        content = content
    )
}


