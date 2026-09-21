package org.codeberg.assertix.openpos.app.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object UiConstants {
    // Spacings & Paddings
    val SpacingMicro: Dp = 2.dp
    val SpacingTiny: Dp = 4.dp
    val SpacingSmall: Dp = 8.dp
    val SpacingMedium: Dp = 16.dp
    val SpacingLarge: Dp = 24.dp
    val SpacingExtraLarge: Dp = 32.dp

    // Table / Column Widths
    val NumberColumnWidth: Dp = 48.dp
    val InvoiceNumberColumnWidth: Dp = 36.dp
    val UnitColumnWidth: Dp = 80.dp
    val ActionColumnWidthSmall: Dp = 100.dp
    val ActionColumnWidthMedium: Dp = 120.dp
    val PriceColumnWidth: Dp = 60.dp
    val SearchFieldWidthSmall: Dp = 220.dp
    val ActionIconButtonWidth: Dp = 48.dp

    // Row Paddings
    val TableRowHorizontalPadding: Dp = 12.dp
    val TableRowVerticalPadding: Dp = 10.dp
    val InvoiceItemRowVerticalPadding: Dp = 6.dp

    // Heights & Sizes
    val ButtonHeightDefault: Dp = 48.dp
    val ButtonHeightLarge: Dp = 56.dp
    val ChipHeight: Dp = 32.dp
    val IconButtonSize: Dp = 32.dp
    val ProgressIndicatorSize: Dp = 16.dp
    val CircularProgressIndicatorSize: Dp = 48.dp
    val StartupIconSizeLarge: Dp = 64.dp
    val StartupIconSizeMedium: Dp = 32.dp
    val StartupIconSizeSmall: Dp = 16.dp
    val DropdownMaxHeight: Dp = 220.dp
    val SummarySectionHeight: Dp = 100.dp

    // Elevations & Thicknesses
    val TonalElevationLow: Dp = 1.dp
    val TonalElevationMedium: Dp = 4.dp
    val ShadowElevationDefault: Dp = 4.dp
    val ShadowElevationHigh: Dp = 8.dp
    val DividerThicknessDefault: Dp = 1.dp
    val DividerThicknessThin: Dp = 0.5.dp
    val StrokeWidthDefault: Dp = 2.dp
    val StrokeWidthThick: Dp = 4.dp

    // Corner Radii
    val CornerRadiusSmall: Dp = 4.dp
    val CornerRadiusMedium: Dp = 16.dp
    val CornerRadiusLarge: Dp = 24.dp

    // Alphas
    val SurfaceVariantAlpha: Float = 0.5f

    // Column Weights
    val WeightDefault: Float = 1f
    val WeightNameColumnSmall: Float = 2.5f
    val WeightNameColumnLarge: Float = 3f
    val WeightPhoneColumn: Float = 1.5f
    val WeightDateColumn: Float = 1.2f
    val WeightStatusColumn: Float = 1.2f
    val WeightTotalColumn: Float = 1.2f
    val WeightPriceColumn: Float = 1.2f
    val WeightQtyColumn: Float = 1f
    val WeightBankName: Float = 2f

    // Animation / Numbers
    val AnimationDurationMs: Int = 350
}
