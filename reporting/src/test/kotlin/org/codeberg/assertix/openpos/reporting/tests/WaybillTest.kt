package org.codeberg.assertix.openpos.reporting.tests

import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.data.model.FullName
import org.codeberg.assertix.openpos.data.model.TaxRate
import org.codeberg.assertix.openpos.data.model.company.BankDetails
import org.codeberg.assertix.openpos.data.model.company.CompanyCredentials
import org.codeberg.assertix.openpos.data.model.company.CompanyInfo
import org.codeberg.assertix.openpos.data.model.company.CompanyProfile
import org.codeberg.assertix.openpos.data.model.company.CompanyType
import org.codeberg.assertix.openpos.data.model.invoice.Invoice
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceItem
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceStatus
import org.codeberg.assertix.openpos.reporting.engine.PebbleTemplateEngine
import org.codeberg.assertix.openpos.reporting.printing.OpenHtmlToPdfConverter
import org.codeberg.assertix.openpos.reporting.rendering.PebbleReportRenderer
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class WaybillTest {
    @Test
    fun testModularRenderingAndConversion() {
        val companyProfile =
            CompanyProfile(
                companyInfo =
                    CompanyInfo(
                        fullNaming = "ООО \"Тест\"\n",
                        companyType = CompanyType.LegalEntity,
                        legalAddress = "г. Москва",
                        phoneNumber =
                            org.codeberg.assertix.openpos.data.model
                                .PhoneNumber("79991234567"),
                    ),
                companyCredentials = CompanyCredentials(inn = "1234567890", kpp = "123456001"),
                bankDetails = BankDetails("Bank", "123456789", "40702810000000000000", "30101810400000000225"),
            )

        val invoice =
            Invoice(
                id = 1,
                invoiceNumber = "АК-00001",
                issueDate = LocalDate.now(),
                updatedAt = LocalDateTime.now(),
                client =
                    Client(
                        id = 1,
                        fullName = FullName("Иван", "Иванов", "Иванович"),
                        phoneNumber = null,
                    ),
                clientNameSnapshot = "Иванов И. И.",
                clientPhoneNumberSnapshot = "+7 999 000-00-00",
                taxRate = TaxRate.Rate20,
                items =
                    listOf(
                        InvoiceItem(
                            id = 1,
                            itemId = 1,
                            productNameSnapshot = "Товар 1",
                            unitOfMeasureSnapshot = "шт",
                            quantity = BigDecimal("2"),
                            unitPrice = BigDecimal("500"),
                            totalPrice = BigDecimal("1000"),
                        ),
                    ),
                totalPrice = BigDecimal("1000"),
                status = InvoiceStatus.ISSUED,
            )

        val templateEngine = PebbleTemplateEngine()
        val renderer = PebbleReportRenderer(templateEngine)
        val html = renderer.renderWaybill(invoice, companyProfile)
        assertNotNull(html)
        assertFalse(html.isBlank())

        val pdfConverter = OpenHtmlToPdfConverter()
        val pdfBytes = pdfConverter.htmlToPdfBytes(html)
        assertNotNull(pdfBytes)
        assert(pdfBytes.isNotEmpty())

        val images = pdfConverter.pdfToImages(pdfBytes, dpi = 72f)
        assertNotNull(images)
        assert(images.isNotEmpty())
    }
}
