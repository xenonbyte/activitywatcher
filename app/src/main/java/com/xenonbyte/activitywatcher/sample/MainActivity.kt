package com.xenonbyte.activitywatcher.sample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.xenonbyte.activitywatcher.ActivityWatcher
import com.xenonbyte.activitywatcher.AppVisibilityCallback

class MainActivity : BaseSampleActivity() {
    override val screenTitleRes: Int = R.string.screen_main_title
    override val screenDescriptionRes: Int = R.string.screen_main_description

    override val primaryAction: SampleAction
        get() = SampleAction(R.string.action_open_single_top) {
            startActivity(Intent(this, SingleTopActivity::class.java))
        }

    override val secondaryAction: SampleAction
        get() = SampleAction(R.string.action_show_delay_dialog) {
            handler.postDelayed(::showDelayedDialog, 3_000)
        }

    override val tertiaryAction: SampleAction
        get() = SampleAction(R.string.action_recreate_main) {
            recreate()
        }

    private val handler = Handler(Looper.getMainLooper())

    private val appVisibilityCallback = object : AppVisibilityCallback {
        override fun onForeground() {
            Toast.makeText(this@MainActivity, R.string.toast_app_foreground, Toast.LENGTH_LONG).show()
        }

        override fun onBackground() {
            Toast.makeText(this@MainActivity, R.string.toast_app_background, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityWatcher.addAppVisibilityCallback(this, appVisibilityCallback)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun buildScenarioSummary(snapshot: WatcherSampleSnapshot): CharSequence {
        return buildString {
            appendLine("Open SingleTopActivity and watch the stack JSON grow from a standard screen.")
            appendLine("The delayed dialog resolves the real stack top at display time via ActivityRecord.execute { ... }.")
            append("Tap \"Recreate MainActivity\" to confirm the logical record remains coherent across recreation. Current recordId: #${snapshot.selfRecord?.activityRecordId ?: "N/A"}")
        }
    }

    private fun showDelayedDialog() {
        ActivityWatcher.getStackTop()?.execute { activity ->
            AlertDialog.Builder(activity)
                .setMessage(R.string.dialog_delay_message)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }
}
