package com.xenonbyte.activitywatcher

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Activity观察
 *
 * @author xubo
 */
class ActivityWatcher private constructor() {
    private object Holder {
        val INSTANCE = ActivityWatcher()
    }

    companion object {
        private fun getInstance() = Holder.INSTANCE

        /**
         * [ActivityWatcher]初始化
         *
         * 尽可能早初始化, 推荐在[Application.onCreate]中初始化
         *
         * @param app Application实例
         */
        @JvmStatic
        fun initialize(app: Application) {
            val watcher = getInstance()
            watcher.init(app)
        }

        /**
         * 获取应用[Activity]栈的Json字符串描述
         *
         * @return 应用[Activity]栈的Json描述
         */
        @JvmStatic
        fun getStackJson(): String {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            return activityStack.getJson()
        }

        /**
         * 获取应用栈顶[ActivityRecord]实例
         *
         * @return [ActivityRecord]实例
         */
        @JvmStatic
        fun getStackTop(): ActivityRecord? {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            return activityStack.getStackTop()
        }

        /**
         * 指定[Activity]实例是否在应用栈顶
         *
         * @return true表示[Activity]实例在应用栈顶, false表示[Activity]实例不在应用栈顶
         */
        @JvmStatic
        fun isStackTop(activity: Activity): Boolean {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            return activityStack.getStackTop()?.match(activity) ?: false
        }

        /**
         * 获取指定[Activity]实例状态
         *
         * @return [ActivityState]实例
         */
        @JvmStatic
        fun getActivityState(activity: Activity): ActivityState? {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            return activityStack.findActivityRecord(activity)?.activityState
        }

        /**
         * 指定[Activity]实例是否在应用的Activity栈中
         *
         * @return true表示[Activity]实例在应用的[ActivityStack]中, false表示[Activity]实例不在应用的[ActivityStack]中
         */
        @JvmStatic
        fun isExist(activity: Activity): Boolean {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            return activityStack.getActivityTaskStack().peekOrNull()?.stack?.peekOrNull()?.match(activity) ?: false
        }

        /**
         * 通过[Activity]实例获取[ActivityRecord]
         *
         * @return [ActivityRecord]实例
         */
        fun getActivityRecord(activity: Activity): ActivityRecord? {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            return activityStack.findActivityRecord(activity)
        }

        /**
         * 通过activityRecordId实例获取[ActivityRecord]
         *
         * @return [ActivityRecord]实例
         */
        fun getActivityRecord(activityRecordId: Int): ActivityRecord? {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            return activityStack.findActivityRecord(activityRecordId)
        }

        /**
         * 应用当前是否处于后台
         *
         * @return true表示当前应用处于后台, false表示当前应用处于前台
         */
        @JvmStatic
        fun isAppBackground(): Boolean {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            return activityStack.isAppBackground
        }

        /**
         * 添加[Activity]生命周期回调
         *
         * @param owner 生命周期提供者
         * @param callback 生命周期回调实例
         */
        @JvmStatic
        @JvmOverloads
        fun addActivityLifecycleCallback(
            owner: LifecycleOwner = AppLifecycleOwner.get(), callback: ActivityLifeCycleCallback
        ) {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            activityStack.addActivityLifeCycleCallback(callback)
            owner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    source.lifecycle.removeObserver(this)
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        activityStack.removeActivityLifeCycleCallback(callback)
                    }
                }
            })
        }

        /**
         * 移除[Activity]生命周期回调
         *
         * @param callback 生命周期回调实例
         */
        @JvmStatic
        fun removeActivityLifecycleCallback(callback: ActivityLifeCycleCallback) {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            activityStack.removeActivityLifeCycleCallback(callback)
        }

        /**
         * 添加应用前后台切换回调
         *
         * @param owner 生命周期提供者
         * @param callback 应用前后台切换回调实例
         */
        @JvmStatic
        @JvmOverloads
        fun addAppVisibilityCallback(
            owner: LifecycleOwner = AppLifecycleOwner.get(), callback: AppVisibilityCallback
        ) {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            activityStack.addAppVisibilityCallback(callback)
            owner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    source.lifecycle.removeObserver(this)
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        activityStack.removeAppVisibilityCallback(callback)
                    }
                }
            })
        }

        /**
         * 移除应用前后台切换回调
         *
         * @param callback 应用前后台切换回调实例
         */
        @JvmStatic
        fun removeAppVisibilityCallback(callback: AppVisibilityCallback) {
            val watcher = getInstance()
            watcher.check()
            val activityStack = watcher.helper.getActivityStack()
            activityStack.removeAppVisibilityCallback(callback)
        }

        /**
         * 生成Activity唯一标识id
         *
         * @return Activity唯一标识id
         */
        internal fun generateActivityId(): Int {
            val watcher = getInstance()
            watcher.check()
            return watcher.helper.generateActivityId()
        }
    }

    /**
     * [Activity]帮助类实例
     */
    private val helper = ActivityHelperImpl()

    /**
     * [ActivityWatcher]是否初始化
     */
    private var isInitialize = false

    /**
     * [ActivityWatcher]初始化
     *
     * @param app Application实例
     */
    private fun init(app: Application) {
        if (isInitialize) {
            return
        }
        isInitialize = true
        AppLifecycleOwner.get().handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                helper.getActivityStack().onActivityCreated(activity, savedInstanceState)
            }

            override fun onActivityStarted(activity: Activity) {
                if (helper.getActivityStackCount() == 1) {
                    AppLifecycleOwner.get().handleLifecycleEvent(Lifecycle.Event.ON_START)
                }
                helper.getActivityStack().onActivityStarted(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                if (helper.getActivityStackCount() == 1) {
                    AppLifecycleOwner.get().handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                }
                helper.getActivityStack().onActivityResumed(activity)
            }

            override fun onActivityPaused(activity: Activity) {
                if (helper.getActivityStackCount() == 1) {
                    AppLifecycleOwner.get().handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                }
                helper.getActivityStack().onActivityPaused(activity)
                if (activity.isFinishing) {
                    helper.getActivityStack().onActivityStopped(activity)
                    helper.getActivityStack().onActivityDestroyed(activity)
                }
            }

            override fun onActivityStopped(activity: Activity) {
                if (helper.getActivityStackCount() == 1) {
                    AppLifecycleOwner.get().handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                }
                helper.getActivityStack().onActivityStopped(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                helper.getActivityStack().onActivitySaveInstanceState(activity, outState)
            }

            override fun onActivityDestroyed(activity: Activity) {
                helper.getActivityStack().onActivityDestroyed(activity)
            }

        })
    }

    /**
     * 安全检查
     *
     * 如果[ActivityWatcher]未初始化将抛出异常
     */
    private fun check() {
        if (!isInitialize) {
            throw ActivityWatcherUninitializedException();
        }
    }

}