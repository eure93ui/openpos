package org.codeberg.assertix.openpos.reporting.rendering

import org.codeberg.assertix.openpos.data.model.company.CompanyProfile
import org.codeberg.assertix.openpos.data.model.invoice.Invoice
import org.codeberg.assertix.openpos.reporting.engine.TemplateEngine
import org.codeberg.assertix.openpos.reporting.mapping.InvoiceReportMapper
import java.io.StringWriter

interface ReportRenderer {
    fun render(
        templateName: String,
        context: Map<String, Any>,
    ): String

    fun renderWaybill(
        invoice: Invoice,
        companyProfile: CompanyProfile? = null,
        templateName: String = "templates/waybill_template.html",
    ): String = render(templateName, InvoiceReportMapper.mapContext(invoice, companyProfile))
}

class PebbleReportRenderer(
    private val templateEngine: TemplateEngine,
) : ReportRenderer {
    override fun render(
        templateName: String,
        context: Map<String, Any>,
    ): String {
        val template = templateEngine.getTemplate(templateName)
        val writer = StringWriter()
        template.evaluate(writer, context)
        return writer.toString()
    }
}
