package org.codeberg.assertix.openpos.data.model.invoice

import kotlinx.serialization.Serializable
import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.data.model.TaxRate
import org.codeberg.assertix.openpos.data.model.containedInFullName
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Invoice(
    val id: Int = -1, // still not sure about making it nullable before database insert
    val invoiceNumber: String,
    val issueDate: LocalDate,
    val updatedAt: LocalDateTime,
    val client: Client,
    val clientNameSnapshot: String,
    val clientPhoneNumberSnapshot: String,
    val taxRate: TaxRate,
    val items: List<InvoiceItem>,
    val totalPrice: BigDecimal,
    val totalPriceUnderTax: BigDecimal,
    val status: InvoiceStatus = InvoiceStatus.DRAFT,
    val notes: String? = null,
)

fun Invoice.filter(query: String) =
    listOf(
        invoiceNumber,
        clientNameSnapshot,
        clientPhoneNumberSnapshot,
    ).map { it.lowercase() }
        .any {
            it.contains(query)
        }.or(client.containedInFullName(query))
