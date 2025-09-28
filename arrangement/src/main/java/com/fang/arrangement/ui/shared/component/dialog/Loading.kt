package com.fang.arrangement.ui.shared.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.foundation.ui.component.DialogThemedScreen
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.TextVibe
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import com.fang.arrangement.Arrangement as FArrangement

@Composable
internal fun Loading(isShow: Boolean, isSplash: Boolean = false) {
    DialogThemedScreen(isShow = isShow) {
        if (FArrangement.isFancy && isSplash) {
            Box(contentAlignment = Alignment.Center) {
                var start by remember { mutableIntStateOf(0) }
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    scope.launch {
                        delay(0.15.seconds)
                        start = start + 1
                        delay(0.45.seconds)
                        start = start + 1
                        delay(0.15.seconds)
                        start = start + 1
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("勾", style = getStyle(true))
                    Text("～", modifier = Modifier.rotate(90f), style = getStyle(start >= 1))
                    Text("者", style = getStyle(start >= 2))
                    Text("思", style = getStyle(start >= 3))
                }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    repeat(3) { i ->
                        DotLottieAnimation(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .scale(2f),
                            source = DotLottieSource.Asset("firework.lottie"),
                            autoplay = true,
                            loop = true,
                            useFrameInterpolation = false,
                            speed =
                                when (i) {
                                    0 -> 2.5f
                                    1 -> 1.5f
                                    else -> 3f
                                },
                        )
                    }
                }
            }
        } else {
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
}

@Composable
private fun getStyle(alpha: Boolean) =
    TextVibe.normal(
        Color.White.copy(alpha = if (alpha) 0.6f else 0f),
        64,
    )

@Composable
internal fun Loading(workState: WorkState) = Loading(workState.loadingState.stateValue())
