package org.codeberg.assertix.openpos.database.api.repository.mapper

import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.data.model.FullName
import org.codeberg.assertix.openpos.data.validation.PhoneNumberValidator
import org.codeberg.assertix.openpos.database.model.ClientsTable
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toClient() =
    Client(
        id = this[ClientsTable.id].value,
        fullName =
            FullName(
                this[ClientsTable.name],
                this[ClientsTable.surname],
                this[ClientsTable.middleName],
            ),
        phoneNumber = PhoneNumberValidator.returnValidated(this[ClientsTable.phoneNumber]),
    )
