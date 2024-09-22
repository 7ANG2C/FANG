package com.fang.arrangement.ui.shared.component.dialog

import androidx.compose.runtime.Composable
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.foundation.ui.ext.stateValue

@Composable
internal fun ErrorDialog(workState: WorkState) {
    SingleOptionDialog(
        text = workState.throwableState.stateValue()?.toString(),
        onDismiss = workState::noThrowable,
    )
}
