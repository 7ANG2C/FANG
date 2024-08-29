package com.fang.arrangement.ui.screen.btmnav

internal enum class BtmNavItem(
    val display: String,
    val route: String,
) {
    SCHEDULE(
        display = "記錄",
        route = "ScHe_DuLe",
    ),
    STATISTIC(
        display = "統計",
        route = "StAt_IsTiC",
    ),
    LOAN(
        display = "工地",
        route = "Lo_aN",
    ),
    SITE(
        display = "工地",
        route = "Si_Te",
    )
    ;

    companion object {
        val all by lazy { entries }
    }
}