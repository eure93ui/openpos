package org.codeberg.assertix.openpos.database.api.repository.finance

import org.codeberg.assertix.openpos.data.model.FinancialConfiguration
import org.codeberg.assertix.openpos.data.model.TaxRate

const val DEFAULT_INVOICE_PREFIX = "INV-2026-"
val DEFAULT_TAX_PERCENT = TaxRate.Rate22.percent // TODO automatically select from
// database setup screen from business type -> indivudual/legal entity

val defaultFinancialConfiguration =
    FinancialConfiguration(taxPercent = DEFAULT_TAX_PERCENT, invoicePrefix = DEFAULT_INVOICE_PREFIX)
