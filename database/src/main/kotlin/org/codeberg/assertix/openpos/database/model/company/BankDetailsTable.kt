package org.codeberg.assertix.openpos.database.model.company

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object BankDetailsTable : IntIdTable("bank_details") {
    val bankName = text("bank_name")
    val bic = text("bic")
    val checkingAccount = text("checkingAccount")
    val correspondentAccount = text("correspondentAccount")
}
