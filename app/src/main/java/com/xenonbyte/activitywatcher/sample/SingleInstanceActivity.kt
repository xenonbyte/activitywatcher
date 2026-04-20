package com.xenonbyte.activitywatcher.sample

import android.content.Intent

class SingleInstanceActivity : BaseSampleActivity() {
    override val screenTitleRes: Int = R.string.screen_single_instance_title
    override val screenDescriptionRes: Int = R.string.screen_single_instance_description

    override val primaryAction: SampleAction
        get() = SampleAction(R.string.action_back_to_single_top) {
            startActivity(Intent(this, SingleTopActivity::class.java))
        }

    override val secondaryAction: SampleAction
        get() = SampleAction(R.string.action_finish_current) {
            finish()
        }

    override fun buildScenarioSummary(snapshot: WatcherSampleSnapshot): CharSequence {
        val topRecordId = snapshot.topRecord?.activityRecordId ?: "N/A"
        val isolationState = if (snapshot.taskCount > 1) {
            "The watcher currently sees this screen isolated in its own task."
        } else {
            "Open this screen from another activity to observe task separation."
        }
        return buildString {
            appendLine("This screen uses launchMode=singleInstance.")
            appendLine("Current task count: ${snapshot.taskCount}, stack-top recordId: #$topRecordId.")
            append(isolationState)
        }
    }
}
