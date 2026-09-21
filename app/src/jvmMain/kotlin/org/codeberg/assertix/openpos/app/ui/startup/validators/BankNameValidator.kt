package org.codeberg.assertix.openpos.app.ui.startup.validators

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class BankNameValidator(initialValue: String = "ПАО СБЕРБАНК") {
    var value by mutableStateOf(initialValue)
    val error: String?
        get() = if (value.isBlank()) "Наименование банка не может быть пустым" else null
    val isValid: Boolean
        get() = error == null
}
