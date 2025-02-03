# ActivityWatcher

Android应用的Activity生命周期监听库，通过ActivityWatcher可轻松获取应用是否处于后台、当前栈顶Activity信息，以及应用的完整Activity栈信息



## 使用

### 1、初始化

```kotlin
ActivityWatcher.initialize(this)
```

> 推荐在`Application.onCreate`中初始化

### 2、获取栈顶Activity信息

```kotlin
val activityRecord:ActivityRecord? = ActivityWatcher.getStackTop()
```

**ActivityRecord属性**

- activityState：Activity状态
- activityRecordId：Activity唯一标识，Activity恢复也不会改变该值（如屏幕旋转）
- activityName：Activity类名
- activityCanonicalName：Activity类全路径名

### 3、安全的使用Activity

> [!IMPORTANT]
>
> 为防止`Activity`不规范使用带来风险，同时保证`Activity`使用安全；`ActivityRecord`不会直接暴露栈顶`Activity`引用，通过`ActivityRecord.execute`可以安全的使用`Activity`

**kotlin用法**

```kotlin
//如果栈顶Activity已销毁，execute方法不会执行
ActivityWatcher.getStackTop()?.execute { 
            //这里it就是Activity实例
            //比如使用Activity弹窗
            AlertDialog.Builder(it)
                .setMessage("test dialog")
                .show()
        }
```

**java用法**

```java
//如果栈顶Activity已销毁，execute方法不会执行
ActivityRecord record = ActivityWatcher.getStackTop();
if (record != null) {
    record.execute(activity -> {
        //比如使用Activity弹窗
        new AlertDialog.Builder(activity)
                .setMessage("test dialog")
                .show();
    });
}
```


### 4、获取完整Activity栈信息

```kotlin
ActivityWatcher.ActivityWatcher.getStackJson()
```

**输出格式如下：**

```json
[
  {
    "taskId": 735,
    "stack": [
      "com.xenonbyte.activitywatcher.sample.MainActivity(CREATED, 1)",
      "com.xenonbyte.activitywatcher.sample.SingleTopActivity(CREATED, 2)"
    ]
  },
  {
    "taskId": 736,
    "stack": [
      "com.xenonbyte.activitywatcher.sample.SingleInstanceActivity(RESUMED, 3)"
    ]
  }
]
```

> taskId：Activity所在Task的唯一标识
>
> stack：Activity任务栈
>
> stack元素：`Activity对象类的全路径(Activity状态, 内部生成的Activity唯一标识)`

### 5、监听Activity生命周期

```kotlin
 ActivityWatcher.addActivityLifecycleCallback
```
> 在具有生命周期的场景，推荐传入`LifecycleOwner`, 否则需要主动移除回调，防止内存泄漏

### 6、监听应用切后台切换

```kotlin
ActivityWatcher.addAppVisibilityCallback
```
> 在具有生命周期的场景，推荐传入`LifecycleOwner`, 否则需要主动移除回调，防止内存泄漏

### 7、方法列表

> [!NOTE]
>
> **Java调用时请注意返回值带`?`的方法，需要做空判断**

| 方法名                                                       | 描述                                 |
| ------------------------------------------------------------ | ------------------------------------ |
| **initialize(Application):Unit**                             | 初始化                               |
| **getStackJson(): String**                                   | 获取应用Activity栈的Json字符串       |
| **getStackTop(): ActivityRecord?**                           | 获取应用栈顶Activity信息             |
| **isStackTop(activity: Activity): Boolean**                  | 指定Activity是否在应用栈顶           |
| **getActivityState(activity: Activity): ActivityState?**     | 获取指定Activity状态                 |
| **isExist(activity: Activity): Boolean**                     | 指定Activity是否在应用中存活         |
| **getActivityRecord(activity: Activity): ActivityRecord?**   | 通过Activity获取Activity信息         |
| **getActivityRecord(activityRecordId: Int): ActivityRecord?** | 通过Activity唯一标识获取Activity信息 |
| **isAppBackground(): Boolean**                               | 应用当前是否处于后台                 |
| **addActivityLifecycleCallback(LifecycleOwner, ActivityLifeCycleCallback):Unit** | 添加Activity生命周期回调             |
| **removeActivityLifecycleCallback(ActivityLifeCycleCallback):Unit** | 移除Activity生命周期回调             |
| **addAppVisibilityCallback(LifecycleOwner, AppVisibilityCallback):Unit** | 添加应用前后台切换回调               |
| **removeAppVisibilityCallback(AppVisibilityCallback):Unit**  | 移除应用前后台切换回调               |



## Download

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.xenonbyte:activitywatcher:1.0.2'
}
```



## License

Copyright [2025] [xubo]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

