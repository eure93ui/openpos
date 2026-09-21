package org.codeberg.assertix.openpos.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.arrow_back
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun ScreenHeader(
    title: String,
    badgeText: String? = null,
    onReturnClick: (() -> Unit)? = null,
    returnIcon: DrawableResource = Res.drawable.arrow_back,
    onPrimaryActionClick: (() -> Unit)? = null,
    primaryActionText: String? = null,
    primaryActionIcon: DrawableResource? = null,
    onSecondaryActionClick: (() -> Unit)? = null,
    secondaryActionText: String? = null,
    secondaryActionIcon: DrawableResource? = null,
    extraContent: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onReturnClick != null) {
                IconButton(onClick = onReturnClick) {
                    returnIcon.toImage()
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
            if (badgeText != null) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            extraContent()
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (secondaryActionText != null && onSecondaryActionClick != null) {
                OutlinedButton(onClick = onSecondaryActionClick) {
                    if (secondaryActionIcon != null) {
                        secondaryActionIcon.toImage()
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(secondaryActionText)
                }
            }
            if (primaryActionText != null && onPrimaryActionClick != null) {
                Button(onClick = onPrimaryActionClick) {
                    if (primaryActionIcon != null) {
                        primaryActionIcon.toImage()
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(primaryActionText)
                }
            }
        }
    }
}
