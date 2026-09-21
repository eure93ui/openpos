package org.codeberg.assertix.openpos.data.model.company

data class CompanyProfile(
    val companyInfo: CompanyInfo,
    val companyCredentials: CompanyCredentials,
    val bankDetails: BankDetails,
)
