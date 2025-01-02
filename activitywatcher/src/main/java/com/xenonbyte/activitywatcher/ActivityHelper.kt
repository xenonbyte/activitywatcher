package com.xenonbyte.activitywatcher

import android.app.Activity

/**
 * [Activity]帮助类
 *
 * @author xubo
 */
internal interface ActivityHelper {
    /**
     * 生成[Activity]实例唯一id
     *
     * @return [Activity]唯一标识id
     */
    fun generateActivityId(): Int

    /**
     * 获取应用[Activity]栈
     *
     * @return [ActivityStack]实例
     */
    fun getActivityStack(): ActivityStack

    /**
     * 获取应用[Activity]栈中[Activity]数量
     *
     * @return 应用[Activity]栈中[Activity]数量
     */
    fun getActivityStackCount(): Int
}