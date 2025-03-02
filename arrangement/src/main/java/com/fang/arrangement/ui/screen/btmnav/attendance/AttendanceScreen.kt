package com.fang.arrangement.ui.screen.btmnav.attendance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.arrangement.foundation.orDash
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.arrangement.ui.shared.component.ArrangementList
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.button.composition.ButtonSets
import com.fang.arrangement.ui.shared.component.chip.ArchivedTag
import com.fang.arrangement.ui.shared.component.chip.AttendanceChip
import com.fang.arrangement.ui.shared.component.chip.DeletedTag
import com.fang.arrangement.ui.shared.component.chip.FullChip
import com.fang.arrangement.ui.shared.component.chip.HalfChip
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.ErrorDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.inputfield.StringInputField
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.EmployeeTag
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.arrangement.ui.shared.dsl.Remark
import com.fang.arrangement.ui.shared.dsl.YMDDayOfWeek
import com.fang.arrangement.ui.shared.dsl.employeeState
import com.fang.arrangement.ui.shared.ext.clickRipple
import com.fang.cosmos.foundation.mapNoNull
import com.fang.cosmos.foundation.ui.component.CustomBottomSheet
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialShape
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.dsl.screenHeightDp
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.fontSize
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.fang.cosmos.foundation.ui.ext.textDp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AttendanceScreen(
    modifier: Modifier,
    viewModel: AttendanceViewModel = koinViewModel(),
) {
    val bundle = viewModel.bundle.stateValue()
    Column(modifier) {
        ArrangementList(
            modifier = Modifier.weight(1f, false),
            items = bundle.attAlls,
            key = { it.id },
            contentType = { it },
            onSelect = { viewModel.onUpdate(it, bundle.sites) },
            onAdd = { viewModel.onInsert(bundle.sites) },
        ) { item ->
            Column(Modifier) {
                var isExpand by rememberSaveable { mutableStateOf(false) }
                val total = item.attendances.sumOf { it.fulls.size + it.halfs.size * 0.5 }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AttendanceChip(
                        attendance = total,
                        bgColor = { HighlightText.color.copy(alpha = 0.22f) },
                        textStyle = { HighlightText.style.color(HighlightText.color) },
                        placeHolder = false,
                    )
                    HorizontalSpacer(8)
                    HighlightText(text = YMDDayOfWeek(item.id).orDash)
                    Spacer(modifier = Modifier.weight(1f))
                    CustomIcon(
                        drawableResId = R.drawable.arr_r24_keyboard_double_arrow_down,
                        modifier =
                            Modifier
                                .clickRipple { isExpand = !isExpand }
                                .rotate(
                                    animateFloatAsState(
                                        if (isExpand) 180f else 0f,
                                        label = "rotate",
                                    ).value,
                                ),
                        tint = ContentText.color,
                    )
                }
                AnimatedVisibility(visible = isExpand) {
                    Column {
                        VerticalSpacer(4f)
                        item.attendances.forEach { mAtt ->
                            if (mAtt.fulls.isNotEmpty() || mAtt.halfs.isNotEmpty()) {
                                val attTotal = mAtt.fulls.size + mAtt.halfs.size * 0.5
                                VerticalSpacer(3.2f)
                                Row {
                                    val style =
                                        ContentText.style.color(MaterialColor.onSecondaryContainer)
                                    AttendanceChip(
                                        attendance = attTotal,
                                        bgColor = { ContentText.color.copy(alpha = 0.24f) },
                                        textStyle = { style },
                                        placeHolder = false,
                                    )
                                    HorizontalSpacer(8)
                                    ArrText(
                                        text = mAtt.site?.name ?: mAtt.siteId.toString(),
                                    ) { style }
                                    if (mAtt.site == null || mAtt.site.isDelete) {
                                        HorizontalSpacer(2.8f)
                                        Box(contentAlignment = Alignment.CenterEnd) {
                                            ArrText(
                                                text = "安",
                                            ) { style.color(Color.Transparent) }
                                            DeletedTag()
                                        }
                                    } else if (mAtt.site.isArchive) {
                                        HorizontalSpacer(2.8f)
                                        Box(contentAlignment = Alignment.CenterEnd) {
                                            ArrText(
                                                text = "安",
                                            ) { style.color(Color.Transparent) }
                                            ArchivedTag()
                                        }
                                    }
                                }
                                val style =
                                    TextStyle(
                                        fontSize = 14.4.textDp,
                                        fontWeight = FontWeight.W400,
                                        color =
                                            MaterialColor.onSurfaceVariant.copy(
                                                alpha = 0.92f,
                                            ),
                                    )
                                var attW by remember { mutableStateOf(0.dp) }
                                var tagW by remember { mutableStateOf(0.dp) }
                                val density = LocalDensity.current
                                if (mAtt.fulls.isNotEmpty()) {
                                    VerticalSpacer(4)
                                    Row {
                                        Box(contentAlignment = Alignment.CenterEnd) {
                                            ArrText(
                                                text = "000.0",
                                                Modifier
                                                    .padding(horizontal = 4.dp)
                                                    .onGloballyPositioned {
                                                        attW =
                                                            with(density) {
                                                                it.size.width.toDp()
                                                            }
                                                    },
                                            ) {
                                                ContentText.style.color(Color.Transparent)
                                            }
                                            FullChip(
                                                modifier =
                                                    Modifier
                                                        .scale(0.88f)
                                                        .onGloballyPositioned {
                                                            tagW =
                                                                with(density) {
                                                                    it.size.width.toDp()
                                                                }
                                                        },
                                            )
                                        }
                                        HorizontalSpacer(8)
                                        FlowRow {
                                            mAtt.fulls.forEachIndexed { i, item ->
                                                Row {
                                                    ArrText(
                                                        text =
                                                            (
                                                                item.employee?.name
                                                                    ?: item.id.toString()
                                                            ),
                                                    ) { style }
                                                    if (employeeState(item.employee)) {
                                                        Box(contentAlignment = Alignment.CenterEnd) {
                                                            ArrText(
                                                                text = "安",
                                                            ) { style.color(Color.Transparent) }
                                                            EmployeeTag(
                                                                modifier = Modifier.scale(0.82f),
                                                                employee = item.employee,
                                                            )
                                                        }
                                                    }
                                                    if (i != mAtt.fulls.lastIndex) {
                                                        ArrText(text = "・") { style }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (mAtt.halfs.isNotEmpty()) {
                                    VerticalSpacer(4)
                                    Row {
                                        Box(contentAlignment = Alignment.CenterEnd) {
                                            ArrText(
                                                text = "000.0",
                                                Modifier
                                                    .padding(horizontal = 4.dp)
                                                    .onGloballyPositioned {
                                                        attW =
                                                            with(density) {
                                                                it.size.width.toDp()
                                                            }
                                                    },
                                            ) {
                                                ContentText.style.color(Color.Transparent)
                                            }
                                            HalfChip(
                                                modifier =
                                                    Modifier
                                                        .scale(0.88f)
                                                        .onGloballyPositioned {
                                                            tagW =
                                                                with(density) {
                                                                    it.size.width.toDp()
                                                                }
                                                        },
                                            )
                                        }
                                        HorizontalSpacer(8)
                                        FlowRow {
                                            mAtt.halfs.forEachIndexed { i, item ->
                                                Row {
                                                    ArrText(
                                                        text =
                                                            (
                                                                item.employee?.name
                                                                    ?: item.id.toString()
                                                            ),
                                                    ) { style }
                                                    if (employeeState(item.employee)) {
                                                        Box(contentAlignment = Alignment.CenterEnd) {
                                                            ArrText(
                                                                text = "安",
                                                            ) { style.color(Color.Transparent) }
                                                            EmployeeTag(
                                                                modifier = Modifier.scale(0.82f),
                                                                employee = item.employee,
                                                            )
                                                        }
                                                    }
                                                    if (i != mAtt.halfs.lastIndex) {
                                                        ArrText(text = "・") { style }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                mAtt.remark?.let {
                                    VerticalSpacer(1.8f)
                                    Row {
                                        (attW + 8.dp - tagW).takeIf { it > 0.dp }?.let {
                                            HorizontalSpacer(it)
                                        }
                                        ArrText(text = it) { style }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    AttEditDialog(
        editBundle = viewModel.editBundle.stateValue(),
        dates = bundle.attAlls.map { it.id },
        viewModel = viewModel,
    )
    ErrorDialog(viewModel)
    Loading(viewModel)
    with(viewModel.mAttEdit.stateValue()) {
        CustomBottomSheet(
            isShow = this != MAttendance.empty,
            onDismiss = viewModel::clearSingleSite,
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .bg(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) { surfaceBright }
                    .heightIn(0.dp, screenHeightDp * 0.6f),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val style = MaterialTypography.titleLarge
                        ArrText(
                            text = site?.name ?: siteId.toString(),
                        ) {
                            style.color(MaterialColor.onSecondaryContainer)
                        }
                        if (site == null || site.isDelete) {
                            HorizontalSpacer(4f)
                            DeletedTag()
                        } else if (site.isArchive) {
                            HorizontalSpacer(4f)
                            ArchivedTag()
                        }
                    }
                }
                HorizontalDivider(
                    Modifier.padding(horizontal = 14.dp),
                    color = MaterialColor.outline.copy(alpha = 0.52f),
                )
                Row(
                    Modifier
                        .padding(vertical = 8.dp)
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FullChip(Modifier.align(Alignment.Center))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        HalfChip(Modifier.align(Alignment.Center))
                    }
                }
                val mEmployees =
                    bundle.employees.mapNoNull({
                        it.notDelete && it.notExpire
                    }) { MEmployee(it.id, it) }
                val employees =
                    (mEmployees + fulls + halfs).distinctBy { it.id }
                        .sortedWith(
                            compareBy<MEmployee>(
                                { it.employee == null },
                                { it.employee?.isDelete == true },
                                { it.employee?.isExpire == true },
                            ).thenByDescending { it.employee?.id },
                        )
                Column(
                    Modifier
                        .weight(1f, false)
                        .padding(horizontal = 14.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    employees.forEach { employee ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            listOf(fulls to true, halfs to false).forEach { (list, isFull) ->
                                val employeeIn = employee in list
                                Row(
                                    modifier =
                                        Modifier
                                            .weight(1f)
                                            .clip(MaterialShape.small)
                                            .bg(MaterialShape.small) {
                                                if (employeeIn) {
                                                    MaterialColor.primaryContainer
                                                } else {
                                                    Color.Transparent
                                                }
                                            }
                                            .clickRipple {
                                                viewModel.editSingleSiteEmployee(isFull, employee)
                                            }
                                            .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        ArrText(
                                            text =
                                                employee.employee?.name
                                                    ?: employee.id.toString(),
                                        ) {
                                            ContentText.style.color(
                                                if (employeeIn) {
                                                    onSecondaryContainer
                                                } else {
                                                    ContentText.color
                                                },
                                            )
                                        }
                                        if (employeeState(employee.employee)) {
                                            HorizontalSpacer(4)
                                            EmployeeTag(employee = employee.employee)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                ) {
                    StringInputField(
                        modifier = Modifier.fillMaxWidth(),
                        titleText = "備註 (${remark.orEmpty().length}/${Remark.L30})",
                        text = remark.orEmpty(),
                        lines = 2,
                        onClear = true,
                        onValueChange = viewModel::editSingleSiteRemark,
                    )
                    ButtonSets(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                        onNegative = viewModel::clearSingleSite,
                    ) {
                        viewModel.doneSingleSite(this@with)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AttEditDialog(
    editBundle: AttEditBundle?,
    dates: List<Long>,
    viewModel: AttendanceViewModel,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            edit?.attSiteEdits?.sumOf {
                it.fulls.size + it.halfs.size * 0.5
            }?.let {
                AttendanceChip(
                    attendance = it,
                    bgColor = {
                        ContentText.color.copy(alpha = 0.2f)
                    },
                    textStyle = {
                        MaterialTypography.headlineSmall.color(onPrimaryContainer)
                    },
                    placeHolder = false,
                )
                HorizontalSpacer(16)
            }
            DateSelector(
                modifier = Modifier.weight(1f),
                titleText = "日期",
                onClear = { viewModel.editDate(null) },
                original = edit?.id,
                isSelectableMillis = { millis ->
                    millis !in (dates - (current?.id ?: 0))
                },
                onConfirm = viewModel::editDate,
            )
        }
        edit?.attSiteEdits?.forEach { mAtt ->
            val total = mAtt.fulls.size + mAtt.halfs.size * 0.5
            val exist = total > 0.0
            val focusManager = LocalFocusManager.current
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialShape.small)
                    .bg(MaterialShape.small) {
                        if (exist) {
                            primaryContainer.copy(alpha = 0.8f)
                        } else {
                            surfaceBright
                        }
                    }
                    .clickRipple {
                        focusManager.clearFocus()
                        viewModel.editSingleSite(mAtt)
                    }
                    .padding(horizontal = 13.2.dp, vertical = 6.dp),
            ) {
                // 工地名
                Row {
                    if (exist) {
                        AttendanceChip(
                            attendance = total,
                            bgColor = {
                                onSecondaryContainer.copy(alpha = 0.28f)
                            },
                            textStyle = {
                                ContentText.style.color(onSecondaryContainer)
                                    .copy(fontWeight = FontWeight.W500)
                            },
                            placeHolder = false,
                        )
                        HorizontalSpacer(8)
                    }
                    ArrText(
                        text = mAtt.site?.name ?: mAtt.siteId.toString(),
                    ) {
                        ContentText.style.color(if (exist) onSecondaryContainer else ContentText.color)
                    }
                    if (mAtt.site == null || mAtt.site.isDelete) {
                        HorizontalSpacer(2.8f)
                        Box(contentAlignment = Alignment.CenterEnd) {
                            ArrText(
                                text = "安",
                            ) { ContentText.style.color(Color.Transparent) }
                            DeletedTag()
                        }
                    } else if (mAtt.site.isArchive) {
                        HorizontalSpacer(2.8f)
                        Box(contentAlignment = Alignment.CenterEnd) {
                            ArrText(
                                text = "安",
                            ) { ContentText.style.color(Color.Transparent) }
                            ArchivedTag()
                        }
                    }
                }
                // 工數
                var attW by remember { mutableStateOf(0.dp) }
                var tagW by remember { mutableStateOf(0.dp) }
                val density = LocalDensity.current
                if (exist) {
                    listOf(mAtt.fulls, mAtt.halfs).forEachIndexed { i, employees ->
                        if (employees.isNotEmpty()) {
                            Row {
                                VerticalSpacer(1.2f)
                                Box(contentAlignment = Alignment.CenterEnd) {
                                    ArrText(
                                        text = "000.0",
                                        modifier =
                                            Modifier.padding(horizontal = 4.dp).onGloballyPositioned {
                                                attW =
                                                    with(density) {
                                                        it.size.width.toDp()
                                                    }
                                            },
                                    ) {
                                        ContentText.style.color(Color.Transparent)
                                    }
                                    if (i == 0) {
                                        FullChip(
                                            Modifier.scale(0.8f).onGloballyPositioned {
                                                tagW =
                                                    with(density) {
                                                        it.size.width.toDp()
                                                    }
                                            },
                                        )
                                    } else {
                                        HalfChip(
                                            Modifier.scale(0.8f).onGloballyPositioned {
                                                tagW =
                                                    with(density) {
                                                        it.size.width.toDp()
                                                    }
                                            },
                                        )
                                    }
                                }
                                HorizontalSpacer(3.8f)
                                FlowRow {
                                    employees.forEachIndexed { i, it ->
                                        val textStyle = ContentText.style.fontSize(13.2.textDp)
                                        val color = ContentText.color
                                        Row {
                                            ArrText(
                                                text = it.employee?.name ?: it.id.toString(),
                                            ) { textStyle.color(color) }
                                            if (employeeState(it.employee)) {
                                                Box(contentAlignment = Alignment.CenterEnd) {
                                                    ArrText(
                                                        text = "安",
                                                    ) { textStyle.color(Color.Transparent) }
                                                    EmployeeTag(
                                                        modifier = Modifier.scale(0.82f),
                                                        employee = it.employee,
                                                    )
                                                }
                                            }
                                            if (i != employees.lastIndex) {
                                                ArrText(text = "・") { textStyle.color(color) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                mAtt.remark?.let { rmk ->
                    Row(Modifier.fillMaxWidth()) {
                        (attW + 8.dp - tagW).takeIf { it > 0.dp }?.let {
                            HorizontalSpacer(it)
                        }
                        ArrText(text = rmk) {
                            ContentText.style.fontSize(13.2.textDp).color(ContentText.color)
                                .copy(lineHeight = 13.6.textDp)
                        }
                    }
                }
            }
        }
    }
}
