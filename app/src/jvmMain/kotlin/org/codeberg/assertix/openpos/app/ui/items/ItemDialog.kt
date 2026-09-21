package org.codeberg.assertix.openpos.app.ui.items

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.data.model.Item

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
        title = { Text(if (item == null) "Новый товар" else "Редактировать товар") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Наименование товара *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = unitOfMeasure,
                    onValueChange = { unitOfMeasure = it },
                    label = { Text("Единица измерения * (например, шт, кг, л)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = defaultPrice,
                    onValueChange = { defaultPrice = it },
                    label = { Text("Цена по умолчанию * (например, 150.00)") },
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
