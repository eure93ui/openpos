package org.codeberg.assertix.openpos.database.api.repository.mapper

import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.data.model.TaxRate
import org.codeberg.assertix.openpos.data.model.invoice.Invoice
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceItem
import org.codeberg.assertix.openpos.database.model.InvoicesTable
import org.jetbrains.exposed.v1.core.ResultRow
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

fun ResultRow.toInvoice(
    client: Client,
    items: List<InvoiceItem>,
) = Invoice(
    id = this[InvoicesTable.id].value,
    invoiceNumber = this[InvoicesTable.invoiceNumber],
    issueDate = LocalDate.parse(this[InvoicesTable.issueDate]),
    updatedAt = LocalDateTime.parse(this[InvoicesTable.updatedAt]),
    client = client,
    clientNameSnapshot = this[InvoicesTable.clientNameSnapshot],
    clientPhoneNumberSnapshot = this[InvoicesTable.clientPhoneNumberSnapshot],
    taxRate = TaxRate.from(this[InvoicesTable.taxRate]),
    items = items,
    totalPrice = BigDecimal(this[InvoicesTable.totalPrice]),
    totalPriceUnderTax = BigDecimal(this[InvoicesTable.totalPriceUnderTax]),
    status = this[InvoicesTable.status],
    notes = this[InvoicesTable.notes],
)
