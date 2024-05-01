package com.fang.arrangement.ui.screen.btmnav.schedule

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.fang.arrangement.R
import com.fang.arrangement.ui.screen.btmnav.building.Site
import com.fang.cosmos.foundation.number.NumberFormat
import com.fang.cosmos.foundation.time.calendar.toDayStart
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.component.DialogThemedScreen
import com.fang.cosmos.foundation.ui.component.spacer.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.spacer.VerticalSpacer
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.fang.cosmos.foundation.ui.ext.textDp
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.androidx.compose.koinViewModel

private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun ScheduleScreen(
    modifier: Modifier,
    viewModel: ScheduleViewModel = koinViewModel(),
) {
    val showEditDialogState = rememberSaveable {
        mutableStateOf(false)
    }
    val needEditSummaryState = remember {
        mutableStateOf<AttendanceSummary?>(null)
    }
    val showDatePickerState = rememberSaveable {
        mutableStateOf(false)
    }
    val showDeleteDialogState = rememberSaveable {
        mutableLongStateOf(-1L)
    }
    val datePickerState = remember {
        DatePickerState(
            locale = Locale.TAIWAN,
            initialSelectedDateMillis = today().timeInMillis,
            yearRange = (2023..2100),
            initialDisplayMode = DisplayMode.Picker,
        )
    }
    val summaries = viewModel.attendanceSummaries.stateValue()
    val sites = viewModel.sites.stateValue()
    Column(modifier = modifier) {
        Button(
            onClick = {
                if (sites.isNotEmpty()) {
                    datePickerState.selectedDateMillis = System.currentTimeMillis()
                    needEditSummaryState.value = null
                    showEditDialogState.value = true
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.End)
        ) {
            if (sites.isEmpty()) {
                Text(text = "沒工地，先去 [工地] 新增", fontSize = 16.textDp)
            } else {
                Text(text = "＋新增一筆記錄", fontSize = 16.textDp)
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            itemsIndexed(
                items = summaries,
                key = { _, item -> item.createTimeMillis },
                contentType = { _, item -> item.attendances }
            ) { i, item ->
                Row(Modifier.fillMaxWidth()) {
                    val total = item.attendances.sumOf { it.attendanceCount }
                    val format = NumberFormat(
                        number = total,
                        decimalCount = if (total % 1 == 0.0) {
                            0
                        } else NumberFormat.UNSPECIFIED_DECIMAL_SIZE,
                        invalidText = "-"
                    )
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "${sdf.format(item.createTimeMillis)} (共 $format 工)",
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.W600
                        )
                        VerticalSpacer(spaceDp = 4)
                        FlowRow(modifier = Modifier.padding(start = 8.dp)) {
                            val valid = item.attendances.filter { it.attendanceCount > 0.0 }
                            valid.forEachIndexed { index, data ->
                                val separator = if (index != valid.lastIndex) "、" else ""
                                val name = sites.find { it.id == data.siteId }?.name ?: "找不到工地"
                                val attendanceCount = NumberFormat(
                                    number = data.attendanceCount,
                                    decimalCount = if (data.attendanceCount % 1 == 0.0) {
                                        0
                                    } else NumberFormat.UNSPECIFIED_DECIMAL_SIZE,
                                    invalidText = "-"
                                )
                                Text(
                                    text = "$name ($attendanceCount)$separator",
                                    fontSize = 14.textDp
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                needEditSummaryState.value = item
                                datePickerState.selectedDateMillis = item.createTimeMillis
                                showEditDialogState.value = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        CustomIcon(
                            drawableResId = R.drawable.arr_baseline_edit_24,
                            tint = Color(0xFF43484D)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                showDeleteDialogState.longValue = item.createTimeMillis
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        CustomIcon(
                            drawableResId = R.drawable.arr_baseline_delete_forever_24,
                            tint = Color(0xFFC75454)
                        )
                    }
                }
                if (i != summaries.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
        }
    }

    BasicTextField(
        viewModel = viewModel,
        allSummaries = summaries,
        sites = sites,
        showEditDialogState = showEditDialogState,
        needEditSummary = needEditSummaryState.value,
        datePickerState = datePickerState,
        showDatePickerState = showDatePickerState,
    )
    DeleteConfirmDialog(
        viewModel = viewModel,
        showDeleteDialogState = showDeleteDialogState
    )
    if (showDatePickerState.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerState.value = false },
            confirmButton = {
                Button(
                    onClick = { showDatePickerState.value = false },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) { Text("確定", fontSize = 14.textDp) }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            ),
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.background(Color.White),
                showModeToggle = false
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BasicTextField(
    viewModel: ScheduleViewModel,
    allSummaries: List<AttendanceSummary>,
    sites: List<Site>,
    showEditDialogState: MutableState<Boolean>,
    needEditSummary: AttendanceSummary?,
    datePickerState: DatePickerState,
    showDatePickerState: MutableState<Boolean>,
) {
    val selectedMillis = datePickerState.selectedDateMillis?.let {
        today().apply { timeInMillis = it }.toDayStart.timeInMillis
    }
    val isAdd = needEditSummary == null
    val isDuplicated = allSummaries.find { s ->
        today().apply {
            timeInMillis = s.createTimeMillis
        }.toDayStart.timeInMillis == selectedMillis
    } != null
    DialogThemedScreen(isShow = showEditDialogState.value) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isAdd && isDuplicated) {
                Text(
                    text = "!!! 日期已經存在，直接用編輯的 !!!",
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.Red,
                    fontSize = 14.textDp
                )
            }
            val context = LocalContext.current
            Button(onClick = {
                if (isAdd) {
                    showDatePickerState.value = true
                } else {
                    Toast.makeText(context, "暫不支援編輯日期", Toast.LENGTH_SHORT).show()
                }
            }) {
                val text = datePickerState.selectedDateMillis?.let {
                    sdf.format(it)
                } ?: "尚未選擇正確日期"
                Text(text = text, fontSize = 14.textDp)
            }
            val editableAttendances = remember {
                mutableStateOf(sites.map { site ->
                    needEditSummary?.attendances?.find { it.siteId == site.id }
                        ?: Attendance(site.id, 0.0)
                })
            }
            editableAttendances.value.forEach { attendance ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val name = sites.find { it.id == attendance.siteId }?.name
                    Text(
                        text = name ?: "找不到工地",
                        style = TextStyle(fontSize = 16.textDp)
                    )
                    val focusManager = LocalFocusManager.current
                    BasicTextField2(
                        value = NumberFormat(
                            number = attendance.attendanceCount,
                            decimalCount = if (attendance.attendanceCount % 1 == 0.0) {
                                0
                            } else NumberFormat.UNSPECIFIED_DECIMAL_SIZE,
                            thousandSeparator = false
                        ),
                        onValueChange = { string ->
                            val currentAttendances = editableAttendances.value
                            val new = currentAttendances.map {
                                if (it.siteId == attendance.siteId) {
                                    if (string.isBlank()) {
                                        it.copy(attendanceCount = 0.0)
                                    } else {
                                        it.copy(attendanceCount = (string.toDoubleOrNull() ?: 0.0))
                                    }
                                } else it
                            }
                            editableAttendances.value = new
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        textStyle = TextStyle(
                            fontSize = 16.textDp,
                            color = Color.Gray
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions { focusManager.clearFocus() },
                        lineLimits = TextFieldLineLimits.SingleLine,
                        decorator = {
                            Column {
                                VerticalSpacer(spaceDp = 4)
                                it()
                                VerticalSpacer(spaceDp = 4)
                                HorizontalDivider(color = Color.Black)
                            }
                        }
                    )
                    Text(
                        text = "工",
                        style = TextStyle(fontSize = 16.textDp)
                    )
                }
            }
            VerticalSpacer(spaceDp = 16)
            Row {
                Button(onClick = {
                    showEditDialogState.value = false
                }) { Text(text = "取消", fontSize = 14.textDp) }
                val validCount = editableAttendances.value.all { it.attendanceCount >= 0.0 }
                    && editableAttendances.value.any { it.attendanceCount > 0.0 }
                val datePickerMillis = datePickerState.selectedDateMillis
                if (validCount && (needEditSummary != null || (!isDuplicated && datePickerMillis != null))) {
                    HorizontalSpacer(spaceDp = 16)
                    Button(onClick = {
                        if (needEditSummary != null) {
                            viewModel.edit(
                                needEditSummary.createTimeMillis,
                                editableAttendances.value
                            )
                        } else datePickerMillis?.let {
                            viewModel.add(it, editableAttendances.value)
                        }
                        showEditDialogState.value = false
                    }) {
                        Text(text = "確定", fontSize = 14.textDp)
                    }
                }
            }
            Text(
                text = "*當 1.日期無重複 2.任一欄位工數 > 0 ，才會出現 [確定] 按鈕",
                fontSize = 10.textDp, color = Color.Blue
            )
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    viewModel: ScheduleViewModel,
    showDeleteDialogState: MutableState<Long>
) {
    val id = showDeleteDialogState.value
    DialogThemedScreen(isShow = id > 0) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "確認刪除嗎?\n刪除後將無法復原", fontSize = 18.textDp)
            VerticalSpacer(spaceDp = 16)
            Row {
                Button(onClick = {
                    showDeleteDialogState.value = -1L
                }) {
                    Text(text = "取消", fontSize = 14.textDp)
                }
                HorizontalSpacer(spaceDp = 16)
                Button(onClick = {
                    viewModel.delete(id)
                    showDeleteDialogState.value = -1L
                }) {
                    Text(text = "刪除", fontSize = 14.textDp)
                }
            }
        }
    }
}
