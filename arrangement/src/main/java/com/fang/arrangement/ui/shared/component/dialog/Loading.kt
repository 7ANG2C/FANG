package com.fang.arrangement.ui.shared.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.foundation.ui.component.DialogThemedScreen
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.ext.stateValue

@Composable
internal fun Loading(isShow: Boolean) {
    DialogThemedScreen(isShow = isShow) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(0.42f)
                    .dialogBg()
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val progressColor = MaterialColor.secondary
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = progressColor,
                strokeWidth = 3.2.dp,
                trackColor = progressColor.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
internal fun Loading(workState: WorkState) = Loading(workState.loadingState.stateValue())
