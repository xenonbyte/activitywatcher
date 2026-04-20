package com.xenonbyte.activitywatcher.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.yuyh.jsonviewer.library.JsonRecyclerView

data class SampleAction(
    @StringRes val labelRes: Int,
    val onClick: () -> Unit
)

abstract class BaseSampleActivity : AppCompatActivity() {
    private val titleView: TextView by lazy { findViewById(R.id.title_tv) }
    private val descriptionView: TextView by lazy { findViewById(R.id.description_tv) }
    private val summaryView: TextView by lazy { findViewById(R.id.summary_tv) }
    private val scenarioView: TextView by lazy { findViewById(R.id.scenario_tv) }
    private val primaryActionButton: Button by lazy { findViewById(R.id.primary_action_btn) }
    private val secondaryActionButton: Button by lazy { findViewById(R.id.secondary_action_btn) }
    private val tertiaryActionButton: Button by lazy { findViewById(R.id.tertiary_action_btn) }
    private val stackJsonView: JsonRecyclerView by lazy { findViewById(R.id.print_tv) }

    @get:StringRes
    protected abstract val screenTitleRes: Int

    @get:StringRes
    protected abstract val screenDescriptionRes: Int

    protected abstract val primaryAction: SampleAction

    protected open val secondaryAction: SampleAction? = null

    protected open val tertiaryAction: SampleAction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_sample_activity)
        bindStaticContent()
        bindAction(primaryActionButton, primaryAction)
        bindAction(secondaryActionButton, secondaryAction)
        bindAction(tertiaryActionButton, tertiaryAction)
    }

    override fun onResume() {
        super.onResume()
        refreshUi()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        refreshUi()
    }

    protected fun refreshUi() {
        val snapshot = WatcherSampleFormatter.create(this)
        summaryView.text = snapshot.summary
        scenarioView.text = buildScenarioSummary(snapshot)
        stackJsonView.bindJson(snapshot.stackJson)
    }

    protected open fun buildScenarioSummary(snapshot: WatcherSampleSnapshot): CharSequence {
        return getString(screenDescriptionRes)
    }

    private fun bindStaticContent() {
        titleView.setText(screenTitleRes)
        descriptionView.setText(screenDescriptionRes)
    }

    private fun bindAction(button: Button, action: SampleAction?) {
        if (action == null) {
            button.visibility = View.GONE
            return
        }
        button.visibility = View.VISIBLE
        button.setText(action.labelRes)
        button.setOnClickListener {
            action.onClick()
        }
    }
}
