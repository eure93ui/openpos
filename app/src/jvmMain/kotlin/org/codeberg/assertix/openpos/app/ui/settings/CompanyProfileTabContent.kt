package org.codeberg.assertix.openpos.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.components.FormRow
import org.codeberg.assertix.openpos.app.ui.components.FormSectionCard
import org.codeberg.assertix.openpos.app.ui.components.FormTextField
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.app.ui.startup.transformations.PhoneVisualTransformation
import org.codeberg.assertix.openpos.data.model.company.CompanyType
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CompanyProfileTabContent(
    state: SettingsUiState,
    viewModel: SettingsViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormSectionCard(title = "Основные реквизиты компании") {
            FormRow {
                FormTextField(
                    value = state.legalName,
                    onValueChange = { viewModel.updateLegalName(it) },
                    label = stringResource(Res.string.legal_name),
                    modifier = Modifier.fillMaxWidth()
                )
            }

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
                                selected = state.companyType == CompanyType.LegalEntity,
                                onClick = { viewModel.updateCompanyType(CompanyType.LegalEntity) }
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(Res.string.legal_entity), style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            RadioButton(
                                selected = state.companyType == CompanyType.Individual,
                                onClick = { viewModel.updateCompanyType(CompanyType.Individual) }
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(Res.string.individual), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            FormRow {
                FormTextField(
                    value = state.inn,
                    onValueChange = { viewModel.updateInn(it.filter { ch -> ch.isDigit() }) },
                    label = stringResource(Res.string.tbl_inn),
                    modifier = Modifier.weight(1f)
                )
                if (state.companyType == CompanyType.LegalEntity) {
                    FormTextField(
                        value = state.kpp,
                        onValueChange = { viewModel.updateKpp(it.filter { ch -> ch.isDigit() }) },
                        label = stringResource(Res.string.kpp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            FormRow {
                FormTextField(
                    value = state.address,
                    onValueChange = { viewModel.updateAddress(it) },
                    label = stringResource(Res.string.legal_address),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Телефоны компании",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                state.phoneNumbers.forEachIndexed { index, phone ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { viewModel.updatePhoneNumber(index, it) },
                            label = { Text(if (index == 0) stringResource(Res.string.tbl_phone) + " (основной)" else "Телефон ${index + 1}") },
                            visualTransformation = PhoneVisualTransformation(),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.weight(1f)
                        )
                        if (index > 0) {
                            IconButton(
                                onClick = { viewModel.removePhoneNumber(index) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Res.drawable.delete.toImage()
                            }
                        }
                    }
                }
                OutlinedButton(
                    onClick = { viewModel.addPhoneNumber() },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(16.dp)) {
                            Res.drawable.add.toImage()
                        }
                        Text("Добавить телефон")
                    }
                }
            }
        }

        FormSectionCard(title = "Банковские реквизиты") {
            FormRow {
                FormTextField(
                    value = state.bankName,
                    onValueChange = { viewModel.updateBankName(it) },
                    label = stringResource(Res.string.bank_name),
                    modifier = Modifier.weight(2f)
                )
                FormTextField(
                    value = state.bic,
                    onValueChange = { viewModel.updateBic(it) },
                    label = stringResource(Res.string.bic),
                    modifier = Modifier.weight(1f)
                )
            }

            FormRow {
                FormTextField(
                    value = state.account,
                    onValueChange = { viewModel.updateAccount(it) },
                    label = stringResource(Res.string.checking_account),
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    value = state.corrAccount,
                    onValueChange = { viewModel.updateCorrAccount(it) },
                    label = stringResource(Res.string.corr_account),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}