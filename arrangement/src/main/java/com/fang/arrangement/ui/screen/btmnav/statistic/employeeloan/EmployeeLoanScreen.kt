package com.fang.arrangement.ui.screen.btmnav.statistic.employeeloan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.arrangement.ui.shared.component.ArrangementCard
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.dsl.HighlightText
import com.fang.arrangement.ui.shared.ext.clickRipple
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.stateValue
import com.fang.cosmos.foundation.ui.ext.textDp
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun EmployeeLoanScreen(
    modifier: Modifier,
    viewModel: EmployeeLoanViewModel = koinViewModel(),
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        val loans = viewModel.loans.stateValue()
        loans.sumOf { it.loan ?: 0 }.takeIf { it > 0 }?.let {
            HighlightText(
                text = "總計  $${NumberFormat(it, 0)}",
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            loans.forEach { eLoan ->
                ArrangementCard(modifier = Modifier.fillMaxWidth()) {
                    var isExpand by rememberSaveable { mutableStateOf(false) }
                    val name = eLoan.employee?.name ?: "(已刪除)"
                    val totalLoan = NumberFormat(eLoan.loan ?: 0, 0)
                    ArrText(
                        text = "$name   $$totalLoan",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickRipple { isExpand = !isExpand },
                    ) { ContentText.style.color { onSecondaryContainer } }
                    AnimatedVisibility(visible = isExpand) {
                        Column(Modifier.padding(horizontal = 8.dp)) {
                            eLoan.loans.forEach { ymLoan ->
                                val style =
                                    ContentText.style
                                        .color(ContentText.color)
                                        .copy(
                                            fontSize = 14.8.textDp,
                                            lineHeight = 15.2.textDp,
                                        )
                                val loan = NumberFormat(ymLoan.loan, 0)
                                val pre = "0".takeIf { ymLoan.month < 9 }.orEmpty()
                                ArrText(
                                    text = "${ymLoan.year}/$pre${ymLoan.month + 1}   $$loan",
                                    modifier = Modifier.fillMaxWidth(),
                                ) { style }
                            }
                        }
                    }
                }
                VerticalSpacer(8)
            }
        }
    }
}
