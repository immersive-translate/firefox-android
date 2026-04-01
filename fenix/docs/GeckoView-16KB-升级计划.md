# GeckoView 16KB 对齐升级计划

## 1. 背景与目标
- Google Play 对 Android 15+ 设备要求原生库支持 16KB page size（生效时间：2025-11-01）。
- 当前项目 GeckoView 固定版本为 `133.0.20241204213141`，不满足 16KB 要求。
- Mozilla 已说明 GeckoView **v144 及以上**支持 Android 16KB page size。
- 本计划目标：完成 GeckoView 升级、回归验证与发布门禁建设，确保可持续满足 Play 要求。

## 2. 当前基线
- GeckoView 版本定义：`android-components/plugins/dependencies/src/main/java/Gecko.kt`
- 当前值：`const val version = "133.0.20241204213141"`
- 渠道：`GeckoChannel.RELEASE`
- 相关耦合：
- Android Components（A-C）与 Fenix 共用依赖插件和版本管理。
- Application Services 当前版本为 `134.0`。
- Fenix 业务代码中有多处直接 `org.mozilla.geckoview` API 引用（含自定义模块）。

## 3. 升级策略
- 最低目标版本：`GeckoView 144.x`。
- 建议目标版本：选定一个可用的 `RELEASE` 通道高位稳定版本（>=144），优先减少后续二次升级成本。
- 先做 GeckoView 单点升级验证，再评估是否同步升级 A-C / AppServices 以降低长期维护风险。

## 4. 实施范围
- 必改：
- GeckoView 依赖版本（`Gecko.kt`）。
- 编译期 API 兼容修复（Fenix + android-components + 自定义扩展模块）。
- 16KB 对齐与运行验证流水线。
- 建议改：
- CI 中加入 16KB 检查门禁。
- 发布前验证清单模板化。

## 5. 分阶段执行计划

### 阶段 A：准备与基线冻结（0.5 天）
- 锁定当前可发布 commit，打基线 tag。
- 记录当前产物（AAB/APK）与关键指标：
- 冷启动耗时、崩溃率、包体积、首屏渲染、核心路径成功率。
- 明确升级分支策略：`feature/gv-16kb-upgrade`。

### 阶段 B：依赖升级与编译修复（1~2 天）
- 修改 `Gecko.kt` 的 `Gecko.version` 到候选版本（>=144）。
- 执行全量编译（app + 关键模块）。
- 修复编译错误/警告（项目启用了 `allWarningsAsErrors`）。
- 对直接 geckoview API 使用点做兼容调整（重点：自定义 `immersive_transalte` 相关代码）。

### 阶段 C：功能回归（2~3 天）
- 核心业务回归：
- 启动/恢复、普通浏览、标签页、多窗口、下载、文件上传、PDF、登录、同步、翻译、扩展插件。
- Gecko 高风险能力回归：
- WebExtension、权限弹窗、媒体播放、打印/保存、页面翻译入口。
- 稳定性回归：
- ANR/Crash、内存、后台恢复、弱网与离线场景。

### 阶段 D：16KB 合规验证（1 天）
- 产物对齐检查（APK）：
- `zipalign -c -P 16 -v 4 <apk>`
- 设备页大小验证（16KB 环境）：
- `adb shell getconf PAGE_SIZE`，预期 `16384`
- 对所有 ABI 的 `.so` 执行 ELF 段对齐检查（按 Android 官方方法）。
- 输出验证报告（命令、截图、构建号、结论）。

### 阶段 E：灰度与发布（0.5~1 天）
- 内部灰度渠道发布，观察崩溃与关键行为。
- 验证通过后合并主分支并发布正式版本。

## 6. 验收标准（DoD）
- 编译与测试：
- `:app:assemble*`、关键单测与 UI Smoke 通过。
- 无阻断级回归缺陷（P0/P1）。
- 16KB 合规：
- APK 对齐检查通过。
- 16KB 设备运行验证通过。
- 发布保障：
- 变更说明、风险说明、回滚方案齐全。
- CI 已加入 16KB 检查门禁（至少对 Release 构建生效）。

## 7. 风险与应对
- 风险 1：Gecko API 变更导致编译或行为回归
- 应对：先编译修复，再按功能域分批回归；高风险模块安排专人盯测。
- 风险 2：仅升级 Gecko，未同步 A-C/AppServices 带来隐藏兼容问题
- 应对：先最小闭环上线；并行准备“版本线同步”二期计划。
- 风险 3：第三方 Native 库不满足 16KB
- 应对：将 `.so` 对齐检查纳入 CI，定位并替换不合规依赖。

## 8. 回滚方案
- 保留升级前基线 tag，可快速回退依赖版本。
- 若灰度出现高风险问题：
- 立即停止扩量。
- 回滚到基线版本重新发包。
- 将问题归档到兼容清单，进入下一轮升级修复。

## 9. 建议任务拆分（可直接建 Jira/Issue）
- 任务 1：升级 GeckoView 版本并修复编译
- 任务 2：Gecko API 兼容扫描与代码修复（含自定义模块）
- 任务 3：16KB 合规校验脚本与 CI 接入
- 任务 4：核心业务回归测试与报告
- 任务 5：灰度发布与线上观察

## 10. 参考资料
- Android 16KB page size 指南：<https://developer.android.com/guide/practices/page-sizes>
- Mozilla Bug（16KB 支持说明）：<https://bugzilla.mozilla.org/show_bug.cgi?id=1992851>
- 对应变更提交：<https://github.com/mozilla-firefox/firefox/commit/6d67e8304793>
