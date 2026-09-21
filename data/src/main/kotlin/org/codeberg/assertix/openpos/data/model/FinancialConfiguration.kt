package org.codeberg.assertix.openpos.data.model

/**
 * Database-level remembered settings
 */
data class FinancialConfiguration(
    val taxPercent: Int,
    val invoicePrefix: String,
)
