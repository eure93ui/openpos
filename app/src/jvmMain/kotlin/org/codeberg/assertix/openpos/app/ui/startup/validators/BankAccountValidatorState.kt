package org.codeberg.assertix.openpos.app.ui.startup.validators

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.codeberg.assertix.openpos.data.validation.BankAccountValidator

@Stable
class BankAccountValidatorState(
    initialValue: String,
    private val accountTypeName: String
) {
    var value by mutableStateOf(initialValue)
    val error: String?
        get() = BankAccountValidator.getValidationError(value, accountTypeName)
    val isValid: Boolean
        get() = error == null
}
