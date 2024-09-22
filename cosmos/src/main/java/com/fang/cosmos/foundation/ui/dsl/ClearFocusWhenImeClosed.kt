package com.fang.cosmos.foundation.ui.dsl

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView

@Composable
fun ClearFocusWhenImeClosed() {
    val focusManager = LocalFocusManager.current
    if (!isImeVisible()) focusManager.clearFocus()
}

@Composable
private fun isImeVisible(): Boolean {
    val view = LocalView.current
    return produceState(initialValue = isKeyboardVisible(view)) {
        val viewTreeObserver = view.viewTreeObserver
        val listener =
            ViewTreeObserver.OnGlobalLayoutListener {
                value = isKeyboardVisible(view)
            }
        kotlin.runCatching {
            viewTreeObserver.addOnGlobalLayoutListener(listener)
        }
        awaitDispose {
            kotlin.runCatching {
                viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        }
    }.value
}

private fun isKeyboardVisible(v: View): Boolean {
    val rect = Rect()
    v.getWindowVisibleDisplayFrame(rect)
    val screenHeight = v.rootView.height
    val keyboardHeight = screenHeight - rect.bottom
    return keyboardHeight > screenHeight * 0.15
}
