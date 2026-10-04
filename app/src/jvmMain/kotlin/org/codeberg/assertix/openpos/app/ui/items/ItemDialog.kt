package org.codeberg.assertix.openpos.app.ui.items

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.components.FormTextField
import org.codeberg.assertix.openpos.data.model.Item
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ItemDialog(
    item: Item?,
    onDismiss: () -> Unit,
    onSave: (productName: String, mpn: String, unitOfMeasure: String, defaultPrice: String) -> Unit
) {
    var productName by remember { mutableStateOf(item?.productName ?: "") }
    var mpn by remember { mutableStateOf(item?.mpn ?: "") }
    var unitOfMeasure by remember { mutableStateOf(item?.unitOfMeasure ?: "шт") }
    var defaultPrice by remember { mutableStateOf(item?.defaultPrice?.toPlainString() ?: "") }

    val productNameError = if (productName.isBlank()) "Наименование товара обязательно" else null
    val unitError = if (unitOfMeasure.isBlank()) "Единица измерения обязательна" else null
    val priceError = if (defaultPrice.isNotBlank()) {
        val normalized = defaultPrice.trim().replace(',', '.')
        val parsed = normalized.toBigDecimalOrNull()
        if (parsed == null || parsed < java.math.BigDecimal.ZERO) "Цена должна содержать только числа" else null
    } else {
        null
    }

    val isFormValid = productNameError == null && unitError == null && priceError == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) stringResource(Res.string.item_dialog_new) else stringResource(Res.string.item_dialog_edit)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = stringResource(Res.string.item_name_label),
                    isError = productNameError != null,
                    supportingText = { productNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )
                FormTextField(
                    value = mpn,
                    onValueChange = { mpn = it },
                    label = stringResource(Res.string.item_mpn_label),
                    modifier = Modifier.fillMaxWidth()
                )
                FormTextField(
                    value = unitOfMeasure,
                    onValueChange = { unitOfMeasure = it },
                    label = stringResource(Res.string.item_unit_label),
                    isError = unitError != null,
                    supportingText = { unitError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )
                FormTextField(
                    value = defaultPrice,
                    onValueChange = { defaultPrice = it },
                    label = stringResource(Res.string.item_price_label),
                    isError = priceError != null,
                    supportingText = { priceError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(productName, mpn, unitOfMeasure, defaultPrice) },
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
