package org.codeberg.assertix.openpos.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.settings.SettingsTab
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.app.util.appId
import org.codeberg.assertix.openpos.app.util.appName
import org.codeberg.assertix.openpos.database.api.DatabaseSavingState
import org.codeberg.assertix.openpos.database.api.SessionInfo
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun AppSidebar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val sessionInfo: SessionInfo = koinInject<SessionInfo>()
    val savingState: DatabaseSavingState by sessionInfo.savingState.collectAsState()

    NavigationRail(
        modifier = Modifier.width(110.dp),
        header = {
            Box(
                modifier = Modifier.padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NavigationRailItem(
                    selected = currentScreen is Screen.Invoices,
                    onClick = {
                        val screen = currentScreen
                        if (screen is Screen.Invoices) {
                            if (screen.editInvoiceId != null) {
                                onNavigate(Screen.Invoices())
                            }
                        } else {
                            onNavigate(Screen.Invoices())
                        }
                    },
                    icon = {
                        Res.drawable.receipt_long.toImage()
                    },
                    label = { Text(stringResource(Res.string.nav_invoices)) }
                )
                NavigationRailItem(
                    selected = currentScreen is Screen.Products,
                    onClick = { onNavigate(Screen.Products) },
                    icon = {
                        Res.drawable.packages.toImage()
                    },
                    label = { Text(org.jetbrains.compose.resources.stringResource(Res.string.nav_products)) }
                )
                NavigationRailItem(
                    selected = currentScreen is Screen.Clients,
                    onClick = { onNavigate(Screen.Clients) },
                    icon = {
                        Res.drawable.group.toImage()
                    },
                    label = { Text(org.jetbrains.compose.resources.stringResource(Res.string.nav_clients)) }
                )
                NavigationRailItem(
                    selected = currentScreen is Screen.History,
                    onClick = { onNavigate(Screen.History) },
                    icon = {
                        Res.drawable.history.toImage()
                    },
                    label = { Text(org.jetbrains.compose.resources.stringResource(Res.string.nav_history)) }
                )
                NavigationRailItem(
                    selected = currentScreen is Screen.Settings,
                    onClick = { onNavigate(Screen.Settings(SettingsTab.CompanyProfile)) },
                    icon = {
                        Res.drawable.settings.toImage()
                    },
                    label = { Text(stringResource(Res.string.nav_settings)) }
                )
            }

            Surface(
                onClick = { onNavigate(Screen.Settings(SettingsTab.DatabaseHardware)) },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.padding(4.dp).fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Res.drawable.database.toImage()
                        Text(
                            text = sessionInfo.uriPath(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        when (savingState) {
                            DatabaseSavingState.SAVED -> {
                                Res.drawable.assignment_turned_in.toImage()
                                Text(
                                    text = stringResource(Res.string.invoice_saved),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            DatabaseSavingState.SAVING -> {
                                Res.drawable.save_clock.toImage()
                                Text(
                                    text = stringResource(Res.string.invoice_saving),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            DatabaseSavingState.ERROR -> {
                                Res.drawable.error.toImage()
                                Text(
                                    text = "Error",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
