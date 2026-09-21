package org.codeberg.assertix.openpos.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FullName(
    val name: String,
    val surname: String,
    val middleName: String,
) {
    override fun toString(): String = "$surname $name $middleName"
}

fun FullName.filter(query: String) =
    name.contains(query) ||
        surname.contains(query) || middleName.contains(query)

@Serializable
data class Client(
    val id: Int,
    val fullName: FullName,
    val phoneNumber: PhoneNumber?,
)

fun Client.containedInFullName(query: String) = fullName.filter(query)
