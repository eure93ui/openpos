package org.codeberg.assertix.openpos.app.ui.startup.validators

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.codeberg.assertix.openpos.data.model.company.CompanyType
import org.codeberg.assertix.openpos.data.validation.KppValidator

@Stable
class KppValidatorState(
    initialValue: String = "770701001",
    private val companyTypeProvider: () -> CompanyType
) {
    var value by mutableStateOf(initialValue)
    val error: String?
        get() = KppValidator.getValidationError(value, companyTypeProvider())
    val isValid: Boolean
        get() = error == null
}
