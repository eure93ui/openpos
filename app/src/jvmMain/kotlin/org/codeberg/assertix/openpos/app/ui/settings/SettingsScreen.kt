package org.codeberg.assertix.openpos.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import org.codeberg.assertix.openpos.app.ui.components.AppScreenContainer
import org.codeberg.assertix.openpos.app.ui.components.ScreenHeader
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.io.path.Path

@Composable
fun SettingsScreen(
    initialTab: SettingsTab = SettingsTab.CompanyProfile,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(initialTab) }

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    SettingsTab.CompanyProfile -> CompanyProfileTabContent(state, viewModel)
                    SettingsTab.DatabaseHardware -> DatabaseHardwareTabContent(
                        state = state,
                        onImport = { importLauncher.launch() },
                        onExportData = { exportDataLauncher.launch(suggestedName = "openpos_data", defaultExtension = "sqlite") },
                        onExportSchema = { exportSchemaLauncher.launch(suggestedName = "openpos_schema_empty", defaultExtension = "sqlite") },
                        onSwitchDatabase = { viewModel.switchDatabase() },
                        onUpdateDefaultPrinter = { viewModel.updateDefaultPrinter(it) }
                    )
                    SettingsTab.FinancialConstants -> FinancialConstantsTabContent(state, viewModel)
                }
            }
    }
}
