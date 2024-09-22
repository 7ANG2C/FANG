package com.fang.arrangement.ui.shared.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.cosmos.foundation.ui.component.AlignText
import com.fang.cosmos.foundation.ui.component.DialogThemedScreen
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.screenHeightDp
import com.fang.cosmos.foundation.ui.ext.color

@Composable
internal fun Dialog(
    text: String?,
    widthFraction: Float = DialogShared.WIDTH_FRACTION,
    options: @Composable ColumnScope.(TextStyle) -> Unit,
) = DialogThemedScreen(isShow = text != null) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(widthFraction)
                .dialogBg(),
    ) {
        VerticalSpacer(20)
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .heightIn(min = 0.dp, max = screenHeightDp * 0.7f)
                    .verticalScroll(rememberScrollState()),
        ) {
            var textState by rememberSaveable { mutableStateOf("") }
            if (text != null) textState = text
            AlignText(
                text = textState,
                modifier = Modifier.fillMaxWidth(),
                style = ContentText.style.color(ContentText.color),
            )
        }
        VerticalSpacer(16)
        options(ContentText.style)
    }
}
