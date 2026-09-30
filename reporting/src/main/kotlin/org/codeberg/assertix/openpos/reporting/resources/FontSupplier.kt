package org.codeberg.assertix.openpos.reporting.resources

import com.openhtmltopdf.extend.FSSupplier

private const val INTER_FONT = "fonts/Inter-VariableFont_opsz,wght.ttf"

internal val interFontSupplier by lazy {
    FSSupplier {
        Thread
            .currentThread()
            .contextClassLoader
            .getResourceAsStream(INTER_FONT) ?: error("Classpath resource $INTER_FONT is missing.")
    }
}
