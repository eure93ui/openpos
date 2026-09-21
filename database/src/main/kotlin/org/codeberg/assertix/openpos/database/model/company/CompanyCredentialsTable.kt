package org.codeberg.assertix.openpos.database.model.company

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_INN_LENGTH = 12
const val MAX_KPP_LENGTH = 9

object CompanyCredentialsTable : IntIdTable("company_credentials") {
    val inn = varchar("inn", MAX_INN_LENGTH)
    val kpp = varchar("kpp", MAX_KPP_LENGTH).default("")
}
