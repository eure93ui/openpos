package org.codeberg.assertix.openpos.app.ui.invoice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import org.codeberg.assertix.openpos.app.ui.clients.ClientDialog
import org.codeberg.assertix.openpos.app.ui.components.*
import org.codeberg.assertix.openpos.app.ui.items.ItemDialog
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.io.path.Path

@Composable
fun InvoiceEditorScreen(
    editInvoiceId: Int? = null,
    onReturn: () -> Unit = {},
    viewModel: InvoiceEditorViewModel = koinViewModel(
        key = editInvoiceId?.toString() ?: "new",
        parameters = { parametersOf(editInvoiceId) }
    )
) {
    val state by viewModel.uiState.collectAsState()
    var showResetConfirmation by remember { mutableStateOf(false) }

    val fileSaverLauncher = rememberFileSaverLauncher(
        dialogSettings = FileKitDialogSettings.createDefault()
    ) { platformFile ->
        platformFile?.file?.path?.let { pathString ->
            viewModel.exportPdf(Path(pathString))
        }
    }

    AppScreenContainer(
        notificationMessage = state.successMessage ?: state.generalError,
        isError = state.generalError != null,
        onDismissNotification = { viewModel.clearMessages() }
    ) {
        ScreenHeader(
            title = if (state.isExisting) state.invoiceNumber else stringResource(Res.string.invoice_title),
            badgeText = if (state.isExisting) state.issueDate else "${state.invoiceNumber} | ${state.issueDate}",
            onReturnClick = if (state.isExisting) onReturn else null,
            onSecondaryActionClick = {
                fileSaverLauncher.launch(
                    suggestedName = "invoice_${state.invoiceNumber}",
                    defaultExtension = "pdf"
                )
            },
            secondaryActionText = stringResource(Res.string.btn_export_pdf),
            secondaryActionIcon = Res.drawable.upload_file,
            primaryActionContent = {
                PrintSplitButton(
                    isProcessing = state.isPdfProcessing,
                    onPrintActive = { viewModel.printActive() },
                    onOpenExternal = { viewModel.printExternal() },
                    onOpenPreview = { viewModel.openPreview() }
                )
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                IconButton(onClick = { showResetConfirmation = true }) {
                    Res.drawable.close.toImage()
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (state.isSaving) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = if (state.isSaving) stringResource(Res.string.invoice_saving) else stringResource(Res.string.invoice_saved),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    { viewModel.save() },
                ) {
                    val text = stringResource(Res.string.btn_save_settings)
                    Text(text)
                }
            }
        }

        // Client Selection Section
        InvoiceClientSection(
            clientSearchQuery = state.clientSearchQuery,
            selectedClient = state.selectedClient,
            clients = state.clients,
            isClientDropdownExpanded = state.isClientDropdownExpanded,
            clientError = state.clientError,
            onClientSearchQueryChange = { viewModel.setClientSearchQuery(it) },
            onRemoveClient = { viewModel.removeClient() },
            onToggleClientDropdown = { viewModel.setClientDropdownExpanded(it) },
            onSelectClient = { viewModel.updateClient(it) },
            onNewClientClick = { viewModel.showNewClientDialog(true) }
        )

        // Status and Date Metadata Section
        InvoiceMetadataSection(
            status = state.status,
            issueDate = state.issueDate,
            taxPercent = state.taxPercent,
            onStatusChange = { viewModel.updateStatus(it) },
            onIssueDateChange = { viewModel.updateIssueDate(it) },
            onTaxPercentChange = { viewModel.updateVat(it) }
        )

        // Items Table & Product Search Section
        InvoiceItemsSection(
            items = state.items,
            itemsError = state.itemsError,
            productSearchQuery = state.productSearchQuery,
            searchResults = state.searchResults,
            isProductDropdownExpanded = state.isProductDropdownExpanded,
            onProductSearchQueryChange = { viewModel.setProductSearchQuery(it) },
            onSelectProduct = { viewModel.addProduct(it) },
            onUpdateQuantity = { id, qty -> viewModel.updateItemQuantity(id, qty) },
            onUpdatePrice = { id, price -> viewModel.updateItemPrice(id, price) },
            onRemoveItem = { viewModel.removeItem(it) },
            onNewProductClick = { viewModel.showNewProductDialog(true) }
        )

        // Footer Summary Section
        InvoiceSummarySection(
            notes = state.notes,
            subtotal = state.subtotal,
            taxPercent = state.taxPercent,
            taxAmount = state.taxAmount,
            grandTotal = state.grandTotal,
            onNotesChange = { viewModel.updateNotes(it) },
            onVatChange = { viewModel.updateVat(it) }
        )
    }

    if (state.isNewClientDialogVisible) {
        ClientDialog(
            client = null,
            onDismiss = { viewModel.showNewClientDialog(false) },
            onSave = { name, surname, middleName, phone ->
                viewModel.saveNewClient(name, surname, middleName, phone)
            }
        )
    }

    if (state.isNewProductDialogVisible) {
        ItemDialog(
            item = null,
            onDismiss = { viewModel.showNewProductDialog(false) },
            onSave = { name, unit, price ->
                viewModel.saveNewProduct(name, unit, price)
            }
        )
    }

    if (state.isPdfPreviewVisible) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.preview_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Button(onClick = { viewModel.closePreview() }) {
                        Text(stringResource(Res.string.btn_close))
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        state.previewImages.forEachIndexed { index, bufferedImage ->
                            Image(
                                bitmap = bufferedImage.toComposeImageBitmap(),
                                contentDescription = "Page ${index + 1}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            )
                        }
                    }
                }
            }
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text(stringResource(Res.string.reset_invoice_title)) },
            text = { Text(stringResource(Res.string.reset_invoice_desc)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetInvoice()
                    showResetConfirmation = false
                }) { Text(stringResource(Res.string.btn_reset)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) { Text(stringResource(Res.string.btn_cancel)) }
            }
        )
    }
}

@Composable
private fun PrintSplitButton(
    isProcessing: Boolean,
    onPrintActive: () -> Unit,
    onOpenExternal: () -> Unit,
    onOpenPreview: () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(40.dp).padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TextButton(
                onClick = onPrintActive,
                enabled = !isProcessing,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Res.drawable.print.toImage()
                }
                Spacer(Modifier.width(4.dp))
                Text(stringResource(Res.string.btn_print))
            }

            VerticalDivider(
                modifier = Modifier.height(20.dp).width(1.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
            )

            Box {
                IconButton(
                    onClick = { dropdownExpanded = true },
                    enabled = !isProcessing,
                    modifier = Modifier.size(32.dp)
                ) {
                    Res.drawable.arrow_forward.toImage()
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.invoice_open_external)) },
                        onClick = {
                            dropdownExpanded = false
                            onOpenExternal()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.invoice_open_preview)) },
                        onClick = {
                            dropdownExpanded = false
                            onOpenPreview()
                        }
                    )
                }
            }
        }
    }
}
