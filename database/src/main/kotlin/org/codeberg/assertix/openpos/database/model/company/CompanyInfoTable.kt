package org.codeberg.assertix.openpos.database.model.company

import org.codeberg.assertix.openpos.data.model.company.CompanyType
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object CompanyInfoTable : IntIdTable("company_info") {
    val fullNaming = text("full_naming")
    val companyType = enumeration("company_type", CompanyType::class)
    val legalAddress = text("legal_address")
    val phoneNumber = text("phone_number")
}
