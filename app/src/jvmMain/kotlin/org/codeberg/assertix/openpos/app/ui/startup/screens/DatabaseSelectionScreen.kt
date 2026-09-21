package org.codeberg.assertix.openpos.app.ui.startup.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import org.codeberg.assertix.openpos.app.ui.startup.*
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.nio.file.Path
import kotlin.io.path.Path

object DatabaseSelectionConstants {
    val SqliteExtensions = listOf(
        "sqlite",
        "sqlite3",
        "db"
    )
}

@Composable
fun DatabaseSelectionScreen(
    onDatabaseReady: (Path) -> Unit,
    onCreateNew: (Path) -> Unit
) {
    var errorResource by remember { mutableStateOf<StringResource?>(null) }

    StartupHeader(
        icon = Res.drawable.database,
        title = stringResource(Res.string.startup_welcome_title),
        subtitle = stringResource(Res.string.startup_welcome_subtitle)
    )

    Spacer(modifier = Modifier.height(StartupConstants.SpacingSmall))

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(StartupConstants.SpacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CreateNew { path ->
            errorResource = null
            onCreateNew(path)
        }

        SelectExisting { pathString ->
            errorResource = null
            onDatabaseReady(pathString)
        }
    }

    AnimatedVisibility(
        visible = errorResource != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        errorResource?.let { resource ->
            StartupErrorBanner(
                text = stringResource(resource),
                onDismiss = { errorResource = null }
            )
        }
    }
}

@Composable
private fun CreateNew(
    onCreation: (Path) -> Unit
) {
    val launcher = rememberFileSaverLauncher(
        dialogSettings = FileKitDialogSettings.createDefault()
    ) { platformFile ->
        platformFile?.file?.path?.let { pathString ->
            val path = Path(pathString)
            onCreation(path)
        }
    }

    StartupPrimaryButton(
        text = stringResource(Res.string.startup_create_db),
        onClick = {
            launcher.launch(
                suggestedName = "database",
                defaultExtension = "sqlite"
            )
        },
        icon = Res.drawable.add,
    )
}

@Composable
private fun SelectExisting(
    onSelection: (Path) -> Unit,
) {
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.File(
            extensions = DatabaseSelectionConstants.SqliteExtensions
        )
    ) { platformFile ->
        platformFile?.file?.path?.let { onSelection(Path(it)) }
    }

    StartupSecondaryButton(
        text = stringResource(Res.string.startup_open_db),
        onClick = { launcher.launch() },
        icon = Res.drawable.search,
    )
}
