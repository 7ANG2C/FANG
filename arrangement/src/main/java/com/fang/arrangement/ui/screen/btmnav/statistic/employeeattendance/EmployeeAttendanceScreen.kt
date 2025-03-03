package com.fang.arrangement.ui.screen.btmnav.statistic.employeeattendance

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.arrangement.ui.shared.component.EmptyScreen
import com.fang.arrangement.ui.shared.component.button.component.PositiveButton
import com.fang.arrangement.ui.shared.component.chip.AttendanceChip
import com.fang.arrangement.ui.shared.component.dialog.DialogShared
import com.fang.arrangement.ui.shared.component.dialog.dialogBg
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.EmployeeTag
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.cosmos.foundation.ui.component.DialogThemedScreen
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialShape
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.dsl.screenHeightDp
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun EmployeeAttendanceScreen(
    modifier: Modifier,
    viewModel: EmployeeAttendanceViewModel = koinViewModel(),
) {
    val yearAttendances = viewModel.yearAttendances.stateValue()
    if (yearAttendances.isEmpty()) {
        EmptyScreen(modifier = modifier)
    } else {
        Box(modifier) {
            val showMonths =
                remember {
                    mutableStateOf<YearAttendance.Summary?>(null)
                }
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
            ) {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f, false),
                ) {
                    yearAttendances.forEach { yearAtt ->
                        stickyHeader {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .bg { surfaceContainerLowest }
                                        .padding(bottom = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                HighlightText(text = "${yearAtt.year}")
                                HorizontalSpacer(1)
                                HighlightText("年")
                            }
                        }
                        item {
                            ElevatedCard(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp),
                                colors =
                                    CardDefaults.elevatedCardColors().copy(
                                        containerColor = MaterialColor.surfaceContainer,
                                    ),
                            ) {
                                VerticalSpacer(6f)
                                yearAtt.summaries.forEach { summary ->
                                    Row(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    showMonths.value = summary
                                                }.padding(horizontal = 14.dp, vertical = 2.8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        AttendanceChip(
                                            attendance = summary.attendance,
                                            bgColor = { ContentText.color.copy(alpha = 0.22f) },
                                            textStyle = { ContentText.style.color(ContentText.color) },
                                            placeHolder = false,
                                        )
                                        HorizontalSpacer(10)
                                        val name =
                                            summary.employee?.name ?: summary.employeeId.toString()
                                        ContentText(text = name)
                                        HorizontalSpacer(2)
                                        EmployeeTag(employee = summary.employee)
                                    }
                                }
                                VerticalSpacer(6f)
                            }
                        }
                    }
                }
            }
            MonthlyDialog(showMonths)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MonthlyDialog(ySummary: MutableState<YearAttendance.Summary?>) {
    DialogThemedScreen(isShow = ySummary.value != null) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(DialogShared.EDIT_WIDTH_FRACTION)
                    .heightIn(min = 0.dp, max = screenHeightDp * 0.84f)
                    .dialogBg(),
        ) {
            ySummary.value?.let { summary ->
                ArrText(
                    text = summary.employee?.name ?: summary.employeeId.toString(),
                    modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp),
                ) { MaterialTypography.titleMedium.color { onSecondaryContainer } }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f, false)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    summary.months.forEach { month ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            val pre = "0".takeIf { month.month < 9 }.orEmpty()
                            ArrText(
                                text = "$pre${month.month + 1}",
                                modifier =
                                    Modifier
                                        .bg(MaterialShape.extraSmall) { primary.copy(alpha = 0.32f) }
                                        .padding(horizontal = 2.dp),
                            ) { HighlightText.style.color(onSecondaryContainer) }
                            HorizontalSpacer(4)
                            FlowRow(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.4.dp),
                            ) {
                                val fulls = month.fullDays.map { it to true }
                                val halfs = month.halfDays.map { it to false }
                                (fulls + halfs)
                                    .sortedBy { it.first }
                                    .forEachIndexed { i, pair ->
                                        val (day, isFull) = pair
                                        Row {
                                            val style =
                                                if (isFull) {
                                                    ContentText.style.color(ContentText.color)
                                                } else {
                                                    HighlightText.style.color(HighlightText.color)
                                                }
                                            if (i != 0) {
                                                HorizontalSpacer(1)
                                                ArrText(text = "·") { style }
                                                HorizontalSpacer(1)
                                            }
                                            ArrText(
                                                text = day.toString(),
                                                modifier =
                                                    if (isFull) {
                                                        Modifier
                                                    } else {
                                                        Modifier.bg(MaterialShape.extraSmall) {
                                                            HighlightText.color.copy(alpha = 0.24f)
                                                        }
                                                    },
                                            ) { style }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
            PositiveButton(
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                onClick = { ySummary.value = null },
            )
        }
    }
}
