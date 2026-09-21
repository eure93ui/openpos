package org.codeberg.assertix.openpos.app.ui.startup.validators

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.codeberg.assertix.openpos.data.validation.PhoneNumberValidator

@Stable
class PhoneValidator(initialValue: String = "+7 (999) 123-45-67") {
    var value by mutableStateOf(initialValue)
    val error: String?
        get() = if (PhoneNumberValidator.returnValidated(value) == null) "Некорректный номер телефона" else null
    val isValid: Boolean
        get() = error == null
}
