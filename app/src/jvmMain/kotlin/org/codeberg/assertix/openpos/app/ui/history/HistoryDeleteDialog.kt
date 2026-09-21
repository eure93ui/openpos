package org.codeberg.assertix.openpos.app.ui.history

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import org.codeberg.assertix.openpos.data.model.invoice.Invoice

@Composable
internal fun HistoryDeleteDialog(
    invoice: Invoice,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удалить накладную") },
        text = { Text("Вы действительно хотите удалить накладную ${invoice.invoiceNumber} (${invoice.clientNameSnapshot})?") },
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
