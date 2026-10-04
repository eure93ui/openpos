package org.codeberg.assertix.openpos.database.api

import org.codeberg.assertix.openpos.data.model.FinancialConfiguration
import org.codeberg.assertix.openpos.data.model.company.CompanyProfile
import org.codeberg.assertix.openpos.database.model.ClientsTable
import org.codeberg.assertix.openpos.database.model.FinancialSettingsTable
import org.codeberg.assertix.openpos.database.model.InvoiceItemsTable
import org.codeberg.assertix.openpos.database.model.InvoicesTable
import org.codeberg.assertix.openpos.database.model.ItemsTable
import org.codeberg.assertix.openpos.database.model.company.BankDetailsTable
import org.codeberg.assertix.openpos.database.model.company.CompanyCredentialsTable
import org.codeberg.assertix.openpos.database.model.company.CompanyInfoTable
import org.codeberg.assertix.openpos.database.model.company.CompanyPhonesTable
import org.codeberg.assertix.openpos.database.model.company.CompanyProfileTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import java.nio.file.Path
import kotlin.io.path.absolutePathString

internal data class DatabaseSession(
    private val path: Path,
) {
    internal val database =
        Database
            .connect(
                "jdbc:sqlite:${path.absolutePathString()}",
                "org.sqlite.JDBC",
            ).apply {
                connector().apply {
                    autoCommit = true
                    prepareStatement("PRAGMA journal_mode = WAL;", false)
                    prepareStatement("PRAGMA synchronous = NORMAL;", false)
                    prepareStatement("PRAGMA busy_timeout = 5000;", false)
                }
            }

    /**
     * Creates database tables required by the application if they do not already exist and inserts initial company profile.
     * @return `true` if creation succeeded; `false` if initialization failed.
     */
    suspend fun createDatabase(
        companyProfile: CompanyProfile,
        financialConfiguration: FinancialConfiguration = FinancialConfiguration(taxPercent = 22, invoicePrefix = "INV-2026-"),
    ): Boolean =
        try {
            query {
                SchemaUtils.create(
                    ClientsTable,
                    ItemsTable,
                    InvoiceItemsTable,
                    InvoicesTable,
                    CompanyInfoTable,
                    CompanyPhonesTable,
                    CompanyCredentialsTable,
                    BankDetailsTable,
                    CompanyProfileTable,
                    FinancialSettingsTable,
                )
                if (CompanyProfileTable.selectAll().where { CompanyProfileTable.id eq 1 }.empty()) {
                    val infoId =
                        CompanyInfoTable.insertAndGetId {
                            it[fullNaming] = companyProfile.companyInfo.fullNaming
                            it[companyType] = companyProfile.companyInfo.companyType
                            it[legalAddress] = companyProfile.companyInfo.legalAddress
                            it[phoneNumber] = companyProfile.companyInfo.phoneNumber.value
                        }
                    companyProfile.companyInfo.phoneNumbers.forEach { phone ->
                        CompanyPhonesTable.insert {
                            it[companyInfo] = infoId
                            it[phoneNumber] = phone.value
                        }
                    }
                    val credentialsId =
                        CompanyCredentialsTable.insertAndGetId {
                            it[inn] = companyProfile.companyCredentials.inn
                            it[kpp] = companyProfile.companyCredentials.kpp
                        }
                    val bankId =
                        BankDetailsTable.insertAndGetId {
                            it[bankName] = companyProfile.bankDetails.bankName
                            it[bic] = companyProfile.bankDetails.bic
                            it[checkingAccount] = companyProfile.bankDetails.checkingAccount
                            it[correspondentAccount] = companyProfile.bankDetails.correspondentAccount
                        }
                    CompanyProfileTable.insert {
                        it[id] = 1
                        it[companyInfo] = infoId
                        it[companyCredentials] = credentialsId
                        it[bankDetails] = bankId
                    }
                }
                if (FinancialSettingsTable.selectAll().where { FinancialSettingsTable.id eq 1 }.empty()) {
                    FinancialSettingsTable.insert {
                        it[id] = 1
                        it[taxPercent] = financialConfiguration.taxPercent
                        it[invoicePrefix] = financialConfiguration.invoicePrefix
                    }
                }
            }
            true
        } catch (_: Exception) {
            false
        }

    internal suspend fun <T> query(block: suspend () -> T): T = suspendTransaction(database) { block() }

    fun close() {
        TransactionManager.closeAndUnregister(database)
    }
}
