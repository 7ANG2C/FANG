package com.fang.arrangement.ui.screen.btmnav.statistic.employeeattendance

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.shared.component.EmptyScreen
import com.fang.arrangement.ui.shared.component.chip.AttendanceChip
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.EmployeeTag
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
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
        Column(
            modifier
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
                                            .padding(horizontal = 14.dp, vertical = 2.8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AttendanceChip(
                                        attendance = summary.attendance,
                                        bgColor = { ContentText.color.copy(alpha = 0.22f) },
                                        textStyle = { ContentText.style.color(ContentText.color) },
                                        placeHolder = false,
                                    )
                                    HorizontalSpacer(10)
                                    val name = summary.employee?.name ?: summary.employeeId.toString()
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
    }
}
