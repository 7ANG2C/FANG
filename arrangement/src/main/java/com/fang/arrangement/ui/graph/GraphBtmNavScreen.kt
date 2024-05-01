package com.fang.arrangement.ui.graph

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.fang.arrangement.ui.graph.dsl.ScreenTransition
import com.fang.arrangement.ui.graph.dsl.composableTransition
import com.fang.arrangement.ui.graph.dsl.isCurrentBtmNavItem
import com.fang.arrangement.ui.screen.btmnav.BtmNavItem
import com.fang.arrangement.ui.screen.btmnav.BtmNavScreen
import com.fang.arrangement.ui.screen.btmnav.building.SiteScreen
import com.fang.arrangement.ui.screen.btmnav.loan.LoanScreen
import com.fang.arrangement.ui.screen.btmnav.schedule.ScheduleScreen
import com.fang.arrangement.ui.screen.btmnav.statistic.StatisticScreen
import com.fang.cosmos.foundation.ui.dsl.ComposableInvoke

internal const val GRAPH_BTM_NAV_SCREEN = "GRAPH_BTM_NAV_SCREEN"
internal fun NavGraphBuilder.graphBtmNavScreen() {
    navigation(startDestination = BtmNavItem.SCHEDULE.route, route = GRAPH_BTM_NAV_SCREEN) {
        composableBtmNav(navItem = BtmNavItem.SCHEDULE) {
            ScheduleScreen(modifier = Modifier.fillMaxSize())
        }
        composableBtmNav(navItem = BtmNavItem.STATISTIC) {
            StatisticScreen(modifier = Modifier.fillMaxSize())
        }
        composableBtmNav(navItem = BtmNavItem.LOAN) {
            LoanScreen(modifier = Modifier.fillMaxSize())
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
    content: ComposableInvoke
) {
    composableTransition(
        route = navItem.route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = {
            ScreenTransition.fadeIn.takeIf { initialState.isCurrentBtmNavItem }
            EnterTransition.None
        },
        exitTransition = {
            ScreenTransition.fadeOut.takeIf { targetState.isCurrentBtmNavItem }
            ExitTransition.None
        },
        popEnterTransition = {
            ScreenTransition.fadeIn.takeIf { initialState.isCurrentBtmNavItem }
            EnterTransition.None
        },
        popExitTransition = {
            ScreenTransition.fadeOut.takeIf { targetState.isCurrentBtmNavItem }
            ExitTransition.None
        }
    ) {
        BtmNavScreen(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            content = content
        )
    }
}
