package com.fang.arrangement.ui.shared.component.dialog

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.foundation.ui.component.CustomImage
import com.fang.cosmos.foundation.ui.component.DialogThemedScreen
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.fang.arrangement.Arrangement as FArrangement

@Composable
internal fun Loading(
    isShow: Boolean,
    isSplash: Boolean = false,
) {
    DialogThemedScreen(isShow = isShow) {
        if (FArrangement.isFancy && isSplash) {
            val animalJump = rememberInfiniteTransition(label = "animalJump")
            val pigOffset by
                animalJump.animateFloat(
                    initialValue = 0f,
                    targetValue = -32f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(300, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    label = "pigJump",
                )
            val rabbitOffset by
                animalJump.animateFloat(
                    initialValue = 0f,
                    targetValue = 32f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(300, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    label = "pigJump",
                )
            val crabOffset by
                animalJump.animateFloat(
                    initialValue = 0f,
                    targetValue = -32f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(300, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                            initialStartOffset = StartOffset(240),
                        ),
                    label = "rabbitJump",
                )
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                CustomImage(R.drawable.arr_png_pig, Modifier.weight(1f).offset(y = pigOffset.dp).scale(0.8f))
                CustomImage(R.drawable.arr_png_rabbit, Modifier.weight(1f).offset(y = rabbitOffset.dp).scale(0.8f))
                CustomImage(R.drawable.arr_png_crab, Modifier.weight(1f).offset(y = crabOffset.dp).scale(0.8f))
                CustomImage(R.drawable.arr_png_libra, Modifier.weight(1f).offset(y = (rabbitOffset + 12).dp).scale(0.8f))
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth(0.5f)
                        .dialogBg()
                        .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val progressColor = MaterialColor.secondary
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = progressColor,
                        strokeWidth = 3.dp,
                        trackColor = progressColor.copy(alpha = 0.55f),
                    )
                    val animalJump = rememberInfiniteTransition(label = "animalJump")
                    val degree by
                        animalJump.animateFloat(
                            initialValue = 45f,
                            targetValue = -45f,
                            animationSpec =
                                infiniteRepeatable(
                                    animation = tween(600, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse,
                                    initialStartOffset = StartOffset(0),
                                ),
                            label = "Degree",
                        )
                    val index by remember {
                        mutableIntStateOf((0..3).random())
                    }
                    CustomImage(
                        listOf(
                            R.drawable.arr_png_crab,
                            R.drawable.arr_png_pig,
                            R.drawable.arr_png_rabbit,
                            R.drawable.arr_png_libra,
                        )[index],
                        Modifier.size(50.dp).rotate(degree),
                    )
                }
            }
        }
    }
}

@Composable
internal fun Loading(workState: WorkState) = Loading(workState.loadingState.stateValue())
