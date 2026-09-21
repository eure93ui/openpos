package org.codeberg.assertix.openpos.app.ui.clients

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import org.codeberg.assertix.openpos.data.model.Client

@Composable
internal fun ClientDeleteDialog(
    client: Client,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удалить клиента") },
        text = { Text("Вы действительно хотите удалить клиента ${client.fullName}?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Удалить")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
