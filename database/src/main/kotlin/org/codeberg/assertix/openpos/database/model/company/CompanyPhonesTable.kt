package org.codeberg.assertix.openpos.database.model.company

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object CompanyPhonesTable : IntIdTable("company_phones") {
    val companyInfo = reference("company_info", CompanyInfoTable)
    val phoneNumber = text("phone_number")
}
