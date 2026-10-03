package org.codeberg.assertix.openpos.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.components.FormRow
import org.codeberg.assertix.openpos.app.ui.components.FormSectionCard
import org.codeberg.assertix.openpos.app.ui.components.FormTextField
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DatabaseHardwareTabContent(
    state: SettingsUiState,
    onImport: () -> Unit,
    onExportData: () -> Unit,
    onExportSchema: () -> Unit,
    onSwitchDatabase: () -> Unit,
    onUpdateDefaultPrinter: (String?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormSectionCard(title = stringResource(Res.string.tab_database_hardware)) {
            FormRow {
                FormTextField(
                    value = state.databasePath,
                    onValueChange = {},
                    readOnly = true,
                    label = stringResource(Res.string.active_db_path),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedButton(
                onClick = onSwitchDatabase,
                modifier = Modifier.fillMaxWidth()
            ) {
                Res.drawable.database.toImage()
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.btn_switch_db))
            }

            OutlinedButton(
                onClick = onImport,
                modifier = Modifier.fillMaxWidth()
            ) {
                Res.drawable.upload_file.toImage()
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.btn_import_db))
            }

            OutlinedButton(
                onClick = onExportData,
                modifier = Modifier.fillMaxWidth()
            ) {
                Res.drawable.save.toImage()
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.btn_export_db))
            }

            OutlinedButton(
                onClick = onExportSchema,
                modifier = Modifier.fillMaxWidth()
            ) {
                Res.drawable.database.toImage()
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.btn_export_empty_db))
            }
        }

        FormSectionCard(title = stringResource(Res.string.pos_printers)) {
            FormRow {
                var expanded by remember { mutableStateOf(false) }
                val currentPrinter = state.defaultPrinter ?: "Не использовать (диалог ОС)"

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = currentPrinter,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(Res.string.default_printer)) },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Text("▼")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Не использовать (всегда показывать диалог ОС)") },
                            onClick = {
                                onUpdateDefaultPrinter(null)
                                expanded = false
                            }
                        )
                        state.availablePrinters.forEach { printerName ->
                            DropdownMenuItem(
                                text = { Text(printerName) },
                                onClick = {
                                    onUpdateDefaultPrinter(printerName)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            Text(
                text = "Если принтер не выбран или установлено «Не использовать», при печати накладных каждый раз будет открываться стандартный диалог выбора принтера в операционной системе.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
