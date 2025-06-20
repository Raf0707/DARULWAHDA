package ru.darulwahda.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color

/* ---------- состояния UI ---------- */
enum class UiState { Loading, Success, Error }

@Composable
fun App(startUrl: String = "https://darulwahda.ru/") {

    var uiState   by remember { mutableStateOf(UiState.Loading) }
    var canGoBack by remember { mutableStateOf(false) }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        enabled  = canGoBack,
                        onClick  = { WebViewDispatcher.tryGoBack?.invoke() },
                        icon     = { Icon(Icons.Default.ArrowBack, null) },
                        label    = { Text("Назад") }
                    )
                }
            }
        ) { paddings ->

            /* WebView – всегда в дереве, поверх него Crossfade-оверлей */
            Box(Modifier
                .fillMaxSize()
                .padding(paddings)) {

                WebViewContainer(                       // expect/actual
                    url = startUrl,
                    modifier = Modifier.fillMaxSize(),
                    onPageState = { state, back ->
                        uiState   = state
                        canGoBack = back
                    }
                )

                Crossfade(uiState, modifier = Modifier.align(Alignment.Center)) {
                    when (it) {
                        UiState.Loading -> LoadingScreen()
                        UiState.Error   -> ErrorScreen {
                            uiState = UiState.Loading
                            WebViewDispatcher.tryReload?.invoke()
                        }
                        UiState.Success -> {}           // ничего поверх
                    }
                }
            }
        }
    }
}

/* ---------- оверлейные экраны ---------- */

@Composable
private fun LoadingScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val t = rememberInfiniteTransition(label = "stroke")
        val stroke by t.animateFloat(
            2f, 6f,
            animationSpec = infiniteRepeatable(
                tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse
            ), label = "strokeWidth"
        )
        CircularProgressIndicator(strokeWidth = stroke.dp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "загрузка…",
            color = Color.White
        )
    }
}

@Composable
private fun ErrorScreen(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Warning, null,
            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(8.dp))
        Text("Проверьте подключение к интернету")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Повторить") }
    }
}

/* ---------- диспетчер+expect ---------- */

object WebViewDispatcher {
    var tryGoBack : (() -> Unit)? = null
    var tryReload : (() -> Unit)? = null
}

@Composable
expect fun WebViewContainer(
    url: String,
    modifier: Modifier = Modifier,
    onPageState: (UiState, canGoBack: Boolean) -> Unit    // UiState приходит отсюда
)
