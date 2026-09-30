package org.codeberg.assertix.openpos.app.navigation

import androidx.compose.runtime.Stable
import org.codeberg.assertix.openpos.app.ui.settings.SettingsTab

@Stable
sealed interface Screen {
    data class Invoices(val editInvoiceId: Int? = null) : Screen
    data object Products : Screen
    data object Clients : Screen
    data object History : Screen
    data class Settings(val initialTab: SettingsTab = SettingsTab.CompanyProfile) : Screen
}
