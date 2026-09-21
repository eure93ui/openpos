package org.codeberg.assertix.openpos.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.codeberg.assertix.openpos.app.ui.UiConstants

@Composable
fun PaginationBar(
    showingText: String = "Showing 1-10 of 55",
    currentPage: Int = 1,
    querySize: QuerySize? = null,
    onQuerySizeChange: ((QuerySize) -> Unit)? = null,
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(UiConstants.SpacingSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = showingText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (querySize != null && onQuerySizeChange != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(UiConstants.SpacingTiny),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "На странице:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    QuerySize.entries.forEach { size ->
                        FilterChip(
                            selected = querySize == size,
                            onClick = { onQuerySizeChange(size) },
                            label = { Text("${size.size}", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.height(UiConstants.ChipHeight)
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(UiConstants.SpacingTiny),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious, modifier = Modifier.size(UiConstants.IconButtonSize)) {
                    Text("<")
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(UiConstants.IconButtonSize)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("$currentPage", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                IconButton(onClick = onNext, modifier = Modifier.size(UiConstants.IconButtonSize)) {
                    Text(">")
                }
            }
        }
    }
}
