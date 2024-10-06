package com.fang.arrangement.ui.shared.component.inputfield

import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.fang.cosmos.foundation.Action
import com.fang.cosmos.foundation.indexOfFirstOrNull
import com.fang.cosmos.foundation.notDigit
import com.fang.cosmos.foundation.takeIfNotBlank
import kotlin.math.ceil

private const val EMPTY_NUM_HOLDER = "@%^&@%^&"
private val trim: TextFieldBuffer.(Boolean) -> Unit by lazy {
    { enableDecimal ->
        if (enableDecimal) {
            with(toString()) {
                val dotIndex = indexOfFirstOrNull { it == '.' }
                reversed().forEachIndexed { i, c ->
                    val index = lastIndex - i
                    if (c.notDigit() && index != dotIndex) delete(index, index + 1)
                }
            }
            with(toString()) {
                val dotIndex = indexOfFirstOrNull { it == '.' } ?: length
                (
                    indexOfFirstOrNull { it != '0' }?.takeIf { it < dotIndex }
                        ?: dotIndex.takeIf { it > 1 }?.let { it - 1 }
                )
                    ?.let { delete(0, it) }
            }
        } else {
            with(toString()) {
                val index = indexOfFirstOrNull { it in '1'..'9' } ?: length
                delete(0, index)
            }
            with(toString()) {
                reversed().forEachIndexed { i, c ->
                    val index = lastIndex - i
                    if (c.notDigit()) delete(index, index + 1)
                }
            }
        }
    }
}
private val pretty: TextFieldBuffer.() -> Unit by lazy {
    {
        val space = 3
        val length =
            with(toString()) {
                substringBefore(".", this)
            }.length
        val times = ceil(length / space.toFloat()).toInt() - 1
        (0..<times).forEach {
            insert(length - space * (it + 1), ",")
        }
    }
}

@Composable
internal fun NumberInputField(
    modifier: Modifier,
    titleText: String,
    text: String?,
    imeAction: ImeAction = ImeAction.Unspecified,
    onClear: Boolean,
    enableDecimal: Boolean = true,
    onValueChange: Action<String?>,
) {
    val textFieldState = rememberTextFieldState(EMPTY_NUM_HOLDER)
    if (textFieldState.text == EMPTY_NUM_HOLDER) {
        textFieldState.edit {
            replace(0, length, text.orEmpty())
            trim(enableDecimal)
            onValueChange(toString().takeIfNotBlank)
            pretty()
        }
    }
    BaseInputField(
        modifier = modifier,
        titleText = titleText,
        textFieldState = textFieldState,
        inputTransformation = {
            if (textFieldState.text != toString()) {
                trim(enableDecimal)
                onValueChange(toString().takeIfNotBlank)
                pretty()
            }
        },
        keyboardType = KeyboardType.Number,
        imeAction = imeAction,
        lineLimits = TextFieldLineLimits.SingleLine,
        onClear = onClear,
        onValueChange = onValueChange,
    )
}
