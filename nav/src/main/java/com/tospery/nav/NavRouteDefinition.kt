package com.tospery.nav

@JvmInline
value class NavRouteId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "NavRouteId value must not be blank." }
    }

    override fun toString(): String = value
}

data class NavRouteDefinition(
    val id: NavRouteId,
    val path: String = id.value,
    val requiresAuth: Boolean = false,
    val defaultForwardMode: ForwardMode = ForwardMode.PUSH,
) {
    init {
        require(path.isNotBlank()) { "NavRouteDefinition path must not be blank." }
    }
}

class NavRouteTable(
    definitions: Collection<NavRouteDefinition>,
) {
    private val definitionsByPath = definitions.associateBy {
        it.normalizedPath
    }

    init {
        require(definitionsByPath.size == definitions.size) {
            "NavRouteTable contains duplicated route paths."
        }
    }

    fun findByPath(path: String): NavRouteDefinition? {
        return definitionsByPath[path.normalizeRoutePath()]
    }

    fun contains(path: String): Boolean {
        return findByPath(path) != null
    }

    private val NavRouteDefinition.normalizedPath: String
        get() = path.normalizeRoutePath()

    private fun String.normalizeRoutePath(): String {
        return trim().trim('/')
    }
}