package org.codeberg.assertix.openpos.database.api.repository

import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.database.api.SessionHolder
import org.codeberg.assertix.openpos.database.api.repository.mapper.toClient
import org.codeberg.assertix.openpos.database.model.ClientsTable
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class ClientRepository(
    private val sessionHolder: SessionHolder,
) {
    private suspend fun <T> query(block: suspend () -> T): T = sessionHolder.query(block)

    suspend fun add(client: Client): Int =
        query {
            ClientsTable
                .insertAndGetId {
                    it[name] = client.fullName.name
                    it[surname] = client.fullName.surname
                    it[middleName] = client.fullName.middleName
                    it[phoneNumber] = client.phoneNumber?.value
                }.value
        }

    suspend fun getById(clientId: Int): Client? =
        query {
            ClientsTable
                .selectAll()
                .where { ClientsTable.id eq clientId }
                .singleOrNull()
                ?.toClient()
        }

    suspend fun getAll(): List<Client> =
        query {
            ClientsTable.selectAll().map { it.toClient() }
        }

    suspend fun getClients(
        limit: Int,
        offset: Long,
        searchQuery: String = "",
    ): List<Client> =
        query {
            val allClients: List<Client> = ClientsTable.selectAll().orderBy(ClientsTable.id to SortOrder.DESC).map { it.toClient() }
            val queryStr: String = searchQuery.trim().lowercase()
            val filtered: List<Client> =
                if (queryStr.isBlank()) {
                    allClients
                } else {
                    allClients.filter { client ->
                        val name: String = client.fullName.name.lowercase()
                        val surname: String = client.fullName.surname.lowercase()
                        val middleName: String = client.fullName.middleName.lowercase()
                        val phone: String = client.phoneNumber?.value?.lowercase() ?: ""
                        name.contains(queryStr) || surname.contains(queryStr) || middleName.contains(queryStr) || phone.contains(queryStr)
                    }
                }
            filtered.drop(offset.toInt()).take(limit)
        }

    suspend fun getCount(searchQuery: String = ""): Long =
        query {
            val allClients: List<Client> = ClientsTable.selectAll().map { it.toClient() }
            val queryStr: String = searchQuery.trim().lowercase()
            if (queryStr.isBlank()) {
                allClients.size.toLong()
            } else {
                allClients
                    .count { client ->
                        val name: String = client.fullName.name.lowercase()
                        val surname: String = client.fullName.surname.lowercase()
                        val middleName: String = client.fullName.middleName.lowercase()
                        val phone: String = client.phoneNumber?.value?.lowercase() ?: ""
                        name.contains(queryStr) || surname.contains(queryStr) || middleName.contains(queryStr) || phone.contains(queryStr)
                    }.toLong()
            }
        }

    suspend fun update(client: Client): Boolean =
        query {
            ClientsTable.update(where = { ClientsTable.id eq client.id }) {
                it[name] = client.fullName.name
                it[surname] = client.fullName.surname
                it[middleName] = client.fullName.middleName
                it[phoneNumber] = client.phoneNumber?.value
            } == ONE_CHANGED
        }

    suspend fun delete(clientId: Int): Boolean =
        query {
            ClientsTable.deleteWhere { ClientsTable.id eq clientId } == ONE_CHANGED
        }
}
