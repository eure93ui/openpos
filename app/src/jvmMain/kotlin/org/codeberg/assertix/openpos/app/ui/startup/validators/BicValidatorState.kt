package org.codeberg.assertix.openpos.app.ui.startup.validators

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.codeberg.assertix.openpos.data.validation.BicValidator

@Stable
class BicValidatorState(initialValue: String = "044525225") {
    var value by mutableStateOf(initialValue)
    val error: String?
        get() = BicValidator.getValidationError(value)
    val isValid: Boolean
        get() = error == null
}
