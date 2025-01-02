package com.xenonbyte.activitywatcher

import android.app.Activity
import java.lang.ref.WeakReference

/**
 * [Activity]记录
 *
 * @param activity [Activity]实例
 * @author xubo
 */
class ActivityRecord private constructor(activity: Activity) {
    companion object {

        /**
         * 创建[Activity]记录
         *
         * @param activity [Activity]实例
         * @return [Activity]记录实例
         */
        internal fun create(activity: Activity): ActivityRecord {
            return ActivityRecord(activity)
        }
    }

    /**
     * [Activity]弱引用
     *
     * 不强引[Activity]实例，[Activity]实例随着系统的回收而断开引用
     */
    private var activityReference: WeakReference<Activity> = WeakReference(activity)

    /**
     * [Activity]状态
     */
    var activityState: ActivityState = ActivityState.CREATED
        get() {
            checkActivityState()
            return field
        }
        private set

    /**
     * [Activity]是否销毁
     */
    val isDestroyed: Boolean
        get() = activityState == ActivityState.DESTROYED

    /**
     * [Activity]记录唯一标识id
     */
    val activityRecordId: Int = ActivityWatcher.generateActivityId()

    /**
     * [Activity]类名
     */
    val activityName: String = activity.javaClass.simpleName

    /**
     * [Activity]全路径名
     */
    val activityCanonicalName: String = activity.javaClass.canonicalName

    /**
     * 执行[Activity]任务
     *
     * [ActivityRecord]不对外暴露[Activity], 需要Activity实例做业务操作时，请使用该方法
     * 如果[activityState]是[ActivityState.DESTROYED]，任务不会执行
     *
     * @param action activity任务
     */
    fun execute(action: (activity: Activity) -> Unit) {
        getActivity()?.let {
            action(it)
        }
    }

    /**
     * 更新[Activity]状态
     *
     * @param state [Activity]状态
     */
    internal fun updateActivityState(state: ActivityState) {
        activityState = state
    }

    /**
     * [Activity]恢复
     *
     * Activity恢复时内部自动调用
     *
     * @param activity [Activity]实例
     * @param state [Activity]状态
     */
    internal fun restoreActivity(activity: Activity, state: ActivityState) {
        activityReference = WeakReference(activity)
        activityState = state
        checkActivityState()
    }

    /**
     * [ActivityRecord]是否匹配该Activity实例
     *
     * @param activity [Activity]实例
     * @return true表示[Activity]实例匹配[ActivityRecord], false表示不匹配
     */
    internal fun match(activity: Activity): Boolean {
        if (isDestroyed) {
            return false
        }
        return activity == getActivity()
    }

    /**
     * 获取Activity
     *
     * [ActivityRecord]不对外暴露[Activity]
     * 如果业务需要使用[Activity]，请使用[ActivityRecord.execute]
     *
     * @return [Activity]实例
     */
    private fun getActivity(): Activity? {
        if (isDestroyed) {
            return null
        }
        return activityReference.get()
    }

    /**
     * 检查[Activity]状态
     *
     * 通常不需要，[activityState]会随着系统生命周期变化而变化，这里只做容错处理
     *
     * @return [Activity]状态
     */
    private fun checkActivityState() {
        activityReference.get() ?: let {
            activityState = ActivityState.DESTROYED
        }
    }

    override fun hashCode(): Int {
        return activityRecordId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ActivityRecord
        return activityRecordId == that.activityRecordId
    }

    override fun toString(): String {
        return "${activityCanonicalName}(${activityState}, ${activityRecordId})"
    }
}