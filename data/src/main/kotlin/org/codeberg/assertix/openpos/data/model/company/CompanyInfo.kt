package org.codeberg.assertix.openpos.data.model.company

import org.codeberg.assertix.openpos.data.model.PhoneNumber

data class CompanyInfo(
    val fullNaming: String,
    val companyType: CompanyType,
    val legalAddress: String,
    val phoneNumbers: List<PhoneNumber>,
) {
    val phoneNumber: PhoneNumber
        get() = phoneNumbers.first()

    constructor(
        fullNaming: String,
        companyType: CompanyType,
        legalAddress: String,
        phoneNumber: PhoneNumber,
    ) : this(fullNaming, companyType, legalAddress, listOf(phoneNumber))

    init {
        require(phoneNumbers.isNotEmpty()) { "Company must have at least one phone number" }
    }
}
