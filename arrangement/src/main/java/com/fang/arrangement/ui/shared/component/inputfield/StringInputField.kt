package com.fang.arrangement.ui.shared.component.inputfield

import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.fang.cosmos.foundation.Action
import com.fang.cosmos.foundation.indexOfFirstOrNull

const val EMPTY_STRING_HOLDER = "@%^&@%^&"
private val textFieldBufferBlock: TextFieldBuffer.() -> Unit by lazy {
    {
        val index = toString().indexOfFirstOrNull { it != ' ' } ?: length
        delete(0, index)
    }
}

@Composable
internal fun StringInputField(
    modifier: Modifier,
    textFieldState: TextFieldState = rememberTextFieldState(EMPTY_STRING_HOLDER),
    titleText: String,
    text: String?,
    imeAction: ImeAction = ImeAction.Unspecified,
    lines: Int? = null,
    onClear: Boolean = false,
    onValueChange: Action<String?>,
) {
    if (textFieldState.text == EMPTY_STRING_HOLDER) {
        textFieldState.edit {
            replace(0, length, text.orEmpty())
            textFieldBufferBlock(this)
            onValueChange(toString())
        }
    }
    BaseInputField(
        modifier = modifier,
        titleText = titleText,
        textFieldState = textFieldState,
        inputTransformation = {
            textFieldBufferBlock(this)
            onValueChange(toString())
        },
        imeAction = imeAction,
        lineLimits =
            lines?.let {
                TextFieldLineLimits.MultiLine(maxHeightInLines = it)
            } ?: TextFieldLineLimits.Default,
        onClear = onClear,
        onValueChange = onValueChange,
    )
}
