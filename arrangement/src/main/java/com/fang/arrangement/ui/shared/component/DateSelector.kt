package com.fang.arrangement.ui.shared.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.fang.arrangement.ui.shared.component.button.component.NegativeButton
import com.fang.arrangement.ui.shared.component.button.component.PositiveButton
import com.fang.arrangement.ui.shared.dsl.YMDDayOfWeek
import com.fang.cosmos.foundation.Action
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple

@Composable
internal fun DateSelector(
    modifier: Modifier,
    titleText: String,
    onClear: Invoke? = null,
    original: Long? = null,
    isSelectableMillis: (Long) -> Boolean,
    onConfirm: Action<Long?>,
) {
    val showState =
        rememberSaveable {
            mutableStateOf(false)
        }
    val focusManager = LocalFocusManager.current
    Field(
        modifier.clickableNoRipple {
            focusManager.clearFocus()
            showState.value = true
        },
        titleText = titleText,
        text =
            original?.let { YMDDayOfWeek(it) }.orEmpty(),
        onClear = onClear,
    )
    DatePicker(
        showState = showState,
        original = original,
        selectable = isSelectableMillis,
        onConfirm = onConfirm,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePicker(
    showState: MutableState<Boolean>,
    hide: Invoke = { showState.value = false },
    original: Long? = null,
    selectable: (Long) -> Boolean,
    onConfirm: Action<Long?>,
) {
    if (showState.value) {
        val pickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = original,
                yearRange = 2023..2050,
                selectableDates =
                    object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean = selectable(utcTimeMillis)
                    },
            )
        DatePickerDialog(
            onDismissRequest = hide,
            confirmButton = {
                PositiveButton(
                    modifier =
                        Modifier
                            .padding(bottom = 8.dp, end = 16.dp),
                    onClick = {
                        hide()
                        onConfirm(pickerState.selectedDateMillis)
                    },
                )
            },
            dismissButton = {
                NegativeButton(Modifier.padding(bottom = 8.dp, end = 12.dp)) {
                    hide()
                    pickerState.selectedDateMillis = original
                }
            },
            properties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                ),
        ) {
            DatePicker(
                state = pickerState,
                showModeToggle = false,
            )
        }
    }
}
