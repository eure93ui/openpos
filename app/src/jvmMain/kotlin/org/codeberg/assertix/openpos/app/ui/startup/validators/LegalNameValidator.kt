package org.codeberg.assertix.openpos.app.ui.startup.validators

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class LegalNameValidator(initialValue: String = "ООО \"Торговый Дом \"Оптима\"") {
    var value by mutableStateOf(initialValue)
    val error: String?
        get() = if (value.isBlank()) "Наименование не может быть пустым" else null
    val isValid: Boolean
        get() = error == null
}
