package org.codeberg.assertix.openpos.app.ui.items

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.codeberg.assertix.openpos.app.ui.components.QuerySize
import org.codeberg.assertix.openpos.data.model.Item
import org.codeberg.assertix.openpos.database.api.repository.ItemsRepository
import java.math.BigDecimal

@Stable
data class ItemsUiState(
    val items: List<Item> = emptyList(),
    val totalItems: Long = 0,
    val currentPage: Int = 1,
    val querySize: QuerySize = QuerySize.default,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isAddEditDialogVisible: Boolean = false,
    val editingItem: Item? = null,
    val isDeleteDialogVisible: Boolean = false,
    val itemToDelete: Item? = null
)

@Stable
class ItemsViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ItemsUiState())
    val uiState: StateFlow<ItemsUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _uiState.value
                val sizeVal = state.querySize.size
                val offset = (state.currentPage - 1).toLong() * sizeVal
                val items = itemsRepository.getItems(sizeVal, offset, state.searchQuery)
                val total = itemsRepository.getCount(state.searchQuery)

                val maxPage = if (total > 0) ((total + sizeVal - 1) / sizeVal).toInt() else 1
                val validPage = if (state.currentPage > maxPage) maxPage else state.currentPage

                val finalItems = if (validPage != state.currentPage) {
                    val newOffset = (validPage - 1).toLong() * sizeVal
                    itemsRepository.getItems(sizeVal, newOffset, state.searchQuery)
                } else {
                    items
                }

                _uiState.update {
                    it.copy(
                        items = finalItems,
                        totalItems = total,
                        currentPage = validPage,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Не удалось загрузить товары"
                    )
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, currentPage = 1) }
        loadItems()
    }

    fun setQuerySize(size: QuerySize) {
        _uiState.update { it.copy(querySize = size, currentPage = 1) }
        loadItems()
    }

    fun setPage(page: Int) {
        val state = _uiState.value
        val sizeVal = state.querySize.size
        val maxPage = if (state.totalItems > 0) ((state.totalItems + sizeVal - 1) / sizeVal).toInt() else 1
        val targetPage = page.coerceIn(1, maxPage)
        if (targetPage != state.currentPage) {
            _uiState.update { it.copy(currentPage = targetPage) }
            loadItems()
        }
    }

    fun nextPage() {
        val state = _uiState.value
        val sizeVal = state.querySize.size
        val maxPage = if (state.totalItems > 0) ((state.totalItems + sizeVal - 1) / sizeVal).toInt() else 1
        if (state.currentPage < maxPage) {
            setPage(state.currentPage + 1)
        }
    }

    fun previousPage() {
        val state = _uiState.value
        if (state.currentPage > 1) {
            setPage(state.currentPage - 1)
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(isAddEditDialogVisible = true, editingItem = null, errorMessage = null) }
    }

    fun showEditDialog(item: Item) {
        _uiState.update { it.copy(isAddEditDialogVisible = true, editingItem = item, errorMessage = null) }
    }

    fun hideAddEditDialog() {
        _uiState.update { it.copy(isAddEditDialogVisible = false, editingItem = null) }
    }

    fun showDeleteDialog(item: Item) {
        _uiState.update { it.copy(isDeleteDialogVisible = true, itemToDelete = item, errorMessage = null) }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = false, itemToDelete = null) }
    }

    fun saveItem(productName: String, unitOfMeasure: String, defaultPriceStr: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                if (productName.isBlank()) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Наименование товара обязательно для заполнения") }
                    return@launch
                }
                if (unitOfMeasure.isBlank()) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Единица измерения обязательна для заполнения") }
                    return@launch
                }

                val normalizedPriceStr = defaultPriceStr.trim().replace(',', '.')
                val price = normalizedPriceStr.toBigDecimalOrNull()
                if (price == null || price < BigDecimal.ZERO) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Некорректная цена по умолчанию") }
                    return@launch
                }

                val editing = _uiState.value.editingItem
                if (editing == null) {
                    val newItem = Item(
                        id = 0,
                        productName = productName.trim(),
                        unitOfMeasure = unitOfMeasure.trim(),
                        defaultPrice = price
                    )
                    itemsRepository.add(newItem)
                    _uiState.update { it.copy(successMessage = "Товар успешно добавлен", isAddEditDialogVisible = false) }
                } else {
                    val updatedItem = Item(
                        id = editing.id,
                        productName = productName.trim(),
                        unitOfMeasure = unitOfMeasure.trim(),
                        defaultPrice = price
                    )
                    itemsRepository.update(updatedItem)
                    _uiState.update { it.copy(successMessage = "Товар успешно обновлен", isAddEditDialogVisible = false) }
                }
                loadItems()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Не удалось сохранить товар"
                    )
                }
            }
        }
    }

    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                itemsRepository.delete(itemId)
                _uiState.update { it.copy(successMessage = "Товар удален", isDeleteDialogVisible = false, itemToDelete = null) }
                loadItems()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Не удалось удалить товар"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
