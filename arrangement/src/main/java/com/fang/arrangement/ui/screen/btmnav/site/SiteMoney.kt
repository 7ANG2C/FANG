package com.fang.arrangement.ui.screen.btmnav.site

internal data class SiteMoney(
    val att: Double?,
    val years: List<Year>,
    val salary: Double?,
) {
    data class YearSummary(
        val name: String,
        val years: List<Year>
    )

    data class Year(
        val year: Int,
        val months: List<Month>
    )

    data class Month(
        val month: Int,
        val days: List<Day>
    )

    data class Day(
        val date: Int,
        val att: Double
    )
}
