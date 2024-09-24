package com.fang.arrangement.ui.screen.btmnav.statistic.pdf

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.shared.component.DateSelector
import com.fang.arrangement.ui.shared.component.dialog.EditDialog
import com.fang.arrangement.ui.shared.component.dialog.Loading
import com.fang.arrangement.ui.shared.component.fieldrow.Average2Row
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.cosmos.foundation.time.transformer.TimeConverter
import com.fang.cosmos.foundation.time.transformer.TimePattern
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.stateValue

@Composable
internal fun PDFDialog(viewModel: PDFViewModel) {
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
                            includeRemark = param.includeRemark,
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
                        param?.endMillis?.let { millis <= it } ?: true
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
                        param?.startMillis?.let { millis >= it } ?: true
                    },
                    onConfirm = viewModel::editEndMillis,
                )
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickableNoRipple(onClick = viewModel::toggleIncludeRemark)
                        .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ContentText(text = "是否包含出勤備註")
                Spacer(modifier = Modifier.weight(1f))
                Checkbox(checked = param?.includeRemark == true, onCheckedChange = null)
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
                    }
                }
            val start = TimeConverter.format(bundle.startMillis, pattern = TimePattern.yyyyMMdd())
            val end = TimeConverter.format(bundle.endMillis, pattern = TimePattern.yyyyMMdd())
            val time = "${start.orEmpty()}_${end.orEmpty()}"
            LaunchedEffect(bundle.pdfDocument) { launcher.launch("工表$time") }
        }
    }
}
