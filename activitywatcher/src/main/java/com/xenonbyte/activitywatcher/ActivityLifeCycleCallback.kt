package com.xenonbyte.activitywatcher

import android.app.Activity
import android.os.Bundle

/**
 * [Activity]生命周期回调
 *
 * @author xubo
 */
interface ActivityLifeCycleCallback {
    /**
     * [Activity.onCreate]生命周期事件
     *
     * 对应状态：[ActivityState.CREATED]
     *
     * 注意：[Activity]因内存回收或其他情况销毁后有恢复情况，activityRecordId不会因为[Activity]销毁恢复而发生改变（[Activity]实例会发生改变）
     *
     * @param activity [Activity]实例
     * @param activityRecordId 内部生成的[Activity]实例id；如果[Activity]因恢复重建，销毁前和重建的[Activity]实例Id不变
     * @param savedInstanceState 如果[Activity]是恢复情况重建，此Bundle保存上次[Activity]实例销毁前数据，否则此Bundle为null
     * @param isRestore 该事件是否因[Activity]恢复产生
     */
    fun onCreate(activity: Activity, activityRecordId: Int, savedInstanceState: Bundle?, isRestore: Boolean)

    /**
     * [Activity.onStart]生命周期事件
     *
     * 对应状态：[ActivityState.STARTED]
     *
     * @param activity [Activity]实例
     * @param activityRecordId 内部生成的[Activity]实例id，[Activity]恢复不会改变该值
     */
    fun onStart(activity: Activity, activityRecordId: Int)

    /**
     * [Activity.onResume]生命周期事件
     *
     * 对应状态：[ActivityState.RESUMED]
     *
     * @param activity [Activity]实例
     * @param activityRecordId 内部生成的[Activity]实例id，[Activity]恢复不会改变该值
     */
    fun onResume(activity: Activity, activityRecordId: Int)

    /**
     * [Activity.onPause]生命周期事件
     *
     * 对应状态：[ActivityState.STARTED]
     *
     * @param activity [Activity]实例
     * @param activityRecordId 内部生成的[Activity]实例id，[Activity]恢复不会改变该值
     */
    fun onPause(activity: Activity, activityRecordId: Int)

    /**
     * [Activity.onStop]生命周期事件
     *
     * 对应状态：[ActivityState.CREATED]
     *
     * @param activity Activity实例
     * @param activityRecordId 内部生成的[Activity]实例id，[Activity]恢复不会改变该值
     */
    fun onStop(activity: Activity, activityRecordId: Int)

    /**
     * [Activity.onDestroy]生命周期事件
     *
     * 对应状态：[ActivityState.DESTROYED]
     *
     * @param activity Activity实例
     * @param activityRecordId 内部生成的[Activity]实例id，[Activity]恢复不会改变该值
     * @param canRestore [Activity]是否有机会恢复
     */
    fun onDestroy(activity: Activity, activityRecordId: Int, canRestore: Boolean)

    /**
     * [Activity.onSaveInstanceState]事件
     *
     * @param activity [Activity]实例
     * @param activityRecordId 内部生成的[Activity]实例id，[Activity]恢复不会改变该值
     * @param outState [Activity]实例需要保存的数据, 方便恢复时使用
     */
    fun onSaveInstanceState(activity: Activity, activityRecordId: Int, outState: Bundle) {

    }
}