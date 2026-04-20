package com.xenonbyte.activitywatcher.sample

import android.content.Intent
import android.os.Bundle

class SingleTopActivity : BaseSampleActivity() {
    override val screenTitleRes: Int = R.string.screen_single_top_title
    override val screenDescriptionRes: Int = R.string.screen_single_top_description

    override val primaryAction: SampleAction
        get() = SampleAction(R.string.action_launch_single_top_self) {
            startActivity(Intent(this, SingleTopActivity::class.java))
        }

    override val secondaryAction: SampleAction
        get() = SampleAction(R.string.action_open_single_instance) {
            startActivity(Intent(this, SingleInstanceActivity::class.java))
        }

    override val tertiaryAction: SampleAction
        get() = SampleAction(R.string.action_finish_current) {
            finish()
        }

    private var onNewIntentCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        onNewIntentCount = savedInstanceState?.getInt(KEY_NEW_INTENT_COUNT) ?: 0
        super.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent?) {
        onNewIntentCount++
        super.onNewIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_NEW_INTENT_COUNT, onNewIntentCount)
    }

    override fun buildScenarioSummary(snapshot: WatcherSampleSnapshot): CharSequence {
        val selfRecordId = snapshot.selfRecord?.activityRecordId ?: "N/A"
        return buildString {
            appendLine("This screen is declared as singleTop.")
            appendLine("Launching it again while it is already on top should reuse the same Activity instance instead of pushing another copy.")
            append("Observed recordId: #$selfRecordId, onNewIntent count: $onNewIntentCount")
        }
    }

    private companion object {
        const val KEY_NEW_INTENT_COUNT = "key_new_intent_count"
    }
}
