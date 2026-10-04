package org.codeberg.assertix.openpos.calculations

import java.math.BigDecimal
import java.math.RoundingMode

private val oneHundreed = BigDecimal(100)

internal fun calculateTotalWithTax(
    baseAmount: BigDecimal,
    taxRatePercent: Int,
): BigDecimal = baseAmount

fun calculateTaxAmount(
    baseAmount: BigDecimal,
    taxRatePercent: Int,
): BigDecimal {
    if (taxRatePercent <= 0) return BigDecimal.ZERO
    val taxRate = BigDecimal(taxRatePercent)
    val denominator = oneHundreed.add(taxRate)
    return baseAmount.multiply(taxRate).divide(denominator, 2, RoundingMode.HALF_UP)
}
