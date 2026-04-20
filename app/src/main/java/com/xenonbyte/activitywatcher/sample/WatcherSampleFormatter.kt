package com.xenonbyte.activitywatcher.sample

import android.app.Activity
import com.xenonbyte.activitywatcher.ActivityRecord
import com.xenonbyte.activitywatcher.ActivityWatcher
import org.json.JSONArray

data class WatcherSampleSnapshot(
    val stackJson: String,
    val taskCount: Int,
    val selfRecord: ActivityRecord?,
    val topRecord: ActivityRecord?,
    val summary: String
)

object WatcherSampleFormatter {
    fun create(activity: Activity): WatcherSampleSnapshot {
        val stackJson = ActivityWatcher.getStackJson()
        val taskCount = JSONArray(stackJson).length()
        val selfRecord = ActivityWatcher.getActivityRecord(activity)
        val topRecord = ActivityWatcher.getStackTop()
        return WatcherSampleSnapshot(
            stackJson = stackJson,
            taskCount = taskCount,
            selfRecord = selfRecord,
            topRecord = topRecord,
            summary = buildSummary(activity, taskCount, selfRecord, topRecord)
        )
    }

    private fun buildSummary(
        activity: Activity,
        taskCount: Int,
        selfRecord: ActivityRecord?,
        topRecord: ActivityRecord?
    ): String {
        return buildString {
            appendLine("Screen      : ${activity.javaClass.simpleName}")
            appendLine("RecordId    : ${selfRecord?.activityRecordId ?: "N/A"}")
            appendLine("State       : ${selfRecord?.activityState ?: "N/A"}")
            appendLine("Is Exist    : ${yesNo(ActivityWatcher.isExist(activity))}")
            appendLine("Is StackTop : ${yesNo(ActivityWatcher.isStackTop(activity))}")
            appendLine("App In Back : ${yesNo(ActivityWatcher.isAppBackground())}")
            appendLine("Task Count  : $taskCount")
            append("Stack Top   : ${formatTopRecord(topRecord)}")
        }
    }

    private fun formatTopRecord(record: ActivityRecord?): String {
        if (record == null) {
            return "N/A"
        }
        return "${simpleName(record.activityCanonicalName)} (#${record.activityRecordId}, ${record.activityState})"
    }

    private fun simpleName(canonicalName: String?): String {
        return canonicalName?.substringAfterLast('.') ?: "Unknown"
    }

    private fun yesNo(value: Boolean): String {
        return if (value) "Yes" else "No"
    }
}
