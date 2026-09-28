package org.codeberg.assertix.openpos.app.ui.clients

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.components.AppScreenContainer
import org.codeberg.assertix.openpos.app.ui.components.ScreenHeader
import org.codeberg.assertix.openpos.app.ui.components.SearchField
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientsScreen(viewModel: ClientsViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadClients()
    }

    AppScreenContainer(
        notificationMessage = state.successMessage ?: state.errorMessage,
        isError = state.errorMessage != null,
        onDismissNotification = { viewModel.clearMessages() }
    ) {
            ScreenHeader(
                title = stringResource(Res.string.clients_title),
                badgeText = "Всего: ${state.totalClients}",
                onPrimaryActionClick = { viewModel.showAddDialog() },
                primaryActionText = stringResource(Res.string.btn_new_client_action),
                primaryActionIcon = Res.drawable.add
            )

            SearchField(
                value = state.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = stringResource(Res.string.search_clients_placeholder)
            )

            ClientsTable(
                clients = state.clients,
                totalClients = state.totalClients,
                currentPage = state.currentPage,
                querySize = state.querySize,
                isLoading = state.isLoading,
                onEdit = { viewModel.showEditDialog(it) },
                onDelete = { viewModel.showDeleteDialog(it) },
                onQuerySizeChange = { viewModel.setQuerySize(it) },
                onPreviousPage = { viewModel.previousPage() },
                onNextPage = { viewModel.nextPage() },
                onAddClient = { viewModel.showAddDialog() },
                onClearSearch = { viewModel.setSearchQuery("") }
            )
    }

    if (state.isAddEditDialogVisible) {
        ClientDialog(
            client = state.editingClient,
            onDismiss = { viewModel.hideAddEditDialog() },
            onSave = { name, surname, middleName, phone ->
                viewModel.saveClient(name, surname, middleName, phone)
            }
        )
    }

    if (state.isDeleteDialogVisible && state.clientToDelete != null) {
        ClientDeleteDialog(
            client = state.clientToDelete!!,
            onDismiss = { viewModel.hideDeleteDialog() },
            onConfirm = { viewModel.deleteClient(state.clientToDelete!!.id) }
        )
    }
}
