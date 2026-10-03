package org.codeberg.assertix.openpos.app.ui.startup

sealed class StartupCreationStep(val index: Int) {
    data object CompanyInfo : StartupCreationStep(0)
    data object CompanyCredentials : StartupCreationStep(1)
    data object BankDetails : StartupCreationStep(2)
    data object FinancialSettings : StartupCreationStep(3)

    val next: StartupCreationStep?
        get() = when (this) {
            is CompanyInfo -> CompanyCredentials
            is CompanyCredentials -> BankDetails
            is BankDetails -> FinancialSettings
            is FinancialSettings -> null
        }

    val previous: StartupCreationStep?
        get() = when (this) {
            is CompanyInfo -> null
            is CompanyCredentials -> CompanyInfo
            is BankDetails -> CompanyCredentials
            is FinancialSettings -> BankDetails
        }

    companion object {
        fun fromIndex(index: Int): StartupCreationStep = when (index) {
            0 -> CompanyInfo
            1 -> CompanyCredentials
            2 -> BankDetails
            else -> FinancialSettings
        }
    }
}
