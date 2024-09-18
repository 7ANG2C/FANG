package com.fang.arrangement.ui.shared.component.inputfield

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.fang.cosmos.foundation.Action
import com.fang.cosmos.foundation.Invoke

@Composable
internal fun NumberInputField(
    modifier: Modifier,
    titleText: String,
    text: String?,
    imeAction: ImeAction = ImeAction.Unspecified,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onClear: Invoke? = null,
    onValueChange: Action<String>,
) = BaseInputField(
    modifier = modifier,
    titleText = titleText,
    text = text,
    inputTransformation = {
        val input = toString()
        if (input == "0" || input == " ") {
            delete(0, length)
        } else {
            replace(0, length, input.filter { it.isDigit() })
        }
    },
    keyboardType = KeyboardType.Number,
    imeAction = imeAction,
    lineLimits = lineLimits,
    onClear = onClear,
    onValueChange = onValueChange,
)
