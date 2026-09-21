package org.codeberg.assertix.openpos.database.model

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object ItemsTable : IntIdTable("items") {
    val productName = text("product_name").index()
    val unitOfMeasure = text("unit_of_measure")
    val defaultPrice = text("default_price")
}
