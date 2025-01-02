package com.xenonbyte.activitywatcher.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.xenonbyte.activitywatcher.ActivityWatcher
import com.yuyh.jsonviewer.library.JsonRecyclerView

class SingleTopActivity : AppCompatActivity(), OnClickListener {
    private val seftBtn: Button by lazy {
        findViewById(R.id.self_btn)
    }
    private val nextBtn: Button by lazy {
        findViewById(R.id.next_btn)
    }
    private val printTv: JsonRecyclerView by lazy {
        findViewById(R.id.print_tv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_single_top_activity)

        seftBtn.setOnClickListener(this)
        nextBtn.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        printTv.bindJson(ActivityWatcher.getStackJson())
    }

    override fun onClick(v: View?) {
        when (v) {
            seftBtn -> {
                val intent = Intent(this, SingleTopActivity::class.java)
                startActivity(intent)
            }

            nextBtn -> {
                val intent = Intent(this, SingleInstanceActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
