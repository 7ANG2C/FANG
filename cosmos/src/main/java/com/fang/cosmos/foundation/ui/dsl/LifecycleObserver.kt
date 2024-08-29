package com.fang.cosmos.foundation.ui.dsl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.fang.cosmos.foundation.typealiaz.Action
import com.fang.cosmos.foundation.typealiaz.Invoke

/**
 * composable lifecycle observer
 */
@Composable
fun LifecycleObserver(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onCreate: Invoke = {},
    onStart: Invoke = {},
    onResume: Invoke = {},
    onPause: Invoke = {},
    onStop: Invoke = {},
    onDestroy: Invoke = {},
    onDispose: Invoke = {},
    onAny: Action<LifecycleEvent> = {},
) {
    val currentOnCreate by rememberUpdatedState(onCreate)
    val currentOnStart by rememberUpdatedState(onStart)
    val currentOnResume by rememberUpdatedState(onResume)
    val currentOnPause by rememberUpdatedState(onPause)
    val currentOnStop by rememberUpdatedState(onStop)
    val currentOnDestroy by rememberUpdatedState(onDestroy)
    val currentOnAny by rememberUpdatedState(onAny)
    val currentOnDispose by rememberUpdatedState(onDispose)
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        currentOnCreate()
                        currentOnAny(LifecycleEvent.ON_CREATE)
                    }
                    Lifecycle.Event.ON_START -> {
                        currentOnStart()
                        currentOnAny(LifecycleEvent.ON_START)
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        currentOnResume()
                        currentOnAny(LifecycleEvent.ON_RESUME)
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        currentOnPause()
                        currentOnAny(LifecycleEvent.ON_PAUSE)
                    }
                    Lifecycle.Event.ON_STOP -> {
                        currentOnStop()
                        currentOnAny(LifecycleEvent.ON_STOP)
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        currentOnDestroy()
                        currentOnAny(LifecycleEvent.ON_DESTROY)
                    }
                    else -> {}
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            currentOnDispose()
            currentOnAny(LifecycleEvent.ON_DISPOSE)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

enum class LifecycleEvent {
    ON_CREATE,
    ON_START,
    ON_RESUME,
    ON_PAUSE,
    ON_STOP,
    ON_DESTROY,
    ON_DISPOSE,
}
