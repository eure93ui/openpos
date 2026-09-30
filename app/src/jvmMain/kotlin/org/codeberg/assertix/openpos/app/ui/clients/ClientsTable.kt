package org.codeberg.assertix.openpos.app.ui.clients

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
import org.codeberg.assertix.openpos.app.ui.UiConstants
import org.codeberg.assertix.openpos.app.ui.components.ActionIconButton
import org.codeberg.assertix.openpos.app.ui.components.AppTableHeaderRow
import org.codeberg.assertix.openpos.app.ui.components.AppTableSurface
import org.codeberg.assertix.openpos.app.ui.components.EmptyStateView
import org.codeberg.assertix.openpos.app.ui.components.PaginationBar
import org.codeberg.assertix.openpos.app.ui.components.QuerySize
import org.codeberg.assertix.openpos.data.model.Client
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.delete
import org.codeberg.assertix.openpos.resources.edit
import org.codeberg.assertix.openpos.resources.group
import org.codeberg.assertix.openpos.resources.tbl_actions
import org.codeberg.assertix.openpos.resources.tbl_client_name
import org.codeberg.assertix.openpos.resources.tbl_number
import org.codeberg.assertix.openpos.resources.tbl_phone
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.ClientsTable(
    clients: List<Client>,
    totalClients: Long,
    currentPage: Int,
    querySize: QuerySize,
    isLoading: Boolean,
    onEdit: (Client) -> Unit,
    onDelete: (Client) -> Unit,
    onQuerySizeChange: (QuerySize) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onAddClient: () -> Unit,
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
                stringResource(Res.string.tbl_client_name),
                modifier = Modifier.weight(UiConstants.WeightNameColumnSmall),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                stringResource(Res.string.tbl_phone),
                modifier = Modifier.weight(UiConstants.WeightPhoneColumn),
                style = MaterialTheme.typography.labelMedium
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
            } else if (clients.isEmpty()) {
                EmptyStateView(
                    icon = Res.drawable.group,
                    title = "Клиенты не найдены",
                    description = "Добавьте клиента или измените параметры поиска",
                    onPrimaryActionClick = onAddClient,
                    primaryActionText = "Добавить клиента",
                    onSecondaryActionClick = onClearSearch,
                    secondaryActionText = "Сбросить поиск"
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(clients) { index, client ->
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
                                text = client.fullName.snapshot(),
                                modifier = Modifier.weight(UiConstants.WeightNameColumnSmall),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = client.phoneNumber?.value ?: "—",
                                modifier = Modifier.weight(UiConstants.WeightPhoneColumn),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (client.phoneNumber != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.width(UiConstants.ActionColumnWidthSmall),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ActionIconButton(
                                    icon = Res.drawable.edit,
                                    onClick = { onEdit(client) }
                                )
                                ActionIconButton(
                                    icon = Res.drawable.delete,
                                    onClick = { onDelete(client) }
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = UiConstants.SurfaceVariantAlpha), thickness = UiConstants.DividerThicknessDefault)
                    }
                }
            }
        }

        val pageSizeVal = querySize.size
        val startIndex = if (totalClients > 0) (currentPage - 1) * pageSizeVal + 1 else 0
        val endIndex = minOf(currentPage * pageSizeVal, totalClients.toInt())
        val showingText = "Показано $startIndex-$endIndex из $totalClients"

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
