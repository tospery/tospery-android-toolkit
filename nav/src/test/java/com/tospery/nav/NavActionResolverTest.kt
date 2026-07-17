package com.tospery.nav

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavActionResolverTest {
    private val resolver = NavActionResolver(
        routeTable = NavRouteTable(
            definitions = listOf(
                NavRouteDefinition(
                    id = NavRouteId("about"),
                ),
                NavRouteDefinition(
                    id = NavRouteId("profile"),
                    requiresAuth = true,
                ),
            ),
        ),
    )

    @Test
    fun registeredInternalRouteResolvesToForwardAction() {
        val action = resolver.resolve(
            target = UrlNavigationTarget.InternalRoute(NavRoute("about")),
            isAuthenticated = false,
        )

        assertEquals(
            NavAction.Forward(route = NavRoute("about")),
            action,
        )
    }

    @Test
    fun unregisteredInternalRouteResolvesToInvalidAction() {
        val action = resolver.resolve(
            target = UrlNavigationTarget.InternalRoute(NavRoute("missing")),
            isAuthenticated = true,
        )

        assertTrue(action is NavAction.Invalid)
    }

    @Test
    fun authRequiredRouteResolvesToRequiresAuthenticationWhenLoggedOut() {
        val action = resolver.resolve(
            target = UrlNavigationTarget.InternalRoute(NavRoute("profile")),
            isAuthenticated = false,
        )

        assertEquals(
            NavAction.RequiresAuthentication(
                target = NavAction.Forward(route = NavRoute("profile")),
            ),
            action,
        )
    }

    @Test
    fun authRequiredRouteResolvesToForwardWhenLoggedIn() {
        val action = resolver.resolve(
            target = UrlNavigationTarget.InternalRoute(NavRoute("profile")),
            isAuthenticated = true,
        )

        assertEquals(
            NavAction.Forward(route = NavRoute("profile")),
            action,
        )
    }

    @Test
    fun externalAppTargetResolvesToOpenAction() {
        val target = UrlNavigationTarget.ExternalApp("mqqapi://example")
        val action = resolver.resolve(target, isAuthenticated = false)

        assertEquals(
            NavAction.Open(target = target, mode = OpenMode.EXTERNAL_APP),
            action,
        )
    }

    @Test
    fun systemUriTargetResolvesToOpenAction() {
        val target = UrlNavigationTarget.SystemUri("mailto:hello@example.com")
        val action = resolver.resolve(target, isAuthenticated = false)

        assertEquals(
            NavAction.Open(target = target, mode = OpenMode.SYSTEM_URI),
            action,
        )
    }

    @Test
    fun webUrlTargetResolvesToOpenAction() {
        val target = UrlNavigationTarget.WebUrl("https://example.com/about")
        val action = resolver.resolve(target, isAuthenticated = false)

        assertEquals(
            NavAction.Open(target = target, mode = OpenMode.WEB),
            action,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun duplicatedRoutePathThrowsException() {
        NavRouteTable(
            definitions = listOf(
                NavRouteDefinition(
                    id = NavRouteId("repo"),
                    path = "repo",
                ),
                NavRouteDefinition(
                    id = NavRouteId("repository"),
                    path = "/repo/",
                ),
            ),
        )
    }
}