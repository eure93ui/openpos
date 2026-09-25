package org.codeberg.assertix.openpos.app.ui.invoice

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.data.model.FullName
import org.codeberg.assertix.openpos.data.model.Item
import org.codeberg.assertix.openpos.data.model.PhoneNumber
import org.codeberg.assertix.openpos.data.model.invoice.Invoice
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceItem
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceStatus
import org.codeberg.assertix.openpos.database.api.repository.ClientRepository
import org.codeberg.assertix.openpos.database.api.repository.finance.FinancialRepository
import org.codeberg.assertix.openpos.database.api.repository.InvoiceRepository
import org.codeberg.assertix.openpos.database.api.repository.ItemsRepository
import java.math.BigDecimal

@Stable
class InvoiceEditorViewModel(
    private val invoiceRepository: InvoiceRepository,
    private val clientRepository: ClientRepository,
    private val itemsRepository: ItemsRepository,
    private val financialRepository: FinancialRepository,
) : ViewModel() {

    private val initialInvoiceId: Int? = null
    private val _uiState = MutableStateFlow(InvoiceEditorUiState())
    val uiState: StateFlow<InvoiceEditorUiState> = _uiState.asStateFlow()

    private var clientSearchJob: Job? = null
    private var productSearchJob: Job? = null

    init {
        if (initialInvoiceId != null && initialInvoiceId > 0) {
            loadInvoice(initialInvoiceId)
        } else {
            viewModelScope.launch {
                val financialSettings = financialRepository.get()
                val id = try {
                    invoiceRepository.getNewId()
                } catch (_: Exception) {
                    0
                }
                val newNumber = "${financialSettings.invoicePrefix}${(id + 1).toString().padStart(5, '0')}"
                _uiState.update {
                    it.copy(
                        taxPercent = financialSettings.taxPercent,
                        invoiceNumber = newNumber,
                    )
                }
            }
        }
        loadClients()
    }

    private fun loadInvoice(id: Int) {
        viewModelScope.launch {
            val invoice = invoiceRepository.getById(id)
            if (invoice != null) {
                _uiState.update {
                    it.copy(
                        invoiceId = invoice.id,
                        invoiceNumber = invoice.invoiceNumber,
                        issueDate = invoice.issueDate.toString(),
                        status = invoice.status,
                        isExisting = true,
                        selectedClient = invoice.client,
                        items = invoice.items,
                        taxPercent = invoice.taxRate.percent,
                        notes = invoice.notes ?: ""
                    )
                }
            }
        }
    }

    fun loadClients(query: String = "") {
        clientSearchJob?.cancel()
        clientSearchJob = viewModelScope.launch {
            delay(300L)
            try {
                val clients = clientRepository.getClients(20, 0, query)
                _uiState.update { it.copy(clients = clients) }
            } catch (e: Exception) {
                // Ignore or handle
            }
        }
    }

    fun setClientSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                clientSearchQuery = query,
                selectedClient = null,
                isClientDropdownExpanded = true,
                clientError = null
            )
        }
        clientSearchJob?.cancel()
        clientSearchJob = viewModelScope.launch {
            delay(300L)
            try {
                val clients = clientRepository.getClients(20, 0, query)
                _uiState.update { it.copy(clients = clients) }
            } catch (e: Exception) {
                // Ignore or handle
            }
        }
    }

    fun setClientDropdownExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isClientDropdownExpanded = expanded) }
        if (expanded && _uiState.value.clients.isEmpty()) {
            loadClients("")
        }
    }

    fun updateClient(client: Client) {
        _uiState.update {
            it.copy(
                selectedClient = client,
                clientError = null,
                isClientDropdownExpanded = false,
                clientSearchQuery = ""
            )
        }
        clientSearchJob?.cancel()
    }

    fun removeClient() {
        _uiState.update {
            it.copy(
                selectedClient = null,
                clientSearchQuery = "",
                isClientDropdownExpanded = false,
                clientError = null
            )
        }
        clientSearchJob?.cancel()
        loadClients("")
    }

    fun setProductSearchQuery(query: String) {
        _uiState.update { it.copy(productSearchQuery = query) }
        productSearchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isProductDropdownExpanded = false) }
            return
        }
        productSearchJob = viewModelScope.launch {
            delay(300L)
            try {
                val results = itemsRepository.getItems(20, 0, query)
                _uiState.update {
                    it.copy(
                        searchResults = results,
                        isProductDropdownExpanded = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(searchResults = emptyList(), isProductDropdownExpanded = false) }
            }
        }
    }

    fun hideProductDropdown() {
        _uiState.update { it.copy(isProductDropdownExpanded = false) }
    }

    fun showNewClientDialog(show: Boolean) {
        _uiState.update { it.copy(isNewClientDialogVisible = show) }
    }

    fun saveNewClient(name: String, surname: String, middleName: String, phone: String?) {
        viewModelScope.launch {
            try {
                val newClient = Client(
                    id = 0,
                    fullName = FullName(name = name, surname = surname, middleName = middleName),
                    phoneNumber = phone?.takeIf { it.isNotBlank() }?.let { PhoneNumber(it) }
                )
                val newId = clientRepository.add(newClient)
                val createdClient = newClient.copy(id = newId)
                loadClients()
                _uiState.update {
                    it.copy(
                        selectedClient = createdClient,
                        isNewClientDialogVisible = false,
                        clientError = null,
                        successMessage = "Клиент успешно добавлен"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(generalError = e.localizedMessage ?: "Не удалось сохранить клиента") }
            }
        }
    }

    fun showNewProductDialog(show: Boolean) {
        _uiState.update { it.copy(isNewProductDialogVisible = show) }
    }

    fun saveNewProduct(name: String, unit: String, priceStr: String) {
        viewModelScope.launch {
            try {
                val price = priceStr.toBigDecimalOrNull() ?: BigDecimal.ZERO
                val newItem = Item(
                    id = 0,
                    productName = name,
                    unitOfMeasure = unit,
                    defaultPrice = price
                )
                val newId = itemsRepository.add(newItem)
                val createdItem = newItem.copy(id = newId)

                addProduct(createdItem)

                _uiState.update {
                    it.copy(
                        isNewProductDialogVisible = false,
                        itemsError = null,
                        successMessage = "Товар успешно добавлен"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(generalError = e.localizedMessage ?: "Не удалось сохранить товар") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, generalError = null, clientError = null, itemsError = null) }
    }

    fun updateVat(vat: Int) {
        _uiState.update { it.copy(taxPercent = vat) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun updateStatus(status: InvoiceStatus) {
        _uiState.update { it.copy(status = status) }
    }

    fun updateIssueDate(date: String) {
        _uiState.update { it.copy(issueDate = date) }
    }

    fun resetInvoice() {
        viewModelScope.launch {
            val financialSettings = financialRepository.get()
            val count: Long = try {
                invoiceRepository.getNewId().toLong()
            } catch (_: Exception) {
                0L
            }
            val newNumber: String = "${financialSettings.invoicePrefix}${(count + 1).toString().padStart(5, '0')}"
            _uiState.value = InvoiceEditorUiState(
                taxPercent = financialSettings.taxPercent,
                invoiceNumber = newNumber
            )
        }
    }

    fun addProduct(item: Item) {
        _uiState.update { state ->
            val existingIndex = state.items.indexOfFirst { it.itemId == item.id }
            val updatedItems = if (existingIndex >= 0) {
                state.items.mapIndexed { index, invoiceItem ->
                    if (index == existingIndex) {
                        val newQty = invoiceItem.quantity.add(BigDecimal.ONE)
                        invoiceItem.copy(
                            quantity = newQty,
                            totalPrice = newQty.multiply(invoiceItem.unitPrice)
                        )
                    } else invoiceItem
                }
            } else {
                state.items + InvoiceItem(
                    id = state.items.size + 1,
                    itemId = item.id,
                    productNameSnapshot = item.productName,
                    unitOfMeasureSnapshot = item.unitOfMeasure,
                    quantity = BigDecimal.ONE,
                    unitPrice = item.defaultPrice,
                    totalPrice = item.defaultPrice
                )
            }
            state.copy(
                items = updatedItems,
                itemsError = null,
                productSearchQuery = "",
                searchResults = emptyList(),
                isProductDropdownExpanded = false
            )
        }
    }

    fun updateItemQuantity(itemId: Int, quantityStr: String) {
        val qty = quantityStr.toBigDecimalOrNull() ?: BigDecimal.ZERO
        _uiState.update { state ->
            val updated = state.items.map { item ->
                if (item.id == itemId) {
                    item.copy(
                        quantity = qty,
                        totalPrice = qty.multiply(item.unitPrice)
                    )
                } else item
            }
            state.copy(items = updated, itemsError = null)
        }
    }

    fun updateItemPrice(itemId: Int?, priceStr: String) {
        val price = priceStr.toBigDecimalOrNull() ?: BigDecimal.ZERO
        _uiState.update { state ->
            val updated = state.items.map { item ->
                if (item.id == itemId) {
                    item.copy(
                        unitPrice = price,
                        totalPrice = item.quantity.multiply(price)
                    )
                } else item
            }
            state.copy(items = updated, itemsError = null)
        }
    }

    fun removeItem(itemId: Int?) {
        _uiState.update { state ->
            state.copy(items = state.items.filter { it.id != itemId })
        }
    }

    fun validate(): Boolean {
        val state = _uiState.value
        var isValid = true
        var clientErr: String? = null
        var itemsErr: String? = null

        if (state.selectedClient == null) {
            clientErr = "Необходимо выбрать клиента"
            isValid = false
        }

        if (state.items.isEmpty()) {
            itemsErr = "Накладная должна содержать хотя бы один товар"
            isValid = false
        } else if (state.items.any { it.quantity <= BigDecimal.ZERO }) {
            itemsErr = "Количество товаров должно быть больше нуля"
            isValid = false
        }

        _uiState.update {
            it.copy(
                clientError = clientErr,
                itemsError = itemsErr
            )
        }
        return isValid
    }

    fun commitAndSave(status: InvoiceStatus? = null, onSuccess: (Invoice) -> Unit = {}) {
        if (!validate()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val state = _uiState.value
                val finalInvoice = if (status != null) {
                    state.toInvoice().copy(status = status)
                } else {
                    state.toInvoice()
                }

                if (state.isExisting) {
                    invoiceRepository.update(finalInvoice)
                } else {
                    invoiceRepository.add(finalInvoice)
                }
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Накладная успешно сохранена",
                        isExisting = true
                    )
                }
                onSuccess(finalInvoice)
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, generalError = e.localizedMessage ?: "Ошибка сохранения") }
            }
        }
    }

    fun save() {
        commitAndSave(status = null)
    }

    fun printInvoice(onPrint: (Invoice) -> Unit) {
        commitAndSave(status = null, onSuccess = onPrint)
    }

    fun exportPdf(onExport: (Invoice) -> Unit) {
        commitAndSave(status = null, onSuccess = onExport)
    }
}
