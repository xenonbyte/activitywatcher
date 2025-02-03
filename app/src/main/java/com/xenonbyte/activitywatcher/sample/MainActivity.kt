package com.xenonbyte.activitywatcher.sample

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.xenonbyte.activitywatcher.ActivityWatcher
import com.xenonbyte.activitywatcher.AppVisibilityCallback
import com.yuyh.jsonviewer.library.JsonRecyclerView

class MainActivity : AppCompatActivity(), OnClickListener {
    private val nextBtn: Button by lazy {
        findViewById(R.id.next_btn)
    }
    private val delayDialogBtn: Button by lazy {
        findViewById(R.id.delay_dialog_btn)
    }
    private val printTv: JsonRecyclerView by lazy {
        findViewById(R.id.print_tv)
    }
    private val appVisibilityCallback = object : AppVisibilityCallback {
        override fun onForeground() {
            Toast.makeText(this@MainActivity, "app enters the foreground", Toast.LENGTH_LONG).show()
        }

        override fun onBackground() {
            Toast.makeText(this@MainActivity, "app enters the background", Toast.LENGTH_LONG).show()
        }

    }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main_activity)

        //注册App前后台切换监听
        ActivityWatcher.addAppVisibilityCallback(this, appVisibilityCallback)

        nextBtn.setOnClickListener(this)
        delayDialogBtn.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        printTv.bindJson(ActivityWatcher.getStackJson())
    }

    override fun onClick(v: View?) {
        when (v) {
            nextBtn -> {
                val intent = Intent(this, SingleTopActivity::class.java)
                startActivity(intent)
            }

            delayDialogBtn -> {
                handler.postDelayed({
                    //safely use the top Activity to display a Dialog
                    ActivityWatcher.getStackTop()?.execute {
                        AlertDialog.Builder(it)
                            .setMessage("delay dialog")
                            .setCancelable(true)
                            .setPositiveButton("ok", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    dialog?.dismiss()
                                }
                            })
                            .show()
                    }
                }, 3000)
            }
        }
    }
}
