package org.codeberg.assertix.openpos.app.ui.clients

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.components.FormTextField
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

    val surnameError = when {
        surname.isBlank() -> "Фамилия обязательна"
        surname.any { it.isDigit() } -> "Фамилия должна содержать только текст"
        else -> null
    }
    val nameError = when {
        name.isBlank() -> "Имя обязательно"
        name.any { it.isDigit() } -> "Имя должно содержать только текст"
        else -> null
    }
    val middleNameError = if (middleName.any { it.isDigit() }) "Отчество должно содержать только текст" else null
    val phoneError = if (phone.isNotBlank() && phone.any { !it.isDigit() }) "Телефон должен содержать только цифры" else null

    val isFormValid = surnameError == null && nameError == null && middleNameError == null && phoneError == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (client == null) stringResource(Res.string.client_dialog_new) else stringResource(Res.string.client_dialog_edit)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = stringResource(Res.string.client_surname_label),
                    isError = surnameError != null,
                    supportingText = { surnameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )
                FormTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(Res.string.client_name_label),
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )
                FormTextField(
                    value = middleName,
                    onValueChange = { middleName = it },
                    label = stringResource(Res.string.client_middle_name_label),
                    isError = middleNameError != null,
                    supportingText = { middleNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )
                FormTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = stringResource(Res.string.client_phone_label),
                    isError = phoneError != null,
                    supportingText = { phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, surname, middleName, phone) },
                enabled = isFormValid
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
