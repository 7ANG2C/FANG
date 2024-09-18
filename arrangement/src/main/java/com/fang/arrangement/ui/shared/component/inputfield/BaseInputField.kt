package com.fang.arrangement.ui.shared.component.inputfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.fang.arrangement.ui.shared.component.BaseField
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.cosmos.foundation.Action
import com.fang.cosmos.foundation.Invoke
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
    onClear: Invoke? = null,
    onValueChange: Action<String>,
) = Box(modifier = modifier) {
    val focusManager = LocalFocusManager.current
    val textFieldState = rememberTextFieldState(text.orEmpty())
    BasicTextField(
        state = textFieldState,
        inputTransformation = inputTransformation,
        textStyle = ContentText.style.color(ContentText.color),
        keyboardOptions =
            KeyboardOptions.Default.copy(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
        onKeyboardAction = {
            focusManager.clearFocus()
        },
        lineLimits = lineLimits,
        cursorBrush = SolidColor(ContentText.color),
        outputTransformation = { onValueChange(toString()) },
        decorator = { innerTextField ->
            BaseField(
                modifier = Modifier.fillMaxWidth(),
                title = titleText,
                onClear =
                    onClear?.let {
                        {
                            textFieldState.edit { delete(0, length) }
                            it()
                        }
                    },
            ) {
                innerTextField()
            }
        },
    )
}
