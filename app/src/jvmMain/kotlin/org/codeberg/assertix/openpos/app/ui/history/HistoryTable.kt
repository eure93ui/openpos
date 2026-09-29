package org.codeberg.assertix.openpos.app.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.UiConstants
import org.codeberg.assertix.openpos.app.ui.components.ActionIconButton
import org.codeberg.assertix.openpos.app.ui.components.AppTableHeaderRow
import org.codeberg.assertix.openpos.app.ui.components.AppTableSurface
import org.codeberg.assertix.openpos.app.ui.components.EmptyStateView
import org.codeberg.assertix.openpos.app.ui.components.PaginationBar
import org.codeberg.assertix.openpos.app.ui.components.QuerySize
import org.codeberg.assertix.openpos.app.ui.components.StatusBadge
import org.codeberg.assertix.openpos.data.model.invoice.Invoice
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.delete
import org.codeberg.assertix.openpos.resources.edit
import org.codeberg.assertix.openpos.resources.history
import org.codeberg.assertix.openpos.resources.print
import org.codeberg.assertix.openpos.resources.tbl_actions
import org.codeberg.assertix.openpos.resources.tbl_client_name
import org.codeberg.assertix.openpos.resources.tbl_date
import org.codeberg.assertix.openpos.resources.tbl_invoice_number
import org.codeberg.assertix.openpos.resources.tbl_number
import org.codeberg.assertix.openpos.resources.tbl_status
import org.codeberg.assertix.openpos.resources.tbl_total
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal

@Composable
internal fun ColumnScope.HistoryTable(
    invoices: List<Invoice>,
    totalInvoices: Long,
    totalPrice: BigDecimal,
    currentPage: Int,
    querySize: QuerySize,
    isLoading: Boolean,
    onPrint: (Invoice) -> Unit,
    onEdit: (Invoice) -> Unit,
    onDelete: (Invoice) -> Unit,
    onQuerySizeChange: (QuerySize) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onNewInvoice: () -> Unit,
    onClearSearch: () -> Unit
) {
    AppTableSurface {
        AppTableHeaderRow {
            Text(
                stringResource(Res.string.tbl_number),
                modifier = Modifier.width(UiConstants.NumberColumnWidth),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(Res.string.tbl_invoice_number),
                modifier = Modifier.weight(UiConstants.WeightDateColumn),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                stringResource(Res.string.tbl_date),
                modifier = Modifier.weight(UiConstants.WeightDateColumn),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                stringResource(Res.string.tbl_client_name),
                modifier = Modifier.weight(UiConstants.WeightNameColumnSmall),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                stringResource(Res.string.tbl_total),
                modifier = Modifier.weight(UiConstants.WeightTotalColumn),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End
            )
            Text(
                stringResource(Res.string.tbl_status),
                modifier = Modifier.weight(UiConstants.WeightStatusColumn),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(Res.string.tbl_actions),
                modifier = Modifier.width(UiConstants.ActionColumnWidthMedium),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }

        Box(modifier = Modifier.weight(UiConstants.WeightDefault).fillMaxWidth()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (invoices.isEmpty()) {
                EmptyStateView(
                    icon = Res.drawable.history,
                    title = "История документов пуста",
                    description = "Создайте новую накладную или измените параметры фильтрации",
                    onPrimaryActionClick = onNewInvoice,
                    primaryActionText = "Создать накладную",
                    onSecondaryActionClick = onClearSearch,
                    secondaryActionText = "Сбросить поиск"
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(invoices) { index, invoice ->
                        val pageSizeVal = querySize.size
                        val rowIndex = (currentPage - 1) * pageSizeVal + index + 1
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = UiConstants.TableRowHorizontalPadding, vertical = UiConstants.TableRowVerticalPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$rowIndex",
                                modifier = Modifier.width(UiConstants.NumberColumnWidth),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = invoice.invoiceNumber,
                                modifier = Modifier.weight(UiConstants.WeightDateColumn),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = invoice.issueDate.toString(),
                                modifier = Modifier.weight(UiConstants.WeightDateColumn),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = invoice.client.fullName.snapshot(),
                                modifier = Modifier.weight(UiConstants.WeightNameColumnSmall),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${invoice.totalPriceUnderTax} ₽",
                                modifier = Modifier.weight(UiConstants.WeightTotalColumn),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.End
                            )
                            Box(
                                modifier = Modifier.weight(UiConstants.WeightStatusColumn),
                                contentAlignment = Alignment.Center
                            ) {
                                StatusBadge(status = invoice.status)
                            }
                            Row(
                                modifier = Modifier.width(UiConstants.ActionColumnWidthMedium),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ActionIconButton(
                                    icon = Res.drawable.print,
                                    onClick = { onPrint(invoice) }
                                )
                                ActionIconButton(
                                    icon = Res.drawable.edit,
                                    onClick = { onEdit(invoice) }
                                )
                                ActionIconButton(
                                    icon = Res.drawable.delete,
                                    onClick = { onDelete(invoice) }
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = UiConstants.SurfaceVariantAlpha), thickness = UiConstants.DividerThicknessDefault)
                    }
                }
            }
        }

        val pageSizeVal = querySize.size
        val startIndex = if (totalInvoices > 0) (currentPage - 1) * pageSizeVal + 1 else 0
        val endIndex = minOf(currentPage * pageSizeVal, totalInvoices.toInt())
        val showingText = "Показано $startIndex-$endIndex из $totalInvoices"

        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = UiConstants.SurfaceVariantAlpha), thickness = UiConstants.DividerThicknessDefault)

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = UiConstants.TableRowHorizontalPadding, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Итого за выбранный период:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$totalPrice ₽",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = UiConstants.SurfaceVariantAlpha), thickness = UiConstants.DividerThicknessDefault)

        PaginationBar(
            showingText = showingText,
            currentPage = currentPage,
            querySize = querySize,
            onQuerySizeChange = onQuerySizeChange,
            onPrevious = onPreviousPage,
            onNext = onNextPage
        )
    }
}
