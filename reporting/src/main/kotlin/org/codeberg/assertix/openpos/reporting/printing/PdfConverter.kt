package org.codeberg.assertix.openpos.reporting.printing

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.apache.pdfbox.Loader
import org.apache.pdfbox.printing.PDFPageable
import org.apache.pdfbox.rendering.PDFRenderer
import org.codeberg.assertix.openpos.reporting.resources.interFontSupplier
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.awt.print.PrinterJob
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.file.Files
import kotlin.io.path.createTempFile

interface PdfConverter {
    fun htmlToPdfStream(
        html: String,
        outputStream: OutputStream,
    )

    fun htmlToPdfBytes(html: String): ByteArray =
        ByteArrayOutputStream().use { out ->
            htmlToPdfStream(html, out)
            out.toByteArray()
        }

    fun pdfToImages(
        pdfBytes: ByteArray,
        dpi: Float = 150f,
    ): List<BufferedImage> {
        val images = mutableListOf<BufferedImage>()
        Loader.loadPDF(pdfBytes).use { document ->
            val pdfRenderer = PDFRenderer(document)
            for (i in 0 until document.numberOfPages) {
                images.add(pdfRenderer.renderImageWithDPI(i, dpi))
            }
        }
        return images
    }

    fun printPdf(pdfBytes: ByteArray) {
        Loader.loadPDF(pdfBytes).use { document ->
            val printerJob = PrinterJob.getPrinterJob()
            printerJob.setPageable(PDFPageable(document))
            if (printerJob.printDialog()) {
                printerJob.print()
            }
        }
    }

    fun openPdfExternal(pdfBytes: ByteArray) {
        val tempFile = createTempFile(prefix = ".pdf")
        Files.write(tempFile, pdfBytes)
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(tempFile.toFile())
        }
        tempFile.toFile().deleteOnExit()
    }
}

class OpenHtmlToPdfConverter : PdfConverter {
    override fun htmlToPdfStream(
        html: String,
        outputStream: OutputStream,
    ) {
        PdfRendererBuilder()
            .useFont(interFontSupplier, "Inter", 400, BaseRendererBuilder.FontStyle.NORMAL, true)
            .useFont(interFontSupplier, "Inter", 700, BaseRendererBuilder.FontStyle.NORMAL, true)
            .withHtmlContent(html, "/")
            .toStream(outputStream)
            .run()
    }
}
