package com.fang.arrangement.ui.screen.btmnav.employee

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.fang.arrangement.foundation.orDash
import com.fang.arrangement.ui.shared.component.AddableRow
import com.fang.arrangement.ui.shared.component.ArrangementList
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.FieldLabelText
import com.fang.arrangement.ui.shared.component.RemovableRow
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.ErrorDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.dialog.TwoOptionDialog
import com.fang.arrangement.ui.shared.component.inputfield.NumberInputField
import com.fang.arrangement.ui.shared.component.inputfield.StringInputField
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.time.transformer.TimeConverter
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun EmployeeScreen(
    modifier: Modifier,
    viewModel: EmployeeViewModel = koinViewModel(),
) {
    Column(modifier) {
        ArrangementList(
            modifier = Modifier.weight(1f, false),
            items = viewModel.employees.stateValue(),
            key = { it.id },
            contentType = { it },
            onSelect = viewModel::onUpdate,
            onAdd = viewModel::onInsert,
        ) { item ->
            val expire = item.isExpire
            Row(modifier = Modifier.fillMaxWidth()) {
                HighlightText(
                    text = item.name.orDash,
                    modifier = Modifier.weight(1f),
                    isAlpha = expire,
                )
                item.expiredMillis?.let {
                    ContentText(
                        text = "離職日：${TimeConverter.format(it)}",
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
                                text = "生效日：${TimeConverter.format(salary.millis)}",
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
            if (current != null) {
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
            lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 2),
            onClear = true,
            onValueChange = viewModel::editName,
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            FieldLabelText(text = "薪資記錄")
            val focusManager = LocalFocusManager.current
            AddableRow(
                modifier = Modifier.fillMaxWidth(),
                first = {
                    NumberInputField(
                        modifier = Modifier.fillMaxWidth(),
                        titleText = "日薪",
                        text = salaryEdit.salary.orEmpty(),
                        imeAction = ImeAction.Done,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        onClear = true,
                        onValueChange = viewModel::editSalary,
                    )
                },
                second = {
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
                    ) {
                        viewModel.editSalaryMillis(it)
                    }
                },
                onAdd =
                    if (salaryEdit.allFilled) {
                        {
                            focusManager.clearFocus()
                            viewModel.addSalary(salaryEdit)
                        }
                    } else {
                        null
                    },
            )
            edit?.salaries?.forEach { salary ->
                RemovableRow(
                    modifier = Modifier.fillMaxWidth(),
                    first = NumberFormat(salary.salary),
                    second = TimeConverter.format(salary.millis).orDash,
                ) {
                    showDeleteDialog = salary
                }
            }
        }
        if (editBundle?.isInsert == false) {
            // 離職生效日
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
    val millis = "生效日：${TimeConverter.format(showDeleteDialog?.millis)}"
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
