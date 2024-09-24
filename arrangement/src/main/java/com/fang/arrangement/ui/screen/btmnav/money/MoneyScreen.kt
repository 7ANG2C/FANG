package com.fang.arrangement.ui.screen.btmnav.money

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
import com.fang.arrangement.ui.screen.btmnav.money.fund.FundScreen
import com.fang.arrangement.ui.screen.btmnav.money.loan.LoanScreen
import com.fang.cosmos.foundation.ui.ext.stateValue
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoneyScreen(
    modifier: Modifier,
    viewModel: MoneyViewModel = koinViewModel(),
) {
    Column(modifier) {
        val pagerState =
            rememberPagerState { Money.entries.size }
        val money = viewModel.money.stateValue()
        val index = Money.entries.indexOf(money)
        LaunchedEffect(index) {
            pagerState.scrollToPage(index)
        }
        PrimaryScrollableTabRow(
            selectedTabIndex = index,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
        ) {
            Money.entries.forEach { item ->
                Tab(
                    selected = item == money,
                    onClick = { viewModel.setMoney(item) },
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
            when (Money.entries[it]) {
                Money.LOAN -> LoanScreen(Modifier.fillMaxSize())
                Money.FUND -> FundScreen(Modifier.fillMaxSize())
            }
        }
    }
}
