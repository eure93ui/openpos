package org.codeberg.assertix.openpos.database.api.repository

import org.codeberg.assertix.openpos.data.model.Item
import org.codeberg.assertix.openpos.database.api.SessionHolder
import org.codeberg.assertix.openpos.database.api.repository.mapper.toItem
import org.codeberg.assertix.openpos.database.model.ItemsTable
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class ItemsRepository(
    private val sessionHolder: SessionHolder,
) {
    private suspend fun <T> query(block: suspend () -> T): T = sessionHolder.query(block)

    suspend fun add(item: Item): Int =
        query {
            ItemsTable
                .insertAndGetId {
                    it[productName] = item.productName
                    it[unitOfMeasure] = item.unitOfMeasure
                    it[defaultPrice] = item.defaultPrice.toPlainString()
                }.value
        }

    suspend fun getById(itemId: Int): Item? =
        query {
            ItemsTable
                .selectAll()
                .where { ItemsTable.id eq itemId }
                .singleOrNull()
                ?.toItem()
        }

    suspend fun getAll(): List<Item> =
        query {
            ItemsTable.selectAll().map { it.toItem() }
        }

    suspend fun getItems(
        limit: Int,
        offset: Long,
        searchQuery: String = "",
    ): List<Item> =
        query {
            val allItems: List<Item> = ItemsTable.selectAll().orderBy(ItemsTable.id to SortOrder.DESC).map { it.toItem() }
            val queryStr: String = searchQuery.trim().lowercase()
            val filtered: List<Item> =
                if (queryStr.isBlank()) {
                    allItems
                } else {
                    allItems.filter { item ->
                        val productName: String = item.productName.lowercase()
                        val unitOfMeasure: String = item.unitOfMeasure.lowercase()
                        productName.contains(queryStr) || unitOfMeasure.contains(queryStr)
                    }
                }
            filtered.drop(offset.toInt()).take(limit)
        }

    suspend fun getCount(searchQuery: String = ""): Long =
        query {
            val allItems: List<Item> = ItemsTable.selectAll().map { it.toItem() }
            val queryStr: String = searchQuery.trim().lowercase()
            if (queryStr.isBlank()) {
                allItems.size.toLong()
            } else {
                allItems
                    .count { item ->
                        val productName: String = item.productName.lowercase()
                        val unitOfMeasure: String = item.unitOfMeasure.lowercase()
                        productName.contains(queryStr) || unitOfMeasure.contains(queryStr)
                    }.toLong()
            }
        }

    suspend fun update(item: Item): Boolean =
        query {
            ItemsTable.update(
                where = { ItemsTable.id eq item.id },
            ) {
                it[productName] = item.productName
                it[unitOfMeasure] = item.unitOfMeasure
                it[defaultPrice] = item.defaultPrice.toPlainString()
            } == ONE_CHANGED
        }

    suspend fun delete(itemId: Int): Boolean =
        query {
            ItemsTable.deleteWhere { ItemsTable.id eq itemId } == ONE_CHANGED
        }
}
