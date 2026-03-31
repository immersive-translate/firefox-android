# Fenix 本地 Android Studio 运行说明（当前工作区）

## 1. 目标

本说明用于记录当前 `D:\firefox_new\android` 工作区中，为了让 `fenix` 在本地 Android Studio 可同步、可编译、可启动所做的关键处理与注意事项。

---

## 2. 当前最小目录结构

当前已精简为：

- `fenix`
- `android-components`
- `gradle`
- `netwerk`
- `.mozbuild-obj`
- `.gradle`

根目录关键文件保留：

- `shared-settings.gradle`
- `autopublish-settings.gradle`
- `version.txt`
- `gradle.py`
- `gradle.configure`

---

## 3. 已处理的关键问题

### 3.1 JDK 版本

- 构建需要 JDK 17+。
- 本地可用路径示例：`C:\ruanjian\Java\jdk-17\bin\java.exe`

---

### 3.2 Glean / 版本对齐

- `gradle/libs.versions.toml` 中 Glean 版本最终保持为：
  - `mozilla-glean = "66.1.0"`
  - `glean = "66.1.0"`
- 避免出现：
  - `Cannot resolve to a single Glean version. Requested: 66.1.0, A-C uses: 64.5.1`

---

### 3.3 lib-crash 缺失 `CrashReport.Annotation`

在 Android 子树环境下，完整 mozilla-central 生成链路缺失，导致 `CrashReport.Annotation` 相关引用找不到。  
已补本地回退文件：

- `android-components/components/lib/crash/src/main/java/mozilla/components/lib/crash/service/CrashReport.kt`

---

### 3.4 Xiaomi/MIUI StrictMode 启动崩溃

启动时出现系统类触发的 `DiskReadViolation`（非业务逻辑问题），已在 Fenix 严格模式忽略器中加厂商定向豁免：

- 文件：
  - `fenix/app/src/main/java/org/mozilla/fenix/perf/ThreadPenaltyDeathWithIgnoresListener.kt`
- 已覆盖 MIUI 相关类：
  - `miui.turbosched.TurboSchedMonitorImpl`
  - `miui.util.TypefaceHelper`
  - `miui.util.font.VFUtils`
  - `miui.util.font.ThemeVFManager`
  - `miui.util.TypefaceUtils`

规则只在 Xiaomi 设备生效。

---

## 4. 与构建相关的重要文件

- `gradle/libs.versions.toml`
- `shared-settings.gradle`
- `fenix/settings.gradle`
- `fenix/app/build.gradle`
- `android-components/components/lib/crash/build.gradle`
- `android-components/components/lib/crash/src/main/java/mozilla/components/lib/crash/service/CrashReport.kt`
- `fenix/app/src/main/java/org/mozilla/fenix/perf/ThreadPenaltyDeathWithIgnoresListener.kt`

---

## 5. `.mozbuild-obj` 说明

`.mozbuild-obj` 是构建输出/中间产物目录（objdir），包含生成代码和缓存。

- 可以删除：能腾空间
- 删除代价：下次需要完整重建，编译会慢；部分生成产物需重新生成

若当前环境已经稳定，建议保留。

---

## 6. 推荐日常操作

1. Android Studio 使用 JDK 17。
2. 每次改完 Gradle 配置后先 `Sync Project with Gradle Files`。
3. 遇到异常缓存问题时，优先：
   - `Clean Project`
   - 再 `Rebuild Project`
4. 若出现新的 StrictMode FATAL，优先看堆栈是否为 OEM 系统类（如 MIUI/Samsung），再决定是否定向忽略。

