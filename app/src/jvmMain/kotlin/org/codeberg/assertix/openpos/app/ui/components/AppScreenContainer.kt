package org.codeberg.assertix.openpos.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppScreenContainer(
    notificationMessage: String?,
    isError: Boolean,
    onDismissNotification: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )

        BottomNotification(
            message = notificationMessage,
            isError = isError,
            onDismiss = onDismissNotification,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
