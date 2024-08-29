package com.fang.arrangement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fang.arrangement.ui.screen.ArrangementScreen

internal class ArrangementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            setContent { ArrangementScreen() }
        }
    }
}
