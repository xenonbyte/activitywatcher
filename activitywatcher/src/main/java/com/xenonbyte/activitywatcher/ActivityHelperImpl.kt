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
        return activityStack.getActivityTaskStack().sumOf { it.stack.size }
    }

    /**
     * id生成器
     *
     * @author xubo
     */
    class IDGenerator {
        private val id = AtomicInteger(0)

        /**
         * 生成唯一标识id
         *
         * @return 唯一标识id
         */
        fun generateId(): Int {
            return id.incrementAndGet()
        }
    }
}
