package org.codeberg.assertix.openpos.app.ui.startup

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CentralLayout(
    modifier: Modifier = Modifier,
    maxWidth: Dp = StartupConstants.MaxWidth,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(StartupConstants.ContainerPadding),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .animateContentSize(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = StartupConstants.TonalElevation,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(StartupConstants.ContainerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(StartupConstants.SpacingLarge),
                content = content
            )
        }
    }
}
