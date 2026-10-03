package org.codeberg.assertix.openpos.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onSwitchDatabase: () -> Unit
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
                FormTextField(
                    value = "АТОЛ 22Ф (USB / COM3)",
                    onValueChange = {},
                    readOnly = true,
                    label = stringResource(Res.string.default_printer),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}