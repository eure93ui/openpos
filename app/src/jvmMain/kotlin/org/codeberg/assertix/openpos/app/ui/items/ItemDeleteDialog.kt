package org.codeberg.assertix.openpos.app.ui.items

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import org.codeberg.assertix.openpos.data.model.Item

@Composable
internal fun ItemDeleteDialog(
    item: Item,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удалить товар") },
        text = { Text("Вы действительно хотите удалить товар «${item.productName}»?") },
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
