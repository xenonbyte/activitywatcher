package com.xenonbyte.activitywatcher

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * App生命周期owner
 *
 * @author xubo
 */
internal class AppLifecycleOwner private constructor() : LifecycleOwner {
    private object Holder {
        val INSTANCE = AppLifecycleOwner()
    }

    companion object {
        fun get(): AppLifecycleOwner {
            return Holder.INSTANCE
        }
    }

    private val registry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() = registry

    fun handleLifecycleEvent(event: Event) {
        registry.handleLifecycleEvent(event)
    }
}