package com.fang.arrangement.ui.screen.btmnav.statistic.sitefind

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.arrangement.ui.screen.btmnav.statistic.sitefind.pdf.SiteFundPDFDialog
import com.fang.arrangement.ui.screen.btmnav.statistic.sitefind.pdf.SiteFundPDFViewModel
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.arrangement.ui.shared.component.DropdownSelector
import com.fang.arrangement.ui.shared.component.SelectedTag
import com.fang.arrangement.ui.shared.component.dialog.DialogShared
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.takeIfNotBlank
import com.fang.cosmos.foundation.time.calendar.ChineseDayOfWeek
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialShape
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.dsl.screenWidthDp
import com.fang.cosmos.foundation.ui.dsl.textAlignEnd
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
internal fun SiteFundScreen(
    modifier: Modifier,
    viewModel: SiteFundViewModel = koinViewModel(),
    pdfViewModel: SiteFundPDFViewModel = koinViewModel(),
) {
    val ymFunds = viewModel.ymFunds.stateValue()
    Box(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val expandedFilter = rememberSaveable { mutableStateOf(false) }
                val v = viewModel.site.stateValue() ?: viewModel.sites.stateValue().firstOrNull()
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .border(1.dp, ContentText.color, MaterialShape.small)
                            .clickableNoRipple {
                                expandedFilter.value = true
                            },
                ) {
                    ContentText(
                        text = v?.name.orEmpty(),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                    DropdownSelector(
                        items = viewModel.sites.stateValue(),
                        modifier =
                            Modifier.width(
                                (screenWidthDp * DialogShared.EDIT_WIDTH_FRACTION - DialogShared.editHPaddingDp * 1.5f),
                            ),
                        selected = viewModel.site.stateValue(),
                        expandedState = expandedFilter,
                        onSelected = viewModel::selectSite,
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ContentText(text = it.name)
                            Spacer(modifier = Modifier.weight(1f))
                            if (it.id == viewModel.site.stateValue()?.id) {
                                SelectedTag()
                            }
                        }
                    }
                }
                HorizontalSpacer(16)
                CustomIcon(
                    drawableResId = R.drawable.arr_r24_picture_as_pdf,
                    modifier =
                        Modifier.clickableNoRipple {
                            v?.let {
                                pdfViewModel.showRequest(it, ymFunds)
                            }
                        },
                    tint = MaterialColor.primary,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val style = MaterialTypography.titleLarge.color { primary }
                val totalFund = ymFunds.sumOf { it.totalFund }
                ArrText(text = "$${NumberFormat(number = totalFund, decimal = 0)}") { style }
                ymFunds.sumOf { it.selectedFund ?: 0 }.takeIf { it > 0 }?.let { selected ->
                    ArrText(text = " - ") { style }
                    ArrText(text = "$${NumberFormat(number = selected, decimal = 0)}") { style }
                    ArrText(text = " = ") { style }
                    ArrText(
                        text = "$${
                            NumberFormat(
                                number = (totalFund - selected),
                                decimal = 0
                            )
                        }"
                    ) { style }
                    Spacer(Modifier.weight(1f))
                    CustomIcon(
                        drawableResId = R.drawable.arr_r24_cancel,
                        modifier = Modifier.clickableNoRipple (onClick = viewModel::clearToggle),
                        tint = MaterialColor.primary
                    )
                }
            }
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, false),
            ) {
                ymFunds.forEach { ymFund ->
                    stickyHeader {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .bg { surfaceContainerLowest },
                        ) {
                            VerticalSpacer(2)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val pre = "0".takeIf { ymFund.month < 9 }.orEmpty()
                                HighlightText("${ymFund.year}-$pre${ymFund.month + 1}")
                                HorizontalSpacer(14)
                                HighlightText(text = ymFund.totalFundDisplay)
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            VerticalSpacer(10)
                        }
                    }
                    items(
                        items = ymFund.dayFunds,
                        key = { "${ymFund.year}${ymFund.month}${it.day}" },
                        contentType = { it },
                    ) { item ->
                        Column {
                            ElevatedCard(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                colors =
                                    CardDefaults.elevatedCardColors().copy(
                                        containerColor = MaterialColor.surfaceContainer,
                                    ),
                            ) {
                                Column(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .bg { primary.copy(alpha = 0.14f) }
                                                .padding(vertical = 5.2.dp, horizontal = 8.dp),
                                    ) {
                                        val pre = "0".takeIf { ymFund.month < 9 }.orEmpty()
                                        val dayPre = "0".takeIf { item.day < 10 }.orEmpty()
                                        val c =
                                            today().apply {
                                                set(Calendar.YEAR, ymFund.year)
                                                set(Calendar.MONTH, ymFund.month)
                                                set(Calendar.DAY_OF_MONTH, item.day)
                                            }
                                        val style =
                                            ContentText.style.color { onSecondaryContainer }
                                        ArrText("$pre${ymFund.month + 1}-$dayPre${item.day}") { style }
                                        HorizontalSpacer(1.2f)
                                        ArrText("(${ChineseDayOfWeek(c.timeInMillis)})") { style }
                                        HorizontalSpacer(14)
                                        ArrText(item.totalFundDisplay) { style }
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                    VerticalSpacer(6)
                                    item.funds.forEach {
                                        Row(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .clickableNoRipple {
                                                        viewModel.toggle(it.id)
                                                    }
                                                    .padding(start = 8.dp, end = 13.6.dp),
                                        ) {
                                            val style =
                                                ContentText.style.color { onSecondaryContainer }
                                            ArrText("$${NumberFormat(it.fund, 0)}") {
                                                style
                                            }
                                            it.remark.takeIfNotBlank?.let { text ->
                                                HorizontalSpacer(10)
                                                ArrText("$text ") { style }
                                            }
                                            if (it.selected) {
                                                HorizontalSpacer(6)
                                                ArrText("X", Modifier.weight(1f)) {
                                                    style.color(
                                                        Color.Red
                                                    ).textAlignEnd()
                                                }
                                            }
                                        }
                                        VerticalSpacer(6)
                                    }
                                }
                            }
                            VerticalSpacer(10)
                        }
                    }
                }
            }
        }
        SiteFundPDFDialog(pdfViewModel, viewModel::clearToggle)
    }
}