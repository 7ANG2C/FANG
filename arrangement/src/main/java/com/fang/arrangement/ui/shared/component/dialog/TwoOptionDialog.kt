package com.fang.arrangement.ui.shared.component.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.shared.component.button.composition.ButtonSets
import com.fang.cosmos.foundation.Invoke

@Composable
internal fun TwoOptionDialog(
    text: String?,
    widthFraction: Float = DialogShared.WIDTH_FRACTION,
    onNegative: Invoke,
    onPositive: Invoke?,
) = Dialog(text = text, widthFraction = widthFraction) {
    ButtonSets(
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 4.dp, bottom = 16.dp),
        onNegative = onNegative,
        onPositive = onPositive,
    )
}
