package org.codeberg.assertix.openpos.app.ui.settings

import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class SettingsTab(
    val titleRes: StringResource,
    val icon: DrawableResource
) {
    CompanyProfile(Res.string.tab_company_profile, Res.drawable.title),
    DatabaseHardware(Res.string.tab_database_hardware, Res.drawable.database),
    FinancialConstants(Res.string.tab_financial_constants, Res.drawable.currency_ruble)
}