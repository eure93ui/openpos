package org.codeberg.assertix.openpos.data.model.invoice

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class InvoiceItem(
    val id: Int,
    val itemId: Int,
    val productNameSnapshot: String,
    val unitOfMeasureSnapshot: String,
    val quantity: BigDecimal,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal,
)
