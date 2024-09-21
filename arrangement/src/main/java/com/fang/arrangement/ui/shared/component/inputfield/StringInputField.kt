package com.fang.arrangement.ui.shared.component.inputfield

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.fang.cosmos.foundation.Action

@Composable
internal fun StringInputField(
    modifier: Modifier,
    titleText: String,
    text: String?,
    imeAction: ImeAction = ImeAction.Unspecified,
    lines: Int? = null,
    onClear: Boolean = false,
    onValueChange: Action<String>,
) = BaseInputField(
    modifier = modifier,
    titleText = titleText,
    text = text,
    inputTransformation = {
        if (toString() == " ") revertAllChanges()
    },
    imeAction = imeAction,
    lineLimits =
        lines?.let {
            TextFieldLineLimits.MultiLine(maxHeightInLines = it)
        } ?: TextFieldLineLimits.Default,
    onClear = onClear,
    onValueChange = onValueChange,
)
