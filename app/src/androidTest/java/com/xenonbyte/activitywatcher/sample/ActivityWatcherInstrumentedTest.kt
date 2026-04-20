package com.xenonbyte.activitywatcher.sample

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.xenonbyte.activitywatcher.ActivityLifeCycleCallback
import com.xenonbyte.activitywatcher.ActivityWatcher
import com.xenonbyte.activitywatcher.AppVisibilityCallback
import org.json.JSONArray
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class ActivityWatcherInstrumentedTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @After
    fun tearDown() {
        finishAllActivities()
    }

    @Test
    fun isExist_returnsTrueForActivityBelowTop() {
        startActivityWithShell(MainActivity::class.java)
        waitUntilTop(MainActivity::class.java)

        lateinit var mainActivity: MainActivity

        executeOnTop { activity ->
            val currentActivity = activity as MainActivity
            mainActivity = currentActivity
            currentActivity.startActivity(Intent(currentActivity, SingleTopActivity::class.java))
        }

        waitUntilTop(SingleTopActivity::class.java)

        instrumentation.runOnMainSync {
            assertTrue(ActivityWatcher.isExist(mainActivity))
            assertFalse(ActivityWatcher.isStackTop(mainActivity))
            assertEquals(
                SingleTopActivity::class.java.name,
                ActivityWatcher.getStackTop()?.activityCanonicalName
            )
        }
    }

    @Test
    fun activityLifecycleCallback_isRemovedWhenOwnerDestroyed() {
        val callback = RecordingActivityLifecycleCallback()
        startActivityWithShell(MainActivity::class.java)
        waitUntilTop(MainActivity::class.java)

        executeOnTop { activity ->
            ActivityWatcher.addActivityLifecycleCallback(activity as MainActivity, callback)
            activity.finish()
        }

        waitUntil {
            ActivityWatcher.getStackTop() == null
        }
        callback.clear()

        startActivityWithShell(SingleTopActivity::class.java)
        waitUntilTop(SingleTopActivity::class.java)

        assertTrue(callback.events.isEmpty())
        ActivityWatcher.removeActivityLifecycleCallback(callback)
    }

    @Test
    fun appVisibilityCallback_doesNotBounceDuringRecreate() {
        val callback = RecordingAppVisibilityCallback()
        ActivityWatcher.addAppVisibilityCallback(callback = callback)

        try {
            startActivityWithShell(MainActivity::class.java)
            waitUntilTop(MainActivity::class.java)
            callback.clear()

            lateinit var originalActivity: MainActivity
            var originalRecordId = -1
            executeOnTop { activity ->
                val currentActivity = activity as MainActivity
                originalActivity = currentActivity
                originalRecordId = ActivityWatcher.getActivityRecord(currentActivity)?.activityRecordId ?: -1
                currentActivity.recreate()
            }
            waitUntilRecreated(
                activityClass = MainActivity::class.java,
                previousActivity = originalActivity,
                expectedRecordId = originalRecordId
            )

            assertTrue(callback.events.isEmpty())
        } finally {
            ActivityWatcher.removeAppVisibilityCallback(callback)
        }
    }

    @Test
    fun singleInstanceActivity_createsSeparateTask() {
        startActivityWithShell(MainActivity::class.java)
        waitUntilTop(MainActivity::class.java)

        executeOnTop { activity ->
            activity.startActivity(Intent(activity, SingleInstanceActivity::class.java))
        }

        waitUntilTop(SingleInstanceActivity::class.java)

        val stackJson = ActivityWatcher.getStackJson()
        val tasks = JSONArray(stackJson)
        assertEquals(2, tasks.length())
        assertNotNull(ActivityWatcher.getActivityRecord(ActivityWatcher.getStackTop()!!.activityRecordId))

        val stacks = buildList {
            for (index in 0 until tasks.length()) {
                add(tasks.getJSONObject(index).getJSONArray("stack"))
            }
        }
        assertTrue(stacks.any { stackContains(it, MainActivity::class.java.name) })
        assertTrue(stacks.any { stackContains(it, SingleInstanceActivity::class.java.name) })
    }

    private fun stackContains(stack: JSONArray, activityName: String): Boolean {
        for (index in 0 until stack.length()) {
            if (stack.getString(index).startsWith(activityName)) {
                return true
            }
        }
        return false
    }

    private fun waitUntil(timeoutMs: Long = 5_000, condition: () -> Boolean) {
        val deadline = SystemClock.elapsedRealtime() + timeoutMs
        while (SystemClock.elapsedRealtime() < deadline) {
            instrumentation.waitForIdleSync()
            if (condition()) {
                return
            }
            SystemClock.sleep(50)
        }
        instrumentation.waitForIdleSync()
        assertTrue("Condition timed out after ${timeoutMs}ms", condition())
    }

    private fun waitUntilTop(activityClass: Class<out Activity>, timeoutMs: Long = 5_000) {
        waitUntil(timeoutMs) {
            ActivityWatcher.getStackTop()?.activityCanonicalName == activityClass.name
        }
    }

    private fun waitUntilRecreated(
        activityClass: Class<out Activity>,
        previousActivity: Activity,
        expectedRecordId: Int,
        timeoutMs: Long = 5_000
    ) {
        waitUntil(timeoutMs) {
            val top = ActivityWatcher.getStackTop() ?: return@waitUntil false
            if (top.activityCanonicalName != activityClass.name || top.activityRecordId != expectedRecordId) {
                return@waitUntil false
            }
            var currentActivity: Activity? = null
            top.execute { activity ->
                currentActivity = activity
            }
            currentActivity != null && currentActivity !== previousActivity
        }
    }

    private fun executeOnTop(block: (Activity) -> Unit) {
        instrumentation.runOnMainSync {
            val top = ActivityWatcher.getStackTop()
            assertNotNull("No stack top activity available", top)
            top!!.execute(block)
        }
    }

    private fun startActivityWithShell(activityClass: Class<out Activity>) {
        val component = ComponentName(instrumentation.targetContext, activityClass)
        val output = executeShellCommand("am start -W -n ${component.flattenToShortString()}")
        assertTrue(
            "Failed to start ${activityClass.name}.\n$output",
            output.contains("Status: ok") || output.contains("Warning: Activity not started")
        )
    }

    private fun executeShellCommand(command: String): String {
        val descriptor = instrumentation.uiAutomation.executeShellCommand(command)
        descriptor.use { parcelFileDescriptor ->
            BufferedReader(InputStreamReader(parcelFileDescriptor.fileDescriptor.inputStream())).use { reader ->
                return reader.readText()
            }
        }
    }

    private fun finishAllActivities(maxIterations: Int = 10) {
        repeat(maxIterations) {
            val top = ActivityWatcher.getStackTop() ?: return
            instrumentation.runOnMainSync {
                top.execute { activity ->
                    activity.finish()
                }
            }
            instrumentation.waitForIdleSync()
            SystemClock.sleep(100)
        }
    }
}

private fun java.io.FileDescriptor.inputStream() = java.io.FileInputStream(this)

private class RecordingActivityLifecycleCallback : ActivityLifeCycleCallback {
    val events = CopyOnWriteArrayList<String>()

    override fun onCreate(
        activity: android.app.Activity,
        activityRecordId: Int,
        savedInstanceState: android.os.Bundle?,
        isRestore: Boolean
    ) {
        events += "create:${activity.javaClass.name}:$activityRecordId:$isRestore"
    }

    override fun onStart(activity: android.app.Activity, activityRecordId: Int) {
        events += "start:${activity.javaClass.name}:$activityRecordId"
    }

    override fun onResume(activity: android.app.Activity, activityRecordId: Int) {
        events += "resume:${activity.javaClass.name}:$activityRecordId"
    }

    override fun onPause(activity: android.app.Activity, activityRecordId: Int) {
        events += "pause:${activity.javaClass.name}:$activityRecordId"
    }

    override fun onStop(activity: android.app.Activity, activityRecordId: Int) {
        events += "stop:${activity.javaClass.name}:$activityRecordId"
    }

    override fun onDestroy(activity: android.app.Activity, activityRecordId: Int, canRestore: Boolean) {
        events += "destroy:${activity.javaClass.name}:$activityRecordId:$canRestore"
    }

    fun clear() {
        events.clear()
    }
}

private class RecordingAppVisibilityCallback : AppVisibilityCallback {
    val foregroundCount = AtomicInteger(0)
    val backgroundCount = AtomicInteger(0)
    val events = CopyOnWriteArrayList<String>()

    override fun onForeground() {
        foregroundCount.incrementAndGet()
        events += "foreground"
    }

    override fun onBackground() {
        backgroundCount.incrementAndGet()
        events += "background"
    }

    fun clear() {
        foregroundCount.set(0)
        backgroundCount.set(0)
        events.clear()
    }
}
