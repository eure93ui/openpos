package org.codeberg.assertix.openpos.database.model

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object InvoiceItemsTable : IntIdTable("invoice_items") {
    val invoice = reference("invoice_id", InvoicesTable).index()
    val item = reference("item_id", ItemsTable).index()
    val productNameSnapshot = text("product_name_snapshot")
    val unitOfMeasureSnapshot = text("unit_of_measure_snapshot")
    val quantity = text("quantity")
    val unitPrice = text("unit_price")
    val totalPrice = text("total_price")
    val mpnSnapshot = text("mpn_snapshot").nullable()
}
