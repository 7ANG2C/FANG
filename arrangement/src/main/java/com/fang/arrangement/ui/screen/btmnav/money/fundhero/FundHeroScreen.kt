package com.fang.arrangement.ui.screen.btmnav.money.fundhero

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fang.arrangement.ui.shared.dsl.ContentText
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun FundHeroScreen(
    modifier: Modifier,
    viewModel: FundHeroViewModel = koinViewModel(),
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        ContentText(text = "開發中，等等我\uD83D\uDE00")
    }
}
