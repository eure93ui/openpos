package org.codeberg.assertix.openpos.app.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.components.*
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryScreen(
    onNewInvoice: () -> Unit,
    onEditInvoice: (Int) -> Unit = {},
    viewModel: HistoryViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInvoices()
    }

    AppScreenContainer(
        notificationMessage = state.successMessage ?: state.errorMessage,
        isError = state.errorMessage != null,
        onDismissNotification = { viewModel.clearMessages() }
    ) {
            ScreenHeader(
                title = stringResource(Res.string.history_title),
                badgeText = "Всего: ${state.totalInvoices}",
                onPrimaryActionClick = onNewInvoice,
                primaryActionText = stringResource(Res.string.btn_new_client_action),
                primaryActionIcon = Res.drawable.add
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = stringResource(Res.string.search_history_placeholder),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = "01.10.2024 - 31.10.2024",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(Res.string.period)) },
                    modifier = Modifier.width(220.dp),
                    shape = MaterialTheme.shapes.medium
                )
            }

            HistoryTable(
                invoices = state.invoices,
                totalInvoices = state.totalInvoices,
                currentPage = state.currentPage,
                querySize = state.querySize,
                isLoading = state.isLoading,
                onPrint = { invoice -> println("Print invoice ${invoice.invoiceNumber}") },
                onEdit = { invoice -> onEditInvoice(invoice.id) },
                onDelete = { invoice -> viewModel.showDeleteDialog(invoice) },
                onQuerySizeChange = { viewModel.setQuerySize(it) },
                onPreviousPage = { viewModel.previousPage() },
                onNextPage = { viewModel.nextPage() },
                onNewInvoice = onNewInvoice,
                onClearSearch = { viewModel.setSearchQuery("") }
            )
    }

    if (state.isDeleteDialogVisible && state.invoiceToDelete != null) {
        HistoryDeleteDialog(
            invoice = state.invoiceToDelete!!,
            onDismiss = { viewModel.hideDeleteDialog() },
            onConfirm = { viewModel.deleteInvoice(state.invoiceToDelete!!.id) }
        )
    }
}
