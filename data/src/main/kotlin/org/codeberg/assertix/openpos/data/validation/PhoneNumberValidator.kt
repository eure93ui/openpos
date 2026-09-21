package org.codeberg.assertix.openpos.data.validation

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.codeberg.assertix.openpos.data.model.PhoneNumber

object PhoneNumberValidator {
    private val phoneNumberUtil = PhoneNumberUtil.getInstance()
    private val outputFormat = PhoneNumberUtil.PhoneNumberFormat.E164

    fun returnValidated(raw: String?): PhoneNumber? {
        if (raw.isNullOrBlank()) return null

        return try {
            val parsed = phoneNumberUtil.parse(raw, "RU")

            if (phoneNumberUtil.isValidNumber(parsed)) {
                val formatted =
                    phoneNumberUtil.format(
                        parsed,
                        outputFormat,
                    )
                PhoneNumber(formatted)
            } else {
                null
            }
        } catch (_: NumberParseException) {
            null
        }
    }
}
