package org.codeberg.assertix.openpos.app.ui.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.codeberg.assertix.openpos.app.ui.UiConstants
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun InvoiceClientSection(
    clientSearchQuery: String,
    selectedClient: Client?,
    clients: List<Client>,
    isClientDropdownExpanded: Boolean,
    clientError: String?,
    onClientSearchQueryChange: (String) -> Unit,
    onRemoveClient: () -> Unit,
    onToggleClientDropdown: (Boolean) -> Unit,
    onSelectClient: (Client) -> Unit,
    onNewClientClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.SpacingMedium),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(UiConstants.WeightDefault)) {
            val noPhoneText = stringResource(Res.string.no_phone)
            val clientTextFieldValue = if (clientSearchQuery.isNotEmpty()) {
                clientSearchQuery
            } else {
                selectedClient?.let { 
                    "${it.fullName.surname} ${it.fullName.name} ${it.fullName.middleName.orEmpty()} (${it.phoneNumber?.value ?: noPhoneText})" 
                } ?: ""
            }

            OutlinedTextField(
                value = clientTextFieldValue,
                onValueChange = onClientSearchQueryChange,
                placeholder = { Text(stringResource(Res.string.invoice_client_placeholder)) },
                label = { Text(stringResource(Res.string.invoice_client_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                isError = clientError != null,
                singleLine = true,
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (selectedClient != null || clientSearchQuery.isNotEmpty()) {
                            IconButton(onClick = onRemoveClient) {
                                Res.drawable.close.toImage()
                            }
                        }
                        IconButton(onClick = { onToggleClientDropdown(!isClientDropdownExpanded) }) {
                            Res.drawable.search.toImage()
                        }
                    }
                }
            )

            if (clientError != null) {
                Spacer(modifier = Modifier.height(UiConstants.SpacingTiny))
                Text(
                    text = clientError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = UiConstants.SpacingTiny)
                )
            }

            if (isClientDropdownExpanded && clients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(UiConstants.SpacingTiny))
                Surface(
                    modifier = Modifier.fillMaxWidth().heightIn(max = UiConstants.DropdownMaxHeight),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = UiConstants.TonalElevationMedium,
                    shadowElevation = UiConstants.ShadowElevationDefault,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth().padding(UiConstants.SpacingTiny)) {
                        items(clients, key = { client -> client.id }) { client ->
                            DropdownMenuItem(
                                text = { Text("${client.fullName.surname} ${client.fullName.name} — ${client.phoneNumber?.value ?: "—"}") },
                                onClick = { onSelectClient(client) }
                            )
                        }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onNewClientClick,
            modifier = Modifier.height(UiConstants.ButtonHeightLarge)
        ) {
            Text(stringResource(Res.string.btn_new_client))
        }
    }
}
