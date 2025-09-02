package com.fang.loan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fang.cosmos.foundation.NumberFormat
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.toLocalDateTime
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

val LocalDate.Companion.today
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
    val scrollState = rememberScrollState()
    Loan.loans
        .mapNotNull { loan ->
            start
                .monthsUntil(todayDate)
                .takeIf { it <= loan.remain }
                ?.let { loan.copy(remain = loan.remain - it) }
        }.takeIf { it.isNotEmpty() }
        ?.let { loans ->
            Column(modifier.padding(horizontal = 20.dp)) {
                val amounts = NumberFormat(loans.sumOf { it.remain * it.amount }) ?: "-"
                val remains = NumberFormat(loans.sumOf { it.amount }) ?: "-"
                Text(
                    text = "$amounts / $remains",
                    modifier = Modifier.padding(vertical = 16.dp),
                    fontSize = 24.sp,
                    color = color,
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    loans.forEach { loan ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = loan.name,
                                color = color,
                                modifier =
                                    Modifier
                                        .fillMaxHeight()
                                        .padding(end = 8.dp),
                                fontSize = 16.sp,
                            )
                            Row(
                                Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .horizontalScroll(scrollState),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(contentAlignment = Alignment.CenterEnd) {
                                    Text(
                                        text = "10,520",
                                        color = Color.Transparent,
                                        modifier =
                                            Modifier
                                                .padding(end = 10.dp),
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = NumberFormat(loan.amount) ?: "-",
                                        color = color,
                                        modifier =
                                            Modifier
                                                .padding(end = 10.dp),
                                        fontSize = 16.sp,
                                    )
                                }
                                Box(contentAlignment = Alignment.CenterEnd) {
                                    Text(
                                        text = "10",
                                        color = Color.Transparent,
                                        modifier =
                                            Modifier
                                                .padding(end = 10.dp),
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = loan.remain.toString(),
                                        color = color,
                                        modifier =
                                            Modifier
                                                .padding(end = 10.dp),
                                        fontSize = 16.sp,
                                    )
                                }
                                Box(contentAlignment = Alignment.CenterEnd) {
                                    Text(
                                        text = "100,000",
                                        color = Color.Transparent,
                                        modifier =
                                            Modifier
                                                .padding(end = 10.dp),
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = NumberFormat(loan.amount * loan.remain) ?: "-",
                                        color = color,
                                        modifier =
                                            Modifier
                                                .padding(end = 10.dp),
                                        fontSize = 16.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } ?: Text(text = "還清", color = color)
}
