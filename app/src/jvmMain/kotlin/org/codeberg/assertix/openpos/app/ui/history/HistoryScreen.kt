package org.codeberg.assertix.openpos.app.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.components.*
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNewInvoice: () -> Unit,
    onEditInvoice: (Int) -> Unit = {},
    viewModel: HistoryViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var dateDropdownExpanded by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    val periodDisplay = when (state.dateFilterOption) {
        HistoryDateFilterOption.WEEK -> "Неделя"
        HistoryDateFilterOption.MONTH -> "Месяц"
        HistoryDateFilterOption.HALF_YEAR -> "Полугодие"
        HistoryDateFilterOption.YEAR -> "Год"
        HistoryDateFilterOption.ALL -> "За все время"
        HistoryDateFilterOption.CUSTOM -> "Произвольный период"
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.customStartDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
                ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            viewModel.setCustomDateRange(selected, state.customEndDate)
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text(stringResource(Res.string.btn_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text(stringResource(Res.string.btn_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.customEndDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
                ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            viewModel.setCustomDateRange(state.customStartDate, selected)
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text(stringResource(Res.string.btn_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text(stringResource(Res.string.btn_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
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

            ExposedDropdownMenuBox(
                expanded = dateDropdownExpanded,
                onExpandedChange = { dateDropdownExpanded = it },
                modifier = Modifier.width(220.dp)
            ) {
                OutlinedTextField(
                    value = periodDisplay,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(Res.string.period)) },
                    leadingIcon = {
                        Res.drawable.history.toImage()
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dateDropdownExpanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                ExposedDropdownMenu(
                    expanded = dateDropdownExpanded,
                    onDismissRequest = { dateDropdownExpanded = false }
                ) {
                    HistoryDateFilterOption.entries.forEach { option ->
                        val label = when (option) {
                            HistoryDateFilterOption.WEEK -> "Неделя"
                            HistoryDateFilterOption.MONTH -> "Месяц"
                            HistoryDateFilterOption.HALF_YEAR -> "Полугодие"
                            HistoryDateFilterOption.YEAR -> "Год"
                            HistoryDateFilterOption.ALL -> "За все время"
                            HistoryDateFilterOption.CUSTOM -> "Произвольный период"
                        }
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                dateDropdownExpanded = false
                                viewModel.setDateFilterOption(option)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            if (state.dateFilterOption == HistoryDateFilterOption.CUSTOM) {
                val startDisplay = state.customStartDate?.format(dateFormatter) ?: "..."
                val endDisplay = state.customEndDate?.format(dateFormatter) ?: "..."

                OutlinedCard(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.width(150.dp).height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text("Начало", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(startDisplay, style = MaterialTheme.typography.bodyMedium)
                        }
                        Res.drawable.history.toImage()
                    }
                }

                OutlinedCard(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.width(150.dp).height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text("Конец", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(endDisplay, style = MaterialTheme.typography.bodyMedium)
                        }
                        Res.drawable.history.toImage()
                    }
                }
            }
        }

        HistoryTable(
            invoices = state.invoices,
            totalInvoices = state.totalInvoices,
            totalPrice = state.totalPrice,
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
