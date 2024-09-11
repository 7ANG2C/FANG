package com.fang.cosmos.foundation.ui.component.pullrefresh

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.dsl.ComposableInvoke
import com.fang.cosmos.foundation.ui.dsl.screenHeight
import kotlin.math.min

/**
 * @param onRefresh do your async task like call api...etc.
 * @param height refresh content height
 * @param refreshContent refresh content like [CircularProgressIndicator] e.t.c.
 * @param content should be scrollable
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultPullRefresh(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    height: Dp = (screenHeight * 0.075).dp,
    onRefresh: Invoke,
    refreshContent: ComposableInvoke,
    content: ComposableInvoke,
) {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        val pullRefreshState =
            rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = onRefresh,
            )

        val isPulling = pullRefreshState.progress > 0
        val loadingHeightDp by animateDpAsState(
            targetValue =
                when {
                    isPulling || isRefreshing -> height
                    else -> 0.dp
                },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "PullRefresh",
        )
        val scale by animateFloatAsState(
            targetValue = if (isPulling) .95f else 1f,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                ),
            label = "PullRefreshScale",
        )

        Box(modifier = modifier.pullRefresh(pullRefreshState, !isRefreshing)) {
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(height)
                        .scale(min(loadingHeightDp / height, 1f)),
                color = Color.Transparent,
                content = refreshContent,
            )
            Surface(
                modifier =
                    Modifier
                        .scale(scale)
                        .offset(x = 0.dp, y = loadingHeightDp)
                        .fillMaxSize(),
                color = Color.Transparent,
                content = content,
            )
        }
    }
}
