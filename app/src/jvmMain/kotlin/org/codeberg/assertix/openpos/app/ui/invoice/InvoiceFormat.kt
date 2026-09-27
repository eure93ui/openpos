package org.codeberg.assertix.openpos.app.ui.invoice

fun formattedId(id: Int, invoicePrefix: String): String {
    val idWithPadding = id.toString().padStart(5, '0')
    return "$invoicePrefix$idWithPadding"
}