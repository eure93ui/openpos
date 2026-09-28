package org.codeberg.assertix.openpos.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import org.codeberg.assertix.openpos.app.ui.components.*
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.data.model.TaxRate
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.io.path.Path

enum class SettingsTab(
    val titleRes: StringResource,
    val icon: DrawableResource
) {
    CompanyProfile(Res.string.tab_company_profile, Res.drawable.title),
    DatabaseHardware(Res.string.tab_database_hardware, Res.drawable.database),
    FinancialConstants(Res.string.tab_financial_constants, Res.drawable.currency_ruble)
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(SettingsTab.CompanyProfile) }

    val importLauncher = rememberFilePickerLauncher(
        type = FileKitType.File(
            extensions = listOf("sqlite", "sqlite3", "db")
        )
    ) { platformFile ->
        platformFile?.file?.path?.let { pathString ->
            viewModel.importDatabase(Path(pathString))
        }
    }

    val exportDataLauncher = rememberFileSaverLauncher(
        dialogSettings = FileKitDialogSettings.createDefault()
    ) { platformFile ->
        platformFile?.file?.path?.let { pathString ->
            viewModel.exportDatabase(Path(pathString))
        }
    }

    val exportSchemaLauncher = rememberFileSaverLauncher(
        dialogSettings = FileKitDialogSettings.createDefault()
    ) { platformFile ->
        platformFile?.file?.path?.let { pathString ->
            viewModel.exportEmptySchema(Path(pathString))
        }
    }

    AppScreenContainer(
        notificationMessage = state.errorMessage ?: if (state.saveSuccess) stringResource(Res.string.invoice_saved) else null,
        isError = state.errorMessage != null,
        onDismissNotification = { viewModel.loadProfile() }
    ) {
            ScreenHeader(
                title = stringResource(Res.string.settings_title),
                badgeText = "OpenPOS v1.0",
                onPrimaryActionClick = { viewModel.saveProfile() },
                primaryActionText = stringResource(Res.string.btn_save_settings),
                primaryActionIcon = Res.drawable.save
            ) {
                if (state.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = stringResource(Res.string.invoice_saving),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            SecondaryTabRow(selectedTab.ordinal) {
                SettingsTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                tab.icon.toImage()
                                Text(stringResource(tab.titleRes))
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            when (selectedTab) {
                SettingsTab.CompanyProfile -> CompanyProfileTabContent(state, viewModel)
                SettingsTab.DatabaseHardware -> DatabaseHardwareTabContent(
                    state = state,
                    onImport = { importLauncher.launch() },
                    onExportData = { exportDataLauncher.launch(suggestedName = "openpos_data", defaultExtension = "sqlite") },
                    onExportSchema = { exportSchemaLauncher.launch(suggestedName = "openpos_schema_empty", defaultExtension = "sqlite") }
                )
                SettingsTab.FinancialConstants -> FinancialConstantsTabContent(state, viewModel)
            }
    }
}

@Composable
private fun CompanyProfileTabContent(
    state: SettingsUiState,
    viewModel: SettingsViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormSectionCard(title = "Основные реквизиты компании") {
            FormRow {
                FormTextField(
                    value = state.legalName,
                    onValueChange = { viewModel.updateLegalName(it) },
                    label = stringResource(Res.string.legal_name),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            FormRow {
                FormTextField(
                    value = state.inn,
                    onValueChange = { viewModel.updateInn(it) },
                    label = stringResource(Res.string.tbl_inn),
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    value = state.kpp,
                    onValueChange = { viewModel.updateKpp(it) },
                    label = stringResource(Res.string.kpp),
                    modifier = Modifier.weight(1f)
                )
            }

            FormRow {
                FormTextField(
                    value = state.address,
                    onValueChange = { viewModel.updateAddress(it) },
                    label = stringResource(Res.string.legal_address),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        FormSectionCard(title = "Банковские реквизиты") {
            FormRow {
                FormTextField(
                    value = state.bankName,
                    onValueChange = { viewModel.updateBankName(it) },
                    label = stringResource(Res.string.bank_name),
                    modifier = Modifier.weight(2f)
                )
                FormTextField(
                    value = state.bic,
                    onValueChange = { viewModel.updateBic(it) },
                    label = stringResource(Res.string.bic),
                    modifier = Modifier.weight(1f)
                )
            }

            FormRow {
                FormTextField(
                    value = state.account,
                    onValueChange = { viewModel.updateAccount(it) },
                    label = stringResource(Res.string.checking_account),
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    value = state.corrAccount,
                    onValueChange = { viewModel.updateCorrAccount(it) },
                    label = stringResource(Res.string.corr_account),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DatabaseHardwareTabContent(
    state: SettingsUiState,
    onImport: () -> Unit,
    onExportData: () -> Unit,
    onExportSchema: () -> Unit
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

@Composable
private fun FinancialConstantsTabContent(
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
