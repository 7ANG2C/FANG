package com.fang.arrangement.ui.screen.btmnav.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.fang.arrangement.foundation.orDash
import com.fang.arrangement.ui.shared.component.ArrangementList
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.FieldLabelText
import com.fang.arrangement.ui.shared.component.ToggleBox
import com.fang.arrangement.ui.shared.component.chip.ExpiredTag
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.ErrorDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.dialog.TwoOptionDialog
import com.fang.arrangement.ui.shared.component.fieldrow.AddableRow
import com.fang.arrangement.ui.shared.component.fieldrow.Average2Row
import com.fang.arrangement.ui.shared.component.fieldrow.RemovableRow
import com.fang.arrangement.ui.shared.component.inputfield.EMPTY_NUM_HOLDER
import com.fang.arrangement.ui.shared.component.inputfield.NumberInputField
import com.fang.arrangement.ui.shared.component.inputfield.StringInputField
import com.fang.arrangement.ui.shared.dsl.AlphaColor
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.arrangement.ui.shared.dsl.YMDDayOfWeek
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun EmployeeScreen(
    modifier: Modifier,
    viewModel: EmployeeViewModel = koinViewModel(),
) {
    Column(modifier) {
        ToggleBox(
            modifier = Modifier.align(Alignment.End).clickableNoRipple(onClick = viewModel::toggle).padding(top = 8.dp, end = 20.dp),
            text = "顯示離職",
            checked = viewModel.showExpire.stateValue(),
        )
        ArrangementList(
            modifier = Modifier.weight(1f, false),
            items =
                viewModel.employees
                    .stateValue()
                    .filter {
                        if (viewModel.showExpire.stateValue()) {
                            true
                        } else {
                            it.notExpire
                        }
                    },
            key = { it.id },
            contentType = { it },
            onSelect = viewModel::onUpdate,
            onAdd = viewModel::onInsert,
        ) { item ->
            val expire = item.isExpire
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    HighlightText(
                        text = item.name.orDash,
                        modifier = Modifier.weight(1f, false),
                        isAlpha = expire,
                    )
                    if (expire) {
                        HorizontalSpacer(5.2f)
                        ExpiredTag(
                            Modifier
                                .scale(0.92f)
                                .alpha(AlphaColor.DEFAULT),
                        )
                    }
                }
                item.expiredMillis?.let {
                    ContentText(
                        text = "離職：${YMDDayOfWeek(it)}",
                        modifier = Modifier.weight(1f),
                        isAlpha = expire,
                    )
                }
            }
            if (item.salaries.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    item.salaries.forEach { salary ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ContentText(
                                text = "日薪：${NumberFormat(salary.salary)}",
                                modifier = Modifier.weight(1f),
                                isAlpha = expire,
                            )
                            ContentText(
                                text = "生效：${YMDDayOfWeek(salary.millis)}",
                                modifier = Modifier.weight(1f),
                                isAlpha = expire,
                            )
                        }
                    }
                }
            }
        }
    }
    EmployeeEditDialog(
        editBundle = viewModel.editBundle.stateValue(),
        salaryEdit = viewModel.salaryEdit.stateValue(),
        viewModel = viewModel,
    )
    ErrorDialog(viewModel)
    Loading(viewModel)
}

@Composable
private fun EmployeeEditDialog(
    editBundle: EmployeeEditBundle?,
    salaryEdit: SalaryEdit,
    viewModel: EmployeeViewModel,
) {
    val current = editBundle?.current
    val edit = editBundle?.edit
    var showDeleteDialog by remember {
        mutableStateOf<SalaryEdit?>(null)
    }
    EditDialog(
        isShow = editBundle != null,
        onDelete =
            if (current != null && current.isExpire) {
                { viewModel.delete(current) }
            } else {
                null
            },
        onCancel = {
            viewModel.clearSalary()
            viewModel.clearEdit()
        },
        onConfirm =
            if (edit?.savable == true && salaryEdit.allBlank) {
                if (editBundle.isInsert) {
                    { viewModel.insert(edit) }
                } else {
                    {
                        viewModel.update(editBundle)
                    }.takeIf { editBundle.anyDiff }
                }
            } else {
                null
            },
    ) {
        // 姓名
        StringInputField(
            modifier = Modifier.fillMaxWidth(),
            titleText = "姓名",
            text = edit?.name.orEmpty(),
            lines = 1,
            onClear = true,
            onValueChange = viewModel::editName,
        )
        // 薪資記錄
        Column(modifier = Modifier.fillMaxWidth()) {
            FieldLabelText(text = "薪資記錄")
            val focusManager = LocalFocusManager.current
            val textFieldState = rememberTextFieldState(EMPTY_NUM_HOLDER)
            AddableRow(
                modifier = Modifier.fillMaxWidth(),
                onAdd =
                    if (salaryEdit.allFilled) {
                        {
                            textFieldState.clearText()
                            focusManager.clearFocus()
                            viewModel.addSalary(salaryEdit)
                        }
                    } else {
                        null
                    },
                decorationAdd = { inner ->
                    Column(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .padding(start = 4.dp),
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        inner()
                        VerticalSpacer(2.8f)
                    }
                },
                content = {
                    Average2Row(modifier = Modifier.fillMaxWidth(), first = {
                        NumberInputField(
                            modifier = Modifier.fillMaxWidth(),
                            textFieldState = textFieldState,
                            titleText = "日薪",
                            text = salaryEdit.salary.orEmpty(),
                            imeAction = ImeAction.Done,
                            onClear = true,
                            onValueChange = viewModel::editSalary,
                        )
                    }) {
                        DateSelector(
                            modifier = Modifier.fillMaxWidth(),
                            titleText = "生效日",
                            onClear = {
                                viewModel.editSalaryMillis(null)
                            },
                            original = salaryEdit.millis,
                            isSelectableMillis = { millis ->
                                millis !in edit?.salaries.orEmpty().mapNotNull { it.millis } &&
                                    if (edit?.expire != null) {
                                        millis < edit.expire
                                    } else {
                                        true
                                    }
                            },
                            onConfirm = viewModel::editSalaryMillis,
                        )
                    }
                },
            )
            if (!edit?.salaries.isNullOrEmpty()) VerticalSpacer(2)
            edit?.salaries?.takeIf { it.isNotEmpty() }?.forEach { salary ->
                RemovableRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                    content = {
                        Column(Modifier.fillMaxWidth()) {
                            Average2Row(modifier = Modifier.fillMaxWidth(), first = {
                                ContentText(text = NumberFormat(salary.salary).orDash)
                            }) {
                                ContentText(
                                    text = YMDDayOfWeek(salary.millis).orDash,
                                )
                            }
                        }
                    },
                ) {
                    showDeleteDialog = salary
                }
            }
        }
        // 離職生效日
        if (editBundle?.isInsert == false) {
            DateSelector(
                modifier = Modifier.fillMaxWidth(),
                titleText = "離職生效日",
                onClear = {
                    viewModel.editExpire(null)
                },
                original = edit?.expire,
                isSelectableMillis = { millis ->
                    edit?.salaries?.firstOrNull()?.millis?.let {
                        millis > it
                    } ?: true
                },
                onConfirm = viewModel::editExpire,
            )
        }
    }
    val salary = "日　薪：${NumberFormat(showDeleteDialog?.salary)}"
    val millis = "生效日：${YMDDayOfWeek(showDeleteDialog?.millis)}"
    TwoOptionDialog(
        text = "是否確定移除\n\n$salary\n$millis".takeIf { showDeleteDialog != null },
        onNegative = { showDeleteDialog = null },
        onPositive = {
            showDeleteDialog?.millis?.let {
                viewModel.removeSalary(it)
            }
            showDeleteDialog = null
        },
    )
}
