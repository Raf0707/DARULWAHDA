package ru.darulwahda.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.webkit.*
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun WebViewContainer(
    url: String,
    modifier: Modifier,
    onPageState: (UiState, Boolean) -> Unit
) {
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {

                settings.javaScriptEnabled     = true
                settings.useWideViewPort       = true
                settings.loadWithOverviewMode  = true
                settings.domStorageEnabled     = true

                /* ---- PROGRESS-observer ---- */
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(v: WebView, newPct: Int) {
                        // <100 → Loading, 100 → Success
                        onPageState(
                            if (newPct < 100) UiState.Loading else UiState.Success,
                            v.canGoBack()
                        )
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(v: WebView, r: WebResourceRequest) = false

                    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                    override fun onPageStarted(v: WebView, u: String?, f: Bitmap?) {
                        if (!ctx.isOnline()) {            // нет сети — мгновенный Error
                            onPageState(UiState.Error, v.canGoBack())
                            v.stopLoading()
                        }
                    }
                    override fun onReceivedError(
                        v: WebView, req: WebResourceRequest, err: WebResourceError
                    ) {
                        onPageState(UiState.Error, v.canGoBack())
                    }
                }

                loadUrl(url)                              // первая загрузка

                /* делегаты для Back / Reload */
                WebViewDispatcher.apply {
                    tryGoBack = { if (canGoBack()) goBack() }
                    tryReload = { loadUrl(url) }
                }
            }
        },
        update   = { it.loadUrl(url) },
        modifier = modifier
    )
}

/* ---------- network helper ---------- */
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
private fun Context.isOnline(): Boolean {
    val cm   = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val net  = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(net) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
