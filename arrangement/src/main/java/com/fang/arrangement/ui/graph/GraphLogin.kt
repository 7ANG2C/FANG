package com.fang.arrangement.ui.graph

import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.fang.arrangement.ui.graph.dsl.ScreenTransition
import com.fang.arrangement.ui.graph.dsl.composableTransition
import com.fang.arrangement.ui.screen.login.LoginScreen

internal const val GRAPH_LOGIN = "GRAPH_LOGIN"

internal object Login {
    const val ROUTE = "Login"
}

internal fun NavGraphBuilder.graphLogin(navController: NavController) {
    navigation(startDestination = Login.ROUTE, route = GRAPH_LOGIN) {
        composableTransition(
            route = Login.ROUTE,
            enterTransition = { ScreenTransition.fadeIn },
            popExitTransition = { ExitTransition.None }
        ) {
            LoginScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            ) {
                navController.navigate(GRAPH_BTM_NAV_SCREEN) {
                    launchSingleTop = true
                }
            }
        }
    }

}