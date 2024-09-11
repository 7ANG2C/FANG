package com.fang.arrangement.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fang.arrangement.ui.graph.GRAPH_BTM_NAV_SCREEN
import com.fang.arrangement.ui.graph.dsl.currentBtmNavItem
import com.fang.arrangement.ui.graph.dsl.currentIsBtmNavItem
import com.fang.arrangement.ui.graph.graphBtmNavScreen
import com.fang.arrangement.ui.screen.btmnav.BottomNavBar
import com.fang.arrangement.ui.shared.component.Loading
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.dsl.ClearFocusWhenImeClosed
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.SystemBarColor
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ArrangementScreen(
    modifier: Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: ArrangementViewModel = koinViewModel(),
    onBack: Invoke,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val focusManager = LocalFocusManager.current
    val bgColor = MaterialColor.surfaceContainerLowest
    SystemBarColor(status = bgColor)
    Column(
        modifier =
            modifier
                .background(bgColor)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
    ) {
        NavHost(
            navController = navController,
            startDestination = GRAPH_BTM_NAV_SCREEN,
            modifier = Modifier.fillMaxWidth().weight(1f),
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
                    restoreState = true
                }
            }
        }
    }
    Loading(isShow = !viewModel.initialized.stateValue())
    ClearFocusWhenImeClosed()
    BackHandler(onBack = onBack)
}
