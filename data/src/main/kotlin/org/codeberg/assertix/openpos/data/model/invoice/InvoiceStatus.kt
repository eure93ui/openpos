package org.codeberg.assertix.openpos.data.model.invoice

import kotlinx.serialization.Serializable

@Serializable
enum class InvoiceStatus {
    DRAFT,
    ISSUED,
    PAID,
}
