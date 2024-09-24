package com.fang.arrangement.ui.screen.btmnav.statistic.salary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fang.arrangement.R
import com.fang.arrangement.foundation.DASH
import com.fang.arrangement.ui.screen.btmnav.statistic.pdf.PDFDialog
import com.fang.arrangement.ui.screen.btmnav.statistic.pdf.PDFViewModel
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.arrangement.ui.shared.component.EmptyScreen
import com.fang.arrangement.ui.shared.component.chip.AttendanceChip
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.EmployeeTag
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.arrangement.ui.shared.ext.clickRipple
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.fontSize
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SalaryScreen(
    modifier: Modifier,
    viewModel: SalaryViewModel = koinViewModel(),
    pdfViewModel: PDFViewModel = koinViewModel(),
) {
    val yearSalaries = viewModel.yearSalaries.stateValue()
    if (yearSalaries.isEmpty()) {
        EmptyScreen(modifier = modifier)
    } else {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
            ) {
                // 總計
                yearSalaries.sumOf { it.attendance }.takeIf { it > 0 }?.let { attendance ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AttendanceChip(
                            attendance = attendance,
                            bgColor = { primary.copy(alpha = 0.24f) },
                            textStyle = { MaterialTypography.titleLarge.color(primary) },
                            placeHolder = false,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                            ArrText(text = "$") {
                                MaterialTypography.titleLarge.color(primary)
                            }
                            val salary =
                                yearSalaries.mapNotNull { s ->
                                    s.salary ?.let { BigDecimal.valueOf(it) }
                                }.takeIf { it.isNotEmpty() }?.sumOf { it }
                            ArrText(text = NumberFormat(salary, 0, invalidText = DASH)) {
                                MaterialTypography.titleLarge.color(primary)
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        CustomIcon(
                            drawableResId = R.drawable.arr_r24_picture_as_pdf,
                            modifier =
                                Modifier.clickableNoRipple {
                                    pdfViewModel.showRequest()
                                },
                            tint = MaterialColor.primary,
                        )
                    }
                }
                VerticalSpacer(10)
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f, false),
                ) {
                    yearSalaries.forEach { year ->
                        stickyHeader {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .bg { surfaceContainerLowest },
                            ) {
                                VerticalSpacer(2)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row {
                                        HighlightText("${year.year}")
                                        HorizontalSpacer(1)
                                        HighlightText("年")
                                    }
                                    AttendanceChip(
                                        attendance = year.attendance,
                                        bgColor = { HighlightText.color.copy(alpha = 0.28f) },
                                        textStyle = { HighlightText.style.color(HighlightText.color) },
                                        placeHolder = false,
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                        HighlightText(text = "$")
                                        HighlightText(
                                            text =
                                                NumberFormat(
                                                    number = year.salary,
                                                    decimalCount = 0,
                                                    invalidText = DASH,
                                                ),
                                        )
                                    }
                                }
                                VerticalSpacer(10)
                            }
                        }
                        items(
                            items = year.months,
                            key = { "${year.year}${it.month}" },
                            contentType = { it },
                        ) { item ->
                            Column {
                                var isExpand by rememberSaveable { mutableStateOf(false) }
                                ElevatedCard(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .clickRipple { isExpand = !isExpand },
                                    colors =
                                        CardDefaults.elevatedCardColors().copy(
                                            containerColor = MaterialColor.surfaceContainer,
                                        ),
                                ) {
                                    Column(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            val color = MaterialColor.onPrimaryContainer
                                            val style = ContentText.style.color(color)
                                            Box(contentAlignment = Alignment.CenterEnd) {
                                                Row {
                                                    ArrText(text = "0000") {
                                                        HighlightText.style.color(Color.Transparent)
                                                    }
                                                    HorizontalSpacer(1)
                                                    ArrText(text = "年") {
                                                        HighlightText.style.color(Color.Transparent)
                                                    }
                                                }
                                                Row {
                                                    val suffix = "0".takeIf { item.month < 9 }.orEmpty()
                                                    ArrText(text = "$suffix${item.month + 1}") { style }
                                                    HorizontalSpacer(1)
                                                    ArrText(text = "月") { style }
                                                }
                                            }
                                            AttendanceChip(
                                                attendance = item.attendance,
                                                bgColor = { color.copy(alpha = 0.22f) },
                                                textStyle = { style },
                                                placeHolder = false,
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                                ArrText(text = "$") { style }
                                                ArrText(
                                                    text =
                                                        NumberFormat(
                                                            number = item.salary,
                                                            decimalCount = 0,
                                                            invalidText = DASH,
                                                        ),
                                                ) { style }
                                            }
                                        }
                                        AnimatedVisibility(visible = isExpand) {
                                            Column {
                                                HorizontalDivider(
                                                    Modifier.padding(
                                                        horizontal = 14.dp,
                                                        vertical = 5.4.dp,
                                                    ),
                                                )
                                                item.employeeSalaries.forEach { eSalary ->
                                                    VerticalSpacer(2.2f)
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 18.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ) {
                                                        val color = ContentText.color
                                                        val textSize =
                                                            MaterialTypography.bodyMedium
                                                                .fontSize(15.2.sp)
                                                        val style =
                                                            textSize
                                                                .color(color.copy(alpha = 0.94f))
                                                        val name =
                                                            eSalary.employee?.name
                                                                ?: eSalary.employeeId.toString()
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            ArrText(text = name) { style }
                                                            HorizontalSpacer(2)
                                                            EmployeeTag(
                                                                employee = eSalary.employee,
                                                                modifier = Modifier.scale(0.88f),
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        AttendanceChip(
                                                            attendance = eSalary.attendance,
                                                            bgColor = { color.copy(alpha = 0.2f) },
                                                            textStyle = {
                                                                textSize.color(color)
                                                            },
                                                            placeHolder = false,
                                                        )
                                                        HorizontalSpacer(6)
                                                        Box(contentAlignment = Alignment.CenterEnd) {
                                                            Row {
                                                                ArrText(
                                                                    text = "$",
                                                                ) { style.color(Color.Transparent) }
                                                                HorizontalSpacer(1)
                                                                ArrText(
                                                                    text =
                                                                        NumberFormat(
                                                                            number = 100000,
                                                                            decimalCount = 0,
                                                                            invalidText = DASH,
                                                                        ),
                                                                ) { style.color(Color.Transparent) }
                                                            }
                                                            Row {
                                                                ArrText(
                                                                    text = "$",
                                                                ) { style }
                                                                HorizontalSpacer(1)
                                                                ArrText(
                                                                    text =
                                                                        NumberFormat(
                                                                            number = eSalary.salary,
                                                                            decimalCount = 0,
                                                                            invalidText = DASH,
                                                                        ),
                                                                ) { style }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                VerticalSpacer(8)
                            }
                        }
                    }
                }
            }
            PDFDialog(pdfViewModel)
        }
    }
}
