package org.codeberg.assertix.openpos.database.api.repository.finance

import org.codeberg.assertix.openpos.data.model.FinancialConfiguration
import org.codeberg.assertix.openpos.database.api.SessionHolder
import org.codeberg.assertix.openpos.database.model.FinancialSettingsTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class FinancialRepository(
    private val sessionHolder: SessionHolder,
) {
    private suspend fun <T> query(block: suspend () -> T): T = sessionHolder.query(block)

    suspend fun get(): FinancialConfiguration =
        query {
            FinancialSettingsTable
                .selectAll()
                .where { FinancialSettingsTable.id eq 1 }
                .singleOrNull()
                ?.let {
                    FinancialConfiguration(
                        taxPercent = it[FinancialSettingsTable.taxPercent],
                        invoicePrefix = it[FinancialSettingsTable.invoicePrefix],
                    )
                } ?: FinancialConfiguration(taxPercent = 20, invoicePrefix = "INV-2026-")
        }

    suspend fun update(settings: FinancialConfiguration): Unit =
        query {
            val row =
                FinancialSettingsTable
                    .selectAll()
                    .where { FinancialSettingsTable.id eq 1 }
                    .singleOrNull()

            if (row != null) {
                FinancialSettingsTable.update({ FinancialSettingsTable.id eq 1 }) {
                    it[FinancialSettingsTable.taxPercent] = settings.taxPercent
                    it[FinancialSettingsTable.invoicePrefix] = settings.invoicePrefix
                }
            } else {
                FinancialSettingsTable.insert {
                    it[id] = 1
                    it[FinancialSettingsTable.taxPercent] = settings.taxPercent
                    it[FinancialSettingsTable.invoicePrefix] = settings.invoicePrefix
                }
            }
        }
}
