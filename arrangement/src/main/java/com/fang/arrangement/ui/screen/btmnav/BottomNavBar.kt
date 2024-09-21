package com.fang.arrangement.ui.screen.btmnav

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fang.arrangement.R
import com.fang.cosmos.foundation.Action
import com.fang.cosmos.foundation.ui.component.AlignText
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.dsl.SystemBarColor
import com.fang.cosmos.foundation.ui.dsl.animateColor

@Composable
internal fun BottomNavBar(
    modifier: Modifier,
    selected: BtmNavItem?,
    onSelect: Action<BtmNavItem>,
) {
    val bgColor = MaterialColor.surfaceContainerLow
    NavigationBar(modifier, containerColor = bgColor) {
        BtmNavItem.entries.forEach { nav ->
            val select = nav == selected
            val contentColor =
                animateColor(label = "BtmNavItem") {
                    primary.copy(alpha = if (select) 1f else 0.5f)
                }
            NavigationBarItem(
                selected = select,
                onClick = { if (!select) onSelect(nav) },
                icon = {
                    CustomIcon(
                        drawableResId =
                            when (nav) {
                                BtmNavItem.ATTENDANCE -> R.drawable.arr_r24_sunny
                                BtmNavItem.LOAN -> R.drawable.arr_r24_attach_money
                                BtmNavItem.STATISTIC -> R.drawable.arr_r24_insert_chart
                                BtmNavItem.EMPLOYEE -> R.drawable.arr_r24_person
                                BtmNavItem.SITE -> R.drawable.arr_r24_location_on
                                BtmNavItem.C -> R.drawable.arr_r24_location_on
                            },
                        tint = contentColor,
                    )
                },
                modifier = Modifier.weight(1f),
                label = {
                    AlignText(
                        text = nav.display,
                        style = MaterialTypography.labelSmall,
                    )
                },
                colors =
                    NavigationBarItemDefaults.colors().copy(
                        selectedTextColor = contentColor,
                        unselectedTextColor = contentColor,
                    ),
            )
        }
    }
    SystemBarColor(navigation = bgColor)
}
