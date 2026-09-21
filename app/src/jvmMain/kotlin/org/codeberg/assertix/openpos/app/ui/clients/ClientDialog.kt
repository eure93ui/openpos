package org.codeberg.assertix.openpos.app.ui.clients

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.data.model.Client

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
        title = { Text(if (client == null) "Новый клиент" else "Редактировать клиента") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Фамилия *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = middleName,
                    onValueChange = { middleName = it },
                    label = { Text("Отчество") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон (например, 79001234567)") },
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
                Text("Сохранить")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
