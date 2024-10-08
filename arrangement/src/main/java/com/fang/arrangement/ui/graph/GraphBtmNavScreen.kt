package com.fang.arrangement.ui.graph

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.fang.arrangement.ui.graph.dsl.composableTransition
import com.fang.arrangement.ui.screen.btmnav.BtmNavItem
import com.fang.arrangement.ui.screen.btmnav.attendance.AttendanceScreen
import com.fang.arrangement.ui.screen.btmnav.employee.EmployeeScreen
import com.fang.arrangement.ui.screen.btmnav.money.MoneyScreen
import com.fang.arrangement.ui.screen.btmnav.site.SiteScreen
import com.fang.arrangement.ui.screen.btmnav.statistic.StatisticScreen
import com.fang.cosmos.foundation.ui.dsl.ComposableInvoke

internal const val GRAPH_BTM_NAV_SCREEN = "GRAPH_BTM_NAV_SCREEN"

internal fun NavGraphBuilder.graphBtmNavScreen() {
    navigation(startDestination = BtmNavItem.ATTENDANCE.route, route = GRAPH_BTM_NAV_SCREEN) {
        composableBtmNav(navItem = BtmNavItem.ATTENDANCE) {
            AttendanceScreen(modifier = Modifier.fillMaxSize())
        }
        composableBtmNav(navItem = BtmNavItem.MONEY) {
            MoneyScreen(modifier = Modifier.fillMaxSize())
        }
        composableBtmNav(navItem = BtmNavItem.STATISTIC) {
            StatisticScreen(modifier = Modifier.fillMaxSize())
        }
        composableBtmNav(navItem = BtmNavItem.EMPLOYEE) {
            EmployeeScreen(modifier = Modifier.fillMaxSize())
        }
        composableBtmNav(navItem = BtmNavItem.SITE) {
            SiteScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

private fun NavGraphBuilder.composableBtmNav(
    navItem: BtmNavItem,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: ComposableInvoke,
) {
    composableTransition(
        route = navItem.route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = {
//            ScreenTransition.fadeIn.takeIf { initialState.currentIsBtmNavItem }
            EnterTransition.None
        },
        exitTransition = {
//            ScreenTransition.fadeOut.takeIf { targetState.currentIsBtmNavItem }
            ExitTransition.None
        },
        popEnterTransition = {
//            ScreenTransition.fadeIn.takeIf { initialState.currentIsBtmNavItem }
            EnterTransition.None
        },
        popExitTransition = {
//            ScreenTransition.fadeOut.takeIf { targetState.currentIsBtmNavItem }
            ExitTransition.None
        },
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            color = Color.Transparent,
            content = content,
        )
    }
}
