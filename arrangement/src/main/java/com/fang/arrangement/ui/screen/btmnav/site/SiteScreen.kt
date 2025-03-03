package com.fang.arrangement.ui.screen.btmnav.site

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.fang.arrangement.foundation.orDash
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.arrangement.ui.shared.component.ArrangementList
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.button.component.PositiveButton
import com.fang.arrangement.ui.shared.component.chip.ArchivedTag
import com.fang.arrangement.ui.shared.component.chip.AttendanceChip
import com.fang.arrangement.ui.shared.component.chip.UnarchivedTag
import com.fang.arrangement.ui.shared.component.dialog.DialogShared
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.ErrorDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.dialog.dialogBg
import com.fang.arrangement.ui.shared.component.fieldrow.Average2Row
import com.fang.arrangement.ui.shared.component.inputfield.NumberInputField
import com.fang.arrangement.ui.shared.component.inputfield.StringInputField
import com.fang.arrangement.ui.shared.dsl.AlphaColor
import com.fang.arrangement.ui.shared.dsl.AttendanceNumFormat
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.arrangement.ui.shared.dsl.YMDDayOfWeek
import com.fang.arrangement.ui.shared.dsl.alphaColor
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.takeIfNotBlank
import com.fang.cosmos.foundation.time.calendar.dayOfMonth
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.ui.component.DialogThemedScreen
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.MaterialShape
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.dsl.screenHeightDp
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SiteScreen(
    modifier: Modifier,
    viewModel: SiteViewModel = koinViewModel(),
) {
    val showMonths =
        remember {
            mutableStateOf<SiteMoney.YearSummary?>(null)
        }
    Column(modifier) {
        ArrangementList(
            modifier = Modifier.weight(1f, false),
            items = viewModel.sites.stateValue(),
            key = { it.id },
            contentType = { it },
            onSelect = viewModel::onUpdate,
            onAdd = viewModel::onInsert,
        ) { item ->
            val archive = item.isArchive
            val attendanceMap = viewModel.attendanceMap.stateValue()[item.id]
            val allAtt = attendanceMap?.att
            // 工地名
            Box {
                allAtt?.let {
                    Box(contentAlignment = Alignment.Center) {
                        AttAllChip(it, fill = true, false)
                        ArrText(
                            text =
                                item.name
                                    .firstOrNull()
                                    ?.toString()
                                    .orDash,
                        ) {
                            HighlightText.style.color(Color.Transparent)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    allAtt?.let {
                        Row(
                            modifier =
                                Modifier.clickableNoRipple {
                                    showMonths.value =
                                        SiteMoney.YearSummary(item.name, attendanceMap.years)
                                },
                        ) {
                            AttAllChip(it, fill = true, true)
                            HorizontalSpacer(6)
                        }
                    }
                    HighlightText(text = item.name, Modifier.weight(1f), isAlpha = archive)
                    if (archive) {
                        HorizontalSpacer(8)
                        Box(contentAlignment = Alignment.CenterEnd) {
                            AttAllChip(1.0, fill = false, true)
                            ArchivedTag(Modifier.alpha(AlphaColor.DEFAULT))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    attendanceMap?.salary?.let {
                        HorizontalSpacer(6)
                        Box(contentAlignment = Alignment.CenterEnd) {
                            AttAllChip(1.0, fill = false, true)
                            HighlightText(text = "$${NumberFormat(it, 0)}")
                        }
                    }
                }
            }
            // 總價
            item.income?.let {
                ContentText(
                    text = "總價：${NumberFormat(it)}",
                    isAlpha = archive,
                )
            }
            // 開工、竣工
            val start = YMDDayOfWeek(item.startMillis)
            val end = YMDDayOfWeek(item.endMillis)
            if (start != null || end != null) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    ContentText(
                        text = "開工：${start.orDash}",
                        modifier = Modifier.weight(1f),
                        isAlpha = archive,
                    )
                    ContentText(
                        text = "竣工：${end.orDash}",
                        modifier = Modifier.weight(1f),
                        isAlpha = archive,
                    )
                }
            }
            // 地址
            item.address.takeIfNotBlank?.let {
                val context = LocalContext.current
                ArrText(
                    text = it,
                    modifier =
                        Modifier
                            .clickableNoRipple {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("geo:0,0?q=${Uri.encode(it)}"),
                                    ).setPackage("com.google.android.apps.maps"),
                                )
                            },
                ) {
                    val color =
                        alphaColor(
                            color = Color(0xFF2191F3),
                            isAlpha = archive,
                        )
                    ContentText.style
                        .color(color)
                        .copy(textDecoration = TextDecoration.Underline)
                }
            }
        }
    }
    MonthlyDialog(showMonths)
    SiteEditDialog(
        editBundle = viewModel.editBundle.stateValue(),
        viewModel = viewModel,
    )
    ErrorDialog(viewModel)
    Loading(viewModel)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MonthlyDialog(ySummary: MutableState<SiteMoney.YearSummary?>) {
    val showEmployee =
        remember {
            mutableStateOf<SiteMoney.Day?>(null)
        }
    DialogThemedScreen(isShow = ySummary.value != null) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(DialogShared.EDIT_WIDTH_FRACTION)
                    .heightIn(min = 0.dp, max = screenHeightDp * 0.84f)
                    .dialogBg()
                    .animateContentSize(),
        ) {
            ySummary.value?.let { summary ->
                var showAtt by rememberSaveable { mutableStateOf(false) }
                ArrText(
                    text = summary.name,
                    modifier =
                        Modifier
                            .clickableNoRipple {
                                showAtt = !showAtt
                            }.align(Alignment.CenterHorizontally)
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
                    summary.years.forEach { year ->
                        Column {
                            HighlightText(text = year.year.toString())
                            year.months.forEach { month ->
                                VerticalSpacer(2)
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
                                        month.days
                                            .sortedBy { it.dateMillis }
                                            .forEachIndexed { i, day ->
                                                Row(
                                                    modifier =
                                                        Modifier.clickableNoRipple {
                                                            showEmployee.value = day
                                                        },
                                                ) {
                                                    val style =
                                                        ContentText.style.color(ContentText.color)
                                                    if (i != 0) {
                                                        HorizontalSpacer(1.2f)
                                                        ArrText(text = "·") { style }
                                                        HorizontalSpacer(1.2f)
                                                    }
                                                    ArrText(
                                                        text = today(day.dateMillis).dayOfMonth.toString(),
                                                    ) { style }
                                                    if (showAtt) {
                                                        ArrText(
                                                            text = "(${AttendanceNumFormat(day.att)})",
                                                        ) { style.color(ContentText.color.copy(alpha = 0.6f)) }
                                                    }
                                                }
                                            }
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
    MonthlyEmployeeDialog(showEmployee)
}

@Composable
private fun MonthlyEmployeeDialog(showEmployee: MutableState<SiteMoney.Day?>) {
    DialogThemedScreen(isShow = showEmployee.value != null) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(DialogShared.EDIT_WIDTH_FRACTION)
                    .heightIn(min = 0.dp, max = screenHeightDp * 0.84f)
                    .dialogBg()
                    .animateContentSize(),
        ) {
            showEmployee.value?.let { summary ->
                ArrText(
                    text = YMDDayOfWeek(summary.dateMillis).orDash,
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
                    summary.fulls?.joinToString("、") { it.name }?.let {
                        ContentText(text = it)
                    }
                    summary.halfs?.joinToString("、") { it.name }?.let {
                        VerticalSpacer(6)
                        ArrText(
                            text = it,
                        ) { ContentText.style.color(HighlightText.color) }
                    }
                }
            }
            PositiveButton(
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                onClick = { showEmployee.value = null },
            )
        }
    }
}

@Composable
private fun SiteEditDialog(
    editBundle: SiteEditBundle?,
    viewModel: SiteViewModel,
) {
    val edit = editBundle?.edit
    val current = editBundle?.current
    EditDialog(
        isShow = edit != null,
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
        onDelete =
            if (current != null) {
                { viewModel.delete(current) }
            } else {
                null
            },
        onCancel = viewModel::clearEdit,
    ) {
        // 封存
        if (editBundle?.isInsert == false) {
            Archive(
                modifier = Modifier.fillMaxWidth(),
                archive = edit?.archive == true,
                click = viewModel::toggleArchive,
            )
        }
        // 工地
        StringInputField(
            modifier = Modifier.fillMaxWidth(),
            titleText = "工地",
            text = edit?.name.takeIfNotBlank.orEmpty(),
            imeAction = ImeAction.Next,
            lines = 2,
            onClear = true,
            onValueChange = viewModel::editName,
        )
        // 地址
        StringInputField(
            modifier = Modifier.fillMaxWidth(),
            titleText = "地址",
            text = edit?.address.takeIfNotBlank.orEmpty(),
            lines = 3,
            onClear = true,
            onValueChange = viewModel::editAddress,
        )
        // 開工、竣工
        Average2Row(
            modifier = Modifier.fillMaxWidth(),
            first = {
                DateSelector(
                    modifier = Modifier.fillMaxWidth(),
                    titleText = "開工",
                    onClear = {
                        viewModel.editStartMillis(null)
                    },
                    original = edit?.startMillis,
                    isSelectableMillis = { millis ->
                        edit?.endMillis?.let { millis <= it } ?: true
                    },
                    onConfirm = viewModel::editStartMillis,
                )
            },
        ) {
            DateSelector(
                modifier = Modifier.fillMaxWidth(),
                titleText = "竣工",
                onClear = {
                    viewModel.editEndMillis(null)
                },
                original = edit?.endMillis,
                isSelectableMillis = { millis ->
                    edit?.startMillis?.let { millis >= it } ?: true
                },
                onConfirm = viewModel::editEndMillis,
            )
        }

        // 總價
        NumberInputField(
            modifier = Modifier.fillMaxWidth(),
            titleText = "總價",
            text = edit?.income.takeIfNotBlank.orEmpty(),
            imeAction = ImeAction.Done,
            onClear = true,
            onValueChange = viewModel::editIncome,
        )
    }
}

@Composable
private fun AttAllChip(
    attAll: Double?,
    fill: Boolean = true,
    placeHolder: Boolean,
) = AttendanceChip(
    attendance = attAll,
    fill = fill,
    bgColor = {
        HighlightText.color.copy(
            alpha = if (placeHolder) 0f else 0.2f,
        )
    },
    textStyle = {
        HighlightText.style.color(
            if (placeHolder) Color.Transparent else HighlightText.color,
        )
    },
    placeHolder = placeHolder,
)

@Composable
private fun Archive(
    modifier: Modifier,
    archive: Boolean,
    click: Invoke,
) {
    Row(modifier) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier =
                Modifier
                    .clickableNoRipple(onClick = click),
        ) {
            if (archive) {
                ArchivedTag(Modifier)
            } else {
                UnarchivedTag(
                    Modifier,
                )
            }
        }
    }
}
