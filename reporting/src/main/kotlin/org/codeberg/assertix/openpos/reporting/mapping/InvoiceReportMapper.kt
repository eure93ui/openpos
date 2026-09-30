package org.codeberg.assertix.openpos.reporting.mapping

import org.codeberg.assertix.openpos.data.model.company.CompanyProfile
import org.codeberg.assertix.openpos.data.model.invoice.Invoice
import org.codeberg.assertix.openpos.reporting.engine.stringRepresentation
import java.math.RoundingMode
import java.util.Locale

object InvoiceReportMapper {
    fun mapContext(
        invoice: Invoice,
        companyProfile: CompanyProfile? = null,
    ): Map<String, Any> {
        val invoiceMap =
            mapOf(
                "invoiceNumber" to invoice.invoiceNumber,
                "issueDate" to invoice.issueDate.toString(),
                "clientNameSnapshot" to invoice.clientNameSnapshot,
                "clientPhoneNumberSnapshot" to invoice.clientPhoneNumberSnapshot,
                "taxRate" to
                    mapOf(
                        "percent" to invoice.taxRate.percent,
                        "name" to invoice.taxRate.toString(),
                    ),
                "items" to
                    invoice.items.mapIndexed { index, item ->
                        mapOf(
                            "index" to index + 1,
                            "productNameSnapshot" to item.productNameSnapshot,
                            "unitOfMeasureSnapshot" to item.unitOfMeasureSnapshot,
                            "quantity" to item.quantity.toPlainString(),
                            "unitPrice" to item.unitPrice.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                            "totalPrice" to item.totalPrice.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                        )
                    },
                "subTotalAmount" to invoice.totalPrice.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                "vatAmount" to
                    invoice.totalPriceUnderTax
                        .subtract(invoice.totalPrice)
                        .setScale(2, RoundingMode.HALF_UP)
                        .toPlainString(),
                "totalAmount" to invoice.totalPriceUnderTax.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                "invoiceAmountInWords" to
                    invoice.totalPriceUnderTax
                        .setScale(2, RoundingMode.HALF_UP)
                        .stringRepresentation(Locale.getDefault().language)
                        .replaceFirstChar { it.uppercase() },
            )

        val companyMap =
            if (companyProfile != null) {
                mapOf(
                    "legalName" to companyProfile.companyInfo.fullNaming,
                    "taxId" to companyProfile.companyCredentials.inn,
                    "kpp" to companyProfile.companyCredentials.kpp,
                    "address" to companyProfile.companyInfo.legalAddress,
                    "phoneNumber" to companyProfile.companyInfo.phoneNumber.value,
                )
            } else {
                mapOf(
                    "legalName" to "",
                    "taxId" to "",
                    "kpp" to "",
                    "address" to "",
                )
            }

        return mapOf(
            "invoice" to invoiceMap,
            "company" to companyMap,
        )
    }
}
