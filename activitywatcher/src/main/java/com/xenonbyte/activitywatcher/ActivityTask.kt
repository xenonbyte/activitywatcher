package com.xenonbyte.activitywatcher

import android.app.Activity
import java.util.Stack

/**
 * [Activity]任务
 *
 * @param taskId 任务id
 * @author xubo
 */
class ActivityTask internal constructor(val taskId: Int) {
    /**
     * 存储[ActivityRecord]栈
     */
    val stack = Stack<ActivityRecord>()

    /**
     * 查看并移除[Activity]任务栈的栈顶[ActivityRecord]实例
     *
     * @return 栈顶[ActivityRecord]实例
     */
    fun pop(): ActivityRecord? {
        var activityRecord: ActivityRecord? = null
        if (!stack.empty()) {
            activityRecord = stack.pop()
        }
        return activityRecord
    }

    /**
     * 仅查看[Activity]任务栈的栈顶[ActivityRecord]实例，但不移除栈顶[ActivityRecord]
     *
     * @return 栈顶[ActivityRecord]实例
     */
    fun peek(): ActivityRecord? {
        var activityRecord: ActivityRecord? = null
        if (!stack.empty()) {
            activityRecord = stack.peek()
        }
        return activityRecord
    }

    /**
     * 压入[Activity]实例，并返回对应的[ActivityRecord]实例
     *
     * @param activity [Activity]实例
     * @return [ActivityRecord]实例
     */
    fun push(activity: Activity): ActivityRecord {
        var activityRecord = findActivityRecord(activity)?.apply {
            stack.remove(this)
        } ?: ActivityRecord.create(activity)
        stack.forEach {
            if (it.activityState.ordinal > ActivityState.CREATED.ordinal) {
                it.updateActivityState(ActivityState.CREATED)
            }
        }
        stack.push(activityRecord)
        return activityRecord
    }

    /**
     * 清空[Activity]任务栈
     */
    fun clear() {
        stack.clear()
    }

    /**
     * 通过[Activity]实例查找对应[ActivityRecord]
     *
     * @param activity [Activity]实例
     * @return 查找匹配成功返回[ActivityRecord]实例，否则返回null
     */
    fun findActivityRecord(activity: Activity): ActivityRecord? {
        stack.forEach {
            if (it.match(activity)) {
                return it
            }
        }
        return null
    }

    /**
     * 通过[activityRecordId]标识查找对应[ActivityRecord]
     *
     * @param activityRecordId [ActivityRecord]唯一标识id
     * @return 查找匹配成功返回[ActivityRecord]实例，否则返回null
     */
    fun findActivityRecord(activityRecordId: Int): ActivityRecord? {
        stack.forEach {
            if (activityRecordId == it.activityRecordId) {
                return it
            }
        }
        return null
    }

    /**
     * [ActivityTask]栈json字字符串
     *
     * @return 输出json字符串
     */
    fun toJsonString(): String {
        val buffer = StringBuffer()
        buffer.append("{")
        buffer.append("\"taskId\"")
        buffer.append(":")
        buffer.append(taskId)
        buffer.append(",")
        buffer.append("\"stack\"")
        buffer.append(":")
        buffer.append(stack.toFullString())
        buffer.append("}")
        return buffer.toString()
    }

    override fun toString(): String {
        return "{" +
                "taskId: ${taskId}, " +
                "stack: ${stack.toFullString()}" +
                "}"
    }

    /**
     * [ActivityRecord]栈字符串输出
     *
     * @return 输出字符串
     */
    private fun Stack<ActivityRecord>.toFullString(): String {
        val buffer = StringBuffer()
        buffer.append("[")
        val size = size
        withIndex().forEach { (index, activityRecord) ->
            buffer.append("\"")
            buffer.append(activityRecord.toString())
            buffer.append("\"")
            if (size - 1 != index) {
                buffer.append(", ")
            }
        }
        buffer.append("]")
        return buffer.toString()
    }

}