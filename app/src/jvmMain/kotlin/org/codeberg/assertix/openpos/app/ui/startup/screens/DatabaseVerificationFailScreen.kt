package org.codeberg.assertix.openpos.app.ui.startup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.codeberg.assertix.openpos.app.ui.startup.StartupConstants
import org.codeberg.assertix.openpos.app.ui.startup.StartupHeader
import org.codeberg.assertix.openpos.app.ui.startup.StartupSecondaryButton
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource
import java.nio.file.Path
import kotlin.io.path.absolutePathString

@Composable
fun DatabaseVerificationFailScreen(
    database: Path,
    onRetry: () -> Unit = {},
    onSelectAnother: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(StartupConstants.SpacingLarge)
    ) {
        StartupHeader(
            icon = Res.drawable.error,
            title = stringResource(Res.string.startup_verification_failed),
            subtitle = stringResource(Res.string.startup_verification_failed_desc)
        )

        OutlinedTextField(
            value = database.absolutePathString(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(StartupConstants.SpacingSmall))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(StartupConstants.SpacingMedium)
        ) {
            StartupSecondaryButton(
                text = stringResource(Res.string.startup_retry),
                onClick = onRetry,
                icon = Res.drawable.search
            )

            StartupSecondaryButton(
                text = stringResource(Res.string.startup_try_another),
                onClick = onSelectAnother,
                icon = Res.drawable.search,
            )
        }
    }
}
