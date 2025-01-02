package com.xenonbyte.activitywatcher

import android.app.Activity

/**
 * [Activity]状态
 *
 * @author xubo
 */
enum class ActivityState {
    /**
     * 销毁状态
     * <p>
     * 生命周期：[Activity]在调用[Activity.onDestroy]之前达到此状态
     * <p>
     * 可恢复情况：因内存不足或其他原因被系统回收是有机会恢复
     * <p>
     * 不可恢复情况：主动[Activity.finish] 或 因singleTask启动模式引发的出栈销毁是不可恢复的
     */
    DESTROYED,

    /**
     * 创建状态
     * <p>
     * 生命周期：[Activity]在调用[Activity.onCreate]后 或 [Activity]在调用[Activity.onStop]之前 达到此状态
     */
    CREATED,

    /**
     * 启动状态
     * <p>
     * 生命周期：[Activity]在调用[Activity.onStart]后 或 [Activity]在调用[Activity.onPause]之前 达到此状态
     */
    STARTED,

    /**
     * 恢复状态
     * <p>
     * 生命周期：[Activity]在调用[Activity.onResume]后达到此状态
     */
    RESUMED;
}