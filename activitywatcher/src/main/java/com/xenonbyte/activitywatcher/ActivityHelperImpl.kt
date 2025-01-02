package com.xenonbyte.activitywatcher

import android.app.Activity
import java.util.concurrent.atomic.AtomicInteger

/**
 * [ActivityHelper]帮助类实现
 *
 * @author xubo
 */
internal class ActivityHelperImpl : ActivityHelper {
    /**
     * id生成器
     */
    private val idGenerator = IDGenerator()

    /**
     * [Activity]栈
     */
    private val activityStack = ActivityStack()

    override fun generateActivityId(): Int {
        return idGenerator.generateId()
    }

    override fun getActivityStack(): ActivityStack {
        return activityStack
    }

    override fun getActivityStackCount(): Int {
        var size = 0
        activityStack.getActivityTaskStack().forEach {
            size += (it.stack.size)
        }
        return size
    }

    /**
     * id生成器
     *
     * @author xubo
     */
    class IDGenerator {
        private var id: AtomicInteger = AtomicInteger(0)

        /**
         * 生成唯一标识id
         *
         * @return 唯一标识id
         */
        fun generateId(): Int {
            var oldId: Int = id.get()
            while (!id.compareAndSet(oldId, oldId + 1)) {
                oldId = id.get()
            }
            return id.get()
        }
    }
}