package org.codeberg.assertix.openpos.app.ui.clients

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ClientDialog(
    client: Client?,
    onDismiss: () -> Unit,
    onSave: (name: String, surname: String, middleName: String, phone: String?) -> Unit
) {
    var surname by remember { mutableStateOf(client?.fullName?.surname ?: "") }
    var name by remember { mutableStateOf(client?.fullName?.name ?: "") }
    var middleName by remember { mutableStateOf(client?.fullName?.middleName ?: "") }
    var phone by remember { mutableStateOf(client?.phoneNumber?.value ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (client == null) stringResource(Res.string.client_dialog_new) else stringResource(Res.string.client_dialog_edit)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text(stringResource(Res.string.client_surname_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(Res.string.client_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = middleName,
                    onValueChange = { middleName = it },
                    label = { Text(stringResource(Res.string.client_middle_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(stringResource(Res.string.client_phone_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, surname, middleName, phone) },
                enabled = name.isNotBlank() && surname.isNotBlank()
            ) {
                Text(stringResource(Res.string.btn_save))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(Res.string.btn_cancel))
            }
        }
    )
}
