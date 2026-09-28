package org.codeberg.assertix.openpos.reporting.engine

import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.ClasspathLoader
import io.pebbletemplates.pebble.template.PebbleTemplate

interface TemplateEngine {
    fun getTemplate(templateName: String): PebbleTemplate
}

class PebbleTemplateEngine : TemplateEngine {
    private val engine: PebbleEngine =
        PebbleEngine
            .Builder()
            .loader(ClasspathLoader())
            .strictVariables(false)
            .build()

    override fun getTemplate(templateName: String): PebbleTemplate = engine.getTemplate(templateName)
}
