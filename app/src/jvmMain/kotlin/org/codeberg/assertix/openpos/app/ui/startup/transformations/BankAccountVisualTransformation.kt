package org.codeberg.assertix.openpos.app.ui.startup.transformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class BankAccountVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val trimmed = if (digits.length > 20) digits.substring(0, 20) else digits

        val out = StringBuilder()
        for (i in trimmed.indices) {
            if (i > 0 && i % 4 == 0) {
                out.append(" ")
            }
            out.append(trimmed[i])
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceAtMost(trimmed.length)
                var spaces = 0
                for (i in 0 until clamped) {
                    if (i > 0 && i % 4 == 0) spaces++
                }
                return clamped + spaces
            }

            override fun transformedToOriginal(offset: Int): Int {
                var digitsCount = 0
                for (i in 0 until offset.coerceAtMost(out.length)) {
                    if (out[i].isDigit()) digitsCount++
                }
                return digitsCount.coerceAtMost(trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}
