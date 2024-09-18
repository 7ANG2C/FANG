package com.fang.arrangement.ui.shared.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.textDp

@Composable
internal fun AddItem2(
    modifier: Modifier,
    tint: Color = MaterialColor.primary,
    onClick: Invoke,
) {
    Column(modifier.clickableNoRipple(onClick = onClick)) {
        Text(
            text = "1",
            color = Color.Transparent,
            fontSize = 14.textDp,
            fontWeight = FontWeight.W400,
        )
        Box(contentAlignment = Alignment.Center) {
            Text(
                "1",
                color = Color.Transparent,
                fontSize = 16.textDp,
                fontWeight = FontWeight.W400,
            )

            CustomIcon(
                drawableResId = R.drawable.arr_r24_add,
                tint = tint,
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Composable
internal fun RemoveItem(
    modifier: Modifier,
    tint: Color = MaterialColor.error,
    onClick: Invoke,
) {
    Column(modifier.clickableNoRipple(onClick = onClick)) {
        Text(
            text = "1",
            color = Color.Transparent,
            fontSize = 14.textDp,
            fontWeight = FontWeight.W400,
        )
        Box(contentAlignment = Alignment.Center) {
            Text(
                "1",
                color = Color.Transparent,
                fontSize = 16.textDp,
                fontWeight = FontWeight.W400,
            )
            CustomIcon(
                drawableResId = R.drawable.arr_r24_remove,
                tint = tint,
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}
