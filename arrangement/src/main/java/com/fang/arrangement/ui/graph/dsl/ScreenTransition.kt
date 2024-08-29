package com.fang.arrangement.ui.graph.dsl

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

internal object ScreenTransition {
    private val fadeAnimSpec = tween<Float>(250)
    private const val ALPHA = 0.8f
    val fadeIn = fadeIn(animationSpec = fadeAnimSpec, initialAlpha = ALPHA)
    val fadeOut = fadeOut(animationSpec = fadeAnimSpec, targetAlpha = ALPHA)
}

private val slideAnimSpec: FiniteAnimationSpec<IntOffset> = tween(300)
private val slideLeft = AnimatedContentTransitionScope.SlideDirection.Left
private val slideRight = AnimatedContentTransitionScope.SlideDirection.Right
private val AnimatedContentTransitionScope<NavBackStackEntry>.defaultEnterTransition
    get() = slideIntoContainer(
        towards = slideLeft,
        animationSpec = slideAnimSpec
    ) + ScreenTransition.fadeIn
private val AnimatedContentTransitionScope<NavBackStackEntry>.defaultExitTransition
    get() = slideOutOfContainer(
        towards = slideLeft,
        animationSpec = slideAnimSpec
    ) + ScreenTransition.fadeOut
private val AnimatedContentTransitionScope<NavBackStackEntry>.defaultPopEnterTransition
    get() = slideIntoContainer(
        towards = slideRight,
        animationSpec = slideAnimSpec
    ) + ScreenTransition.fadeIn
private val AnimatedContentTransitionScope<NavBackStackEntry>.defaultPopExitTransition
    get() = slideOutOfContainer(
        towards = slideRight,
        animationSpec = slideAnimSpec
    ) + ScreenTransition.fadeOut

/**
 * A to B:
 * Screen A: exitTransition
 * Screen B: enterTransition
 * B back to A:
 * Screen A: popEnterTransition
 * Screen B: popExitTransition
 */
internal fun NavGraphBuilder.composableTransition(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        enterTransition,
    popExitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = enterTransition?.let {
            { it() ?: defaultEnterTransition }
        } ?: { defaultEnterTransition },
        exitTransition = exitTransition?.let {
            { it() ?: defaultExitTransition }
        } ?: { defaultExitTransition },
        popEnterTransition = popEnterTransition?.let {
            { it() ?: defaultPopEnterTransition }
        } ?: { defaultPopEnterTransition },
        popExitTransition = popExitTransition?.let {
            { it() ?: defaultPopExitTransition }
        } ?: { defaultPopExitTransition },
        content = content
    )
}