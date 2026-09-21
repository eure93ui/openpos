package org.codeberg.assertix.openpos.app.ui.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.UiConstants
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.invoice_total
import org.codeberg.assertix.openpos.resources.notes_placeholder
import org.codeberg.assertix.openpos.resources.subtotal
import org.codeberg.assertix.openpos.resources.vat_rate
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal

@Composable
internal fun InvoiceSummarySection(
    notes: String,
    subtotal: BigDecimal,
    taxPercent: Int,
    taxAmount: BigDecimal,
    grandTotal: BigDecimal,
    onNotesChange: (String) -> Unit,
    onVatChange: (Int) -> Unit
) {
    var vatExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text(stringResource(Res.string.notes_placeholder)) },
            modifier = Modifier.weight(UiConstants.WeightDefault).height(UiConstants.SummarySectionHeight),
            maxLines = 3,
            shape = MaterialTheme.shapes.medium
        )

        Spacer(Modifier.width(UiConstants.SpacingLarge))

        Surface(
            modifier = Modifier.width(320.dp),
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
                    Text(stringResource(Res.string.subtotal), style = MaterialTheme.typography.bodyMedium)
                    Text("$subtotal ₽", style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(Res.string.vat_rate), style = MaterialTheme.typography.bodyMedium)
                    Box {
                        OutlinedButton(onClick = { vatExpanded = true }) {
                            Text("$taxPercent% ($taxAmount ₽)")
                        }
                        DropdownMenu(
                            expanded = vatExpanded,
                            onDismissRequest = { vatExpanded = false }
                        ) {
                            listOf(0, 5, 7, 20).forEach { rate ->
                                DropdownMenuItem(
                                    text = { Text("$rate%") },
                                    onClick = {
                                        onVatChange(rate)
                                        vatExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = UiConstants.SpacingTiny))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(Res.string.invoice_total),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "$grandTotal ₽",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
