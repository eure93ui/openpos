package org.codeberg.assertix.openpos.app.ui.history

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.codeberg.assertix.openpos.app.settings.AppSettings
import org.codeberg.assertix.openpos.app.ui.components.QuerySize
import org.codeberg.assertix.openpos.data.model.invoice.Invoice
import org.codeberg.assertix.openpos.database.api.repository.InvoiceRepository
import java.math.BigDecimal
import java.time.LocalDate

@Stable
data class HistoryUiState(
    val invoices: List<Invoice> = emptyList(),
    val totalInvoices: Long = 0,
    val totalPrice: BigDecimal = BigDecimal.ZERO,
    val currentPage: Int = 1,
    val querySize: QuerySize = QuerySize.default,
    val searchQuery: String = "",
    val dateFilterOption: HistoryDateFilterOption = HistoryDateFilterOption.YEAR,
    val customStartDate: LocalDate? = null,
    val customEndDate: LocalDate? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isDeleteDialogVisible: Boolean = false,
    val invoiceToDelete: Invoice? = null
)

@Stable
class HistoryViewModel(
    private val invoiceRepository: InvoiceRepository,
    private val appSettings: AppSettings
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        val savedOptionName = appSettings.historyDateFilterOption.value
        val option = try {
            HistoryDateFilterOption.valueOf(savedOptionName)
        } catch (e: Exception) {
            HistoryDateFilterOption.YEAR
        }
        val startStr = appSettings.historyCustomStartDate.value
        val endStr = appSettings.historyCustomEndDate.value
        val startDate = startStr?.let { try { LocalDate.parse(it) } catch (e: Exception) { null } }
        val endDate = endStr?.let { try { LocalDate.parse(it) } catch (e: Exception) { null } }

        _uiState.update {
            it.copy(
                dateFilterOption = option,
                customStartDate = startDate,
                customEndDate = endDate
            )
        }
        loadInvoices()
    }

    private fun getDateRange(): Pair<LocalDate?, LocalDate?> {
        val state = _uiState.value
        return when (state.dateFilterOption) {
            HistoryDateFilterOption.CUSTOM -> Pair(state.customStartDate, state.customEndDate)
            else -> {
                val range = DateRange.calculate(state.dateFilterOption)
                Pair(range?.startDate, range?.endDate)
            }
        }
    }

    fun loadInvoices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _uiState.value
                val sizeVal = state.querySize.size
                val offset = (state.currentPage - 1).toLong() * sizeVal
                val (startDate, endDate) = getDateRange()

                val invoices = invoiceRepository.getInvoices(sizeVal, offset, state.searchQuery, startDate, endDate)
                val total = invoiceRepository.count(state.searchQuery, startDate, endDate)
                val totalPriceSum = invoiceRepository.getTotalPrice(state.searchQuery, startDate, endDate)

                val maxPage = if (total > 0) ((total + sizeVal - 1) / sizeVal).toInt() else 1
                val validPage = if (state.currentPage > maxPage) maxPage else state.currentPage

                val finalInvoices = if (validPage != state.currentPage) {
                    val newOffset = (validPage - 1).toLong() * sizeVal
                    invoiceRepository.getInvoices(sizeVal, newOffset, state.searchQuery, startDate, endDate)
                } else {
                    invoices
                }

                _uiState.update {
                    it.copy(
                        invoices = finalInvoices,
                        totalInvoices = total,
                        totalPrice = totalPriceSum,
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

    fun setDateFilterOption(option: HistoryDateFilterOption) {
        appSettings.updateHistoryDateFilterOption(option.name)
        val startDate = if (option == HistoryDateFilterOption.CUSTOM && _uiState.value.customStartDate == null) {
            LocalDate.now().minusMonths(1)
        } else {
            _uiState.value.customStartDate
        }
        val endDate = if (option == HistoryDateFilterOption.CUSTOM && _uiState.value.customEndDate == null) {
            LocalDate.now()
        } else {
            _uiState.value.customEndDate
        }
        if (option == HistoryDateFilterOption.CUSTOM) {
            appSettings.updateHistoryCustomRange(startDate, endDate)
        }
        _uiState.update {
            it.copy(
                dateFilterOption = option,
                customStartDate = startDate,
                customEndDate = endDate,
                currentPage = 1
            )
        }
        loadInvoices()
    }

    fun setCustomDateRange(startDate: LocalDate?, endDate: LocalDate?) {
        appSettings.updateHistoryCustomRange(startDate, endDate)
        _uiState.update {
            it.copy(
                dateFilterOption = HistoryDateFilterOption.CUSTOM,
                customStartDate = startDate,
                customEndDate = endDate,
                currentPage = 1
            )
        }
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
