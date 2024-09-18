package com.fang.arrangement.ui.screen.btmnav.site

import com.fang.arrangement.definition.Site
import com.fang.arrangement.foundation.Bool

internal data class SiteEditBundle(
    val current: Site?,
    val edit: SiteEdit,
) {
    val isInsert get() = current == null
    val anyDiff
        get() =
            current?.name?.trim() != edit.name?.trim() ||
                current?.address?.trim() != edit.address?.trim() ||
                current?.income != edit.income?.toIntOrNull() ||
                current?.startMillis != edit.startMillis ||
                current?.endMillis != edit.endMillis ||
                current?.archive != Bool(edit.archive)
}
