package org.codeberg.assertix.openpos.database.model

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.eq

object FinancialSettingsTable : IntIdTable("financial_settings") {
    val taxPercent = integer("tax_percent")
    val invoicePrefix = varchar("invoice_prefix", 50).default("INV-2026-")

    init {
        check { id eq 1 }
    }
}
