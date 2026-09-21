package org.codeberg.assertix.openpos.app.ui.startup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.codeberg.assertix.openpos.app.ui.startup.StartupConstants
import org.codeberg.assertix.openpos.app.ui.startup.StartupHeader
import org.codeberg.assertix.openpos.database.api.SessionHolder
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.app_name
import org.codeberg.assertix.openpos.resources.database
import org.codeberg.assertix.openpos.resources.startup_verifying
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import java.nio.file.Path

@Composable
fun DatabaseVerificationScreen(
    path: Path,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val sessionHolder = koinInject<SessionHolder>()

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = StartupConstants.SpacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(StartupConstants.SpacingLarge)
    ) {
        StartupHeader(
            icon = Res.drawable.database,
            title = stringResource(Res.string.app_name),
            subtitle = stringResource(Res.string.startup_verifying)
        )

        Spacer(modifier = Modifier.height(StartupConstants.SpacingSmall))

        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }

    LaunchedEffect(path) {
        val valid = withContext(Dispatchers.IO) {
            sessionHolder.validateDatabase(path)
        }
        if (valid) onSuccess() else onError()
    }
}
