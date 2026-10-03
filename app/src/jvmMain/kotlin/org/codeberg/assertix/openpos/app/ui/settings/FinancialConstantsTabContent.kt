package org.codeberg.assertix.openpos.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.components.FormRow
import org.codeberg.assertix.openpos.app.ui.components.FormSectionCard
import org.codeberg.assertix.openpos.app.ui.components.FormTextField
import org.codeberg.assertix.openpos.data.model.TaxRate
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.invoice_prefix
import org.codeberg.assertix.openpos.resources.vat_rate
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FinancialConstantsTabContent(
    state: SettingsUiState,
    viewModel: SettingsViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormSectionCard(title = "Финансовые константы и налоги") {
            FormRow {
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = "${state.taxPercent}%",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(Res.string.vat_rate)) },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Text("▼")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        TaxRate.all.forEach { rate ->
                            DropdownMenuItem(
                                text = { Text("${rate.percent}%") },
                                onClick = {
                                    viewModel.updateTaxPercent(rate.percent)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                FormTextField(
                    value = state.invoicePrefix,
                    onValueChange = { viewModel.updateInvoicePrefix(it) },
                    label = stringResource(Res.string.invoice_prefix),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Установленная ставка НДС и префикс накладных применяются по умолчанию ко всем новым создаваемым документам в системе.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}