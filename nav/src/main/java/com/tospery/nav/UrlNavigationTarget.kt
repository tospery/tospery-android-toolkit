package com.tospery.nav

enum class WebOpenMode {
    IN_APP,
    EXTERNAL_BROWSER,
}

sealed interface UrlNavigationTarget {
    data class InternalRoute(
        val route: NavRoute,
    ) : UrlNavigationTarget

    data class ExternalApp(
        val uri: String,
    ) : UrlNavigationTarget

    data class SystemUri(
        val uri: String,
    ) : UrlNavigationTarget

    data class WebUrl(
        val url: String,
        val openMode: WebOpenMode = WebOpenMode.EXTERNAL_BROWSER,
    ) : UrlNavigationTarget

    data class Unknown(
        val uri: String,
    ) : UrlNavigationTarget
}