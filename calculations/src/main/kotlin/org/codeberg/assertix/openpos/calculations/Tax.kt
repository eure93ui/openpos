package org.codeberg.assertix.openpos.calculations

import java.math.BigDecimal

private val oneHundreed = BigDecimal(100)

internal fun calculateTotalWithTax(
    baseAmount: BigDecimal,
    taxRatePercent: Int,
): BigDecimal {
    val taxAmount = calculateTaxAmount(baseAmount, taxRatePercent)
    return baseAmount.add(taxAmount)
}

internal fun calculateTaxAmount(
    baseAmount: BigDecimal,
    taxRatePercent: Int,
): BigDecimal {
    val taxRate = BigDecimal(taxRatePercent)
    val taxRateFraction = taxRate.divide(oneHundreed)

    return baseAmount.multiply(taxRateFraction)
}
