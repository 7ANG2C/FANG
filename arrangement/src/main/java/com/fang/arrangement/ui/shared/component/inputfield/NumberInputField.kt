package com.fang.arrangement.ui.shared.component.inputfield

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.fang.cosmos.foundation.Action

@Composable
internal fun NumberInputField(
    modifier: Modifier,
    titleText: String,
    text: String?,
    imeAction: ImeAction = ImeAction.Unspecified,
    onClear: Boolean = false,
    onValueChange: Action<String>,
) = BaseInputField(
    modifier = modifier,
    titleText = titleText,
    text = text,
    inputTransformation = {
        val input = toString()
        if (input == "0") {
            revertAllChanges()
        } else {
            input.forEachIndexed { i, c ->
                if (!c.isDigit()) delete(i, i + 1)
            }
        }
    },
    keyboardType = KeyboardType.Number,
    imeAction = imeAction,
    lineLimits = TextFieldLineLimits.SingleLine,
    onClear = onClear,
    transformOutput = {
        val insert = 3
        var commaInsertIndex = it.length - insert
        while (commaInsertIndex > 0) {
            insert(commaInsertIndex, ",")
            commaInsertIndex -= insert
        }
    },
    onValueChange = onValueChange,
)
