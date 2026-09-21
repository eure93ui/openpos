package org.codeberg.assertix.openpos.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.close
import org.codeberg.assertix.openpos.resources.search

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(placeholder) },
        modifier = modifier,
        leadingIcon = { Res.drawable.search.toImage() },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Res.drawable.close.toImage()
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}
