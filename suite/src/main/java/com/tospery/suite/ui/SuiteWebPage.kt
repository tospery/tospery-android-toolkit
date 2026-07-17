package com.tospery.suite.ui

import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.tospery.suite.R

/**
 * 用于展示 HTTPS 页面的无业务通用组件。
 *
 * 为获得接近移动浏览器的渲染效果，启用 JavaScript 和 DOM Storage；
 * 同时保持 HTTPS 白名单、禁止本地文件与 ContentProvider 访问，并且不暴露原生 JS bridge。
 * [title] 有非空值时优先显示；否则使用网页通过 WebChromeClient 返回的标题。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuiteWebPage(
    url: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    val isValidUrl = url.isSafeHttpsUrl()
    val preferredTitle = title?.trim()?.takeIf(String::isNotEmpty)
    var documentTitle by remember(url) { mutableStateOf("") }
    var loadingProgress by remember(url) { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = preferredTitle ?: documentTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription =
                                    stringResource(R.string.suite_web_back),
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                        ),
                )

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                ) {
                    if (isValidUrl && loadingProgress < 100) {
                        LinearProgressIndicator(
                            progress = { loadingProgress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            if (isValidUrl) {
                key(url) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                settings.apply {
                                    // 现代移动网页通常依赖脚本和 DOM Storage 完成响应式布局与内容渲染。
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    javaScriptCanOpenWindowsAutomatically = false
                                    setSupportMultipleWindows(false)

                                    allowFileAccess = false
                                    allowContentAccess = false
                                    mixedContentMode =
                                        WebSettings.MIXED_CONTENT_NEVER_ALLOW
                                    safeBrowsingEnabled = true

                                    // 保留系统 WebView 的真实版本，只移除嵌入式标识以请求站点的移动浏览器页面。
                                    userAgentString = userAgentString.asMobileBrowserUserAgent()

                                    // 固定为控件宽度，避免缺少 width=device-width 的页面按桌面宽度缩放。
                                    useWideViewPort = false
                                    loadWithOverviewMode = false
                                    layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL

                                    // 保留移动端浏览器常用的双指缩放，但隐藏旧式屏幕缩放按钮。
                                    setSupportZoom(true)
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                }

                                webViewClient =
                                    object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(
                                            view: WebView,
                                            request: WebResourceRequest,
                                        ): Boolean {
                                            return !request.url
                                                .toString()
                                                .isSafeHttpsUrl()
                                        }

                                        override fun onPageFinished(
                                            view: WebView,
                                            url: String,
                                        ) {
                                            loadingProgress = 100
                                        }
                                    }

                                webChromeClient =
                                    object : WebChromeClient() {
                                        override fun onReceivedTitle(
                                            view: WebView,
                                            receivedTitle: String,
                                        ) {
                                            documentTitle = receivedTitle.trim()
                                        }

                                        override fun onProgressChanged(
                                            view: WebView,
                                            newProgress: Int,
                                        ) {
                                            loadingProgress =
                                                newProgress.coerceIn(0, 100)
                                        }
                                    }

                                loadUrl(url)
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        onRelease = { webView ->
                            webView.stopLoading()
                            webView.webChromeClient = WebChromeClient()
                            webView.webViewClient = WebViewClient()
                            webView.destroy()
                        },
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.suite_web_invalid_url),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

private fun String.isSafeHttpsUrl(): Boolean {
    val uri = runCatching { Uri.parse(trim()) }.getOrNull() ?: return false
    return uri.scheme?.equals("https", ignoreCase = true) == true &&
        !uri.host.isNullOrBlank()
}

private fun String.asMobileBrowserUserAgent(): String {
    return replace("; wv", "", ignoreCase = true)
        .replace(" Version/4.0", "", ignoreCase = true)
}
