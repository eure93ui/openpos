package org.codeberg.assertix.openpos.app.ui.startup.transformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val trimmed = if (digits.length > 11) digits.substring(0, 11) else digits

        val out = StringBuilder()
        for (i in trimmed.indices) {
            if (i == 0) out.append("+")
            else if (i == 1) out.append(" (")
            else if (i == 4) out.append(") ")
            else if (i == 7 || i == 9) out.append("-")
            out.append(trimmed[i])
        }

        val phoneNumberOffsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val clamped = offset.coerceAtMost(trimmed.length)
                var transformed = 0
                for (i in 0 until clamped) {
                    transformed += when (i) {
                        0 -> 2 // + and space after? Wait: i=0 appends "+", i=1 appends " ("
                        1 -> 2 // " ("
                        4 -> 2 // ") "
                        7, 9 -> 1 // "-"
                        else -> 1
                    }
                }
                return transformed.coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val digitsOnly = out.toString().filter { it.isDigit() }.length
                return offset.coerceAtMost(digitsOnly)
            }
        }

        return TransformedText(AnnotatedString(out.toString()), phoneNumberOffsetMapping)
    }
}
