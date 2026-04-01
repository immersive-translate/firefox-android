# 16KB SO 对齐升级 - 最终状态

## 最终结果

- `:app:assembleLocalDebug` 构建通过。
- 真机启动验证通过（无闪退）。
- 生成 APK 中 `arm64-v8a` 下所有 `.so` 均通过 ELF `PT_LOAD >= 16384` 检查（`bad_count=0`）。
- 升级 `jna` 后，`libjnidispatch.so` 的相关告警在你本地验证中已消失。

已验证 APK：

- `D:\AndroidStudioProjects\firefox-android\fenix\app\build\outputs\apk\local\debug\1.2.8-local-debug-arm64-v8a.apk`

## 最终版本组合

- GeckoView：`136.0.20250317200840`
- Application Services：`137.0`
- Glean：`63.1.0`
- JNA：`5.18.1`（通过依赖解析策略强制）

对应修改文件：

- `android-components/plugins/dependencies/src/main/java/Gecko.kt`
- `android-components/plugins/dependencies/src/main/java/ApplicationServices.kt`
- `android-components/plugins/dependencies/src/main/java/DependenciesPlugin.kt`
- `fenix/build.gradle`（强制 `net.java.dev.jna:jna:5.18.1`）
- `android-components/build.gradle`（强制 `net.java.dev.jna:jna:5.18.1`）

## 已完成迁移内容

### 1) `service-sync-logins` 适配 AppServices 137

修改文件：

- `android-components/components/service/sync-logins/src/main/java/mozilla/components/service/sync/logins/Types.kt`
- `android-components/components/service/sync-logins/src/main/java/mozilla/components/service/sync/logins/SyncableLoginsStorage.kt`
- `android-components/components/service/sync-logins/src/main/java/mozilla/components/service/sync/logins/LoginsCrypto.kt`

关键改动：

- 适配 `mozilla.appservices.logins.Login / LoginEntry` 的扁平化模型。
- `DatabaseLoginsStorage` 改为新构造参数（需要 `KeyManager`）。
- `add / update / addOrUpdate / findLoginToUpdate` 改为新签名（移除 raw key 参数）。
- 保持 A-C `EncryptedLogin` 接口兼容：`secFields` 改为基于 key 的真实加密载荷，并保留兼容回退。
- 错误类型映射更新：`IncorrectKey` 对应新异常 `InvalidKey`。

### 2) Glean 新 API 适配（避免 `-Werror` 失败）

修改文件：

- `fenix/app/src/main/java/org/mozilla/fenix/components/metrics/GleanMetricsService.kt`

变更：

- `Glean.setUploadEnabled(...)` -> `Glean.setCollectionEnabled(...)`

## 16KB 验证细节

### 迁移过程中关键实测结论

- GeckoView `134.0.20250120135430`：`libxul.so PT_LOAD=4096`
- GeckoView `136.0.20250317200840`：Gecko 相关 `.so` 为 `PT_LOAD=16384`
- AppServices `136.0`：`libmegazord.so PT_LOAD=4096`
- AppServices `137.0`：`libmegazord.so PT_LOAD=16384`

### 最终 APK 校验

- 脚本输出：`bad_count=0`
- 含义：`lib/arm64-v8a` 下无 `PT_LOAD < 16384` 的 `.so`

## 关于 `libjnidispatch.so`

- `libjnidispatch.so` 来源于 `net.java.dev.jna:jna`（由 AppServices 传递依赖带入）。
- 本次已强制升级到 `jna 5.18.1`，你本地验证结果：该库告警消失，且 APK 可正常启动。
- 结论：当前版本组合下，不再阻塞 16KB 对齐目标。

## 构建与校验命令

```powershell
cd fenix
$env:JAVA_HOME='C:\ruanjian\Java\jdk-17'
.\gradlew.bat :app:assembleLocalDebug --no-daemon --max-workers=1
```

```powershell
# ELF PT_LOAD 检查
$readobj='C:\Users\pengx\AppData\Local\Android\Sdk\ndk\25.1.8937393\toolchains\llvm\prebuilt\windows-x86_64\bin\llvm-readobj.exe'
& $readobj --program-headers <path-to-so>
```

## 上架前剩余动作

1. 使用相同版本组合构建 release APK/AAB。
2. 在 Play Console 执行预检。
3. 固化依赖版本（避免 `jna` 被后续传递依赖回退）。
