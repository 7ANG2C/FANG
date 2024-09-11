package com.fang.arrangement.ui.graph

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.fang.arrangement.ui.graph.dsl.ScreenTransition
import com.fang.arrangement.ui.graph.dsl.composableTransition
import com.fang.arrangement.ui.graph.dsl.currentIsBtmNavItem
import com.fang.arrangement.ui.screen.btmnav.BtmNavItem
import com.fang.arrangement.ui.screen.btmnav.BtmNavScreen
import com.fang.arrangement.ui.screen.btmnav.attendance.AttendanceScreen
import com.fang.arrangement.ui.screen.btmnav.employee.EmployeeScreen
import com.fang.arrangement.ui.screen.btmnav.loan.LoanScreen
import com.fang.arrangement.ui.screen.btmnav.site.SiteScreen
import com.fang.arrangement.ui.screen.btmnav.statistic.StatisticScreen
import com.fang.arrangement.ui.theme.darkScheme
import com.fang.arrangement.ui.theme.lightScheme
import com.fang.cosmos.foundation.ui.component.ColorSchemeScreen
import com.fang.cosmos.foundation.ui.dsl.ComposableInvoke
import com.fang.cosmos.foundation.ui.ext.bg

internal const val GRAPH_BTM_NAV_SCREEN = "GRAPH_BTM_NAV_SCREEN"

internal fun NavGraphBuilder.graphBtmNavScreen() {
    navigation(startDestination = BtmNavItem.ATTENDANCE.route, route = GRAPH_BTM_NAV_SCREEN) {
        composableBtmNav(navItem = BtmNavItem.ATTENDANCE) {
            AttendanceScreen(modifier = Modifier.fillMaxSize())
        }
        composableBtmNav(navItem = BtmNavItem.LOAN) {
            LoanScreen(modifier = Modifier.fillMaxSize())
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
        composableBtmNav(navItem = BtmNavItem.MEMO) {
            ColorSchemeScreen(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                dark = darkScheme,
                light = lightScheme,
            )
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
            ScreenTransition.fadeIn.takeIf { initialState.currentIsBtmNavItem }
        },
        exitTransition = {
            ScreenTransition.fadeOut.takeIf { targetState.currentIsBtmNavItem }
        },
        popEnterTransition = {
            ScreenTransition.fadeIn.takeIf { initialState.currentIsBtmNavItem }
        },
        popExitTransition = {
            ScreenTransition.fadeOut.takeIf { targetState.currentIsBtmNavItem }
        },
    ) {
        BtmNavScreen(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .bg { surfaceContainerLowest },
            content = content,
        )
    }
}
