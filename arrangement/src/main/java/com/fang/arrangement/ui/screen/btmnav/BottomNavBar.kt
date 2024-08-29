package com.fang.arrangement.ui.screen.btmnav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fang.cosmos.foundation.typealiaz.Action
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.textDp

@Composable
internal fun BottomNavBar(
    modifier: Modifier,
    selected: BtmNavItem?,
    navItems: List<BtmNavItem>,
    onSelect: Action<BtmNavItem>
) {
    Column(Modifier.fillMaxWidth()) {
        HorizontalDivider(color = Color.LightGray)
        Row(
            modifier = modifier
                .height(IntrinsicSize.Max)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .navigationBarsPadding(),
        ) {
            navItems.forEach { navItem ->
                NavItemScreen(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .then(
                            if (navItem == selected) {
                                Modifier
                            } else {
                                Modifier.clickableNoRipple { onSelect(navItem) }
                            }
                        )
                        .padding(vertical = 8.dp),
                    name = navItem.display
                )
            }
        }
    }
}

@Composable
private fun NavItemScreen(
    modifier: Modifier,
    name: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .fillMaxSize(),
            style = TextStyle(
                fontSize = 18.textDp,
                fontWeight = FontWeight.W400,
                textAlign = TextAlign.Center
            )
        )
    }
}
