package org.codeberg.assertix.openpos.database.api.repository

import org.codeberg.assertix.openpos.data.model.invoice.Invoice
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceItem
import org.codeberg.assertix.openpos.data.model.invoice.filter
import org.codeberg.assertix.openpos.database.api.SessionHolder
import org.codeberg.assertix.openpos.database.api.repository.mapper.toClient
import org.codeberg.assertix.openpos.database.api.repository.mapper.toInvoice
import org.codeberg.assertix.openpos.database.api.repository.mapper.toInvoiceItem
import org.codeberg.assertix.openpos.database.model.ClientsTable
import org.codeberg.assertix.openpos.database.model.InvoiceItemsTable
import org.codeberg.assertix.openpos.database.model.InvoicesTable
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class InvoiceRepository(
    private val sessionHolder: SessionHolder,
) {
    private suspend fun <T> query(block: suspend () -> T): T = sessionHolder.query(block)

    suspend fun add(invoice: Invoice): Unit =
        query {
            val invoiceId: Int =
                InvoicesTable
                    .insertAndGetId {
                        it[invoiceNumber] = invoice.invoiceNumber
                        it[issueDate] = invoice.issueDate.toString()
                        it[updatedAt] = invoice.updatedAt.toString()
                        it[client] = invoice.client.id
                        it[clientNameSnapshot] = invoice.clientNameSnapshot
                        it[clientPhoneNumberSnapshot] = invoice.clientPhoneNumberSnapshot
                        it[taxRate] = invoice.taxRate.percent
                        it[totalPrice] = invoice.totalPrice.toPlainString()
                        it[totalPriceUnderTax] = invoice.totalPriceUnderTax.toPlainString()
                        it[status] = invoice.status
                        it[notes] = invoice.notes
                    }.value

            saveInvoiceItems(invoiceId, invoice.items)
        }

    suspend fun getById(invoiceId: Int): Invoice? =
        query {
            val invoiceJoinedClient =
                (InvoicesTable innerJoin ClientsTable)
                    .selectAll()
                    .where { InvoicesTable.id eq invoiceId }
                    .singleOrNull() ?: return@query null

            val client = invoiceJoinedClient.toClient()
            val items: List<InvoiceItem> =
                InvoiceItemsTable
                    .selectAll()
                    .where { InvoiceItemsTable.invoice eq invoiceId }
                    .map { it.toInvoiceItem() }

            invoiceJoinedClient.toInvoice(client, items)
        }

    private fun getAllInternal(): List<Invoice> {
        val invoiceJoinedClients =
            (InvoicesTable innerJoin ClientsTable)
                .selectAll()
                .orderBy(InvoicesTable.id to SortOrder.DESC)
                .toList()

        if (invoiceJoinedClients.isEmpty()) return emptyList()

        val invoiceIds: List<Int> = invoiceJoinedClients.map { it[InvoicesTable.id].value }

        val itemsByInvoiceIds: Map<Int, List<InvoiceItem>> =
            InvoiceItemsTable
                .selectAll()
                .where { InvoiceItemsTable.invoice inList invoiceIds }
                .map { it[InvoiceItemsTable.invoice].value to it.toInvoiceItem() }
                .groupBy({ it.first }, { it.second })

        return invoiceJoinedClients.map { row ->
            val invoiceId: Int = row[InvoicesTable.id].value
            val invoiceItems: List<InvoiceItem> = itemsByInvoiceIds[invoiceId] ?: emptyList()
            val client = row.toClient()
            row.toInvoice(client, invoiceItems)
        }
    }

    suspend fun getInvoices(
        limit: Int,
        offset: Long,
        searchQuery: String = "",
    ): List<Invoice> =
        query {
            val allInvoices: List<Invoice> = getAllInternal()
            val query: String = searchQuery.trim().lowercase()
            val filtered: List<Invoice> =
                if (query.isBlank()) {
                    allInvoices
                } else {
                    allInvoices.filter { invoice ->
                        invoice.filter(query)
                    }
                }
            filtered.drop(offset.toInt()).take(limit)
        }

    suspend fun getNewId() = query { InvoicesTable.insertAndGetId {}.value }

    suspend fun getCount(searchQuery: String = ""): Long =
        query {
            val allInvoices = getAllInternal()
            val query: String = searchQuery.trim().lowercase()
            if (query.isBlank()) {
                allInvoices.size.toLong()
            } else {
                allInvoices.count { it.filter(query) }.toLong()
            }
        }

    suspend fun delete(invoiceId: Int): Boolean =
        query {
            InvoiceItemsTable.deleteWhere { InvoiceItemsTable.invoice eq invoiceId }
            InvoicesTable.deleteWhere { InvoicesTable.id eq invoiceId } == ONE_CHANGED
        }

    suspend fun update(invoice: Invoice): Unit =
        query {
            InvoicesTable.update(where = { InvoicesTable.id eq invoice.id }) {
                it[invoiceNumber] = invoice.invoiceNumber
                it[issueDate] = invoice.issueDate.toString()
                it[updatedAt] = invoice.updatedAt.toString()
                it[client] = invoice.client.id
                it[clientNameSnapshot] = invoice.clientNameSnapshot
                it[clientPhoneNumberSnapshot] = invoice.clientPhoneNumberSnapshot
                it[taxRate] = invoice.taxRate.percent
                it[totalPrice] = invoice.totalPrice.toPlainString()
                it[totalPriceUnderTax] = invoice.totalPriceUnderTax.toPlainString()
                it[status] = invoice.status
                it[notes] = invoice.notes
            }

            InvoiceItemsTable.deleteWhere { InvoiceItemsTable.invoice eq invoice.id }
            saveInvoiceItems(invoice.id, invoice.items)
        }

    private fun saveInvoiceItems(
        invoiceId: Int,
        invoiceItems: List<InvoiceItem>,
    ): Int =
        if (invoiceItems.isEmpty()) {
            NOTHING_CHANGED
        } else {
            InvoiceItemsTable
                .batchInsert(invoiceItems) { item ->
                    this[InvoiceItemsTable.invoice] = invoiceId
                    this[InvoiceItemsTable.item] = item.id
                    this[InvoiceItemsTable.productNameSnapshot] = item.productNameSnapshot
                    this[InvoiceItemsTable.unitOfMeasureSnapshot] = item.unitOfMeasureSnapshot
                    this[InvoiceItemsTable.quantity] = item.quantity.toPlainString()
                    this[InvoiceItemsTable.unitPrice] = item.unitPrice.toPlainString()
                    this[InvoiceItemsTable.totalPrice] = item.totalPrice.toPlainString()
                }.size
        }
}
