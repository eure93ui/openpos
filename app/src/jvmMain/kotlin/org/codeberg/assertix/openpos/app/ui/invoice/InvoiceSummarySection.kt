package org.codeberg.assertix.openpos.app.ui.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.UiConstants
import java.math.BigDecimal

@Composable
internal fun InvoiceSummarySection(
    totalPrice: BigDecimal,
    taxPercent: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        Surface(
            modifier = Modifier.width(360.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            tonalElevation = UiConstants.TonalElevationLow
        ) {
            Column(
                modifier = Modifier.padding(UiConstants.SpacingMedium),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(UiConstants.SpacingSmall)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "ИТОГО (в т.ч. НДС $taxPercent%):",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "$totalPrice ₽",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
