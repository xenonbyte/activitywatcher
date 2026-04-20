# ActivityWatcher 1.0.3

## Release Title

`ActivityWatcher 1.0.3`

## Tag

`1.0.3`

## Release Notes

### Highlights

- 修复 `isExist(activity)` 仅检查当前全局栈顶的问题，改为全栈查找
- 修复基于 `LifecycleOwner` 的自动解绑逻辑，确保在 `ON_DESTROY` 时正确移除回调
- 修复配置变更 / `recreate()` 期间错误触发 `onBackground()` / `onForeground()` 的问题
- 修复 owner-scoped 回调在非主线程注册时的主线程约束与竞态窗口
- 新增 instrumentation tests，覆盖非栈顶存活判定、自动解绑、配置变更、防抖动和 `singleInstance` task 行为
- 重构 sample app，使 `standard / singleTop / singleInstance` 的 watcher 行为更直观
- 升级到 `compileSdk 36` / `targetSdk 36`
- 升级工具链到 `AGP 8.9.1 + Gradle 8.11.1`

### Verification

- `./gradlew :activitywatcher:assemble`
- `./gradlew :app:assembleDebug`
- `./gradlew :app:lintDebug`
- `./gradlew :app:connectedDebugAndroidTest`

真机 instrumentation 已通过以下场景：

- 非栈顶 `Activity` 的 `isExist(activity)` 判定
- `LifecycleOwner` 销毁后的自动解绑
- `recreate()` / 配置变更下应用前后台回调不抖动
- `singleInstance` Activity 的独立 task 行为

### Dependency

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.xenonbyte:activitywatcher:1.0.3'
}
```
