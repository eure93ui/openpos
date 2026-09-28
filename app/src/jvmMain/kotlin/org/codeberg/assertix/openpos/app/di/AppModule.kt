package org.codeberg.assertix.openpos.app.di

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.codeberg.assertix.openpos.app.settings.AppSettings
import org.codeberg.assertix.openpos.app.ui.clients.ClientsViewModel
import org.codeberg.assertix.openpos.app.ui.history.HistoryViewModel
import org.codeberg.assertix.openpos.app.ui.invoice.InvoiceEditorViewModel
import org.codeberg.assertix.openpos.app.ui.items.ItemsViewModel
import org.codeberg.assertix.openpos.app.ui.settings.SettingsViewModel
import org.codeberg.assertix.openpos.app.util.appId
import org.codeberg.assertix.openpos.database.api.SessionHolder
import org.codeberg.assertix.openpos.database.api.SessionInfo
import org.codeberg.assertix.openpos.database.api.repository.*
import org.codeberg.assertix.openpos.database.api.repository.finance.FinancialRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.util.prefs.Preferences

val appModule = module {
    single<Settings> {
        val preferences = Preferences.userRoot().node(appId)
        PreferencesSettings(preferences)
    }

    single { AppSettings(get()) }

    single { SessionHolder() }

    single {
        SessionInfo(get())
    }

    single { ClientRepository(get()) }
    single { ItemsRepository(get()) }
    single { InvoiceRepository(get()) }
    single { CompanyProfileRepository(get()) }
    single { FinancialRepository(get()) }

    viewModel {
        ClientsViewModel(get())
    }

    viewModel {
        ItemsViewModel(get())
    }

    viewModel {
        HistoryViewModel(get())
    }

    viewModel { (invoiceId: Int?) ->
        InvoiceEditorViewModel(get(), get(), get(), get(), get(), invoiceId)
    }

    viewModel {
        SettingsViewModel(get(), get(), get(), get())
    }
}
