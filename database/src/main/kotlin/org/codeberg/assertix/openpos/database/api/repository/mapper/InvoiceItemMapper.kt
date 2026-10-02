package org.codeberg.assertix.openpos.database.api.repository.mapper

import org.codeberg.assertix.openpos.data.model.invoice.InvoiceItem
import org.codeberg.assertix.openpos.database.model.InvoiceItemsTable
import org.jetbrains.exposed.v1.core.ResultRow
import java.math.BigDecimal

fun ResultRow.toInvoiceItem() =
    InvoiceItem(
        id = this[InvoiceItemsTable.id].value,
        itemId = this[InvoiceItemsTable.item].value,
        productNameSnapshot = this[InvoiceItemsTable.productNameSnapshot],
        unitOfMeasureSnapshot = this[InvoiceItemsTable.unitOfMeasureSnapshot],
        quantity = BigDecimal(this[InvoiceItemsTable.quantity]),
        unitPrice = BigDecimal(this[InvoiceItemsTable.unitPrice]),
        totalPrice = BigDecimal(this[InvoiceItemsTable.totalPrice]),
        mpnSnapshot = this[InvoiceItemsTable.mpnSnapshot],
    )
