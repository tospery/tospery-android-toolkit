package com.tospery.nav

import org.junit.Assert.assertEquals
import org.junit.Test

class UrlIdentifiersTest {
    @Test
    fun schemeNormalizesToLowercase() {
        assertEquals("higit", UrlScheme("HiGit").normalized())
    }

    @Test(expected = IllegalArgumentException::class)
    fun schemeMustNotContainSeparator() {
        UrlScheme("higit://")
    }

    @Test
    fun hostNormalizesToLowercase() {
        assertEquals("higit.com", UrlHost("HiGit.com").normalized())
    }

    @Test(expected = IllegalArgumentException::class)
    fun hostMustNotContainPath() {
        UrlHost("higit.com/about")
    }
}