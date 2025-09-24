package com.fang.arrangement.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fang.arrangement.ui.graph.GRAPH_BTM_NAV_SCREEN
import com.fang.arrangement.ui.graph.dsl.currentBtmNavItem
import com.fang.arrangement.ui.graph.dsl.currentIsBtmNavItem
import com.fang.arrangement.ui.graph.graphBtmNavScreen
import com.fang.arrangement.ui.screen.btmnav.BottomNavBar
import com.fang.arrangement.ui.screen.btmnav.BtmNavItem
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.dsl.ClearFocusWhenImeClosed
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.SystemBarColor
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.fang.cosmos.foundation.ui.ext.tapClearFocus
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ArrangementScreen(
    modifier: Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: ArrangementViewModel = koinViewModel(),
    onBack: Invoke,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val bgColor = MaterialColor.surfaceContainerLowest
    SystemBarColor(status = bgColor)
    val initialized = viewModel.initialized.stateValue()
    Column(
        modifier =
            modifier
                .background(bgColor)
                .tapClearFocus(),
    ) {
        NavHost(
            navController = navController,
            startDestination = GRAPH_BTM_NAV_SCREEN,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .alpha(animateFloatAsState(if (initialized) 1f else 0f).value),
            route = "ROOT",
        ) {
            graphBtmNavScreen()
        }
        if (backStackEntry.currentIsBtmNavItem) {
            BottomNavBar(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                selected = backStackEntry.currentBtmNavItem,
            ) { page ->
                navController.navigate(page.route) {
                    popUpTo(GRAPH_BTM_NAV_SCREEN) {
                        inclusive = false
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = page != BtmNavItem.STATISTIC
                }
            }
        }
    }
    Loading(isShow = !initialized)
    ClearFocusWhenImeClosed()
    BackHandler(onBack = onBack)
}
