package org.codeberg.assertix.openpos.data.model.company

import org.codeberg.assertix.openpos.data.model.PhoneNumber

data class CompanyInfo(
    val fullNaming: String,
    val companyType: CompanyType,
    val legalAddress: String,
    val phoneNumber: PhoneNumber,
)
