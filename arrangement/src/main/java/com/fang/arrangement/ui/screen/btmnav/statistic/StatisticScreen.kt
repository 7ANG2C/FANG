package com.fang.arrangement.ui.screen.btmnav.statistic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.screen.btmnav.statistic.employeeattendance.EmployeeAttendanceScreen
import com.fang.arrangement.ui.screen.btmnav.statistic.employeeloan.EmployeeLoanScreen
import com.fang.arrangement.ui.screen.btmnav.statistic.salary.SalaryScreen
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StatisticScreen(
    modifier: Modifier,
    viewModel: StatisticViewModel = koinViewModel(),
) {
    Column(modifier) {
        val pagerState =
            rememberPagerState { Statistic.entries.size }
        val statistic = viewModel.statistic.stateValue()
        val index = Statistic.entries.indexOf(statistic)
        LaunchedEffect(index) {
            pagerState.scrollToPage(index)
        }
        PrimaryScrollableTabRow(
            selectedTabIndex = index,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
        ) {
            Statistic.entries.forEach { item ->
                Tab(
                    selected = item == statistic,
                    onClick = { viewModel.setStatistic(item) },
                    text = { Text(text = item.display) },
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            userScrollEnabled = false,
        ) {
            when (Statistic.entries[it]) {
                Statistic.SALARY -> SalaryScreen(Modifier.fillMaxSize())
                Statistic.EMPLOYEE -> EmployeeAttendanceScreen(Modifier.fillMaxSize())
                Statistic.SITE_FUND -> EmployeeAttendanceScreen(Modifier.fillMaxSize())
                Statistic.LOAN -> EmployeeLoanScreen(Modifier.fillMaxSize())
            }
        }
    }
}
