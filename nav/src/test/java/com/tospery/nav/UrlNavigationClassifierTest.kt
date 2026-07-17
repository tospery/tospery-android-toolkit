package com.tospery.nav

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlNavigationClassifierTest {
    private val classifier = UrlNavigationClassifier(
        config = UrlNavigationConfig(
            appSchemes = setOf(UrlScheme("higit")),
            trustedHosts = setOf(UrlHost("higit.com")),
        ),
    )

    @Test
    fun customSchemeUsesHostAsInternalRoutePath() {
        val target = classifier.classify("higit://about")

        assertEquals(
            UrlNavigationTarget.InternalRoute(NavRoute("about")),
            target,
        )
    }

    @Test
    fun appLinkUsesPathAsInternalRoutePath() {
        val target = classifier.classify("https://higit.com/about")

        assertEquals(
            UrlNavigationTarget.InternalRoute(NavRoute("about")),
            target,
        )
    }

    @Test
    fun thirdPartySchemeIsExternalApp() {
        val target = classifier.classify("mqqapi://example")

        assertEquals(
            UrlNavigationTarget.ExternalApp("mqqapi://example"),
            target,
        )
    }

    @Test
    fun systemSchemeIsSystemUri() {
        val target = classifier.classify("mailto:hello@example.com")

        assertEquals(
            UrlNavigationTarget.SystemUri("mailto:hello@example.com"),
            target,
        )
    }

    @Test
    fun untrustedHttpsUrlIsWebUrl() {
        val target = classifier.classify("https://example.com/about")

        assertEquals(
            UrlNavigationTarget.WebUrl("https://example.com/about"),
            target,
        )
    }

    @Test
    fun invalidUriIsUnknown() {
        val target = classifier.classify("https://exa mple.com")

        assertTrue(target is UrlNavigationTarget.Unknown)
    }

    @Test
    fun customSchemeWithoutRoutePathIsUnknown() {
        val target = classifier.classify("higit://")

        assertTrue(target is UrlNavigationTarget.Unknown)
    }

    @Test
    fun appLinkWithoutRoutePathIsUnknown() {
        val target = classifier.classify("https://higit.com")

        assertTrue(target is UrlNavigationTarget.Unknown)
    }
}