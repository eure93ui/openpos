package org.codeberg.assertix.openpos.app.ui.items

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.data.model.Item
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ItemDialog(
    item: Item?,
    onDismiss: () -> Unit,
    onSave: (productName: String, unitOfMeasure: String, defaultPrice: String) -> Unit
) {
    var productName by remember { mutableStateOf(item?.productName ?: "") }
    var unitOfMeasure by remember { mutableStateOf(item?.unitOfMeasure ?: "шт") }
    var defaultPrice by remember { mutableStateOf(item?.defaultPrice?.toPlainString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) stringResource(Res.string.item_dialog_new) else stringResource(Res.string.item_dialog_edit)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text(stringResource(Res.string.item_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = unitOfMeasure,
                    onValueChange = { unitOfMeasure = it },
                    label = { Text(stringResource(Res.string.item_unit_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = defaultPrice,
                    onValueChange = { defaultPrice = it },
                    label = { Text(stringResource(Res.string.item_price_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(productName, unitOfMeasure, defaultPrice) },
                enabled = productName.isNotBlank() && unitOfMeasure.isNotBlank() && defaultPrice.isNotBlank()
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
