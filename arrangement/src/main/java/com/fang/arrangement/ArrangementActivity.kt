package com.fang.arrangement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.fang.arrangement.ui.screen.ArrangementScreen
import com.fang.arrangement.ui.theme.ArrangementTheme

internal class ArrangementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            setContent {
                ArrangementTheme {
                    ArrangementScreen(
                        modifier = Modifier.fillMaxSize(),
                        onBack = ::finish,
                    )
                }
            }
        }
    }
}
