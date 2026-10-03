package org.codeberg.assertix.openpos.app.util

import javax.print.PrintService
import javax.print.PrintServiceLookup

object PrinterManager {
    fun getAvailablePrinters(): List<String> {
        return try {
            val printServices = PrintServiceLookup.lookupPrintServices(null, null)
            printServices.mapNotNull { it.name }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getPrintService(name: String): PrintService? {
        return try {
            val printServices = PrintServiceLookup.lookupPrintServices(null, null)
            printServices.find { it.name.equals(name, ignoreCase = true) }
        } catch (e: Exception) {
            null
        }
    }
}
