package org.codeberg.assertix.openpos.data.model.company

data class CompanyCredentials(
    val inn: String,
    val kpp: String = "",
)
