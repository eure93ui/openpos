package org.codeberg.assertix.openpos.database.model

import org.codeberg.assertix.openpos.data.model.invoice.InvoiceStatus
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object InvoicesTable : IntIdTable("invoices") {
    val invoiceNumber = text("invoice_number").uniqueIndex()
    val issueDate = text("issue_date")
    val updatedAt = text("updated_at")
    val client = reference("client_id", ClientsTable)
    val clientNameSnapshot = text("client_name_snapshot").index()
    val clientPhoneNumberSnapshot = text("client_phone_snapshot").index()
    val taxRate = integer("tax_rate")
    val totalPrice = text("total_price")
    val totalPriceUnderTax = text("total_price_under_tax")
    val status = enumerationByName("status", 10, InvoiceStatus::class)
    val printableNotes = text("printable_notes").nullable()
    val internalNotes = text("internal_notes").nullable()
}
