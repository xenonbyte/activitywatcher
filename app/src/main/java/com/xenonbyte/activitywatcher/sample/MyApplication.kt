package com.xenonbyte.activitywatcher.sample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.xenonbyte.activitywatcher.ActivityLifeCycleCallback
import com.xenonbyte.activitywatcher.ActivityWatcher

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //初始化ActivityWatcher
        ActivityWatcher.initialize(this)
        ActivityWatcher.addActivityLifecycleCallback(callback = object : ActivityLifeCycleCallback {
            override fun onCreate(
                activity: Activity,
                activityRecordId: Int,
                savedInstanceState: Bundle?,
                isRestore: Boolean
            ) {
                Log.d(
                    "ActivityWatcher",
                    "Activity: $activity >> onCreate(activityRecordId = ${activityRecordId}, isRestore = $isRestore)"
                )
            }

            override fun onStart(activity: Activity, activityRecordId: Int) {
                Log.d("ActivityWatcher", "Activity: $activity >> onStart(activityRecordId = $activityRecordId)")
            }

            override fun onResume(activity: Activity, activityRecordId: Int) {
                Log.d("ActivityWatcher", "Activity: $activity >> onResume(activityRecordId = $activityRecordId)")
            }

            override fun onPause(activity: Activity, activityRecordId: Int) {
                Log.d("ActivityWatcher", "Activity: $activity >> onPause(activityRecordId = $activityRecordId, isFinishing = ${activity.isFinishing})")
            }

            override fun onStop(activity: Activity, activityRecordId: Int) {
                Log.d("ActivityWatcher", "Activity: $activity >> onStop(activityRecordId = $activityRecordId)")
            }

            override fun onDestroy(activity: Activity, activityRecordId: Int, canRestore: Boolean) {
                Log.d("ActivityWatcher", "Activity: $activity >> onDestroy(activityRecordId = $activityRecordId, canRestore = $canRestore)")
            }

            override fun onSaveInstanceState(activity: Activity, activityRecordId: Int, outState: Bundle) {
                Log.d("ActivityWatcher", "Activity: $activity >> onSaveInstanceState(activityRecordId = $activityRecordId)")
            }

        })
    }
}