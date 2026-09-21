package org.codeberg.assertix.openpos.app.ui.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.codeberg.assertix.openpos.app.ui.UiConstants
import org.codeberg.assertix.openpos.app.ui.components.ActionIconButton
import org.codeberg.assertix.openpos.app.ui.components.AppTableHeaderRow
import org.codeberg.assertix.openpos.app.ui.components.AppTableSurface
import org.codeberg.assertix.openpos.app.ui.components.EmptyStateView
import org.codeberg.assertix.openpos.app.ui.components.PaginationBar
import org.codeberg.assertix.openpos.app.ui.components.QuerySize
import org.codeberg.assertix.openpos.data.model.Item
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.ItemsTable(
    items: List<Item>,
    totalItems: Long,
    currentPage: Int,
    querySize: QuerySize,
    isLoading: Boolean,
    onEdit: (Item) -> Unit,
    onDelete: (Item) -> Unit,
    onQuerySizeChange: (QuerySize) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onAddItem: () -> Unit,
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
                stringResource(Res.string.tbl_product_name),
                modifier = Modifier.weight(UiConstants.WeightNameColumnLarge),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                stringResource(Res.string.tbl_unit),
                modifier = Modifier.width(UiConstants.UnitColumnWidth),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(Res.string.tbl_default_price),
                modifier = Modifier.weight(UiConstants.WeightPriceColumn),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End
            )
            Text(
                stringResource(Res.string.tbl_actions),
                modifier = Modifier.width(UiConstants.ActionColumnWidthSmall),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }

        Box(modifier = Modifier.weight(UiConstants.WeightDefault).fillMaxWidth()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (items.isEmpty()) {
                EmptyStateView(
                    icon = Res.drawable.packages,
                    title = "Товары не найдены",
                    description = "Добавьте новый товар или измените поисковый запрос",
                    onPrimaryActionClick = onAddItem,
                    primaryActionText = stringResource(Res.string.btn_new_product),
                    onSecondaryActionClick = onClearSearch,
                    secondaryActionText = "Сбросить поиск"
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(items) { index, item ->
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
                                text = item.productName,
                                modifier = Modifier.weight(UiConstants.WeightNameColumnLarge),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = item.unitOfMeasure,
                                modifier = Modifier.width(UiConstants.UnitColumnWidth),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${item.defaultPrice} ₽",
                                modifier = Modifier.weight(UiConstants.WeightPriceColumn),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.End
                            )
                            Row(
                                modifier = Modifier.width(UiConstants.ActionColumnWidthSmall),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ActionIconButton(
                                    icon = Res.drawable.edit,
                                    onClick = { onEdit(item) }
                                )
                                ActionIconButton(
                                    icon = Res.drawable.delete,
                                    onClick = { onDelete(item) }
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = UiConstants.SurfaceVariantAlpha), thickness = UiConstants.DividerThicknessDefault)
                    }
                }
            }
        }

        val pageSizeVal = querySize.size
        val startIndex = if (totalItems > 0) (currentPage - 1) * pageSizeVal + 1 else 0
        val endIndex = minOf(currentPage * pageSizeVal, totalItems.toInt())
        val showingText = "Показано $startIndex-$endIndex из $totalItems"

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
