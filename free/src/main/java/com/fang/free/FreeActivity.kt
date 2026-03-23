package com.fang.free

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.fang.cosmos.definition.externalcoroutinescope.createExternalCoroutineScope
import com.fang.cosmos.foundation.logD
import com.fang.free.tick.GetTickFlowUseCase
import com.fang.free.ui.theme.FreeTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class FreeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        setContent {
            FreeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
        lifecycleScope.launch {
            GetTickFlowUseCase(createExternalCoroutineScope())()
                .collectLatest { ticks ->
                    ticks.takeLast(1).forEach {
                        logD("DATA_", it)
                    }
                }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}
