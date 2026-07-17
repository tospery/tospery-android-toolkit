package com.tospery.base.analytics

class CompositeAnalyticsTracker(
    private val trackers: List<AnalyticsTracker>,
) : AnalyticsTracker {
    override fun isEnabled(): Boolean = trackers.any { it.isEnabled() }

    override fun setEnabled(enabled: Boolean) {
        trackers.forEach { it.setEnabled(enabled) }
    }

    override fun track(event: AnalyticsEvent) {
        trackers
            .filter { it.isEnabled() }
            .forEach { it.track(event) }
    }

    override fun identify(user: AnalyticsUser) {
        trackers
            .filter { it.isEnabled() }
            .forEach { it.identify(user) }
    }

    override fun setUserProperties(properties: AnalyticsProperties) {
        trackers
            .filter { it.isEnabled() }
            .forEach { it.setUserProperties(properties) }
    }

    override fun trackScreen(screen: AnalyticsScreen) {
        trackers
            .filter { it.isEnabled() }
            .forEach { it.trackScreen(screen) }
    }

    override fun clearUser() {
        trackers.forEach { it.clearUser() }
    }

    override fun flush() {
        trackers.forEach { it.flush() }
    }

    override fun reset() {
        trackers.forEach { it.reset() }
    }
}