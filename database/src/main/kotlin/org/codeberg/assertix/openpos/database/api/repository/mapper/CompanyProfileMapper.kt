package org.codeberg.assertix.openpos.database.api.repository.mapper

import org.codeberg.assertix.openpos.data.model.PhoneNumber
import org.codeberg.assertix.openpos.data.model.company.BankDetails
import org.codeberg.assertix.openpos.data.model.company.CompanyCredentials
import org.codeberg.assertix.openpos.data.model.company.CompanyInfo
import org.codeberg.assertix.openpos.data.model.company.CompanyProfile
import org.codeberg.assertix.openpos.data.validation.PhoneNumberValidator
import org.codeberg.assertix.openpos.database.model.company.BankDetailsTable
import org.codeberg.assertix.openpos.database.model.company.CompanyCredentialsTable
import org.codeberg.assertix.openpos.database.model.company.CompanyInfoTable
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toCompanyProfile(phoneNumbers: List<PhoneNumber> = emptyList()): CompanyProfile =
    CompanyProfile(
        companyInfo =
            CompanyInfo(
                fullNaming = this[CompanyInfoTable.fullNaming],
                companyType = this[CompanyInfoTable.companyType],
                legalAddress = this[CompanyInfoTable.legalAddress],
                phoneNumbers =
                    phoneNumbers.ifEmpty {
                        listOf(
                            PhoneNumberValidator.returnValidated(this[CompanyInfoTable.phoneNumber])
                                ?: PhoneNumber(this[CompanyInfoTable.phoneNumber]),
                        )
                    },
            ),
        companyCredentials =
            CompanyCredentials(
                inn = this[CompanyCredentialsTable.inn],
                kpp = this[CompanyCredentialsTable.kpp],
            ),
        bankDetails =
            BankDetails(
                bankName = this[BankDetailsTable.bankName],
                bic = this[BankDetailsTable.bic],
                checkingAccount = this[BankDetailsTable.checkingAccount],
                correspondentAccount = this[BankDetailsTable.correspondentAccount],
            ),
    )
