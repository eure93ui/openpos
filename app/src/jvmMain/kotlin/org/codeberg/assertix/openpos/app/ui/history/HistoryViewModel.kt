package org.codeberg.assertix.openpos.app.ui.history

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.codeberg.assertix.openpos.app.ui.components.QuerySize
import org.codeberg.assertix.openpos.data.model.invoice.Invoice
import org.codeberg.assertix.openpos.database.api.repository.InvoiceRepository

@Stable
data class HistoryUiState(
    val invoices: List<Invoice> = emptyList(),
    val totalInvoices: Long = 0,
    val currentPage: Int = 1,
    val querySize: QuerySize = QuerySize.default,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isDeleteDialogVisible: Boolean = false,
    val invoiceToDelete: Invoice? = null
)

@Stable
class HistoryViewModel(private val invoiceRepository: InvoiceRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadInvoices()
    }

    fun loadInvoices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _uiState.value
                val sizeVal = state.querySize.size
                val offset = (state.currentPage - 1).toLong() * sizeVal
                val invoices = invoiceRepository.getInvoices(sizeVal, offset, state.searchQuery)
                val total = invoiceRepository.count(state.searchQuery)

                val maxPage = if (total > 0) ((total + sizeVal - 1) / sizeVal).toInt() else 1
                val validPage = if (state.currentPage > maxPage) maxPage else state.currentPage

                val finalInvoices = if (validPage != state.currentPage) {
                    val newOffset = (validPage - 1).toLong() * sizeVal
                    invoiceRepository.getInvoices(sizeVal, newOffset, state.searchQuery)
                } else {
                    invoices
                }

                _uiState.update {
                    it.copy(
                        invoices = finalInvoices,
                        totalInvoices = total,
                        currentPage = validPage,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Не удалось загрузить историю документов"
                    )
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, currentPage = 1) }
        loadInvoices()
    }

    fun setQuerySize(size: QuerySize) {
        _uiState.update { it.copy(querySize = size, currentPage = 1) }
        loadInvoices()
    }

    fun setPage(page: Int) {
        val state = _uiState.value
        val sizeVal = state.querySize.size
        val maxPage = if (state.totalInvoices > 0) ((state.totalInvoices + sizeVal - 1) / sizeVal).toInt() else 1
        val targetPage = page.coerceIn(1, maxPage)
        if (targetPage != state.currentPage) {
            _uiState.update { it.copy(currentPage = targetPage) }
            loadInvoices()
        }
    }

    fun nextPage() {
        val state = _uiState.value
        val sizeVal = state.querySize.size
        val maxPage = if (state.totalInvoices > 0) ((state.totalInvoices + sizeVal - 1) / sizeVal).toInt() else 1
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

    fun showDeleteDialog(invoice: Invoice) {
        _uiState.update { it.copy(isDeleteDialogVisible = true, invoiceToDelete = invoice, errorMessage = null) }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = false, invoiceToDelete = null) }
    }

    fun deleteInvoice(invoiceId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                invoiceRepository.delete(invoiceId)
                _uiState.update { it.copy(successMessage = "Накладная удалена", isDeleteDialogVisible = false, invoiceToDelete = null) }
                loadInvoices()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Не удалось удалить накладную"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
