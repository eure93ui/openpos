package org.codeberg.assertix.openpos.database.model.company

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.eq

object CompanyProfileTable : IntIdTable("company_profile") {
    val companyInfo = reference("company_info", CompanyInfoTable)
    val companyCredentials = reference("company_credentials", CompanyCredentialsTable)
    val bankDetails = reference("bank_details", BankDetailsTable)

    init {
        check { id eq 1 } // singleton
    }
}
