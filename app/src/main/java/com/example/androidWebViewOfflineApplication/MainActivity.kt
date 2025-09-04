package com.example.androidWebViewOfflineApplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Import this

class MainActivity : ComponentActivity() {
    private val applicationUrl = "file:///android_asset/index.html"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        // MUST be called before super.onCreate()
        installSplashScreen()

        super.onCreate(savedInstanceState)

        val webView = WebView(this).apply {
            // ... rest of your WebView setup remains the same
            webViewClient = WebViewClient()
            settings.apply {
                javaScriptEnabled = true
                allowFileAccess = true
                domStorageEnabled = true
            }
            addJavascriptInterface(WebAppInterface(this@MainActivity), "AndroidBridge")
            loadUrl(applicationUrl)
        }
        setContentView(webView)
    }
}