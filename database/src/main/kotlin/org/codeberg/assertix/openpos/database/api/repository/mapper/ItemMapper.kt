package org.codeberg.assertix.openpos.database.api.repository.mapper

import org.codeberg.assertix.openpos.data.model.Item
import org.codeberg.assertix.openpos.database.model.ItemsTable
import org.jetbrains.exposed.v1.core.ResultRow
import java.math.BigDecimal

fun ResultRow.toItem() =
    Item(
        id = this[ItemsTable.id].value,
        productName = this[ItemsTable.productName],
        unitOfMeasure = this[ItemsTable.unitOfMeasure],
        defaultPrice = BigDecimal(this[ItemsTable.defaultPrice]),
        mpn = this[ItemsTable.mpn],
    )
