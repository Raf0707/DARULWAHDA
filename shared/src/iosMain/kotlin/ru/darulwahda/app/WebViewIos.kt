package ru.darulwahda.app

/*import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.*
import platform.CoreGraphics.CGRectZero
import platform.Foundation.*
import platform.Network.*
import platform.WebKit.*
import platform.darwin.NSObject

/* ——— вспомогательная проверка сети ——— */
private fun isReachable(): Boolean {
    val monitor = nw_path_monitor_create()
    var ok = false
    nw_path_monitor_set_update_handler(monitor) { path ->
        ok = nw_path_get_status(path) == nw_path_status_t.NW_PATH_STATUS_SATISFIED
    }
    nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
    nw_path_monitor_start(monitor)
    return ok
}

@Composable
actual fun WebViewContainer(
    url: String,
    modifier: Modifier,
    onPageState: (UiState, Boolean) -> Unit
) {
    UIKitView(
        modifier = modifier,
        factory = {
            val cfg = WKWebViewConfiguration().apply {
                allowsInlineMediaPlayback = true
                mediaTypesRequiringUserActionForPlayback = WKAudiovisualMediaTypeNone
            }

            /* создаём сам WKWebView */
            val webView = WKWebView(CGRectZero.readValue(), cfg)

            /* 1. делегат навигации */
            webView.navigationDelegate = object : NSObject(), WKNavigationDelegateProtocol {

                override fun webView(
                    webView: WKWebView,
                    didStartProvisionalNavigation: WKNavigation?
                ) {
                    if (!isReachable()) {
                        onPageState(UiState.Error, webView.canGoBack)
                        webView.stopLoading()
                    } else {
                        onPageState(UiState.Loading, webView.canGoBack)
                    }
                }

                override fun webView(
                    webView: WKWebView,
                    didFinishNavigation: WKNavigation?
                ) = onPageState(UiState.Success, webView.canGoBack)

                override fun webView(
                    webView: WKWebView,
                    didFailNavigation: WKNavigation?,
                    withError: NSError
                ) = onPageState(UiState.Error, webView.canGoBack)
            }

            /* 2. наблюдаем progress до 100 % */
            val progressObserver = object : NSObject() {
                override fun observeValueForKeyPath(
                    keyPath: String?,
                    obj: Any?,
                    change: Map<Any?, *>?,
                    context: CPointer<*>?
                ) {
                    val pct = (webView.estimatedProgress * 100).toInt()
                    onPageState(
                        if (pct < 100) UiState.Loading else UiState.Success,
                        webView.canGoBack
                    )
                }
            }
            webView.addObserver(
                progressObserver,
                keyPath = "estimatedProgress",
                options = NSKeyValueObservingOptionNew,
                context = null
            )

            /* 3. делегаты для Back / Reload */
            WebViewDispatcher.apply {
                tryGoBack = { if (webView.canGoBack) webView.goBack() }
                tryReload = { webView.reload() }
            }

            /* 4. первая загрузка */
            webView.loadRequest(NSURLRequest(NSURL(string = url)!!))
            webView
        },
        update = { it.loadRequest(NSURLRequest(NSURL(string = url)!!)) }
    )
}
*/