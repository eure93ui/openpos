package org.codeberg.assertix.openpos.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.settings.AppSettings
import org.codeberg.assertix.openpos.app.navigation.AppSidebar
import org.codeberg.assertix.openpos.app.navigation.Screen
import org.codeberg.assertix.openpos.app.ui.clients.ClientsScreen
import org.codeberg.assertix.openpos.app.ui.history.HistoryScreen
import org.codeberg.assertix.openpos.app.ui.invoice.InvoiceEditorScreen
import org.codeberg.assertix.openpos.app.ui.items.ItemsScreen
import org.codeberg.assertix.openpos.app.ui.settings.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Invoices()) }

    Row(modifier = Modifier.fillMaxSize()) {
        AppSidebar(
            currentScreen = currentScreen,
            onNavigate = { currentScreen = it }
        )

        VerticalDivider()

        Box(
            modifier = Modifier.weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            when (val screen = currentScreen) {
                is Screen.Invoices -> InvoiceEditorScreen(
                    editInvoiceId = screen.editInvoiceId,
                    onReturn = { currentScreen = Screen.Invoices() }
                )
                is Screen.Products -> ItemsScreen()
                is Screen.Clients -> ClientsScreen()
                is Screen.History -> HistoryScreen(
                    onNewInvoice = { currentScreen = Screen.Invoices() },
                    onEditInvoice = { invoiceId -> currentScreen = Screen.Invoices(editInvoiceId = invoiceId) }
                )

                is Screen.Settings -> SettingsScreen()
            }
        }
    }
}

@Composable
fun UpdatableAppWrapper() {
    val baseDensity = LocalDensity.current
    val settings = koinInject<AppSettings>()
    val zoom by settings.screenZoom.collectAsState()

    val userDensity = remember(baseDensity, zoom) {
        Density(
            baseDensity.density * zoom,
            baseDensity.fontScale * zoom
        )
    }

    CompositionLocalProvider(LocalDensity provides userDensity) {
        App()
    }
}