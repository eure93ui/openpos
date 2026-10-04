package org.codeberg.assertix.openpos.database.api.repository

import org.codeberg.assertix.openpos.data.model.PhoneNumber
import org.codeberg.assertix.openpos.data.model.company.CompanyProfile
import org.codeberg.assertix.openpos.data.validation.PhoneNumberValidator
import org.codeberg.assertix.openpos.database.api.SessionHolder
import org.codeberg.assertix.openpos.database.api.repository.mapper.toCompanyProfile
import org.codeberg.assertix.openpos.database.model.company.BankDetailsTable
import org.codeberg.assertix.openpos.database.model.company.CompanyCredentialsTable
import org.codeberg.assertix.openpos.database.model.company.CompanyInfoTable
import org.codeberg.assertix.openpos.database.model.company.CompanyPhonesTable
import org.codeberg.assertix.openpos.database.model.company.CompanyProfileTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
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

            CompanyPhonesTable.deleteWhere { CompanyPhonesTable.companyInfo eq infoId }
            companyProfile.companyInfo.phoneNumbers.forEach { phone ->
                CompanyPhonesTable.insert {
                    it[companyInfo] = infoId
                    it[phoneNumber] = phone.value
                }
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
            val profileRow =
                CompanyProfileTable
                    .innerJoin(CompanyInfoTable)
                    .innerJoin(CompanyCredentialsTable)
                    .innerJoin(BankDetailsTable)
                    .selectAll()
                    .where { CompanyProfileTable.id eq 1 }
                    .single()

            val infoId = profileRow[CompanyProfileTable.companyInfo].value
            val phoneNumbers =
                CompanyPhonesTable
                    .selectAll()
                    .where { CompanyPhonesTable.companyInfo eq infoId }
                    .map {
                        PhoneNumberValidator.returnValidated(it[CompanyPhonesTable.phoneNumber])
                            ?: PhoneNumber(it[CompanyPhonesTable.phoneNumber])
                    }.takeIf { it.isNotEmpty() }
                    ?: listOf(PhoneNumber(profileRow[CompanyInfoTable.phoneNumber]))

            profileRow.toCompanyProfile(phoneNumbers)
        }
}
