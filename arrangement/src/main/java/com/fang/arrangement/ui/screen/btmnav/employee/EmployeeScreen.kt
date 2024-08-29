package com.fang.arrangement.ui.screen.btmnav.employee

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.component.DialogThemedScreen
import com.fang.cosmos.foundation.ui.component.spacer.HorizontalSpacer
import com.fang.cosmos.foundation.ui.component.spacer.VerticalSpacer
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.fang.cosmos.foundation.ui.ext.textDp
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.androidx.compose.koinViewModel

private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN)

@Composable
internal fun EmployeeScreen(
    modifier: Modifier,
    viewModel: EmployeeViewModel = koinViewModel()
) {
    val idState = rememberSaveable {
        mutableLongStateOf(-1L)
    }
    val showEditeFieldState = rememberSaveable {
        mutableStateOf(false)
    }
    val showDeleteDialogState = rememberSaveable {
        mutableLongStateOf(-1L)
    }
    Column(modifier = modifier) {
        Button(
            onClick = {
                idState.longValue = -1L
                showEditeFieldState.value = true
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.End)
        ) { Text(text = "＋新增員工", fontSize = 16.textDp) }
        val items = viewModel.sites.stateValue()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(
                items = items,
                key = { _, item -> item.id },
                contentType = { _, item -> item.name }
            ) { i, item ->
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(text = "工　　地：${item.name}", fontSize = 14.textDp)
                        Text(text = "建立時間：${sdf.format(item.id)}", fontSize = 14.textDp)
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                idState.longValue = item.id
                                showEditeFieldState.value = true
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
                                showDeleteDialogState.longValue = item.id
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        CustomIcon(
                            drawableResId = R.drawable.arr_baseline_delete_forever_24,
                            tint = Color(0xFFC75454)
                        )
                    }
                }
                if (i != items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 22.dp),
                        color = Color.LightGray
                    )
                }
            }
        }
    }
    BuildingSiteEditField(
        viewModel = viewModel,
        id = idState.longValue,
        showEditeFieldState = showEditeFieldState,
    )
    DeleteConfirmDialog(
        viewModel = viewModel,
        showDeleteDialogState = showDeleteDialogState
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BuildingSiteEditField(
    viewModel: EmployeeViewModel,
    id: Long,
    showEditeFieldState: MutableState<Boolean>,
) {
    val inputName = rememberSaveable {
        mutableStateOf("")
    }
    DialogThemedScreen(isShow = showEditeFieldState.value) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val focusManager = LocalFocusManager.current
            val text = if (id > 0) "編輯" else "新增"
            Text(text = "${text}工地名稱", fontSize = 14.textDp)
            BasicTextField2(
                value = inputName.value,
                onValueChange = { inputName.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                textStyle = TextStyle(
                    fontSize = 16.textDp,
                ),
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
                lineLimits = TextFieldLineLimits.SingleLine,
            )
            HorizontalDivider(color = Color.DarkGray)
            VerticalSpacer(spaceDp = 12)
            Row {
                Button(
                    onClick = {
                        inputName.value = ""
                        showEditeFieldState.value = false
                    }
                ) { Text(text = "取消", fontSize = 14.textDp) }
                HorizontalSpacer(spaceDp = 16)
                Button(onClick = {
                    if (inputName.value.isNotBlank()) {
                        if (id > 0) {
                            viewModel.edit(id, inputName.value)
                            showEditeFieldState.value = false
                            inputName.value = ""
                        } else {
                            viewModel.add(inputName.value)
                            showEditeFieldState.value = false
                            inputName.value = ""
                        }
                    }
                }) { Text(text = "確定", fontSize = 14.textDp) }
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    viewModel: EmployeeViewModel,
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
