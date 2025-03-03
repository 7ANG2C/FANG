package com.fang.arrangement.ui.screen.btmnav.money.payback

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
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
import com.fang.arrangement.definition.Boss
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
import com.fang.arrangement.ui.shared.dsl.BossTag
import com.fang.arrangement.ui.shared.dsl.ContentText
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
import com.fang.cosmos.foundation.ui.dsl.screenHeightDp
import com.fang.cosmos.foundation.ui.dsl.screenWidthDp
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.fontSize
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.fang.cosmos.foundation.ui.ext.textDp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun PaybackScreen(
    modifier: Modifier,
    viewModel: PaybackViewModel = koinViewModel(),
) = Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = modifier) {
        var expandedState by rememberSaveable {
            mutableStateOf(false)
        }
        CustomIcon(
            drawableResId = R.drawable.arr_r24_keyboard_double_arrow_down,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickableNoRipple {
                        expandedState = !expandedState
                    }.padding(horizontal = 12.dp)
                    .padding(top = 8.dp),
            tint = MaterialColor.primary,
        )
        Box(Modifier.fillMaxWidth().weight(1f)) {
            ArrangementList(
                modifier = Modifier.fillMaxWidth(),
                items = viewModel.bundle.stateValue().paybacks,
                key = { it.id },
                contentType = { it },
                onSelect = viewModel::onUpdate,
                onAdd = viewModel::onInsert,
            ) { item ->
                val isClear = item.isClear
                Row {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HighlightText(
                            text = item.boss.name.takeIfNotBlank ?: item.boss.id.toString(),
                            modifier = Modifier.weight(1f, false),
                            isAlpha = isClear,
                        )
                        HorizontalSpacer(2)
                        BossTag(
                            boss = item.boss,
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
                        text = "欠日：${YMDDayOfWeek(item.millis)}",
                        modifier = Modifier.weight(1f),
                        isAlpha = isClear,
                    )
                    HighlightText(
                        text = "總額：${NumberFormat(item.payback)}",
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
                                        text = "金額：${NumberFormat(record.payback)}",
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
            Crossfade(
                targetState = expandedState,
                label = "expandedState",
            ) { expand ->
                if (expand) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .bg { scrim.copy(alpha = 0.42f) },
                    ) {
                        FlowRow(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .bg(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)) {
                                        surfaceContainerLowest
                                    }.heightIn(1.dp, screenHeightDp * 0.58f)
                                    .padding(horizontal = 18.dp)
                                    .padding(bottom = 16.dp)
                                    .verticalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ContentText(
                                text = "  ＋  ",
                                modifier =
                                    Modifier
                                        .border(1.dp, ContentText.color, MaterialShape.small)
                                        .clickableNoRipple(onClick = viewModel::bossOnInsert)
                                        .padding(horizontal = 5.2.dp, vertical = 3.2.dp),
                            )
                            viewModel.bundle.stateValue().bosses.filter { it.notDelete }.forEach {
                                ContentText(
                                    text = it.name,
                                    modifier =
                                        Modifier
                                            .border(1.dp, ContentText.color, MaterialShape.small)
                                            .clickableNoRipple { viewModel.bossOnUpdate(it) }
                                            .padding(horizontal = 5.2.dp, vertical = 3.2.dp),
                                )
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clickableNoRipple { expandedState = false },
                        )
                    }
                }
            }
        }
    }
    PaybackEditDialog(
        bosses = viewModel.bundle.stateValue().bosses,
        editBundle = viewModel.editBundle.stateValue(),
        recordEdit = viewModel.recordEdit.stateValue(),
        viewModel = viewModel,
    )
    BossEditDialog(
        editBundle = viewModel.bossEditBundle.stateValue(),
        viewModel = viewModel,
    )
    ErrorDialog(viewModel)
    Loading(viewModel)
}

@Composable
private fun BossEditDialog(
    editBundle: BossEditBundle?,
    viewModel: PaybackViewModel,
) {
    val current = editBundle?.current
    val edit = editBundle?.edit
    EditDialog(
        isShow = editBundle != null,
        onDelete =
            if (current != null) {
                { viewModel.bossDelete(current) }
            } else {
                null
            },
        onCancel = {
            viewModel.bossClearEdit()
        },
        onConfirm =
            if (edit?.savable == true) {
                if (editBundle.isInsert) {
                    { viewModel.bossInsert(edit) }
                } else {
                    {
                        viewModel.bossUpdate(editBundle)
                    }.takeIf { editBundle.anyDiff }
                }
            } else {
                null
            },
    ) {
        StringInputField(
            modifier = Modifier.fillMaxWidth(),
            titleText = "姓名",
            text = edit?.name.orEmpty(),
            lines = 1,
            onClear = true,
            onValueChange = viewModel::editName,
        )
    }
}

@Composable
private fun PaybackEditDialog(
    bosses: List<Boss>,
    editBundle: PaybackEditBundle?,
    recordEdit: RecordEdit,
    viewModel: PaybackViewModel,
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
        // 選擇業主
        val currentBoss = current?.boss
        val selectableBosses =
            bosses.filter {
                it.id == currentBoss?.id || it.notDelete
            }
        val allBosses =
            selectableBosses +
                listOfNotNull(
                    currentBoss?.takeIf {
                        currentBoss.id !in selectableBosses.map { it.id }
                    },
                )

        Column {
            if (editBundle?.isInsert == false) {
                TextChip(
                    text = "欠款",
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
                title = "業主",
                onClear = { viewModel.editBoss(null) },
            ) {
                val boss = edit?.boss
                if (editBundle?.isInsert == false || boss != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ContentText(
                            boss?.name.takeIfNotBlank ?: boss?.id?.toString().orDash,
                        )
                        HorizontalSpacer(4)
                        BossTag(boss = boss, Modifier.scale(0.92f))
                    }
                }
            }
            if (allBosses.isNotEmpty()) {
                DropdownSelector(
                    items = allBosses,
                    modifier =
                        Modifier.width(
                            screenWidthDp * DialogShared.EDIT_WIDTH_FRACTION - DialogShared.editHPaddingDp * 2,
                        ),
                    selected = allBosses.find { it.id == edit?.boss?.id },
                    expandedState = expandedState,
                    onSelected = viewModel::editBoss,
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ContentText(text = it.name.takeIfNotBlank ?: it.id.toString())
                        HorizontalSpacer(4)
                        BossTag(boss = it, Modifier.scale(0.92f))
                        Spacer(modifier = Modifier.weight(1f))
                        if (it.id == edit?.boss?.id) {
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
                titleText = "欠款金額",
                text = edit?.payback.orEmpty(),
                imeAction = ImeAction.Next,
                onClear = true,
                onValueChange = viewModel::editPayback,
            )
        }) {
            DateSelector(
                modifier = Modifier.fillMaxWidth(),
                titleText = "欠款日",
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
                                    text = recordEdit.payback.orEmpty(),
                                    imeAction = ImeAction.Next,
                                    onClear = true,
                                    onValueChange = viewModel::editRecordPayback,
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
                                        ContentText(text = NumberFormat(record.payback))
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
    val salary = "金　額：${NumberFormat(showDeleteDialog?.payback)}"
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
