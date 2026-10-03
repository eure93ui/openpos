package org.codeberg.assertix.openpos.app.ui.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.codeberg.assertix.openpos.app.ui.UiConstants
import org.codeberg.assertix.openpos.app.ui.components.ActionIconButton
import org.codeberg.assertix.openpos.app.ui.components.EmptyStateView
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.data.model.Item
import org.codeberg.assertix.openpos.data.model.invoice.InvoiceItem
import org.codeberg.assertix.openpos.resources.*
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal

@Composable
internal fun InvoiceItemsSection(
    items: List<InvoiceItem>,
    itemsError: String?,
    productSearchQuery: String,
    searchResults: List<Item>,
    isProductDropdownExpanded: Boolean,
    totalPrice: BigDecimal,
    taxPercent: Int,
    onProductSearchQueryChange: (String) -> Unit,
    onSelectProduct: (Item) -> Unit,
    onUpdateQuantity: (Int, String) -> Unit,
    onUpdatePrice: (Int, String) -> Unit,
    onUpdateMpn: (Int, String) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onNewProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = UiConstants.TonalElevationLow
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(UiConstants.TableRowHorizontalPadding)) {
            // Table Header
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = UiConstants.SurfaceVariantAlpha), shape = MaterialTheme.shapes.small)
                    .padding(UiConstants.TableRowHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(Res.string.tbl_number),
                    modifier = Modifier.width(UiConstants.InvoiceNumberColumnWidth),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    stringResource(Res.string.tbl_mpn),
                    modifier = Modifier.width(120.dp),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    stringResource(Res.string.tbl_product_name),
                    modifier = Modifier.weight(UiConstants.WeightNameColumnLarge),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    stringResource(Res.string.tbl_qty),
                    modifier = Modifier.weight(UiConstants.WeightQtyColumn),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.End
                )
                Text(
                    stringResource(Res.string.tbl_unit),
                    modifier = Modifier.width(UiConstants.PriceColumnWidth),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    stringResource(Res.string.tbl_price),
                    modifier = Modifier.weight(UiConstants.WeightPriceColumn),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.End
                )
                Text(
                    stringResource(Res.string.tbl_total),
                    modifier = Modifier.weight(UiConstants.WeightTotalColumn),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(UiConstants.ActionIconButtonWidth))
            }

            // Items List or Empty State
            Box(modifier = Modifier.weight(UiConstants.WeightDefault).fillMaxWidth()) {
                if (items.isEmpty()) {
                    EmptyStateView(
                        icon = Res.drawable.shopping_cart,
                        title = stringResource(Res.string.invoice_empty_title),
                        description = stringResource(Res.string.invoice_empty_desc)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = UiConstants.TableRowHorizontalPadding, vertical = UiConstants.InvoiceItemRowVerticalPadding),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    modifier = Modifier.width(UiConstants.InvoiceNumberColumnWidth),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                Box(modifier = Modifier.width(120.dp), contentAlignment = Alignment.CenterStart) {
                                    OutlinedTextField(
                                        value = item.mpnSnapshot ?: "",
                                        onValueChange = { onUpdateMpn(item.id, it) },
                                        modifier = Modifier.width(120.dp),
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.small,
                                        textStyle = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = item.productNameSnapshot,
                                    modifier = Modifier.weight(UiConstants.WeightNameColumnLarge),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Box(modifier = Modifier.weight(UiConstants.WeightQtyColumn), contentAlignment = Alignment.CenterEnd) {
                                    OutlinedTextField(
                                        value = item.quantity.toPlainString(),
                                        onValueChange = { onUpdateQuantity(item.id, it) },
                                        modifier = Modifier.width(UiConstants.UnitColumnWidth),
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.small
                                    )
                                }
                                Text(
                                    text = item.unitOfMeasureSnapshot,
                                    modifier = Modifier.width(UiConstants.PriceColumnWidth),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                Box(modifier = Modifier.weight(UiConstants.WeightPriceColumn), contentAlignment = Alignment.CenterEnd) {
                                    OutlinedTextField(
                                        value = item.unitPrice.toPlainString(),
                                        onValueChange = { onUpdatePrice(item.id, it) },
                                        modifier = Modifier.width(UiConstants.ActionColumnWidthSmall),
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.small
                                    )
                                }
                                Text(
                                    text = "${item.totalPrice} ₽",
                                    modifier = Modifier.weight(UiConstants.WeightTotalColumn),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.End
                                )
                                ActionIconButton(
                                    icon = Res.drawable.delete,
                                    onClick = { onRemoveItem(item.id) },
                                    modifier = Modifier.width(UiConstants.ActionIconButtonWidth)
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = UiConstants.SurfaceVariantAlpha), thickness = UiConstants.DividerThicknessThin)
                        }
                    }
                }
            }

            if (itemsError != null) {
                Spacer(modifier = Modifier.height(UiConstants.SpacingTiny))
                Text(
                    text = itemsError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = UiConstants.SpacingSmall)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = UiConstants.SpacingSmall),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            // Product Search & Add New Product Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(UiConstants.SpacingMedium),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(UiConstants.WeightDefault)) {
                    OutlinedTextField(
                        value = productSearchQuery,
                        onValueChange = onProductSearchQueryChange,
                        label = { Text(stringResource(Res.string.search_product_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Res.drawable.search.toImage() },
                        trailingIcon = {
                            if (productSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { onProductSearchQueryChange("") }) {
                                    Res.drawable.close.toImage()
                                }
                            }
                        },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    if (isProductDropdownExpanded && searchResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(UiConstants.SpacingTiny))
                        Surface(
                            modifier = Modifier.fillMaxWidth().heightIn(max = UiConstants.DropdownMaxHeight),
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = UiConstants.TonalElevationMedium,
                            shadowElevation = UiConstants.ShadowElevationDefault,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxWidth().padding(UiConstants.SpacingTiny)) {
                                items(searchResults, key = { product -> product.id }) { product ->
                                    val prefix = if (!product.mpn.isNullOrBlank()) "[${product.mpn}] " else ""
                                    DropdownMenuItem(
                                        text = { Text("$prefix${product.productName} — ${product.defaultPrice} ₽ (${product.unitOfMeasure})") },
                                        onClick = { onSelectProduct(product) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Total stats at the left of "new product" button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ИТОГО (в т.ч. НДС $taxPercent%):", style = MaterialTheme.typography.labelMedium)
                    Text("$totalPrice ₽", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }

                OutlinedButton(
                    onClick = onNewProductClick,
                    modifier = Modifier.height(UiConstants.ButtonHeightLarge)
                ) {
                    Text(stringResource(Res.string.btn_new_product))
                }
            }
        }
    }
}
