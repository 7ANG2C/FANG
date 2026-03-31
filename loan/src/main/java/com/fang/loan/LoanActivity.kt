package com.fang.loan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import kotlinx.coroutines.delay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

internal class LoanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoanContent(
                modifier =
                    Modifier
                        .background(Color.Black)
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
            )
        }
    }
}

private val LocalDate.Companion.today
    get() =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
val color = Color(0xFFCCCCCC)

@Composable
private fun LoanContent(modifier: Modifier) {
    val start = LocalDate.parse("2025-05-05")
    var todayDate by remember {
        mutableStateOf(LocalDate.today)
    }
    LaunchedEffect(Unit) {
        delay(1.seconds)
        todayDate = LocalDate.today
    }
    val loansState =
        remember {
            mutableStateOf(Loan.loans)
        }
    loansState.value
        .mapNotNull { loan ->
            start
                .monthsUntil(todayDate)
                .takeIf { it <= loan.remain }
                ?.let { loan.copy(remain = loan.remain - it) }
        }.takeIf { it.isNotEmpty() }
        ?.let { loans ->
            Column(modifier.padding(horizontal = 20.dp)) {
                val amounts = NumberFormat(loans.sumOf { it.remainAmount }) ?: "-"
                val remains = NumberFormat(loans.sumOf { it.amount }) ?: "-"
                Text(
                    text = "$amounts / $remains",
                    modifier = Modifier.padding(vertical = 16.dp),
                    fontSize = 24.sp,
                    color = color,
                )
                loans.forEach { loan ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        with(loansState) {
                            LoanText(loan.name)
                            LoanText(NumberFormat(loan.amount), "00,000") { amount }
                            LoanText(loan.remain, "000") { remain }
                            LoanText(NumberFormat(loan.remainAmount), "000,000") { remainAmount }
                            LoanText(
                                with(
                                    todayDate.plus(
                                        loan.remain -
                                            if (todayDate.day >= 6) {
                                                0
                                            } else {
                                                1
                                            },
                                        DateTimeUnit.MONTH,
                                    ),
                                ) {
                                    "$year-${month.number.toString().padStart(2, '0')}"
                                },
                            ) { remain }
                            LoanText(loan.day, "000") { day }
                        }
                    }
                }
            }
        } ?: Text(text = "還清", color = color)
}

@Composable
private fun MutableState<List<Loan>>.LoanText(
    text: Any?,
    holder: String? = null,
    trans: (Loan.() -> Int)? = null,
) = Box(contentAlignment = Alignment.CenterEnd) {
    val content = text?.toString() ?: "-"
    Text(
        text = content,
        color = color,
        fontSize = 16.sp,
    )
    Text(
        text = holder ?: content,
        modifier =
            Modifier.clickableNoRipple {
                value = trans?.let {
                    val asc = value.sortedBy { trans(it) }
                    if (asc == value) {
                        Loan.loans.sortedByDescending { trans(it) }
                    } else {
                        asc
                    }
                } ?: Loan.loans
            },
        color = Color.Transparent,
        fontSize = 16.sp,
    )
}
