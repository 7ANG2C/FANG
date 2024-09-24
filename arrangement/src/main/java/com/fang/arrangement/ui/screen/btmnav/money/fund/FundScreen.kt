package com.fang.arrangement.ui.screen.btmnav.money.fund

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.fang.arrangement.foundation.orDash
import com.fang.arrangement.ui.shared.component.ArrangementList
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.ErrorDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.fieldrow.Average2Row
import com.fang.arrangement.ui.shared.component.inputfield.NumberInputField
import com.fang.arrangement.ui.shared.component.inputfield.StringInputField
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.arrangement.ui.shared.dsl.Remark
import com.fang.arrangement.ui.shared.dsl.YMDDayOfWeek
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun FundScreen(
    modifier: Modifier,
    viewModel: FundViewModel = koinViewModel(),
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier) {
            val funds = viewModel.funds.stateValue()
            funds.sumOf { it.fund }.takeIf { it > 0 }?.let {
                HighlightText(
                    text = "總計 $${NumberFormat(it, 0)}",
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 10.dp),
                )
            }
            ArrangementList(
                modifier = Modifier.weight(1f, false),
                items = funds,
                key = { it.id },
                contentType = { it },
                onSelect = viewModel::onUpdate,
                onAdd = viewModel::onInsert,
            ) { item ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    ContentText(text = YMDDayOfWeek(item.millis).orDash, Modifier.weight(1f))
                    ContentText(text = NumberFormat(item.fund, 0), Modifier.weight(1f))
                }
                ContentText(text = item.remark.orEmpty())
            }
        }
        FundEditDialog(
            editBundle = viewModel.editBundle.stateValue(),
            viewModel = viewModel,
        )
        ErrorDialog(viewModel)
        Loading(viewModel)
    }
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
                isSelectableMillis = { _ ->
                    true
                },
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
