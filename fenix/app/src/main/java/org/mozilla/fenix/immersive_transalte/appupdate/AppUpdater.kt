/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.appupdate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.mozilla.fenix.immersive_transalte.ImmersivePluginConfig
import org.mozilla.fenix.immersive_transalte.bean.AppVersionBean.AppVersion
import org.mozilla.fenix.immersive_transalte.net.service.HomePageService
import org.mozilla.fenix.immersive_transalte.utils.SPUtil
import java.io.File
import java.io.FileOutputStream


/**
 * app 更新
 */
object AppUpdater {
    private val scope = MainScope()
    private const val apkName = "update.apk"
    private var installPermissionLauncher: ActivityResultLauncher<Intent>? = null

    private var appVersion: AppVersion? = null
    private var installFile: File? = null

    // init ActivityResultLauncher
    fun init(activity: AppCompatActivity) {
        installPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            installFile?.let {
                gotoInstall(it, activity)
            }
        }
    }

    fun checkVersion(activity: AppCompatActivity) {
        Handler(Looper.getMainLooper()).postDelayed({ checkAppUpdate(activity) }, 500)
    }

    private fun checkAppUpdate(activity: AppCompatActivity) {
        val appChannel = ImmersivePluginConfig.localPluginChannel
        if (TextUtils.equals(appChannel, "google")) {
            return
        }

        scope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                HomePageService.checkAppUpdate()
            }
            appVersion = response.data?.data?.appUpdate

            val isNotifyUpdate = appVersion?.isShowUpdateNotification ?: false
            if (!isNotifyUpdate) {
                return@launch
            }

            val isNeedUpdate = appVersion?.let { checkVersion(activity, it) } ?: false
            if (!isNeedUpdate) {
                return@launch
            }

            installFile = withContext(Dispatchers.IO) {
                downloadApk(activity, appVersion!!.downloadUrl)
            }

            // 弹框提醒安装
            installFile?.let {
                AppUpdateDialog(
                    activity, appVersion!!,
                    { saveVersionNotify(activity) },
                    { gotoInstall(activity) },
                ).show()
            }

        }

    }

    /**
     * install apk
     */
    private fun installApk(apkFile: File, activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !activity.packageManager.canRequestPackageInstalls()
        ) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            intent.data = Uri.parse("package:" + activity.packageName)
            installPermissionLauncher?.launch(intent)
            return
        }
        gotoInstall(apkFile, activity)
    }

    /**
     * goto install apk
     */
    private fun gotoInstall(apkFile: File, activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !activity.packageManager.canRequestPackageInstalls()
        ) {
            return
        }
        val apkUri: Uri = FileProvider.getUriForFile(
            activity,
            activity.packageName + ".fileprovider",
            apkFile,
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }

    /**
     * goto install apk
     */
    fun gotoInstall(activity: Activity) {
        installFile?.let {
            gotoInstall(it, activity)
        }
    }

    /**
     * download apk
     */
    private fun downloadApk(cxt: Context, apkUrl: String): File? {
        // 创建下载的目标文件
        try {
            val apkFile = File(cxt.filesDir, apkName)
            if (apkFile.exists()) {
                apkFile.delete()
            }
            apkFile.createNewFile()

            val client = OkHttpClient()
            val request = Request.Builder().url(apkUrl).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful && response.body != null) {
                // 保存到文件
                val inputStream = response.body!!.byteStream()
                val outputStream = FileOutputStream(apkFile)
                val buffer = ByteArray(4096 * 10)
                var bytesRead: Int
                while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                inputStream.close()

                return apkFile
            }
        } catch (_: Exception) {
        }

        return null
    }

    /**
     * check is need update
     */
    private fun checkVersion(
        activity: Activity,
        appVersion: AppVersion,
    ): Boolean {
        if (TextUtils.isEmpty(appVersion.downloadUrl)) {
            return false
        }
        val appVersionCode = getVersionCode(activity)
        if (appVersionCode >= appVersion.versionCode) {
            return false
        }
        val hasNotifyVersion = SPUtil.getAppVersionNotify(activity)
        return hasNotifyVersion < appVersion.versionCode
    }

    /**
     * get app versionCode
     */
    @Suppress("DEPRECATION")
    private fun getVersionCode(ctx: Context): Long {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ctx.packageManager.getPackageInfo(ctx.packageName, 0).longVersionCode
            } else {
                ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionCode.toLong()
            }
        } catch (_: Exception) {
            Long.MAX_VALUE
        }
    }

    private fun saveVersionNotify(context: Context) {
        appVersion?.let {
            SPUtil.saveAppVersionNotify(context, it.versionCode)
        }
    }
}
