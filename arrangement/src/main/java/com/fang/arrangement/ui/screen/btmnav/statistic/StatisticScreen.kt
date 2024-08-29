package com.fang.arrangement.ui.screen.btmnav.statistic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fang.cosmos.foundation.number.NumberFormat
import com.fang.cosmos.foundation.ui.component.spacer.VerticalSpacer
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.fang.cosmos.foundation.ui.ext.textDp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StatisticScreen(
    modifier: Modifier,
    viewModel: StatisticViewModel = koinViewModel(),
) {
    Column(modifier = modifier) {
        val byDates = viewModel.byDates.stateValue()
        val total = NumberFormat(
            number = byDates.sumOf { item -> item.items.sumOf { it.second } },
            decimalCount = if (byDates.sumOf { item -> item.items.sumOf { it.second } } % 1 == 0.0) {
                0
            } else NumberFormat.UNSPECIFIED_DECIMAL_SIZE,
            invalidText = "-"
        )
        Text(
            text = "共 $total 工",
            modifier = Modifier.padding(8.dp),
            fontSize = 20.textDp,
            fontWeight = FontWeight.W600,
        )
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 8.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = "以月分計：",
                fontSize = 16.textDp,
            )
            VerticalSpacer(spaceDp = 10)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                itemsIndexed(
                    items = byDates,
                    key = { _, item -> item.date },
                    contentType = { _, item -> item.items }
                ) { i, byDate ->
                    val monthTotal = NumberFormat(
                        number = byDate.items.sumOf { it.second },
                        decimalCount = if (byDate.items.sumOf { it.second } % 1 == 0.0) {
                            0
                        } else NumberFormat.UNSPECIFIED_DECIMAL_SIZE,
                        invalidText = "-"
                    )
                    Column {
                        Text(
                            text = "${byDate.date} (共 $monthTotal 工)",
                            fontSize = 14.textDp,
                            fontWeight = FontWeight.W600
                        )
                        FlowRow {
                            val valid = byDate.items.filter { it.second > 0.0 }
                            valid.forEachIndexed { index, data ->
                                val separator = if (index != valid.lastIndex) "、" else ""
                                val count = NumberFormat(
                                    number = data.second,
                                    decimalCount = if (data.second % 1 == 0.0) {
                                        0
                                    } else NumberFormat.UNSPECIFIED_DECIMAL_SIZE,
                                    invalidText = "-"
                                )
                                Text(
                                    text = "${data.first}($count)$separator",
                                    fontSize = 14.textDp,
                                )
                            }
                        }
                    }
                    if (i != byDates.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 8.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = "以工地計：",
                fontSize = 16.textDp,
            )
            VerticalSpacer(spaceDp = 10)
            val bySites = viewModel.bySites.stateValue()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                itemsIndexed(
                    items = bySites,
                    key = { _, item -> item.siteName },
                    contentType = { _, item -> item.items }
                ) { i, bySite ->
                    val monthTotal = NumberFormat(
                        number = bySite.items.sumOf { it.second },
                        decimalCount = if (bySite.items.sumOf { it.second } % 1 == 0.0) {
                            0
                        } else NumberFormat.UNSPECIFIED_DECIMAL_SIZE,
                        invalidText = "-"
                    )
                    Column {
                        Text(
                            text = "${bySite.siteName} (共 $monthTotal 工)",
                            fontSize = 14.textDp,
                            fontWeight = FontWeight.W600
                        )
                        FlowRow {
                            val valid = bySite.items.filter { it.second > 0.0 }
                            valid.forEachIndexed { index, data ->
                                val separator = if (index != valid.lastIndex) "、" else ""
                                val count = NumberFormat(
                                    number = data.second,
                                    decimalCount = if (data.second % 1 == 0.0) {
                                        0
                                    } else NumberFormat.UNSPECIFIED_DECIMAL_SIZE,
                                    invalidText = "-"
                                )
                                Text(
                                    text = "${data.first}($count)$separator",
                                    fontSize = 14.textDp,
                                )
                            }
                        }
                    }
                    if (i != bySites.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}