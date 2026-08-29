package com.fk.allinone

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat


class WebViewActivity : AppCompatActivity() {

    // ================================================================
    // VIEWS
    // ================================================================

    private lateinit var webView: WebView

    private lateinit var progressBar: ProgressBar


    // ================================================================
    // ACTIVITY
    // ================================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_webview
        )

        initializeViews()

        setupSystemBars()

        setupWebView()

        setupBackNavigation()

        loadWebsite()
    }


    // ================================================================
    // INITIALIZE
    // ================================================================

    private fun initializeViews() {

        webView =
            findViewById(
                R.id.webView
            )

        progressBar =
            findViewById(
                R.id.webViewProgress
            )
    }


    // ================================================================
    // SYSTEM BARS
    // ================================================================

    private fun setupSystemBars() {

        /*
         * Keep application content below
         * the system status/navigation bars.
         */
        WindowCompat.setDecorFitsSystemWindows(
            window,
            true
        )


        /*
         * White status bar.
         */
        @Suppress("DEPRECATION")
        window.statusBarColor =
            Color.WHITE


        /*
         * White navigation bar.
         */
        @Suppress("DEPRECATION")
        window.navigationBarColor =
            Color.WHITE


        val controller =
            WindowInsetsControllerCompat(
                window,
                window.decorView
            )


        /*
         * Dark status-bar icons.
         */
        controller.isAppearanceLightStatusBars =
            true


        /*
         * Dark navigation-bar icons.
         */
        controller.isAppearanceLightNavigationBars =
            true
    }


    // ================================================================
    // WEBVIEW
    // ================================================================

    private fun setupWebView() {

        val settings =
            webView.settings


        // ------------------------------------------------------------
        // JAVASCRIPT
        // ------------------------------------------------------------

        @Suppress("SetJavaScriptEnabled")
        settings.javaScriptEnabled =
            true


        // ------------------------------------------------------------
        // DOM STORAGE
        // ------------------------------------------------------------

        settings.domStorageEnabled =
            true


        // ------------------------------------------------------------
        // VIEWPORT
        // ------------------------------------------------------------

        settings.useWideViewPort =
            true

        settings.loadWithOverviewMode =
            true


        // ------------------------------------------------------------
        // ZOOM
        // ------------------------------------------------------------

        settings.setSupportZoom(
            true
        )

        settings.builtInZoomControls =
            true

        settings.displayZoomControls =
            false


        // ------------------------------------------------------------
        // CACHE
        // ------------------------------------------------------------

        settings.cacheMode =
            WebSettings.LOAD_DEFAULT


        // ------------------------------------------------------------
        // LIGHT THEME
        // ------------------------------------------------------------

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            settings.setAlgorithmicDarkeningAllowed(
                false
            )
        }


        // ============================================================
        // WEBVIEW CLIENT
        // ============================================================

        webView.webViewClient =
            object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {

                    /*
                     * Keep normal navigation inside
                     * the WebView.
                     */
                    return false
                }


                override fun onPageStarted(
                    view: WebView,
                    url: String?,
                    favicon: android.graphics.Bitmap?
                ) {

                    super.onPageStarted(
                        view,
                        url,
                        favicon
                    )

                    progressBar.visibility =
                        View.VISIBLE
                }


                override fun onPageFinished(
                    view: WebView,
                    url: String?
                ) {

                    super.onPageFinished(
                        view,
                        url
                    )

                    progressBar.visibility =
                        View.GONE
                }


                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {

                    super.onReceivedError(
                        view,
                        request,
                        error
                    )


                    if (request.isForMainFrame) {

                        progressBar.visibility =
                            View.GONE


                        Toast.makeText(
                            this@WebViewActivity,
                            "Unable to load website",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        // ============================================================
        // CHROME CLIENT
        // ============================================================

        webView.webChromeClient =
            object : WebChromeClient() {

                override fun onProgressChanged(
                    view: WebView,
                    newProgress: Int
                ) {

                    super.onProgressChanged(
                        view,
                        newProgress
                    )


                    progressBar.progress =
                        newProgress


                    if (newProgress >= 100) {

                        progressBar.visibility =
                            View.GONE

                    } else {

                        progressBar.visibility =
                            View.VISIBLE
                    }
                }
            }
    }


    // ================================================================
    // LOAD WEBSITE
    // ================================================================

    private fun loadWebsite() {

        val websiteUrl =
            intent.getStringExtra(
                EXTRA_URL
            )


        if (websiteUrl.isNullOrBlank()) {

            Toast.makeText(
                this,
                "Website URL is missing",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        webView.loadUrl(
            websiteUrl
        )
    }


    // ================================================================
    // BACK NAVIGATION
    // ================================================================

    private fun setupBackNavigation() {

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    /*
                     * Navigate inside WebView history
                     * before closing the Activity.
                     */
                    if (webView.canGoBack()) {

                        webView.goBack()

                    } else {

                        finish()
                    }
                }
            }
        )
    }


    // ================================================================
    // CLEANUP
    // ================================================================

    override fun onDestroy() {

        webView.stopLoading()

        webView.webChromeClient =
            null


        /*
         * WebViewClient is non-null and should
         * not be assigned null.
         */

        val parent =
            webView.parent

        if (parent is android.view.ViewGroup) {

            parent.removeView(webView)
        }


        webView.destroy()

        super.onDestroy()
    }


    // ================================================================
    // CONSTANTS
    // ================================================================

    companion object {

        const val EXTRA_URL =
            "website_url"

        const val EXTRA_NAME =
            "website_name"
    }
}