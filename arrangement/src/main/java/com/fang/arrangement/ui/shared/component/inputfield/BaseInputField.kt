package com.fang.arrangement.ui.shared.component.inputfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.fang.arrangement.ui.shared.component.BaseField
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.cosmos.foundation.Action
import com.fang.cosmos.foundation.ui.ext.color

@Composable
internal fun BaseInputField(
    modifier: Modifier,
    titleText: String,
    text: String?,
    inputTransformation: InputTransformation? = null,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    imeAction: ImeAction = ImeAction.Unspecified,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onClear: Boolean = false,
    transformOutput: (TextFieldBuffer.(String) -> Unit)? = null,
    onValueChange: Action<String>,
) = Box(modifier = modifier) {
    val textFieldState = rememberTextFieldState(text.orEmpty())
    if (text.isNullOrEmpty()) textFieldState.clearText()
    BasicTextField(
        state = textFieldState,
        inputTransformation = inputTransformation,
        textStyle = ContentText.style.color(ContentText.color),
        keyboardOptions =
            KeyboardOptions.Default.copy(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
        lineLimits = lineLimits,
        cursorBrush = SolidColor(ContentText.color),
        outputTransformation = {
            onValueChange(toString())
            transformOutput?.invoke(this, toString())
        },
        decorator = { innerTextField ->
            BaseField(
                modifier = Modifier.fillMaxWidth(),
                title = titleText,
                onClear = { textFieldState.clearText() }.takeIf { onClear },
            ) {
                innerTextField()
            }
        },
    )
}