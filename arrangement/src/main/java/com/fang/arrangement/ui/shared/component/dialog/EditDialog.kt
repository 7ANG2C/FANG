package com.fang.arrangement.ui.shared.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.shared.component.button.composition.ButtonSets
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.component.DialogThemedScreen
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.screenHeightDp

@Composable
internal fun EditDialog(
    isShow: Boolean,
    onDelete: Invoke?,
    onCancel: Invoke,
    onConfirm: Invoke?,
    content: @Composable ColumnScope.() -> Unit,
) {
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    DialogThemedScreen(isShow = isShow) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(DialogShared.EDIT_WIDTH_FRACTION)
                    .heightIn(min = 0.dp, max = screenHeightDp * 0.84f)
                    .dialogBg(),
        ) {
            VerticalSpacer(20)
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(horizontal = DialogShared.editHPaddingDp)
                        .weight(1f, false)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content,
            )
            val focusManager = LocalFocusManager.current
            ButtonSets(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                onDelete =
                    onDelete?.let {
                        {
                            showDeleteDialog = true
                            focusManager.clearFocus()
                        }
                    },
                onNegative = {
                    focusManager.clearFocus()
                    onCancel.invoke()
                },
                onPositive =
                    onConfirm?.let {
                        {
                            focusManager.clearFocus()
                            it()
                        }
                    },
            )
        }
    }
    TwoOptionDialog(
        text = "是否確定刪除？".takeIf { showDeleteDialog },
        widthFraction = 0.8f,
        onNegative = { showDeleteDialog = false },
        onPositive = {
            showDeleteDialog = false
            onDelete?.invoke()
        },
    )
}
