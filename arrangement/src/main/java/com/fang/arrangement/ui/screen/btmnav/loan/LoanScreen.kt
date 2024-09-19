package com.fang.arrangement.ui.screen.btmnav.loan

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.foundation.orDash
import com.fang.arrangement.ui.shared.component.ArrangementList
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.DropdownSelector
import com.fang.arrangement.ui.shared.component.Field
import com.fang.arrangement.ui.shared.component.FieldLabelText
import com.fang.arrangement.ui.shared.component.dialog.DialogShared
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.ErrorDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.fieldrow.AddableRow
import com.fang.arrangement.ui.shared.component.fieldrow.Average2Row
import com.fang.arrangement.ui.shared.component.fieldrow.RemovableRow
import com.fang.arrangement.ui.shared.component.inputfield.NumberInputField
import com.fang.arrangement.ui.shared.component.inputfield.StringInputField
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.EmployeeState
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.arrangement.ui.shared.dsl.Remark
import com.fang.arrangement.ui.shared.dsl.alphaColor
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.takeIfNotBlank
import com.fang.cosmos.foundation.time.transformer.TimeConverter
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.screenWidthDp
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.fang.cosmos.foundation.ui.ext.textDp
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun LoanScreen(
    modifier: Modifier,
    viewModel: LoanViewModel = koinViewModel(),
) {
    Column(modifier = modifier) {
        ArrangementList(
            modifier = Modifier.weight(1f, false),
            items = viewModel.bundle.stateValue().loans,
            key = { it.id },
            contentType = { it },
            onSelect = viewModel::onUpdate,
            onAdd = viewModel::onInsert,
        ) { item ->
            val isClear = item.isClear
            Row {
                val suffix = EmployeeState(item.employee)
                HighlightText(
                    text = "${item.employee?.name ?: item.employeeId.toString()} $suffix",
                    modifier = Modifier.weight(1f),
                    isAlpha = isClear,
                )
                val clearMillis = item.records.maxOfOrNull { it.millis }
                if (item.isClear && clearMillis != null) {
                    ContentText(
                        text = "還清日：${TimeConverter.format(clearMillis)}",
                        modifier = Modifier.weight(1f),
                        isAlpha = isClear,
                    )
                } else {
                    HighlightText(
                        text = "尚欠：${NumberFormat(item.remain)}",
                        modifier = Modifier.weight(1f),
                        isAlpha = isClear,
                    )
                }
            }
            Row {
                ContentText(
                    text = "借日：${TimeConverter.format(item.millis)}",
                    modifier = Modifier.weight(1f),
                    isAlpha = isClear,
                )
                ContentText(
                    text = "金額：${NumberFormat(item.loan)}",
                    modifier = Modifier.weight(1f),
                    isAlpha = isClear,
                )
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
                                    text = "還日：${TimeConverter.format(record.millis)}",
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
                                    listOf("**", remark).forEachIndexed { i, rmk ->
                                        Text(
                                            text = rmk,
                                            style =
                                                ContentText.style.color(
                                                    if (isClear) {
                                                        alphaColor(color = ContentText.color)
                                                    } else {
                                                        ContentText.color.copy(alpha = 0.72f)
                                                    },
                                                ).copy(
                                                    fontSize = 13.2.sp,
                                                    lineHeight = 13.2.sp,
                                                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                                                ),
                                        )
                                        if (i != 1) {
                                            HorizontalSpacer(1.2f)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 備註
            item.remark.takeIfNotBlank?.let {
                HorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                )
                Row {
                    ContentText(
                        text = "**",
                        isAlpha = isClear,
                    )
                    HorizontalSpacer(1.2f)
                    ContentText(
                        text = it,
                        modifier = Modifier.weight(1f),
                        isAlpha = isClear,
                    )
                }
            }
        }
    }
    LoanEditDialog(
        employees = viewModel.bundle.stateValue().employees,
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
        val selectableEmployees =
            employees.filter {
                it.id == current?.employee?.id || (it.notExpire && it.notDelete)
            }
        Column {
            val expandedState =
                rememberSaveable {
                    mutableStateOf(false)
                }
            Field(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickableNoRipple {
                            expandedState.value = true
                        },
                titleText = "員工",
                text =
                    if (editBundle?.isInsert == true && edit?.employee == null) {
                        ""
                    } else {
                        "${edit?.employee?.name.orDash} ${EmployeeState(edit?.employee)}"
                    },
            ) {
                viewModel.editEmployee(null)
            }
            if (selectableEmployees.isNotEmpty()) {
                DropdownSelector(
                    items = selectableEmployees,
                    modifier =
                        Modifier.width(
                            screenWidthDp * DialogShared.EDIT_WIDTH_FRACTION - DialogShared.editHPaddingDp * 2,
                        ),
                    selected = selectableEmployees.find { it.id == edit?.employee?.id },
                    expandedState = expandedState,
                    onSelected = viewModel::editEmployee,
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${it.name} ${EmployeeState(it)}",
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.W400,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (it.id == edit?.employee?.id) {
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
                lineLimits = TextFieldLineLimits.SingleLine,
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
        // 還款紀錄 (編輯才有)
        if (editBundle?.isInsert == false) {
            Column {
                FieldLabelText(text = "還款記錄")
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
                                    lineLimits = TextFieldLineLimits.SingleLine,
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
                                        (millis !in edit?.records?.map { it.millis }.orEmpty()) &&
                                            (edit?.millis?.let { millis >= it } ?: true)
                                    },
                                    onConfirm = viewModel::editRecordMillis,
                                )
                            }
                            val remark = recordEdit.remark.takeIfNotBlank.orEmpty()
                            StringInputField(
                                modifier = Modifier.fillMaxWidth(),
                                titleText = "備註 (${remark.length}/${LoanViewModel.R_REMARK})",
                                text = remark,
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
                        ) {
                            inner()
                        }
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
                                    ContentText(text = TimeConverter.format(record.millis).orDash)
                                }
                                Row {
                                    listOfNotNull("**", record.remark).takeIf { it.size > 1 }?.forEachIndexed { i, rmk ->
                                        Text(
                                            text = rmk,
                                            style =
                                                ContentText.style.color(
                                                    ContentText.color.copy(alpha = 0.72f),
                                                ).copy(
                                                    fontSize = 13.2.sp,
                                                    lineHeight = 13.2.sp,
                                                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                                                ),
                                        )
                                        if (i != 1) {
                                            HorizontalSpacer(1.2f)
                                        }
                                    }
                                }
                            }
                        },
                    ) {
                        record.millis?.let {
                            viewModel.removeRecord(it)
                        }
                    }
                }
            }
        }
        // 備註
        val remark = edit?.remark.takeIfNotBlank.orEmpty()
        StringInputField(
            modifier = Modifier.fillMaxWidth(),
            titleText = "備註 (${remark.length}/${Remark.LENGTH})",
            text = remark,
            onClear = true,
            onValueChange = viewModel::editRemark,
        )
    }
}
