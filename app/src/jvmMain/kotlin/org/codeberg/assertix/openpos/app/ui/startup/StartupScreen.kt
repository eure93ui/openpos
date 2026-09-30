package org.codeberg.assertix.openpos.app.ui.startup

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.russhwolf.settings.Settings
import org.codeberg.assertix.openpos.app.TrialVerifier
import org.codeberg.assertix.openpos.app.UpdatableAppWrapper
import org.codeberg.assertix.openpos.app.settings.AppSettings
import org.codeberg.assertix.openpos.app.ui.startup.StartupState.*
import org.codeberg.assertix.openpos.app.ui.startup.screens.DatabaseCreationScreen
import org.codeberg.assertix.openpos.app.ui.startup.screens.DatabaseSelectionScreen
import org.codeberg.assertix.openpos.app.ui.startup.screens.DatabaseVerificationFailScreen
import org.codeberg.assertix.openpos.app.ui.startup.screens.DatabaseVerificationScreen
import org.codeberg.assertix.openpos.database.api.SessionHolder
import org.koin.compose.koinInject

@Composable
fun StartupScreen() {
    val settings = koinInject<Settings>()

    val verifier = TrialVerifier(settings)
    if (!verifier.isTrialActive()) {
        return
    }

    val appSettings = koinInject<AppSettings>()
    val rememberedDatabase by (appSettings.rememberedDatabase.collectAsState())
    val sessionHolder = koinInject<SessionHolder>()
    val coroutineScope = rememberCoroutineScope()

    var startupState by remember {
        mutableStateOf(
            rememberedDatabase?.let { Verifying(it) }
                ?: Selecting
        )
    }

    LaunchedEffect(rememberedDatabase) {
        if (rememberedDatabase == null) {
            startupState = Selecting
        }
    }

    val currentState = startupState
    if (currentState is Ready) {
        if (!sessionHolder.isInitialized) {
            sessionHolder.initialize(currentState.path)
        }
        UpdatableAppWrapper()
    } else {
        CentralLayout {
            when (currentState) {
                is Selecting -> DatabaseSelectionScreen(
                    onDatabaseReady = { path ->
                        startupState = Verifying(path)
                    },
                    onCreateNew = { path ->
                        startupState = Creation(path)
                    }
                )

                is Verifying -> DatabaseVerificationScreen(
                    currentState.path,
                    onSuccess = {
                        appSettings.rememberDatabase(currentState.path)
                        startupState = Ready(currentState.path)
                    },
                    onError = {
                        startupState = Failed(currentState.path)
                    }
                )

                is Failed -> DatabaseVerificationFailScreen(
                    database = currentState.path,
                    onRetry = {
                        startupState = Verifying(currentState.path)
                    },
                    onSelectAnother = {
                        startupState = Selecting
                    },
                )

                is Creation -> DatabaseCreationScreen(
                    onBack = {
                        startupState = Selecting
                    },
                    onComplete = { profile ->
                        coroutineScope.launch {
                            val wasCreated = sessionHolder.createDatabase(currentState.path, profile)
                            if (wasCreated) {
                                appSettings.rememberDatabase(currentState.path)
                                startupState = Ready(currentState.path)
                            } else {
                                startupState = Failed(currentState.path)
                            }
                        }
                    }
                )
            }
        }
    }
}
