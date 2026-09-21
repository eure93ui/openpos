package org.codeberg.assertix.openpos.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.error

@Composable
fun MessageBanner(
    errorMessage: String?,
    successMessage: String?,
    onDismiss: () -> Unit
) {
    if (errorMessage != null) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Res.drawable.error.toImage()
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text("ОК", color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }
    }

    if (successMessage != null) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = successMessage,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onDismiss) {
                    Text("ОК", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}
