package com.fang.arrangement.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fang.arrangement.ui.graph.GRAPH_BTM_NAV_SCREEN
import com.fang.arrangement.ui.graph.GRAPH_LOGIN
import com.fang.arrangement.ui.graph.dsl.currentBtmNavItem
import com.fang.arrangement.ui.graph.dsl.isCurrentBtmNavItem
import com.fang.arrangement.ui.graph.graphBtmNavScreen
import com.fang.arrangement.ui.graph.graphLogin
import com.fang.arrangement.ui.screen.btmnav.BottomNavBar
import com.fang.arrangement.ui.screen.btmnav.BtmNavItem
import com.fang.cosmos.foundation.ui.dsl.ClearFocusWhenImeClosed
import com.fang.cosmos.foundation.ui.dsl.StatusBarColor

@Composable
internal fun ArrangementScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .background(Color(0xfff1f1f1))
            .fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = GRAPH_LOGIN,
            modifier = Modifier.weight(1f),
            route = "ROOT",
        ) {
            graphLogin(navController)
            graphBtmNavScreen()
        }
        if (backStackEntry.isCurrentBtmNavItem) {
            BottomNavBar(
                modifier = Modifier
                    .fillMaxWidth(),
                selected = backStackEntry.currentBtmNavItem,
                navItems = BtmNavItem.all
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
    StatusBarColor(color = Color.Black)
    ClearFocusWhenImeClosed()
}