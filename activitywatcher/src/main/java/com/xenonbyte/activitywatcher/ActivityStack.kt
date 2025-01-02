package com.xenonbyte.activitywatcher

import android.app.Activity
import android.os.Bundle
import java.util.Stack

/**
 * [Activity]栈
 *
 * 一个App可能存在1个或多个[ActivityTask]任务，这跟App中[Activity]启动模式有关
 *
 * @author xubo
 */
class ActivityStack internal constructor() : ActivityEventRecevier {

    companion object {
        const val ACTIVITY_RECORD_INVALID_ID: Int = -1
        const val ACTIVITY_RECORD_ID_RESTORE_KEY: String = "xenon_byte.activity_watcher:ActivityRecordId"
    }

    /**
     * App是否处于后台
     */
    var isAppBackground = false

    /**
     * Activity任务栈
     *
     *  一个App可能存在1个或多个[ActivityTask]任务
     */
    private val stack = Stack<ActivityTask>()

    /**
     * [Activity]生命周期回调集合
     */
    private val activityLifeCycleCallbackList = mutableSetOf<ActivityLifeCycleCallback>()

    /**
     * App前后台回调集合
     */
    private val appVisibilityCallbackList = mutableSetOf<AppVisibilityCallback>()

    /**
     * 获取[Activity]栈顶[ActivityRecord]
     *
     * @return [ActivityRecord]实例
     */
    internal fun getStackTop(): ActivityRecord? {
        val activityTask = stack.peekOrNull() ?: return null
        return activityTask.stack.peekOrNull()
    }

    /**
     * 获取[ActivityStack]中[ActivityTask]栈
     *
     * @return [Stack]
     */
    internal fun getActivityTaskStack(): Stack<ActivityTask> {
        return stack
    }

    /**
     * 获取[ActivityStack]的Json字符串描述
     *
     * @return [ActivityStack]的Json字符串描述
     */
    internal fun getJson(): String {
        val buffer = StringBuffer()
        buffer.append("[")
        val size = stack.size
        stack.withIndex().forEach { (index, activityTask) ->
            buffer.append(activityTask.toJsonString())
            if (size - 1 != index) {
                buffer.append(", ")
            }
        }
        buffer.append("]")
        return buffer.toString()
    }

    /**
     * 通过[Activity]实例寻找[ActivityRecord]
     *
     * @param activity [Activity]实例
     * @return [ActivityRecord]实例
     */
    internal fun findActivityRecord(activity: Activity): ActivityRecord? {
        return stack.firstNotNullOfOrNull {
            it.findActivityRecord(activity)
        }
    }

    /**
     * 通过activityRecordId实例寻找[ActivityRecord]
     *
     * @param activityRecordId [Activity]唯一标识id
     * @return [ActivityRecord]实例
     */
    internal fun findActivityRecord(activityRecordId: Int): ActivityRecord? {
        return stack.firstNotNullOfOrNull {
            it.findActivityRecord(activityRecordId)
        }
    }

    /**
     * 添加生命周期监听
     *
     * @param callback 生命周期回调
     */
    internal fun addActivityLifeCycleCallback(callback: ActivityLifeCycleCallback) {
        activityLifeCycleCallbackList.add(callback)
    }

    /**
     * 移除生命周期监听
     *
     * @param callback 生命周期回调
     */
    internal fun removeActivityLifeCycleCallback(callback: ActivityLifeCycleCallback) {
        activityLifeCycleCallbackList.remove(callback)
    }

    /**
     * 添加App前后台监听
     *
     * @param callback App前后台回调
     */
    internal fun addAppVisibilityCallback(callback: AppVisibilityCallback) {
        appVisibilityCallbackList.add(callback)
    }

    /**
     * 移除App前后台监听
     *
     * @param callback App前后台回调
     */
    internal fun removeAppVisibilityCallback(callback: AppVisibilityCallback) {
        appVisibilityCallbackList.remove(callback)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        //Activity恢复
        savedInstanceState?.let {
            val lastActivityRecordId =
                savedInstanceState.getInt(ACTIVITY_RECORD_ID_RESTORE_KEY, ACTIVITY_RECORD_INVALID_ID)
            if (lastActivityRecordId != ACTIVITY_RECORD_INVALID_ID && restoreActivityRecord(
                    lastActivityRecordId,
                    activity
                )
            ) {
                activityLifeCycleCallbackList.forEach {
                    it.onCreate(activity, lastActivityRecordId, savedInstanceState, true)
                }
                return
            }
        }

        //Activity压入Activity栈
        val taskId = activity.taskId
        val activityTask = findActivityTask(taskId) ?: ActivityTask(taskId)
        val activityRecord = activityTask.push(activity)
        activityRecord.updateActivityState(ActivityState.CREATED)
        updateActivityTaskPosition(activityTask)
        stack.forEach { activityTask ->
            if (activityTask.taskId != taskId) {
                activityTask.stack.forEach {
                    if (it.activityState.ordinal > ActivityState.CREATED.ordinal) {
                        it.updateActivityState(ActivityState.CREATED)
                    }
                }
            }
        }
        activityLifeCycleCallbackList.forEach {
            it.onCreate(activity, activityRecord.activityRecordId, savedInstanceState, false)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        findActivityRecord(activity)?.apply {
            updateActivityState(ActivityState.STARTED)
            activityLifeCycleCallbackList.forEach {
                it.onStart(activity, activityRecordId)
            }
            if (isAppBackground) {
                isAppBackground = false
                appVisibilityCallbackList.forEach {
                    it.onForeground()
                }
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        findActivityRecord(activity)?.apply {
            //Activity是栈顶Activity，直接更新状态
            if (this == getStackTop()) {
                updateActivityState(ActivityState.RESUMED)
                activityLifeCycleCallbackList.forEach {
                    it.onResume(activity, activityRecordId)
                }
                return
            }

            //Activity压入Activity栈
            val taskId = activity.taskId
            val activityTask = findActivityTask(taskId) ?: ActivityTask(taskId)
            val activityRecord = activityTask.push(activity)
            activityRecord.updateActivityState(ActivityState.RESUMED)
            updateActivityTaskPosition(activityTask)
            stack.forEach { activityTask ->
                if (activityTask.taskId != taskId) {
                    activityTask.stack.forEach {
                        if (it.activityState.ordinal > ActivityState.CREATED.ordinal) {
                            it.updateActivityState(ActivityState.CREATED)
                        }
                    }
                }
            }
            activityLifeCycleCallbackList.forEach {
                it.onResume(activity, activityRecord.activityRecordId)
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        findActivityRecord(activity)?.apply {
            updateActivityState(ActivityState.STARTED)
            activityLifeCycleCallbackList.forEach {
                it.onPause(activity, activityRecordId)
            }
        }
    }

    override fun onActivityStopped(activity: Activity) {
        findActivityRecord(activity)?.apply {
            updateActivityState(ActivityState.CREATED)
            activityLifeCycleCallbackList.forEach {
                it.onStop(activity, activityRecordId)
            }
            if (!isAppForeground()) {
                isAppBackground = true
                appVisibilityCallbackList.forEach {
                    it.onBackground()
                }
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        findActivityRecord(activity)?.apply {
            outState.putInt(ACTIVITY_RECORD_ID_RESTORE_KEY, activityRecordId)
            activityLifeCycleCallbackList.forEach {
                it.onSaveInstanceState(activity, activityRecordId, outState)
            }
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        findActivityRecord(activity)?.apply {
            updateActivityState(ActivityState.DESTROYED)
            val taskId = activity.taskId
            val canRestore = !activity.isFinishing
            val activityTask = findActivityTask(taskId)
            if (!canRestore) {
                activityTask?.let {
                    it.stack.remove(this)
                    if (it.stack.isEmpty()) {
                        stack.remove(it)
                    }
                }
            }
            activityLifeCycleCallbackList.forEach {
                it.onDestroy(activity, activityRecordId, canRestore)
            }
        }
    }

    /**
     * 恢复[ActivityRecord]
     *
     * @param activityRecordId [ActivityRecord]唯一标识id
     * @param activity [Activity]实例
     */
    private fun restoreActivityRecord(activityRecordId: Int, activity: Activity): Boolean {
        return findActivityRecord(activityRecordId)?.let {
            it.restoreActivity(activity, ActivityState.CREATED)
            true
        } ?: false
    }

    /**
     * 应用是否在前台
     */
    private fun isAppForeground(): Boolean {
        val activityRecord = getStackTop()
        return activityRecord?.let {
            return it.activityState.ordinal > ActivityState.CREATED.ordinal
        } ?: false
    }

    /**
     * 寻找[ActivityTask]
     *
     * @param taskId 任务id
     * @return [ActivityTask]实例
     */
    private fun findActivityTask(taskId: Int): ActivityTask? {
        return stack.firstOrNull {
            it.taskId == taskId
        }
    }

    /**
     * 更新[ActivityTask]在[Activity]栈中的位置
     *
     * @param task [ActivityTask]实例
     */
    private fun updateActivityTaskPosition(task: ActivityTask) {
        stack.remove(task)
        stack.push(task)
    }
}

internal fun Stack<ActivityTask>.peekOrNull(): ActivityTask? {
    return if (this.isNotEmpty()) this.peek() else null
}

internal fun Stack<ActivityRecord>.peekOrNull(): ActivityRecord? {
    return if (this.isNotEmpty()) this.peek() else null
}