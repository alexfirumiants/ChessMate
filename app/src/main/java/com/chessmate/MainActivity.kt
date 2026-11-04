package com.chessmate

import android.app.Activity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        val webView = WebView(this)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest
            ): WebResourceResponse? {
                val uri = request.url
                val path = uri.path.orEmpty()

                // WebAssembly must be served with the application/wasm MIME type.
                if (uri.host == "appassets.androidplatform.net" && path.endsWith(".wasm")) {
                    val assetName = path.removePrefix("/assets/")
                    return try {
                        WebResourceResponse(
                            "application/wasm",
                            null,
                            assets.open(assetName)
                        )
                    } catch (_: Exception) {
                        null
                    }
                }

                return assetLoader.shouldInterceptRequest(uri)
            }
        }

        setContentView(webView)
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")
    }
}
