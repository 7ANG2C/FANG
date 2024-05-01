package com.fang.cosmos.foundation.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fang.cosmos.foundation.typealiaz.Invoke
import com.fang.cosmos.foundation.ui.dsl.ComposableInvoke
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple

/**
 * 客製化 BottomSheet，解決 [ModalBottomSheet] 無法設置底部 padding 問題
 */
@Composable
fun CustomBottomSheet(
    isShow: Boolean,
    overlayColor: Long = 0x80000000,
    dividerColor: Color = Color.Unspecified,
    onDismissRequest: Invoke,
    content: ComposableInvoke,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (isShow) {
                    Modifier.clickableNoRipple(onClick = onDismissRequest)
                } else Modifier
            )
            .background(
                animateColorAsState(
                    targetValue = if (isShow) Color(overlayColor) else Color.Transparent,
                    label = "BottomSheetOverlayColor"
                ).value
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = isShow,
            modifier = Modifier.clickableNoRipple {
                /* intercept */
            },
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
                HorizontalDivider(thickness = 1.dp, color = dividerColor)
            }
        }
    }

}