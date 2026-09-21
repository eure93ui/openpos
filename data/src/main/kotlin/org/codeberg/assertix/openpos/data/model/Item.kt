package org.codeberg.assertix.openpos.data.model

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class Item(
    val id: Int,
    val productName: String,
    val unitOfMeasure: String,
    val defaultPrice: BigDecimal,
)
