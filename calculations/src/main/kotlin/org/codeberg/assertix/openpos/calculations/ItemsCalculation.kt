package org.codeberg.assertix.openpos.calculations

import org.codeberg.assertix.openpos.data.model.invoice.InvoiceItem
import java.math.BigDecimal

fun calculateTaxForItems(
    items: List<InvoiceItem>,
    taxRatePercent: Int,
): BigDecimal {
    val itemsTotal = itemsTotal(items)

    return calculateTaxAmount(itemsTotal, taxRatePercent)
}

fun calculateTotalUnderTax(
    items: List<InvoiceItem>,
    taxRatePercent: Int,
): BigDecimal {
    val itemsTotal = itemsTotal(items)
    val taxForItems = calculateTaxForItems(items, taxRatePercent)

    return itemsTotal.add(taxForItems)
}

fun itemsTotal(items: List<InvoiceItem>): BigDecimal =
    items.fold(BigDecimal.ZERO) { acc, item ->
        acc.add(item.totalPrice)
    }
