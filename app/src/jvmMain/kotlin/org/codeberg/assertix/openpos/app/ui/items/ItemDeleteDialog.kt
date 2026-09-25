package org.codeberg.assertix.openpos.app.ui.items

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import org.codeberg.assertix.openpos.data.model.Item
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ItemDeleteDialog(
    item: Item,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.delete_item_title)) },
        text = { Text("Вы действительно хотите удалить товар «${item.productName}»?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(Res.string.btn_delete))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(Res.string.btn_cancel))
            }
        }
    )
}
