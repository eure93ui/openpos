package org.codeberg.assertix.openpos.database.api

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.codeberg.assertix.openpos.data.model.PhoneNumber
import org.codeberg.assertix.openpos.data.model.company.BankDetails
import org.codeberg.assertix.openpos.data.model.company.CompanyCredentials
import org.codeberg.assertix.openpos.data.model.company.CompanyInfo
import org.codeberg.assertix.openpos.data.model.company.CompanyProfile
import org.codeberg.assertix.openpos.data.model.company.CompanyType
import org.codeberg.assertix.openpos.database.model.ClientsTable
import org.codeberg.assertix.openpos.database.model.FinancialSettingsTable
import org.codeberg.assertix.openpos.database.model.InvoiceItemsTable
import org.codeberg.assertix.openpos.database.model.InvoicesTable
import org.codeberg.assertix.openpos.database.model.ItemsTable
import org.codeberg.assertix.openpos.database.model.company.BankDetailsTable
import org.codeberg.assertix.openpos.database.model.company.CompanyCredentialsTable
import org.codeberg.assertix.openpos.database.model.company.CompanyInfoTable
import org.codeberg.assertix.openpos.database.model.company.CompanyProfileTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.isRegularFile

class SessionHolder : AutoCloseable {
    private var databaseSession: DatabaseSession? = null
    private var _currentPath: Path? = null
    val currentPath: Path? get() = _currentPath

    private val _savingState: MutableStateFlow<DatabaseSavingState> = MutableStateFlow(DatabaseSavingState.SAVED)
    val savingState: StateFlow<DatabaseSavingState> = _savingState.asStateFlow()

    internal fun requireDatabaseSession(): DatabaseSession = databaseSession ?: error("Database session has not been initialized.")

    val isInitialized: Boolean get() = databaseSession != null

    suspend fun <T> query(block: suspend () -> T): T {
        val session: DatabaseSession = requireDatabaseSession()
        _savingState.value = DatabaseSavingState.SAVING
        return try {
            val result: T = session.query(block)
            _savingState.value = DatabaseSavingState.SAVED
            result
        } catch (e: Exception) {
            _savingState.value = DatabaseSavingState.ERROR
            throw e
        }
    }

    suspend fun validateDatabase(path: Path): Boolean =
        path.hasContent() &&
            try {
                val db = Database.connect("jdbc:sqlite:${path.absolutePathString()}", "org.sqlite.JDBC")
                val isValid =
                    suspendTransaction(db) {
                        var schemaValid = false
                        exec("PRAGMA schema_version") { result ->
                            schemaValid = result.next()
                        }

                        val existingTables = mutableSetOf<String>()
                        exec("SELECT name FROM sqlite_master WHERE type='table';") { result ->
                            while (result.next()) {
                                result.getString("name")?.let { existingTables.add(it) }
                            }
                        }

                        val requiredTables =
                            listOf(
                                ClientsTable.tableName,
                                ItemsTable.tableName,
                                InvoiceItemsTable.tableName,
                                InvoicesTable.tableName,
                                CompanyInfoTable.tableName,
                                CompanyCredentialsTable.tableName,
                                BankDetailsTable.tableName,
                                CompanyProfileTable.tableName,
                                FinancialSettingsTable.tableName,
                            )

                        schemaValid && requiredTables.all { it in existingTables }
                    }
                TransactionManager.closeAndUnregister(db)
                isValid
            } catch (e: Exception) {
                false
            }

    suspend fun createDatabase(
        path: Path,
        companyProfile: CompanyProfile =
            CompanyProfile(
                companyInfo =
                    CompanyInfo(
                        fullNaming = "ООО \"Торговый Дом \"Оптима\"",
                        companyType = CompanyType.LegalEntity,
                        legalAddress = "125009, г. Москва, Тверская ул., д. 12",
                        phoneNumber = PhoneNumber("+7 (999) 123-45-67"),
                    ),
                companyCredentials =
                    CompanyCredentials(
                        inn = "7707083893",
                        kpp = "770701001",
                    ),
                bankDetails =
                    BankDetails(
                        bankName = "ПАО СБЕРБАНК",
                        bic = "044525225",
                        checkingAccount = "40702810838000012345",
                        correspondentAccount = "30101810400000000225",
                    ),
            ),
    ): Boolean {
        close()
        val session: DatabaseSession = DatabaseSession(path)
        val success: Boolean = session.createDatabase(companyProfile)
        if (success) {
            _currentPath = path
            databaseSession = session
        } else {
            session.close()
        }
        return success
    }

    fun initialize(path: Path) {
        check(databaseSession == null) {
            "DatabaseSession has already been initialized."
        }
        _currentPath = path
        databaseSession = DatabaseSession(path)
    }

    suspend fun switchSession(path: Path) {
        close()
        _currentPath = path
        databaseSession = DatabaseSession(path)
    }

    override fun close() {
        databaseSession?.let { session ->
            session.close()
            databaseSession = null
            _currentPath = null
        }
    }
}

private const val SQLITE_HEADER_LENGTH = 16

private fun Path.hasContent() = exists() && isRegularFile() && fileSize() > SQLITE_HEADER_LENGTH
