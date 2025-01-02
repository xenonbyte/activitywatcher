package com.xenonbyte.activitywatcher

/**
 * 应用前后台监听
 *
 * @author xubo
 */
interface AppVisibilityCallback {

    /**
     * 应用进入前台回调
     */
    fun onForeground()

    /**
     * 应用进入后台回调
     */
    fun onBackground()
}