package org.codeberg.assertix.openpos.app.ui.invoice

import org.codeberg.assertix.openpos.calculations.calculateTaxForItems
import org.codeberg.assertix.openpos.calculations.calculateTotalUnderTax
import org.codeberg.assertix.openpos.calculations.itemsTotal
import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.data.model.TaxRate
import org.codeberg.assertix.openpos.data.model.invoice.Invoice
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceItem
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class InvoiceEditorUiState(
    val invoiceId: Int = -1,
    val invoiceNumber: String = "INV-2026-00045",
    val issueDate: String = LocalDate.now().toString(),
    val status: InvoiceStatus = InvoiceStatus.DRAFT,
    val isSaving: Boolean = false,
    val isExisting: Boolean = false,
    val selectedClient: Client? = null,
    val clientSearchQuery: String = "",
    val clients: List<Client> = emptyList(),
    val isClientDropdownExpanded: Boolean = false,
    val productSearchQuery: String = "",
    val searchResults: List<org.codeberg.assertix.openpos.data.model.Item> = emptyList(),
    val isProductDropdownExpanded: Boolean = false,
    val isNewClientDialogVisible: Boolean = false,
    val isNewProductDialogVisible: Boolean = false,
    val items: List<InvoiceItem> = listOf(),
    val taxPercent: Int = 20,
    val printableNotes: String = "",
    val internalNotes: String = "",
    val isNotesScreenVisible: Boolean = false,
    val clientError: String? = null,
    val itemsError: String? = null,
    val generalError: String? = null,
    val successMessage: String? = null,
    val isPdfPreviewVisible: Boolean = false,
    val previewImages: List<java.awt.image.BufferedImage> = emptyList(),
    val isPdfProcessing: Boolean = false
) {
    val subtotal: BigDecimal = itemsTotal(items)
    val taxAmount: BigDecimal = calculateTaxForItems(items, taxPercent)
    val grandTotal: BigDecimal = calculateTotalUnderTax(items, taxPercent)

    fun toInvoice(): Invoice {
        val client = selectedClient ?: throw IllegalStateException("Client must be selected")
        return Invoice(
            id = invoiceId,
            invoiceNumber = invoiceNumber,
            issueDate = LocalDate.parse(issueDate),
            updatedAt = LocalDateTime.now(),
            client = client,
            clientNameSnapshot = client.fullName.snapshot(),
            clientPhoneNumberSnapshot = client.phoneNumber?.value ?: "",
            taxRate = TaxRate.from(taxPercent),
            items = items,
            totalPrice = subtotal,
            totalPriceUnderTax = grandTotal,
            status = status,
            printableNotes = printableNotes.takeIf { it.isNotBlank() },
            internalNotes = internalNotes.takeIf { it.isNotBlank() }
        )
    }
}
