package com.tospery.nav

class NavActionResolver(
    private val routeTable: NavRouteTable,
) {
    fun resolve(
        target: UrlNavigationTarget,
        isAuthenticated: Boolean,
    ): NavAction {
        return when (target) {
            is UrlNavigationTarget.InternalRoute -> resolveInternalRoute(
                route = target.route,
                isAuthenticated = isAuthenticated,
            )

            is UrlNavigationTarget.ExternalApp -> NavAction.Open(
                target = target,
                mode = OpenMode.EXTERNAL_APP,
            )

            is UrlNavigationTarget.SystemUri -> NavAction.Open(
                target = target,
                mode = OpenMode.SYSTEM_URI,
            )

            is UrlNavigationTarget.WebUrl -> NavAction.Open(
                target = target,
                mode = OpenMode.WEB,
            )

            is UrlNavigationTarget.Unknown -> NavAction.Invalid(
                reason = "Unknown navigation target.",
                source = target.uri,
            )
        }
    }

    private fun resolveInternalRoute(
        route: NavRoute,
        isAuthenticated: Boolean,
    ): NavAction {
        val parsedRoute = route.parse()
        val definition = routeTable.findByPath(parsedRoute.path)
            ?: return NavAction.Invalid(
                reason = "Route is not registered.",
                source = route.value,
            )

        val action = NavAction.Forward(
            route = route,
            mode = definition.defaultForwardMode,
        )

        return if (definition.requiresAuth && !isAuthenticated) {
            NavAction.RequiresAuthentication(action)
        } else {
            action
        }
    }
}