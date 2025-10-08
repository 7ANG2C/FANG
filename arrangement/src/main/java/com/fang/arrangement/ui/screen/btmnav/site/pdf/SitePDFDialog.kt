package com.fang.arrangement.ui.screen.btmnav.site.pdf

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.stateValue

@Composable
internal fun SitePDFDialog(viewModel: SitePDFViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        val param = viewModel.request.stateValue()
        EditDialog(
            isShow = param != null,
            onDelete = null,
            onCancel = viewModel::clearRequest,
            onConfirm =
                param?.let {
                    { viewModel.startDownload(it) }
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CheckRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickableNoRipple(onClick = viewModel::toggleShowSiteName),
                    text = "顯示工地名稱",
                    check = param?.showSiteName,
                )
                CheckRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickableNoRipple(onClick = viewModel::toggleShowStartEnd),
                    text = "顯示起訖日",
                    check = param?.showStartEnd,
                )
                CheckRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickableNoRipple(onClick = viewModel::toggleShowTotal),
                    text = "顯示總工數",
                    check = param?.showTotal,
                )
                CheckRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickableNoRipple(onClick = viewModel::toggleShowEmployeeSummary),
                    text = "顯示員工總工數",
                    check = param?.showEmployeeSummary,
                )
                CheckRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickableNoRipple(onClick = viewModel::toggleShowDailyEmployee),
                    text = "顯示出勤員工",
                    check = param?.showDailyEmployee,
                )
                CheckRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickableNoRipple(onClick = viewModel::toggleIncludeRemark),
                    text = "是否包含出勤備註",
                    check = param?.includeRemark,
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
            LaunchedEffect(bundle.pdfDocument) { launcher.launch("${bundle.name.orEmpty()}工表") }
        }
    }
}

@Composable
private fun CheckRow(
    modifier: Modifier,
    text: String,
    check: Boolean?,
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
) {
    ContentText(text = text)
    Spacer(modifier = Modifier.weight(1f))
    Checkbox(checked = check == true, onCheckedChange = null)
}
