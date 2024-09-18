package com.fang.arrangement.ui.shared.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.fang.cosmos.foundation.ui.dsl.MaterialShape
import com.fang.cosmos.foundation.ui.ext.bg

internal interface DialogShared {
    companion object {
        const val WIDTH_FRACTION = 0.8f
        const val EDIT_WIDTH_FRACTION = 0.92f
        val editHPaddingDp @Composable get() = 20.dp
    }
}

internal fun Modifier.dialogBg() =
    composed {
        then(
            Modifier.bg(MaterialShape.large) { surfaceBright },
        )
    }
