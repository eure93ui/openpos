package org.codeberg.assertix.openpos.app.ui.startup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.startup.*
import org.codeberg.assertix.openpos.app.ui.startup.transformations.BankAccountVisualTransformation
import org.codeberg.assertix.openpos.app.ui.startup.transformations.PhoneVisualTransformation
import org.codeberg.assertix.openpos.app.ui.startup.validators.DatabaseCreationFormState
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.app.util.PrinterManager
import org.codeberg.assertix.openpos.data.model.FinancialConfiguration
import org.codeberg.assertix.openpos.data.model.PhoneNumber
import org.codeberg.assertix.openpos.data.model.company.*
import org.codeberg.assertix.openpos.data.validation.CompanyProfileValidator
import org.codeberg.assertix.openpos.data.validation.PhoneNumberValidator
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun DatabaseCreationScreen(
    onBack: () -> Unit,
    onComplete: (CompanyProfile, FinancialConfiguration, String?) -> Unit
) {
    val formState = remember { DatabaseCreationFormState() }

    val stepTitle = when (formState.currentStep) {
        is StartupCreationStep.CompanyInfo -> stringResource(Res.string.step_company_info)
        is StartupCreationStep.CompanyCredentials -> stringResource(Res.string.step_company_credentials)
        is StartupCreationStep.BankDetails -> stringResource(Res.string.step_bank_details)
        is StartupCreationStep.FinancialSettings -> stringResource(Res.string.step_financial_settings)
        is StartupCreationStep.DefaultPrinter -> stringResource(Res.string.step_default_printer)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(StartupConstants.SpacingMedium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(28.dp)) {
                Res.drawable.group_add.toImage()
            }
            Text(
                text = stringResource(Res.string.startup_create_db),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        StartupStepProgress(
            currentStep = formState.currentStep.index,
            totalSteps = 5,
            stepTitle = stepTitle
        )

        Spacer(modifier = Modifier.height(4.dp))

        when (formState.currentStep) {
            is StartupCreationStep.CompanyInfo -> {
                OutlinedTextField(
                    value = formState.legalName.value,
                    onValueChange = { formState.legalName.value = it },
                    label = { Text(stringResource(Res.string.legal_name)) },
                    isError = formState.legalName.error != null,
                    supportingText = { formState.legalName.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(Res.string.company_type),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                RadioButton(
                                    selected = formState.companyType == CompanyType.LegalEntity,
                                    onClick = { formState.companyType = CompanyType.LegalEntity }
                                )
                                Text(stringResource(Res.string.legal_entity), style = MaterialTheme.typography.bodyMedium)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                RadioButton(
                                    selected = formState.companyType == CompanyType.Individual,
                                    onClick = { formState.companyType = CompanyType.Individual }
                                )
                                Text(stringResource(Res.string.individual), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = formState.legalAddress.value,
                    onValueChange = { formState.legalAddress.value = it },
                    label = { Text(stringResource(Res.string.legal_address)) },
                    isError = formState.legalAddress.error != null,
                    supportingText = { formState.legalAddress.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = formState.phone.value,
                    onValueChange = { formState.phone.value = it },
                    label = { Text(stringResource(Res.string.tbl_phone)) },
                    isError = formState.phone.error != null,
                    supportingText = { formState.phone.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    visualTransformation = PhoneVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }
            is StartupCreationStep.CompanyCredentials -> {
                OutlinedTextField(
                    value = formState.inn.value,
                    onValueChange = { formState.inn.value = it.filter { ch -> ch.isDigit() } },
                    label = { Text(stringResource(Res.string.tbl_inn)) },
                    isError = formState.inn.error != null,
                    supportingText = { formState.inn.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                if (formState.companyType == CompanyType.LegalEntity) {
                    OutlinedTextField(
                        value = formState.kpp.value,
                        onValueChange = { formState.kpp.value = it.filter { ch -> ch.isDigit() } },
                        label = { Text(stringResource(Res.string.kpp)) },
                        isError = formState.kpp.error != null,
                        supportingText = { formState.kpp.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }
            is StartupCreationStep.BankDetails -> {
                OutlinedTextField(
                    value = formState.bankName.value,
                    onValueChange = { formState.bankName.value = it },
                    label = { Text(stringResource(Res.string.bank_name)) },
                    isError = formState.bankName.error != null,
                    supportingText = { formState.bankName.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = formState.bic.value,
                    onValueChange = { formState.bic.value = it.filter { ch -> ch.isDigit() } },
                    label = { Text(stringResource(Res.string.bic)) },
                    isError = formState.bic.error != null,
                    supportingText = { formState.bic.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = formState.checkingAccount.value,
                    onValueChange = { formState.checkingAccount.value = it.filter { ch -> ch.isDigit() } },
                    label = { Text(stringResource(Res.string.checking_account)) },
                    isError = formState.checkingAccount.error != null,
                    supportingText = { formState.checkingAccount.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    visualTransformation = BankAccountVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = formState.correspondentAccount.value,
                    onValueChange = { formState.correspondentAccount.value = it.filter { ch -> ch.isDigit() } },
                    label = { Text(stringResource(Res.string.corr_account)) },
                    isError = formState.correspondentAccount.error != null,
                    supportingText = { formState.correspondentAccount.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    visualTransformation = BankAccountVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }
            is StartupCreationStep.FinancialSettings -> {
                OutlinedTextField(
                    value = formState.invoicePrefix.value,
                    onValueChange = { formState.invoicePrefix.value = it },
                    label = { Text(stringResource(Res.string.invoice_prefix)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = formState.taxPercent.value,
                    onValueChange = { formState.taxPercent.value = it.filter { ch -> ch.isDigit() } },
                    label = { Text(stringResource(Res.string.vat_rate)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("0", "5", "7", "20", "22").forEach { rate ->
                        FilterChip(
                            selected = formState.taxPercent.value == rate,
                            onClick = { formState.taxPercent.value = rate },
                            label = { Text("$rate%") }
                        )
                    }
                }
            }
            is StartupCreationStep.DefaultPrinter -> {
                val printers = remember { PrinterManager.getAvailablePrinters() }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Выберите принтер для печати чеков и накладных по умолчанию (или пропустите для показа диалога ОС каждый раз):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        onClick = { formState.selectedPrinter.value = null }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = formState.selectedPrinter.value == null,
                                onClick = { formState.selectedPrinter.value = null }
                            )
                            Text("Не использовать (всегда показывать диалог печати ОС)", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    if (printers.isEmpty()) {
                        Text(
                            text = "Системные принтеры не обнаружены. Будет использоваться диалог печати ОС.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(printers) { printerName ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    onClick = { formState.selectedPrinter.value = printerName }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        RadioButton(
                                            selected = formState.selectedPrinter.value == printerName,
                                            onClick = { formState.selectedPrinter.value = printerName }
                                        )
                                        Text(printerName, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                StartupSecondaryButton(
                    text = stringResource(Res.string.btn_back),
                    onClick = {
                        if (formState.currentStep.previous != null) {
                            formState.proceedToPreviousStep()
                        } else {
                            onBack()
                        }
                    },
                    icon = Res.drawable.close
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                StartupPrimaryButton(
                    text = if (formState.currentStep.next != null) stringResource(Res.string.btn_next) else stringResource(Res.string.btn_create_db),
                    onClick = {
                        if (formState.currentStep.next != null) {
                            formState.proceedToNextStep()
                        } else {
                            val profile = CompanyProfile(
                                companyInfo = CompanyInfo(
                                    fullNaming = formState.legalName.value,
                                    companyType = formState.companyType,
                                    legalAddress = formState.legalAddress.value,
                                    phoneNumber = PhoneNumberValidator.returnValidated(formState.phone.value) ?: PhoneNumber(
                                        formState.phone.value
                                    )
                                ),
                                companyCredentials = CompanyCredentials(
                                    inn = formState.inn.value,
                                    kpp = if (formState.companyType == CompanyType.Individual) "" else formState.kpp.value
                                ),
                                bankDetails = BankDetails(
                                    bankName = formState.bankName.value,
                                    bic = formState.bic.value,
                                    checkingAccount = formState.checkingAccount.value,
                                    correspondentAccount = formState.correspondentAccount.value
                                )
                            )
                            val financialConfig = FinancialConfiguration(
                                taxPercent = formState.taxPercent.value.toIntOrNull() ?: 22,
                                invoicePrefix = formState.invoicePrefix.value
                            )
                            val validationResult = CompanyProfileValidator.validate(profile)
                            if (validationResult.isValid) {
                                onComplete(profile, financialConfig, formState.selectedPrinter.value)
                            }
                        }
                    },
                    enabled = formState.isCurrentStepValid,
                    icon = Res.drawable.add
                )
            }
        }
    }
}
