package org.codeberg.assertix.openpos.database.model

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val PHONE_NUMBER_LENGTH = 12

object ClientsTable : IntIdTable("clients") {
    val name = text("name").index()
    val surname = text("surname").index()
    val middleName = text("middle_name").index()
    val phoneNumber = char("phone_number", PHONE_NUMBER_LENGTH).nullable().index()
}
