package org.codeberg.assertix.openpos.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import org.codeberg.assertix.openpos.app.ui.UiConstants
import org.codeberg.assertix.openpos.app.ui.theme.AppColors
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceStatus
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.status_draft
import org.codeberg.assertix.openpos.resources.status_issued
import org.codeberg.assertix.openpos.resources.status_paid
import org.codeberg.assertix.openpos.resources.status_shipped
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatusBadge(status: InvoiceStatus) {
    val (textRes, color) = when (status) {
        InvoiceStatus.PAID -> Res.string.status_paid to AppColors.statusGreen
        InvoiceStatus.ISSUED -> Res.string.status_issued to AppColors.statusBlue
        InvoiceStatus.DRAFT -> Res.string.status_draft to AppColors.statusGray
        InvoiceStatus.SHIPPED -> Res.string.status_shipped to Color(0xFF7C3AED)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(UiConstants.CornerRadiusSmall))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = UiConstants.SpacingSmall, vertical = UiConstants.SpacingTiny)
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
