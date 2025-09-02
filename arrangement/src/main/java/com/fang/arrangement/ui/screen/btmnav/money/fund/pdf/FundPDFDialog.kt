package com.fang.arrangement.ui.screen.btmnav.money.fund.pdf

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.fieldrow.Average2Row
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.time.transformer.TimeConverter
import com.fang.cosmos.foundation.time.transformer.TimePattern
import com.fang.cosmos.foundation.ui.ext.stateValue

@Composable
internal fun FundPDFDialog(
    viewModel: FundPDFViewModel,
    onResetPdfId: Invoke,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val param = viewModel.request.stateValue()
        EditDialog(
            isShow = param != null,
            onDelete = null,
            onCancel = viewModel::clearRequest,
            onConfirm =
                if (param?.downloadable == true && param.startMillis != null && param.endMillis != null) {
                    {
                        viewModel.startDownload(
                            startMillis = param.startMillis,
                            endMillis = param.endMillis,
                        )
                    }
                } else {
                    null
                },
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
                        }
                    } ?: viewModel.clearPdf()
                }
            val start = TimeConverter.format(bundle.startMillis, pattern = TimePattern.yyyyMMdd())
            val end = TimeConverter.format(bundle.endMillis, pattern = TimePattern.yyyyMMdd())
            val time = "${start.orEmpty()}_${end.orEmpty()}"
            LaunchedEffect(bundle.pdfDocument) { launcher.launch("公帳代墊$time") }
        }
    }
    with(viewModel.pdfDocument.stateValue()) {
        this?.let { pdfDocument ->
            val contentResolver = LocalContext.current.contentResolver
            val contract = ActivityResultContracts.CreateDocument("application/pdf")
            val launcher =
                rememberLauncherForActivityResult(contract) { uri ->
                    uri?.let {
                        contentResolver.openOutputStream(uri)?.use { out ->
                            viewModel.finishDownloadSpecific(out = out, pdf = pdfDocument)
                            onResetPdfId()
                        }
                    } ?: viewModel.clearPdf()
                }
            LaunchedEffect(pdfDocument) { launcher.launch("公帳代墊") }
        }
    }
}
