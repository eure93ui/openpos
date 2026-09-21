package org.codeberg.assertix.openpos.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.toImage
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun EmptyStateView(
    icon: DrawableResource,
    title: String,
    description: String,
    onPrimaryActionClick: (() -> Unit)? = null,
    primaryActionText: String? = null,
    onSecondaryActionClick: (() -> Unit)? = null,
    secondaryActionText: String? = null
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                icon.toImage()
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (primaryActionText != null || secondaryActionText != null) {
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (secondaryActionText != null && onSecondaryActionClick != null) {
                    OutlinedButton(onClick = onSecondaryActionClick) {
                        Text(secondaryActionText)
                    }
                }
                if (primaryActionText != null && onPrimaryActionClick != null) {
                    Button(onClick = onPrimaryActionClick) {
                        Text(primaryActionText)
                    }
                }
            }
        }
    }
}
