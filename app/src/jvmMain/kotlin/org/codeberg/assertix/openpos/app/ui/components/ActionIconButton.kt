package org.codeberg.assertix.openpos.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.toImage
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun ActionIconButton(
    icon: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(32.dp)
) {
    IconButton(onClick = onClick, modifier = modifier) {
        icon.toImage()
    }
}
