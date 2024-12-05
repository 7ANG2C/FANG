package com.fang.arrangement.ui.screen.btmnav.money.fund

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.arrangement.ui.screen.btmnav.money.fund.pdf.FundPDFDialog
import com.fang.arrangement.ui.screen.btmnav.money.fund.pdf.FundPDFViewModel
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.EmptyScreen
import com.fang.arrangement.ui.shared.component.Fab
import com.fang.arrangement.ui.shared.component.button.component.DeleteButton
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.ErrorDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.dialog.TwoOptionDialog
import com.fang.arrangement.ui.shared.component.fieldrow.Average2Row
import com.fang.arrangement.ui.shared.component.inputfield.NumberInputField
import com.fang.arrangement.ui.shared.component.inputfield.StringInputField
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.arrangement.ui.shared.dsl.Remark
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.isMulti
import com.fang.cosmos.foundation.time.calendar.ChineseDayOfWeek
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FundScreen(
    modifier: Modifier,
    viewModel: FundViewModel = koinViewModel(),
    pdfViewModel: FundPDFViewModel = koinViewModel(),
) = Box(modifier = Modifier.fillMaxWidth()) {
    val ymFunds = viewModel.ymFunds.stateValue()
    if (ymFunds.isEmpty()) {
        EmptyScreen(modifier = modifier)
    } else {
        var showDeleteDialog by remember {
            mutableStateOf<List<MFund>?>(null)
        }
        Column(
            modifier
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val style = MaterialTypography.titleLarge.color { primary }
                ymFunds.selectedFund?.let {
                    ArrText(text = it) { style }
                    HorizontalSpacer(2)
                    ArrText(text = "/") { style }
                    HorizontalSpacer(2)
                }
                ArrText(text = ymFunds.totalFund) { style }
                Spacer(modifier = Modifier.weight(1f))
                val ids =
                    ymFunds.flatMap { ymFund ->
                        ymFund.dayFunds.flatMap { fund ->
                            fund.funds.filter { it.selected }
                        }
                    }
                if (ids.size >= 2) {
                    DeleteButton(text = "批量刪除", color = { error }) {
                        showDeleteDialog = ids
                    }
                } else {
                    CustomIcon(
                        drawableResId = R.drawable.arr_r24_picture_as_pdf,
                        modifier = Modifier.clickableNoRipple(onClick = pdfViewModel::showRequest),
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
                                ymFund.selectedFundDisplay?.let {
                                    Box(contentAlignment = Alignment.CenterEnd) {
                                        HighlightText(text = it)
                                    }
                                    HorizontalSpacer(2)
                                    HighlightText(text = "/")
                                    HorizontalSpacer(2)
                                }
                                HighlightText(text = ymFund.totalFundDisplay)
                                Spacer(modifier = Modifier.weight(1f))
                                HorizontalSpacer(6)
                                TriCheckbox(
                                    mains = ymFund.dayFunds,
                                    items = ymFund.dayFunds.flatMap { it.funds },
                                    on = { it.selected },
                                    checkedBoxColor = HighlightText.color,
                                ) {
                                    viewModel.toggle(ymFund.year, ymFund.month)
                                }
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
                                        ArrText(
                                            text = "$pre${ymFund.month + 1}-$dayPre${item.day}",
                                        ) { style }
                                        HorizontalSpacer(1.2f)
                                        ArrText(
                                            text = "(${ChineseDayOfWeek(c.timeInMillis)})",
                                        ) { style }
                                        HorizontalSpacer(14)
                                        item.selectedFundDisplay?.let {
                                            ArrText(
                                                text = it,
                                            ) { style }
                                            HorizontalSpacer(2)
                                            ArrText(
                                                text = "/",
                                            ) { style }
                                            HorizontalSpacer(2)
                                        }
                                        ArrText(
                                            text = item.totalFundDisplay,
                                        ) { style }
                                        Spacer(modifier = Modifier.weight(1f))
                                        HorizontalSpacer(6)
                                        TriCheckbox(
                                            mains = item.funds,
                                            items = item.funds,
                                            on = { it.selected },
                                            checkedBoxColor = MaterialColor.primary,
                                        ) {
                                            viewModel.toggle(
                                                ymFund.year,
                                                ymFund.month,
                                                item.day,
                                            )
                                        }
                                    }
                                    VerticalSpacer(6)
                                    item.funds.forEach {
                                        Row(
                                            modifier =
                                                Modifier
                                                    .padding(start = 8.dp, end = 13.6.dp)
                                                    .clickableNoRipple {
                                                        viewModel.onUpdate(it)
                                                    },
                                        ) {
                                            ContentText(text = "$${NumberFormat(it.fund, 0)}")
                                            HorizontalSpacer(10)
                                            ContentText(
                                                text = it.remark.orEmpty(),
                                                modifier = Modifier.weight(1f),
                                            )
                                            HorizontalSpacer(6)
                                            Box(
                                                modifier =
                                                    Modifier.clickableNoRipple {
                                                        viewModel.toggle(it.id)
                                                    },
                                            ) {
                                                val color =
                                                    ContentText.color.copy(alpha = 0.92f)
                                                Checkbox(
                                                    checked = it.selected,
                                                    onCheckedChange = null,
                                                    modifier = Modifier.scale(0.8f),
                                                    colors =
                                                        CheckboxDefaults.colors().copy(
                                                            checkedBoxColor = color,
                                                            checkedBorderColor = color,
                                                            uncheckedBorderColor = color,
                                                        ),
                                                )
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
                item {
                    Fab(
                        modifier =
                            Modifier
                                .padding(bottom = 24.dp)
                                .alpha(0f),
                        onClick = {},
                    )
                }
            }
        }
        TwoOptionDialog(
            text =
                showDeleteDialog?.takeIf { it.isNotEmpty() }?.let {
                    "是否批量刪除（結清）所選項目？"
                },
            widthFraction = 0.8f,
            onNegative = { showDeleteDialog = null },
            onPositive = {
                showDeleteDialog?.map { it.id.toString() }?.let {
                    viewModel.deletes(it)
                }
                showDeleteDialog = null
            },
        )
    }
    Fab(
        modifier =
            Modifier
                .padding(20.dp)
                .align(Alignment.BottomEnd),
        onClick = viewModel::onInsert,
    )
    FundEditDialog(
        editBundle = viewModel.editBundle.stateValue(),
        viewModel = viewModel,
    )
    ErrorDialog(viewModel)
    Loading(viewModel)
    FundPDFDialog(pdfViewModel)
}

@Composable
private fun FundEditDialog(
    editBundle: FundEditBundle?,
    viewModel: FundViewModel,
) {
    val current = editBundle?.current
    val edit = editBundle?.edit
    EditDialog(
        isShow = editBundle != null,
        onDelete =
            if (current != null) {
                { viewModel.delete(current.id.toString()) }
            } else {
                null
            },
        onCancel = viewModel::clearEdit,
        onConfirm =
            if (edit?.savable == true) {
                if (editBundle.isInsert) {
                    { viewModel.insert(edit) }
                } else {
                    { viewModel.update(editBundle) }.takeIf { editBundle.anyDiff }
                }
            } else {
                null
            },
    ) {
        Average2Row(modifier = Modifier.fillMaxWidth(), first = {
            NumberInputField(
                modifier = Modifier.fillMaxWidth(),
                titleText = "金額",
                text = edit?.fund.orEmpty(),
                imeAction = ImeAction.Next,
                onClear = true,
                onValueChange = viewModel::editFund,
            )
        }) {
            DateSelector(
                modifier = Modifier.fillMaxWidth(),
                titleText = "日期",
                onClear = {
                    viewModel.editMillis(null)
                },
                original = edit?.millis,
                isSelectableMillis = { true },
                onConfirm = viewModel::editMillis,
            )
        }
        StringInputField(
            modifier = Modifier.fillMaxWidth(),
            titleText = "備註 (${edit?.remark.orEmpty().length}/${Remark.L30})",
            text = edit?.remark.orEmpty(),
            lines = 2,
            onClear = true,
            onValueChange = viewModel::editRemark,
        )
    }
}

@Composable
private fun <T, R> TriCheckbox(
    modifier: Modifier = Modifier,
    mains: List<T>,
    items: List<R>,
    on: (R) -> Boolean,
    checkedBoxColor: Color,
    onClick: Invoke,
) {
    if (mains.isMulti) {
        Box(modifier = modifier) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                TriStateCheckbox(
                    state = if (items.all { on(it) }) ToggleableState.On else ToggleableState.Off,
                    modifier = Modifier.scale(0.8f),
                    colors =
                        CheckboxDefaults.colors().copy(
                            checkedBoxColor = checkedBoxColor,
                            checkedBorderColor = checkedBoxColor,
                            uncheckedBorderColor = checkedBoxColor.copy(alpha = 0.88f),
                        ),
                    onClick = onClick,
                )
            }
        }
    }
}
