package com.tospery.nav

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NavRouteTableTest {
    @Test
    fun exactStaticRouteMatchesWithoutParameters() {
        val definition =
            NavRouteDefinition(
                id = NavRouteId("settings-appearance"),
                path = "settings/appearance",
            )
        val table = NavRouteTable(listOf(definition))

        val match = requireNotNull(table.match("settings/appearance"))

        assertEquals(definition, match.definition)
        assertEquals(emptyMap<String, String>(), match.pathParameters)
    }

    @Test
    fun overlayDiscriminatorSelectsDialogRoute() {
        val table = accountAndDialogRouteTable()

        val match =
            requireNotNull(
                table.match(
                    NavRoute("dialog?id=logout").parse(),
                ),
            )

        assertEquals(NavRouteId("dialog"), match.definition.id)
        assertEquals(emptyMap<String, String>(), match.pathParameters)
    }

    @Test
    fun dialogWithoutDiscriminatorMatchesAccountRoute() {
        val table = accountAndDialogRouteTable()

        val match =
            requireNotNull(
                table.match(NavRoute("dialog").parse()),
            )

        assertEquals(NavRouteId("account"), match.definition.id)
        assertEquals(
            mapOf("login" to "dialog"),
            match.pathParameters,
        )
    }

    @Test
    fun unrelatedDialogQueryMatchesAccountRoute() {
        val table = accountAndDialogRouteTable()

        val match =
            requireNotNull(
                table.match(
                    NavRoute("dialog?foo=bar").parse(),
                ),
            )

        assertEquals(NavRouteId("account"), match.definition.id)
        assertEquals(
            mapOf("login" to "dialog"),
            match.pathParameters,
        )
    }

    @Test
    fun incompleteOverlayQueryStillSelectsDialogRoute() {
        val table = accountAndDialogRouteTable()

        val match =
            requireNotNull(
                table.match(
                    NavRoute("dialog?title=MissingActions").parse(),
                ),
            )

        assertEquals(NavRouteId("dialog"), match.definition.id)
    }

    @Test
    fun singleParameterRouteExtractsLogin() {
        val definition =
            NavRouteDefinition(
                id = NavRouteId("account"),
                path = "{login}",
            )
        val table = NavRouteTable(listOf(definition))

        val match = requireNotNull(table.match("devxoul"))

        assertEquals(definition, match.definition)
        assertEquals(
            mapOf("login" to "devxoul"),
            match.pathParameters,
        )
    }

    @Test
    fun repositoryRouteExtractsOwnerAndRepo() {
        val definition =
            NavRouteDefinition(
                id = NavRouteId("repository"),
                path = "{owner}/{repo}",
            )
        val table = NavRouteTable(listOf(definition))

        val match =
            requireNotNull(
                table.match("devxoul/ReactorKit"),
            )

        assertEquals(definition, match.definition)
        assertEquals(
            mapOf(
                "owner" to "devxoul",
                "repo" to "ReactorKit",
            ),
            match.pathParameters,
        )
    }

    @Test
    fun staticRouteWinsOverFullyDynamicRoute() {
        val repository =
            NavRouteDefinition(
                id = NavRouteId("repository"),
                path = "{owner}/{repo}",
            )
        val settingsProfile =
            NavRouteDefinition(
                id = NavRouteId("settings-profile"),
                path = "settings/profile",
            )
        val table =
            NavRouteTable(
                definitions =
                    listOf(
                        repository,
                        settingsProfile,
                    ),
            )

        val match =
            requireNotNull(
                table.match("settings/profile"),
            )

        assertEquals(settingsProfile, match.definition)
        assertEquals(emptyMap<String, String>(), match.pathParameters)
    }

    @Test
    fun routeWithMoreLiteralSegmentsWins() {
        val repository =
            NavRouteDefinition(
                id = NavRouteId("repository"),
                path = "{owner}/{repo}",
            )
        val settingsSection =
            NavRouteDefinition(
                id = NavRouteId("settings-section"),
                path = "settings/{section}",
            )
        val table =
            NavRouteTable(
                definitions =
                    listOf(
                        repository,
                        settingsSection,
                    ),
            )

        val match =
            requireNotNull(
                table.match("settings/appearance"),
            )

        assertEquals(settingsSection, match.definition)
        assertEquals(
            mapOf("section" to "appearance"),
            match.pathParameters,
        )
    }

    @Test
    fun differentSegmentCountDoesNotMatch() {
        val table =
            NavRouteTable(
                definitions =
                    listOf(
                        NavRouteDefinition(
                            id = NavRouteId("repository"),
                            path = "{owner}/{repo}",
                        ),
                    ),
            )

        assertNull(table.match("devxoul"))
        assertNull(table.match("devxoul/ReactorKit/issues"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun equivalentDynamicPatternsAreRejected() {
        NavRouteTable(
            definitions =
                listOf(
                    NavRouteDefinition(
                        id = NavRouteId("repository"),
                        path = "{owner}/{repo}",
                    ),
                    NavRouteDefinition(
                        id = NavRouteId("project"),
                        path = "{account}/{project}",
                    ),
                ),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun overlappingPatternsWithEqualSpecificityAreRejected() {
        NavRouteTable(
            definitions =
                listOf(
                    NavRouteDefinition(
                        id = NavRouteId("settings-section"),
                        path = "settings/{section}",
                    ),
                    NavRouteDefinition(
                        id = NavRouteId("profile-child"),
                        path = "{login}/profile",
                    ),
                ),
        )
    }

    @Test
    fun equalSpecificityWithoutOverlapIsAllowed() {
        val settingsSection =
            NavRouteDefinition(
                id = NavRouteId("settings-section"),
                path = "settings/{section}",
            )
        val trendingRange =
            NavRouteDefinition(
                id = NavRouteId("trending-range"),
                path = "trending/{range}",
            )
        val table =
            NavRouteTable(
                definitions =
                    listOf(
                        settingsSection,
                        trendingRange,
                    ),
            )

        assertEquals(
            settingsSection,
            table.match("settings/appearance")?.definition,
        )
        assertEquals(
            trendingRange,
            table.match("trending/daily")?.definition,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun malformedParameterSegmentIsRejected() {
        NavRouteTable(
            definitions =
                listOf(
                    NavRouteDefinition(
                        id = NavRouteId("invalid"),
                        path = "{owner",
                    ),
                ),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun duplicatedParameterNameIsRejected() {
        NavRouteTable(
            definitions =
                listOf(
                    NavRouteDefinition(
                        id = NavRouteId("invalid"),
                        path = "{owner}/{owner}",
                    ),
                ),
        )
    }

    private fun accountAndDialogRouteTable(): NavRouteTable {
        return NavRouteTable(
            definitions =
                listOf(
                    NavRouteDefinition(
                        id = NavRouteId("account"),
                        path = "{login}",
                    ),
                    navOverlayRouteDefinition(
                        presentation = NavPresentation.DIALOG,
                    ),
                ),
        )
    }
}
