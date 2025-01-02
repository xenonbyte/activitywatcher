package com.xenonbyte.activitywatcher

import android.app.Activity
import android.os.Bundle

/**
 * [Activity]事件接收者
 *
 * @author xubo
 */
internal interface ActivityEventRecevier {

    /**
     * [Activity.onCreate]事件
     *
     * @param activity [Activity]实例
     * @param savedInstanceState [Activity]恢复时上次实例保存的数据
     */
    fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?)

    /**
     * [Activity.onStart]事件
     *
     * @param activity [Activity]实例
     */
    fun onActivityStarted(activity: Activity)

    /**
     * [Activity.onResume]事件
     *
     * @param activity [Activity]实例
     */
    fun onActivityResumed(activity: Activity)

    /**
     * [Activity.onPause]事件
     *
     * @param activity [Activity]实例
     */
    fun onActivityPaused(activity: Activity)

    /**
     * [Activity.onStop]事件
     *
     * @param activity [Activity]实例
     */
    fun onActivityStopped(activity: Activity)

    /**
     * [Activity.onSaveInstanceState]事件
     *
     * @param activity [Activity]实例
     * @param outState [Activity]实例需要保存的数据, 方便恢复时使用
     */
    fun onActivitySaveInstanceState(activity: Activity, outState: Bundle)

    /**
     * [Activity.onDestroy]事件
     *
     * @param activity [Activity]实例
     */
    fun onActivityDestroyed(activity: Activity)
}