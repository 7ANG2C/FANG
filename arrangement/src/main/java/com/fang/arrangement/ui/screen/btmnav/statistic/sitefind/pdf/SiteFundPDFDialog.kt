package com.fang.arrangement.ui.screen.btmnav.statistic.sitefind.pdf

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.fieldrow.Average2Row
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.takeIfNotBlank
import com.fang.cosmos.foundation.time.calendar.ChineseDayOfWeek
import com.fang.cosmos.foundation.time.calendar.dayOfMonth
import com.fang.cosmos.foundation.time.calendar.month
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer
import com.fang.cosmos.foundation.ui.ext.stateValue

@Composable
internal fun SiteFundPDFDialog(
    viewModel: SiteFundPDFViewModel,
    onResetPdfId: Invoke,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val param = viewModel.request.stateValue()
        EditDialog(
            isShow = param != null,
            onDelete = null,
            onCancel = viewModel::clearRequest,
            onConfirm =
                param?.let {
                    { viewModel.startDownload(it) }
                }
        ) {
            Average2Row(Modifier.fillMaxWidth(), {
                DateSelector(
                    modifier = Modifier.fillMaxWidth(),
                    titleText = "起日",
                    onClear = { viewModel.editStartMillis(null) },
                    original = param?.startMillis,
                    isSelectableMillis = { millis ->
                        param?.endMillis?.let { millis <= it } != false
                    },
                    onConfirm = viewModel::editStartMillis,
                )
            }) {
                DateSelector(
                    modifier = Modifier.fillMaxWidth(),
                    titleText = "迄日",
                    onClear = { viewModel.editEndMillis(null) },
                    original = param?.endMillis,
                    isSelectableMillis = { millis ->
                        param?.startMillis?.let { millis >= it } != false
                    },
                    onConfirm = viewModel::editEndMillis,
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val all = param?.ymFunds?.filter { it.selected }
                if (all != null) {
                    ContentText("排除項")
                    all.forEach {
                        Row {
                            val c = today(it.millis)
                            val month = c.month
                            val day = c.dayOfMonth
                            val pre = "0".takeIf { month < 9 }.orEmpty()
                            val dayPre = "0".takeIf { day < 10 }.orEmpty()
                            ContentText("$pre${month + 1}-$dayPre${day}")
                            HorizontalSpacer(1.2f)
                            ContentText("(${ChineseDayOfWeek(c.timeInMillis)})")
                            HorizontalSpacer(10)
                            ContentText("$${NumberFormat(it.fund, 0)}")
                            it.remark.takeIfNotBlank?.let { text ->
                                HorizontalSpacer(10)
                                ContentText("$text ")
                            }
                        }
                    }
                }
            }
        }
        Loading(viewModel)
    }
    with(viewModel.pdfBundle.stateValue()) {
        this?.let { bundle ->
            val contentResolver = LocalContext.current.contentResolver
            val contract = ActivityResultContracts.CreateDocument("application/pdf")
            val launcher =
                rememberLauncherForActivityResult(contract) { uri ->
                    uri?.let {
                        contentResolver.openOutputStream(uri)?.use { out ->
                            viewModel.finishDownload(out = out, pdf = bundle.pdfDocument)
                            onResetPdfId()
                        }
                    } ?: viewModel.clearPdf()
                }
            LaunchedEffect(bundle.pdfDocument) { launcher.launch("${bundle.name}_公帳代墊") }
        }
    }
}
