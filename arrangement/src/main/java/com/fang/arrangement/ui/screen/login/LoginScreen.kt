package com.fang.arrangement.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fang.cosmos.foundation.typealiaz.Invoke
import com.fang.cosmos.foundation.ui.ext.textDp

@Composable
internal fun LoginScreen(
    modifier: Modifier,
    login: Invoke
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = login) {
            Text(text = "登入", fontSize = 20.textDp)
        }
    }
}