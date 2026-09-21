package org.codeberg.assertix.openpos.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = AppColors.md_theme_light_primary,
    onPrimary = AppColors.md_theme_light_onPrimary,
    primaryContainer = AppColors.md_theme_light_primaryContainer,
    onPrimaryContainer = AppColors.md_theme_light_onPrimaryContainer,
    secondary = AppColors.md_theme_light_secondary,
    onSecondary = AppColors.md_theme_light_onSecondary,
    secondaryContainer = AppColors.md_theme_light_secondaryContainer,
    onSecondaryContainer = AppColors.md_theme_light_onSecondaryContainer,
    background = AppColors.md_theme_light_background,
    onBackground = AppColors.md_theme_light_onBackground,
    surface = AppColors.md_theme_light_surface,
    onSurface = AppColors.md_theme_light_onSurface,
    surfaceVariant = AppColors.md_theme_light_surfaceVariant,
    onSurfaceVariant = AppColors.md_theme_light_onSurfaceVariant,
    outline = AppColors.md_theme_light_outline
)

@Composable
fun OpenPosTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
