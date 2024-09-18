package com.fang.arrangement.ui.screen.btmnav.site

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.arrangement.foundation.orDash
import com.fang.arrangement.ui.shared.component.ArrangementList
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.ErrorDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.inputfield.NumberInputField
import com.fang.arrangement.ui.shared.component.inputfield.StringInputField
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.takeIfNotBlank
import com.fang.cosmos.foundation.time.transformer.TimeConverter
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.dsl.animateColor
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SiteScreen(
    modifier: Modifier,
    viewModel: SiteViewModel = koinViewModel(),
) {
    Column(modifier) {
        ArrangementList(
            modifier = Modifier.weight(1f, false),
            items = viewModel.sites.stateValue(),
            key = { it.id },
            contentType = { it },
            onSelect = viewModel::onUpdate,
            onAdd = viewModel::onInsert,
        ) { item ->
            val notArchive = !item.isArchive
            HighlightText(
                text = item.name,
                isAlpha = notArchive,
            )
            // 開工、竣工
            val start = TimeConverter.format(item.startMillis)
            val end = TimeConverter.format(item.endMillis)
            if (start != null || end != null) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    ContentText(
                        text = "開工：${start.orDash}",
                        modifier = Modifier.weight(1f),
                        isAlpha = notArchive,
                    )
                    ContentText(
                        text = "竣工：${end.orDash}",
                        modifier = Modifier.weight(1f),
                        isAlpha = notArchive,
                    )
                }
            }
            // 總價
            item.income?.let {
                ContentText(
                    text = "總價：${NumberFormat(it)}",
                    isAlpha = notArchive,
                )
            }
            // 地址
            item.address.takeIfNotBlank?.let {
                val context = LocalContext.current
                ContentText(
                    text = "$it ↗",
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
                    isAlpha = notArchive,
                )
            }
        }
    }
    SiteEditDialog(
        editBundle = viewModel.editBundle.stateValue(),
        viewModel = viewModel,
    )
    ErrorDialog(viewModel)
    Loading(viewModel)
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
            if (edit?.valid == true) {
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
                lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 2),
                onClear = { viewModel.editName(null) },
                onValueChange = viewModel::editName,
            )
            // 地址
            StringInputField(
                modifier = Modifier.fillMaxWidth(),
                titleText = "地址",
                text = edit?.address.takeIfNotBlank.orEmpty(),
                lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 3),
                onClear = {
                    viewModel.editAddress(null)
                },
                onValueChange = viewModel::editAddress,
            )
            // 開工
            DateSelector(
                modifier = Modifier.fillMaxWidth(),
                titleText = "開工日",
                onClear = {
                    viewModel.editStartMillis(null)
                },
                original = edit?.startMillis,
                isSelectableMillis = { millis ->
                    edit?.endMillis?.let { millis <= it } ?: true
                },
                onConfirm = viewModel::editStartMillis,
            )
            // 竣工
            DateSelector(
                modifier = Modifier.fillMaxWidth(),
                titleText = "竣工日",
                onClear = {
                    viewModel.editEndMillis(null)
                },
                original = edit?.endMillis,
                isSelectableMillis = { millis ->
                    edit?.startMillis?.let { millis >= it } ?: true
                },
                onConfirm = viewModel::editEndMillis,
            )
            // 總價
            NumberInputField(
                modifier = Modifier.fillMaxWidth(),
                titleText = "總價",
                text = edit?.income.takeIfNotBlank.orEmpty(),
                imeAction = ImeAction.Done,
                lineLimits = TextFieldLineLimits.SingleLine,
                onClear = {
                    viewModel.editIncome(null)
                },
                onValueChange = viewModel::editIncome,
            )
        }
    }
}

@Composable
private fun Archive(
    modifier: Modifier,
    archive: Boolean,
    click: Invoke,
) {
    Row(modifier) {
        Spacer(modifier = Modifier.weight(1f))
        CustomIcon(
            drawableResId = if (archive) R.drawable.arr_r24_save else R.drawable.arr_red24_save,
            modifier =
                Modifier.clickableNoRipple(onClick = click),
            tint =
                animateColor(label = "Archive") {
                    primary.copy(alpha = if (archive) 1f else 0.45f)
                },
        )
    }
}
