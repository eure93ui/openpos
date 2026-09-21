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
import org.codeberg.assertix.openpos.app.ui.UiConstants
import org.codeberg.assertix.openpos.app.ui.theme.AppColors
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceStatus

@Composable
fun StatusBadge(status: InvoiceStatus) {
    val (text, color) = when (status) {
        InvoiceStatus.PAID -> "Оплачено" to AppColors.statusGreen
        InvoiceStatus.ISSUED -> "Выставлено" to AppColors.statusBlue
        InvoiceStatus.DRAFT -> "Черновик" to AppColors.statusGray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(UiConstants.CornerRadiusSmall))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = UiConstants.SpacingSmall, vertical = UiConstants.SpacingTiny)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
