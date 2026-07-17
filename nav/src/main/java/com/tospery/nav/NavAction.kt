package com.tospery.nav

enum class ForwardMode {
    PUSH,
    PRESENT,
    REPLACE,
    SINGLE_TOP,
}

enum class BackMode {
    AUTO,
    POP_ONE,
    POP_TO_ROOT,
    DISMISS,
}

enum class OpenMode {
    TOAST,
    ALERT,
    SHEET,
    POPUP,
    LOGIC,
    EXTERNAL_APP,
    SYSTEM_URI,
    WEB,
}

sealed interface NavAction {
    data class Forward(
        val route: NavRoute,
        val mode: ForwardMode = ForwardMode.PUSH,
    ) : NavAction

    data class Back(
        val mode: BackMode = BackMode.AUTO,
    ) : NavAction

    data class Open(
        val target: UrlNavigationTarget,
        val mode: OpenMode,
    ) : NavAction

    data class RequiresAuthentication(
        val target: NavAction,
    ) : NavAction

    data class Invalid(
        val reason: String,
        val source: String? = null,
    ) : NavAction
}