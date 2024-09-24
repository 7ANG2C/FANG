package com.fang.arrangement.ui.screen.btmnav

internal enum class BtmNavItem(val route: String, val display: String) {
    ATTENDANCE(route = "Att_en_DanCe", display = "出勤"),
    MONEY(route = "Mo_N_ey", display = "錢錢"),
    STATISTIC(route = "StAt_IsTiC", display = "統計"),
    EMPLOYEE(route = "Em_PloYee", display = "員工"),
    SITE(route = "Si_Te", display = "工地"),
}
