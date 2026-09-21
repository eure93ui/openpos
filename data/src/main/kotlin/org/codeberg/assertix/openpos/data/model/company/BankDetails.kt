package org.codeberg.assertix.openpos.data.model.company

import kotlinx.serialization.Serializable

@Serializable
data class BankDetails(
    val bankName: String,
    val bic: String,
    val checkingAccount: String,
    val correspondentAccount: String,
)
