package org.codeberg.assertix.openpos.app.ui.clients

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.codeberg.assertix.openpos.app.ui.components.QuerySize
import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.data.model.FullName
import org.codeberg.assertix.openpos.data.model.PhoneNumber
import org.codeberg.assertix.openpos.database.api.repository.ClientRepository

@Stable
data class ClientsUiState(
    val clients: List<Client> = emptyList(),
    val totalClients: Long = 0,
    val currentPage: Int = 1,
    val querySize: QuerySize = QuerySize.default,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isAddEditDialogVisible: Boolean = false,
    val editingClient: Client? = null,
    val isDeleteDialogVisible: Boolean = false,
    val clientToDelete: Client? = null
)

@Stable
class ClientsViewModel(private val clientRepository: ClientRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ClientsUiState())
    val uiState: StateFlow<ClientsUiState> = _uiState.asStateFlow()

    init {
        loadClients()
    }

    fun loadClients() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _uiState.value
                val sizeVal = state.querySize.size
                val offset = (state.currentPage - 1).toLong() * sizeVal
                val clients = clientRepository.getClients(sizeVal, offset, state.searchQuery)
                val total = clientRepository.getCount(state.searchQuery)

                val maxPage = if (total > 0) ((total + sizeVal - 1) / sizeVal).toInt() else 1
                val validPage = if (state.currentPage > maxPage) maxPage else state.currentPage

                val finalClients = if (validPage != state.currentPage) {
                    val newOffset = (validPage - 1).toLong() * sizeVal
                    clientRepository.getClients(sizeVal, newOffset, state.searchQuery)
                } else {
                    clients
                }

                _uiState.update {
                    it.copy(
                        clients = finalClients,
                        totalClients = total,
                        currentPage = validPage,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Не удалось загрузить клиентов"
                    )
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, currentPage = 1) }
        loadClients()
    }

    fun setQuerySize(size: QuerySize) {
        _uiState.update { it.copy(querySize = size, currentPage = 1) }
        loadClients()
    }

    fun setPage(page: Int) {
        val state = _uiState.value
        val sizeVal = state.querySize.size
        val maxPage = if (state.totalClients > 0) ((state.totalClients + sizeVal - 1) / sizeVal).toInt() else 1
        val targetPage = page.coerceIn(1, maxPage)
        if (targetPage != state.currentPage) {
            _uiState.update { it.copy(currentPage = targetPage) }
            loadClients()
        }
    }

    fun nextPage() {
        val state = _uiState.value
        val sizeVal = state.querySize.size
        val maxPage = if (state.totalClients > 0) ((state.totalClients + sizeVal - 1) / sizeVal).toInt() else 1
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
        _uiState.update { it.copy(isAddEditDialogVisible = true, editingClient = null, errorMessage = null) }
    }

    fun showEditDialog(client: Client) {
        _uiState.update { it.copy(isAddEditDialogVisible = true, editingClient = client, errorMessage = null) }
    }

    fun hideAddEditDialog() {
        _uiState.update { it.copy(isAddEditDialogVisible = false, editingClient = null) }
    }

    fun showDeleteDialog(client: Client) {
        _uiState.update { it.copy(isDeleteDialogVisible = true, clientToDelete = client, errorMessage = null) }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = false, clientToDelete = null) }
    }

    fun saveClient(name: String, surname: String, middleName: String, phoneNumberStr: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                if (name.isBlank() || surname.isBlank()) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Имя и фамилия обязательны для заполнения") }
                    return@launch
                }

                val phone = if (!phoneNumberStr.isNullOrBlank()) {
                    PhoneNumber(phoneNumberStr.trim().filter { it.isDigit() })
                } else null

                val editing = _uiState.value.editingClient
                if (editing == null) {
                    val newClient = Client(
                        id = 0,
                        fullName = FullName(name.trim(), surname.trim(), middleName.trim()),
                        phoneNumber = phone
                    )
                    clientRepository.add(newClient)
                    _uiState.update { it.copy(successMessage = "Клиент успешно добавлен", isAddEditDialogVisible = false) }
                } else {
                    val updatedClient = Client(
                        id = editing.id,
                        fullName = FullName(name.trim(), surname.trim(), middleName.trim()),
                        phoneNumber = phone
                    )
                    clientRepository.update(updatedClient)
                    _uiState.update { it.copy(successMessage = "Клиент успешно обновлен", isAddEditDialogVisible = false) }
                }
                loadClients()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Не удалось сохранить клиента"
                    )
                }
            }
        }
    }

    fun deleteClient(clientId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                clientRepository.delete(clientId)
                _uiState.update { it.copy(successMessage = "Клиент удален", isDeleteDialogVisible = false, clientToDelete = null) }
                loadClients()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Не удалось удалить клиента"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
