package org.codeberg.assertix.openpos.app.ui.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.codeberg.assertix.openpos.app.ui.UiConstants
import org.codeberg.assertix.openpos.app.ui.components.StatusBadge
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceStatus
import androidx.compose.ui.Alignment
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InvoiceMetadataSection(
    status: InvoiceStatus,
    issueDate: String,
    taxPercent: Int,
    onStatusChange: (InvoiceStatus) -> Unit,
    onIssueDateChange: (String) -> Unit,
    onTaxPercentChange: (Int) -> Unit
) {
    var statusExpanded by remember { mutableStateOf(false) }
    var taxExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = try {
            LocalDate.parse(issueDate)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedLocalDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onIssueDateChange(selectedLocalDate.toString())
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(Res.string.btn_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text(stringResource(Res.string.btn_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.SpacingMedium)
    ) {
        // Status Dropdown
        Box(modifier = Modifier.weight(UiConstants.WeightDefault)) {
            OutlinedCard(
                onClick = { statusExpanded = true },
                modifier = Modifier.fillMaxWidth().height(UiConstants.ButtonHeightLarge),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = UiConstants.SpacingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(Res.string.invoice_status_label), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    StatusBadge(status = status)
                }
            }

            DropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                InvoiceStatus.entries.forEach { itemStatus ->
                    DropdownMenuItem(
                        text = { StatusBadge(status = itemStatus) },
                        onClick = {
                            onStatusChange(itemStatus)
                            statusExpanded = false
                        }
                    )
                }
            }
        }

        // Tax Percent Dropdown
        Box(modifier = Modifier.weight(UiConstants.WeightDefault)) {
            OutlinedCard(
                onClick = { taxExpanded = true },
                modifier = Modifier.fillMaxWidth().height(UiConstants.ButtonHeightLarge),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = UiConstants.SpacingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(Res.string.vat_rate), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$taxPercent%", style = MaterialTheme.typography.bodyMedium)
                }
            }

            DropdownMenu(
                expanded = taxExpanded,
                onDismissRequest = { taxExpanded = false }
            ) {
                listOf(0, 5, 7, 20).forEach { rate ->
                    DropdownMenuItem(
                        text = { Text("$rate%") },
                        onClick = {
                            onTaxPercentChange(rate)
                            taxExpanded = false
                        }
                    )
                }
            }
        }

        // Issue Date Field with Picker Trigger
        Box(modifier = Modifier.weight(UiConstants.WeightDefault)) {
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth().height(UiConstants.ButtonHeightLarge),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = UiConstants.SpacingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(stringResource(Res.string.invoice_date_label), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(issueDate, style = MaterialTheme.typography.bodyMedium)
                    }
                    IconButton(onClick = { showDatePicker = true }) {
                        Res.drawable.history.toImage()
                    }
                }
            }
        }
    }
}
