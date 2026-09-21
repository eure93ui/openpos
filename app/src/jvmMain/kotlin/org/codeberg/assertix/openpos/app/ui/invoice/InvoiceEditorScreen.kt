package org.codeberg.assertix.openpos.app.ui.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.clients.ClientDialog
import org.codeberg.assertix.openpos.app.ui.components.*
import org.codeberg.assertix.openpos.app.ui.items.ItemDialog
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

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

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onScreenSwitch()
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
                onPrimaryActionClick = { 
                    viewModel.printInvoice { invoice ->
                        println("Printed invoice: ${invoice.invoiceNumber}")
                    } 
                },
                primaryActionText = stringResource(Res.string.btn_print),
                primaryActionIcon = Res.drawable.print,
                onSecondaryActionClick = { 
                    viewModel.exportPdf { invoice ->
                        println("Exported PDF invoice: ${invoice.invoiceNumber}")
                    } 
                },
                secondaryActionText = stringResource(Res.string.btn_export_pdf)
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

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Сбросить накладную?") },
            text = { Text("Все несохраненные изменения будут потеряны.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetInvoice()
                    showResetConfirmation = false
                }) { Text("Сбросить") }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) { Text("Отмена") }
            }
        )
    }
}
