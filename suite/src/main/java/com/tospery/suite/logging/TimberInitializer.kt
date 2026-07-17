package com.tospery.suite.logging

import timber.log.Timber

object TimberInitializer {
    fun plantDebugTree() {
        Timber.plant(Timber.DebugTree())
    }

    fun plant(tree: Timber.Tree) {
        Timber.plant(tree)
    }

    fun uprootAll() {
        Timber.uprootAll()
    }
}
