package org.codeberg.assertix.openpos.database.api.repository

import org.codeberg.assertix.openpos.data.model.company.CompanyProfile
import org.codeberg.assertix.openpos.database.api.SessionHolder
import org.codeberg.assertix.openpos.database.api.repository.mapper.toCompanyProfile
import org.codeberg.assertix.openpos.database.model.company.BankDetailsTable
import org.codeberg.assertix.openpos.database.model.company.CompanyCredentialsTable
import org.codeberg.assertix.openpos.database.model.company.CompanyInfoTable
import org.codeberg.assertix.openpos.database.model.company.CompanyProfileTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class CompanyProfileRepository(
    private val sessionHolder: SessionHolder,
) {
    private suspend fun <T> query(block: suspend () -> T): T = sessionHolder.query(block)

    suspend fun update(companyProfile: CompanyProfile): Unit =
        query {
            val profileRow =
                CompanyProfileTable
                    .selectAll()
                    .where { CompanyProfileTable.id eq 1 }
                    .singleOrNull()
                    ?: error("Company profile with ID 1 does not exist. Run initialization first.")

            val infoId = profileRow[CompanyProfileTable.companyInfo].value
            val credentialsId = profileRow[CompanyProfileTable.companyCredentials].value
            val bankId = profileRow[CompanyProfileTable.bankDetails].value

            CompanyInfoTable.update({ CompanyInfoTable.id eq infoId }) {
                it[fullNaming] = companyProfile.companyInfo.fullNaming
                it[companyType] = companyProfile.companyInfo.companyType
                it[legalAddress] = companyProfile.companyInfo.legalAddress
                it[phoneNumber] = companyProfile.companyInfo.phoneNumber.value
            }

            CompanyCredentialsTable.update({ CompanyCredentialsTable.id eq credentialsId }) {
                it[inn] = companyProfile.companyCredentials.inn
                it[kpp] = companyProfile.companyCredentials.kpp
            }

            BankDetailsTable.update({ BankDetailsTable.id eq bankId }) {
                it[bankName] = companyProfile.bankDetails.bankName
                it[bic] = companyProfile.bankDetails.bic
                it[checkingAccount] = companyProfile.bankDetails.checkingAccount
                it[correspondentAccount] = companyProfile.bankDetails.correspondentAccount
            }
        }

    suspend fun get(): CompanyProfile =
        query {
            CompanyProfileTable
                .innerJoin(CompanyInfoTable)
                .innerJoin(CompanyCredentialsTable)
                .innerJoin(BankDetailsTable)
                .selectAll()
                .where { CompanyProfileTable.id eq 1 }
                .single()
                .toCompanyProfile()
        }
}
