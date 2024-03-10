package jinproject.stepwalk.profile.screen.terms.component

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal fun TermsWebView(
    url: String,
    webViewClient: WebViewClient = WebViewClient()
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                this.webViewClient = webViewClient
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}

