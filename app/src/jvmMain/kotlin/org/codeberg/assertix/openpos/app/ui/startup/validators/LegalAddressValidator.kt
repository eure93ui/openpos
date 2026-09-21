package org.codeberg.assertix.openpos.app.ui.startup.validators

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class LegalAddressValidator(initialValue: String = "125009, г. Москва, Тверская ул., д. 12") {
    var value by mutableStateOf(initialValue)
    val error: String?
        get() = if (value.isBlank()) "Адрес не может быть пустым" else null
    val isValid: Boolean
        get() = error == null
}
