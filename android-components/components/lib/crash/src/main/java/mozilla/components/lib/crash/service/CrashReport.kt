/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.lib.crash.service

/**
 * Fallback crash annotation model for Android-subtree builds.
 *
 * In full mozilla-central checkouts this is generated from upstream crash
 * annotation definitions. Local Android-only checkouts don't have that
 * generation input, so we keep a minimal compatible copy here.
 */
internal object CrashReport {
    @Suppress("EnumEntryName")
    enum class Annotation(private val key: String) {
        ProductName("ProductName"),
        ProductID("ProductID"),
        Version("Version"),
        ApplicationBuildID("ApplicationBuildID"),
        AndroidComponentVersion("AndroidComponentVersion"),
        GleanVersion("GleanVersion"),
        ApplicationServicesVersion("ApplicationServicesVersion"),
        GeckoViewVersion("GeckoViewVersion"),
        BuildID("BuildID"),
        Vendor("Vendor"),
        Breadcrumbs("Breadcrumbs"),
        useragent_locale("useragent_locale"),
        DistributionID("DistributionID"),
        JavaStackTrace("JavaStackTrace"),
        JavaException("JavaException"),
        CrashType("CrashType"),
        ReleaseChannel("ReleaseChannel"),
        StartupTime("StartupTime"),
        CrashTime("CrashTime"),
        Android_PackageName("Android_PackageName"),
        Android_Manufacturer("Android_Manufacturer"),
        Android_Model("Android_Model"),
        Android_Board("Android_Board"),
        Android_Brand("Android_Brand"),
        Android_Device("Android_Device"),
        Android_Display("Android_Display"),
        Android_Fingerprint("Android_Fingerprint"),
        Android_Hardware("Android_Hardware"),
        Android_Version("Android_Version"),
        Android_CPU_ABI("Android_CPU_ABI"),
        Android_CPU_ABI2("Android_CPU_ABI2"),
        additional_minidumps("additional_minidumps"),
        Android_ProcessName("Android_ProcessName"),
        InstallTime("InstallTime"),
        AsyncShutdownTimeout("AsyncShutdownTimeout"),
        BackgroundTaskName("BackgroundTaskName"),
        EventLoopNestingLevel("EventLoopNestingLevel"),
        FontName("FontName"),
        GPUProcessLaunchCount("GPUProcessLaunchCount"),
        ipc_channel_error("ipc_channel_error"),
        IsGarbageCollecting("IsGarbageCollecting"),
        MainThreadRunnableName("MainThreadRunnableName"),
        MozCrashReason("MozCrashReason"),
        ProfilerChildShutdownPhase("ProfilerChildShutdownPhase"),
        QuotaManagerShutdownTimeout("QuotaManagerShutdownTimeout"),
        ShutdownProgress("ShutdownProgress"),
        StackTraces("StackTraces"),
        StartupCrash("StartupCrash"),
        HeadlessMode("HeadlessMode"),
        AvailablePageFile("AvailablePageFile"),
        AvailablePhysicalMemory("AvailablePhysicalMemory"),
        AvailableSwapMemory("AvailableSwapMemory"),
        AvailableVirtualMemory("AvailableVirtualMemory"),
        JSLargeAllocationFailure("JSLargeAllocationFailure"),
        JSOutOfMemory("JSOutOfMemory"),
        LowPhysicalMemoryEvents("LowPhysicalMemoryEvents"),
        OOMAllocationSize("OOMAllocationSize"),
        PurgeablePhysicalMemory("PurgeablePhysicalMemory"),
        SystemMemoryUsePercentage("SystemMemoryUsePercentage"),
        TextureUsage("TextureUsage"),
        TotalPageFile("TotalPageFile"),
        TotalPhysicalMemory("TotalPhysicalMemory"),
        TotalVirtualMemory("TotalVirtualMemory"),
    ;

        override fun toString(): String = key
    }
}

