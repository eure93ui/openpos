package org.codeberg.assertix.openpos.database

import kotlinx.coroutines.runBlocking
import org.codeberg.assertix.openpos.data.model.FinancialConfiguration
import org.codeberg.assertix.openpos.data.model.PhoneNumber
import org.codeberg.assertix.openpos.data.model.company.BankDetails
import org.codeberg.assertix.openpos.data.model.company.CompanyCredentials
import org.codeberg.assertix.openpos.data.model.company.CompanyInfo
import org.codeberg.assertix.openpos.data.model.company.CompanyProfile
import org.codeberg.assertix.openpos.data.model.company.CompanyType
import org.codeberg.assertix.openpos.database.api.DatabaseSession
import java.nio.file.Path
import kotlin.io.path.Path

object SchemaGenerator {
    @JvmStatic
    fun main(args: Array<String>) {
        val targetPath = if (args.isNotEmpty()) Path(args[0]) else Path("schema.sqlite")
        targetPath.toFile().parentFile?.mkdirs()
        if (targetPath.toFile().exists()) {
            targetPath.toFile().delete()
        }
        runBlocking {
            val session = DatabaseSession(targetPath)
            session.createDatabase(
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
                FinancialConfiguration(taxPercent = 22, invoicePrefix = "INV-2026-"),
            )
            session.close()
        }
    }
}
