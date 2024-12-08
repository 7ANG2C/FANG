package com.fang.arrangement.ui.shared.dsl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fang.arrangement.definition.Boss
import com.fang.arrangement.ui.shared.component.chip.DeletedTag

@Composable
internal fun BossTag(
    boss: Boss?,
    modifier: Modifier = Modifier,
) {
    when {
        boss == null || boss.isDelete -> DeletedTag(modifier)
        else -> {}
    }
}

internal fun bossState(employee: Boss?) =
    when {
        employee == null || employee.isDelete -> true
        else -> false
    }
