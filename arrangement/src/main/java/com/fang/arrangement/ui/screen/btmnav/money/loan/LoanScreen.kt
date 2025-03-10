package com.fang.arrangement.ui.screen.btmnav.money.loan

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.foundation.orDash
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.arrangement.ui.shared.component.ArrangementList
import com.fang.arrangement.ui.shared.component.BaseField
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.DropdownSelector
import com.fang.arrangement.ui.shared.component.chip.RemarkTag
import com.fang.arrangement.ui.shared.component.chip.TextChip
import com.fang.arrangement.ui.shared.component.dialog.DialogShared
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.ErrorDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.dialog.TwoOptionDialog
import com.fang.arrangement.ui.shared.component.fieldrow.AddableRow
import com.fang.arrangement.ui.shared.component.fieldrow.Average2Row
import com.fang.arrangement.ui.shared.component.fieldrow.RemovableRow
import com.fang.arrangement.ui.shared.component.inputfield.NumberInputField
import com.fang.arrangement.ui.shared.component.inputfield.StringInputField
import com.fang.arrangement.ui.shared.dsl.AlphaColor
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.EmployeeTag
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.arrangement.ui.shared.dsl.Remark
import com.fang.arrangement.ui.shared.dsl.YMDDayOfWeek
import com.fang.arrangement.ui.shared.dsl.alphaColor
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.takeIfNotBlank
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialShape
import com.fang.cosmos.foundation.ui.dsl.screenWidthDp
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.fontSize
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.fang.cosmos.foundation.ui.ext.textDp
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun LoanScreen(
    modifier: Modifier,
    viewModel: LoanViewModel = koinViewModel(),
) = Box(modifier = Modifier.fillMaxSize()) {
    val employees = viewModel.bundle.stateValue().employees
    val loans = viewModel.bundle.stateValue().loans
    Column(modifier = modifier) {
        val selectableFilters =
            employees.filter { e ->
                e.notExpire &&
                    e.notDelete &&
                    e.id in
                    loans.mapNotNull {
                        if (it.isClear) null else it.employee.id
                    }
            }
        var isSimpleMode by rememberSaveable { mutableStateOf(false) }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 10.dp)
                    .padding(top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val filter = viewModel.filter.stateValue()
            if (selectableFilters.isNotEmpty()) {
                val expandedFilter = rememberSaveable { mutableStateOf(false) }
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .border(1.dp, ContentText.color, MaterialShape.small)
                            .clickableNoRipple { expandedFilter.value = true },
                ) {
                    ContentText(
                        text = filter?.name ?: "全部",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                    DropdownSelector(
                        items = selectableFilters,
                        modifier =
                            Modifier.width(
                                (screenWidthDp * DialogShared.EDIT_WIDTH_FRACTION - DialogShared.editHPaddingDp * 1.5f),
                            ),
                        selected = filter,
                        expandedState = expandedFilter,
                        onSelected = viewModel::filterEmployee,
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
                            if (it.id == filter?.id) {
                                SelectedTag()
                            }
                        }
                    }
                }
                CustomIcon(
                    drawableResId = R.drawable.arr_r24_cancel,
                    tint = ContentText.color,
                    modifier =
                        Modifier
                            .clickableNoRipple {
                                isSimpleMode = false
                                viewModel.filterEmployee(null)
                            }.padding(vertical = 4.dp)
                            .padding(start = 8.dp),
                )
            }
            if (filter != null) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    HorizontalSpacer(4)
                    Switch(
                        checked = isSimpleMode,
                        onCheckedChange = null,
                        modifier =
                            Modifier
                                .scale(0.82f)
                                .clickableNoRipple {
                                    isSimpleMode = !isSimpleMode
                                },
                    )
                }
            }
        }
        ArrangementList(
            modifier = Modifier.weight(1f, false),
            items = loans,
            key = { it.id },
            contentType = { it },
            onSelect = viewModel::onUpdate,
            onAdd = viewModel::onInsert,
        ) { item ->
            val isClear = item.isClear
            if (isSimpleMode) {
                Row {
                    HighlightText(
                        text = YMDDayOfWeek(item.millis).orDash,
                        modifier = Modifier.weight(1f),
                        isAlpha = isClear,
                    )
                    HighlightText(
                        text = "$${NumberFormat(item.loan)}",
                        modifier = Modifier.weight(1f),
                        isAlpha = isClear,
                    )
                }
                // 備註
                item.remark.takeIfNotBlank?.let {
                    Row {
                        val alpha = if (isClear) AlphaColor.DEFAULT else 0.72f
                        val style =
                            HighlightText.style
                                .color(
                                    HighlightText.color.copy(alpha = alpha),
                                ).fontSize(12.8.textDp)
                                .copy(lineHeight = 13.2.textDp)
                        Box(contentAlignment = Alignment.CenterStart) {
                            ArrText(
                                text = "註",
                            ) { style.color(Color.Transparent) }
                            RemarkTag(
                                modifier = Modifier.scale(0.84f),
                                tint = HighlightText.color.copy(alpha = alpha),
                            )
                        }
                        HorizontalSpacer(2.8f)
                        ArrText(
                            text = it,
                            modifier = Modifier.weight(1f),
                        ) { style }
                    }
                }
            } else {
                Row {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HighlightText(
                            text = item.employee.name.takeIfNotBlank ?: item.employee.id.toString(),
                            modifier = Modifier.weight(1f, false),
                            isAlpha = isClear,
                        )
                        HorizontalSpacer(2)
                        EmployeeTag(
                            employee = item.employee,
                            modifier =
                                Modifier
                                    .scale(0.88f)
                                    .alpha(if (isClear) AlphaColor.DEFAULT else 1f),
                        )
                    }
                    if (!isClear) {
                        HighlightText(
                            text = "尚欠：${NumberFormat(item.remain)}",
                            modifier = Modifier.weight(1f),
                            isAlpha = false,
                        )
                    }
                }
                Row {
                    HighlightText(
                        text = "借日：${YMDDayOfWeek(item.millis)}",
                        modifier = Modifier.weight(1f),
                        isAlpha = isClear,
                    )
                    HighlightText(
                        text = "金額：${NumberFormat(item.loan)}",
                        modifier = Modifier.weight(1f),
                        isAlpha = isClear,
                    )
                }

                // 備註
                item.remark.takeIfNotBlank?.let {
                    Row {
                        val alpha = if (isClear) AlphaColor.DEFAULT else 0.72f
                        val style =
                            HighlightText.style
                                .color(
                                    HighlightText.color.copy(alpha = alpha),
                                ).fontSize(12.8.textDp)
                                .copy(lineHeight = 13.2.textDp)
                        Box(contentAlignment = Alignment.CenterStart) {
                            ArrText(
                                text = "註",
                            ) { style.color(Color.Transparent) }
                            RemarkTag(
                                modifier = Modifier.scale(0.84f),
                                tint = HighlightText.color.copy(alpha = alpha),
                            )
                        }
                        HorizontalSpacer(2.8f)
                        ArrText(
                            text = it,
                            modifier = Modifier.weight(1f),
                        ) { style }
                    }
                }
                // 還款紀錄
                if (item.records.isNotEmpty()) {
                    HorizontalDivider(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        item.records.forEach { record ->
                            Column {
                                Row {
                                    ContentText(
                                        text = "還日：${YMDDayOfWeek(record.millis)}",
                                        modifier = Modifier.weight(1f),
                                        isAlpha = isClear,
                                    )
                                    ContentText(
                                        text = "金額：${NumberFormat(record.loan)}",
                                        modifier = Modifier.weight(1f),
                                        isAlpha = isClear,
                                    )
                                }
                                record.remark?.let { remark ->
                                    Row {
                                        val color =
                                            if (isClear) {
                                                alphaColor(color = ContentText.color)
                                            } else {
                                                ContentText.color.copy(alpha = 0.72f)
                                            }
                                        val style =
                                            ContentText.style.color(color).copy(
                                                fontSize = 13.2.textDp,
                                                lineHeight = 13.2.textDp,
                                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                                            )
                                        Box(contentAlignment = Alignment.CenterStart) {
                                            ArrText(
                                                text = "註",
                                            ) { style.color(Color.Transparent) }
                                            RemarkTag(
                                                modifier = Modifier.scale(0.84f),
                                                tint = color,
                                            )
                                        }
                                        HorizontalSpacer(2.4f)
                                        ArrText(
                                            text = remark,
                                            modifier = Modifier.weight(1f),
                                        ) { style }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    LoanEditDialog(
        employees = employees,
        editBundle = viewModel.editBundle.stateValue(),
        recordEdit = viewModel.recordEdit.stateValue(),
        viewModel = viewModel,
    )
    ErrorDialog(viewModel)
    Loading(viewModel)
}

@Composable
private fun LoanEditDialog(
    employees: List<Employee>,
    editBundle: LoanEditBundle?,
    recordEdit: RecordEdit,
    viewModel: LoanViewModel,
) {
    val current = editBundle?.current
    val edit = editBundle?.edit
    var showDeleteDialog by remember {
        mutableStateOf<RecordEdit?>(null)
    }
    EditDialog(
        isShow = editBundle != null,
        onDelete =
            if (current != null) {
                { viewModel.delete(current.id.toString()) }
            } else {
                null
            },
        onCancel = {
            viewModel.clearRecord()
            viewModel.clearEdit()
        },
        onConfirm =
            if (edit?.savable == true && recordEdit.allBlank) {
                if (editBundle.isInsert) {
                    { viewModel.insert(edit) }
                } else {
                    { viewModel.update(editBundle) }.takeIf { editBundle.anyDiff }
                }
            } else {
                null
            },
    ) {
        // 選擇員工
        val currentEmployee = current?.employee
        val selectableEmployees =
            employees.filter {
                it.id == currentEmployee?.id || (it.notExpire && it.notDelete)
            }
        val allEmployees =
            selectableEmployees +
                listOfNotNull(
                    currentEmployee?.takeIf {
                        currentEmployee.id !in selectableEmployees.map { it.id }
                    },
                )

        Column {
            if (editBundle?.isInsert == false) {
                TextChip(
                    text = "借款",
                    bgColor = { primary },
                    textStyle = {
                        TextStyle(fontSize = 16.8.textDp, fontWeight = FontWeight.W600)
                            .color(surface)
                    },
                    placeHolder = false,
                )
                VerticalSpacer(4)
            }
            val expandedState =
                rememberSaveable {
                    mutableStateOf(false)
                }
            BaseField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickableNoRipple {
                            expandedState.value = true
                        },
                title = "員工",
                onClear = { viewModel.editEmployee(null) },
            ) {
                val employee = edit?.employee
                if (editBundle?.isInsert == false || employee != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ContentText(
                            employee?.name.takeIfNotBlank ?: employee?.id?.toString().orDash,
                        )
                        HorizontalSpacer(4)
                        EmployeeTag(employee = employee, Modifier.scale(0.92f))
                    }
                }
            }
            if (allEmployees.isNotEmpty()) {
                DropdownSelector(
                    items = allEmployees,
                    modifier =
                        Modifier.width(
                            screenWidthDp * DialogShared.EDIT_WIDTH_FRACTION - DialogShared.editHPaddingDp * 2,
                        ),
                    selected = allEmployees.find { it.id == edit?.employee?.id },
                    expandedState = expandedState,
                    onSelected = viewModel::editEmployee,
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ContentText(text = it.name.takeIfNotBlank ?: it.id.toString())
                        HorizontalSpacer(4)
                        EmployeeTag(employee = it, Modifier.scale(0.92f))
                        Spacer(modifier = Modifier.weight(1f))
                        if (it.id == edit?.employee?.id) {
                            SelectedTag()
                        }
                    }
                }
            }
        }
        // 輸入金額與日期
        Average2Row(modifier = Modifier.fillMaxWidth(), first = {
            NumberInputField(
                modifier = Modifier.fillMaxWidth(),
                titleText = "借款金額",
                text = edit?.loan.orEmpty(),
                imeAction = ImeAction.Next,
                onClear = true,
                onValueChange = viewModel::editLoan,
            )
        }) {
            DateSelector(
                modifier = Modifier.fillMaxWidth(),
                titleText = "借款日",
                onClear = {
                    viewModel.editMillis(null)
                },
                original = edit?.millis,
                isSelectableMillis = { millis ->
                    editBundle?.edit?.records?.mapNotNull { it.millis }?.minOrNull()?.let {
                        millis <= it
                    } ?: true
                },
                onConfirm = viewModel::editMillis,
            )
        }
        // 備註
        val remark = edit?.remark.takeIfNotBlank.orEmpty()
        StringInputField(
            modifier = Modifier.fillMaxWidth(),
            titleText = "備註 (${remark.length}/${Remark.L50})",
            text = remark,
            lines = 3,
            onClear = true,
            onValueChange = viewModel::editRemark,
        )
        // 還款紀錄 (編輯才有)
        if (editBundle?.isInsert == false) {
            Column {
                VerticalSpacer(4)
                TextChip(
                    text = "還款",
                    bgColor = { primary },
                    textStyle = {
                        TextStyle(fontSize = 16.8.textDp, fontWeight = FontWeight.W600)
                            .color(surface)
                    },
                    placeHolder = false,
                )
                VerticalSpacer(4)
                val focusManager = LocalFocusManager.current
                AddableRow(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        Column {
                            Average2Row(modifier = Modifier.fillMaxWidth(), first = {
                                NumberInputField(
                                    modifier = Modifier.fillMaxWidth(),
                                    titleText = "金額",
                                    text = recordEdit.loan.orEmpty(),
                                    imeAction = ImeAction.Next,
                                    onClear = true,
                                    onValueChange = viewModel::editRecordLoan,
                                )
                            }) {
                                DateSelector(
                                    modifier = Modifier.fillMaxWidth(1f),
                                    titleText = "日期",
                                    onClear = {
                                        viewModel.editRecordMillis(null)
                                    },
                                    original = recordEdit.millis,
                                    isSelectableMillis = { millis ->
                                        edit?.millis?.let { millis >= it } ?: true
                                    },
                                    onConfirm = viewModel::editRecordMillis,
                                )
                            }
                            val rmk = recordEdit.remark.takeIfNotBlank.orEmpty()
                            StringInputField(
                                modifier = Modifier.fillMaxWidth(),
                                titleText = "備註 (${rmk.length}/${Remark.L30})",
                                text = rmk,
                                lines = 2,
                                onClear = true,
                                onValueChange = viewModel::editRecordRemark,
                            )
                        }
                    },
                    onAdd =
                        if (recordEdit.allFilled) {
                            {
                                focusManager.clearFocus()
                                viewModel.addRecord(recordEdit)
                            }
                        } else {
                            null
                        },
                    decorationAdd = { inner ->
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.Center,
                        ) { inner() }
                    },
                )
                if (!edit?.records.isNullOrEmpty()) VerticalSpacer(2)
                edit?.records?.takeIf { it.isNotEmpty() }?.forEach { record ->
                    RemovableRow(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.8.dp),
                        content = {
                            Column(Modifier.fillMaxWidth()) {
                                Average2Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    first = {
                                        ContentText(text = NumberFormat(record.loan))
                                    },
                                ) {
                                    ContentText(text = YMDDayOfWeek(record.millis).orDash)
                                }
                                record.remark?.let {
                                    Row {
                                        val color = ContentText.color.copy(alpha = 0.72f)
                                        val style =
                                            ContentText.style
                                                .copy(
                                                    fontSize = 13.2.textDp,
                                                    lineHeight = 13.2.textDp,
                                                    platformStyle =
                                                        PlatformTextStyle(includeFontPadding = false),
                                                )
                                        Box(contentAlignment = Alignment.CenterStart) {
                                            ArrText(text = "註") { style.color(Color.Transparent) }
                                            RemarkTag(
                                                modifier = Modifier.scale(0.8f),
                                                tint = color,
                                            )
                                        }
                                        HorizontalSpacer(1.8f)
                                        ArrText(text = it) { style.color(color) }
                                    }
                                }
                            }
                        },
                    ) {
                        showDeleteDialog = record
                    }
                }
            }
        }
    }
    val salary = "金　額：${NumberFormat(showDeleteDialog?.loan)}"
    val millis = "生效日：${YMDDayOfWeek(showDeleteDialog?.millis)}"
    TwoOptionDialog(
        text = "是否確定移除\n\n$salary\n$millis".takeIf { showDeleteDialog != null },
        onNegative = { showDeleteDialog = null },
        onPositive = {
            showDeleteDialog?.millis?.let {
                viewModel.removeRecord(it)
            }
            showDeleteDialog = null
        },
    )
}

@Composable
private fun SelectedTag() {
    Box(
        modifier =
            Modifier
                .size(16.dp)
                .border(1.dp, MaterialColor.primary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(8.dp)
                    .bg(CircleShape) { primary },
        )
    }
}
