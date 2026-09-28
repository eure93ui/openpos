package org.codeberg.assertix.openpos.app.ui.items

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
fun ItemsScreen(viewModel: ItemsViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }

    AppScreenContainer(
        notificationMessage = state.successMessage ?: state.errorMessage,
        isError = state.errorMessage != null,
        onDismissNotification = { viewModel.clearMessages() }
    ) {
            ScreenHeader(
                title = stringResource(Res.string.products_title),
                badgeText = "Всего: ${state.totalItems}",
                onPrimaryActionClick = { viewModel.showAddDialog() },
                primaryActionText = stringResource(Res.string.btn_new_product),
                primaryActionIcon = Res.drawable.add
            )

            SearchField(
                value = state.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = stringResource(Res.string.search_products_placeholder)
            )

            ItemsTable(
                items = state.items,
                totalItems = state.totalItems,
                currentPage = state.currentPage,
                querySize = state.querySize,
                isLoading = state.isLoading,
                onEdit = { viewModel.showEditDialog(it) },
                onDelete = { viewModel.showDeleteDialog(it) },
                onQuerySizeChange = { viewModel.setQuerySize(it) },
                onPreviousPage = { viewModel.previousPage() },
                onNextPage = { viewModel.nextPage() },
                onAddItem = { viewModel.showAddDialog() },
                onClearSearch = { viewModel.setSearchQuery("") }
            )
    }

    if (state.isAddEditDialogVisible) {
        ItemDialog(
            item = state.editingItem,
            onDismiss = { viewModel.hideAddEditDialog() },
            onSave = { name, unit, price ->
                viewModel.saveItem(name, unit, price)
            }
        )
    }

    if (state.isDeleteDialogVisible && state.itemToDelete != null) {
        ItemDeleteDialog(
            item = state.itemToDelete!!,
            onDismiss = { viewModel.hideDeleteDialog() },
            onConfirm = { viewModel.deleteItem(state.itemToDelete!!.id) }
        )
    }
}
