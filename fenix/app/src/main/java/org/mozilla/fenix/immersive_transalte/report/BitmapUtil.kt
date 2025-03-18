/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.report

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import org.mozilla.fenix.immersive_transalte.bean.UploadFileBean
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object BitmapUtil {

    fun compress(context: Context, uri: Uri, limitKB: Int): UploadFileBean? {
        try {

            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            val outputStream = ByteArrayOutputStream()
            var quality = 90 // 从 90% 质量开始递减
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            var size = outputStream.toByteArray().size

            while (size != 0 && size / 1024F > limitKB && quality > 10) {
                outputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                quality -= 10
                size = outputStream.toByteArray().size
            }
            bitmap.recycle()

            val byteArray = outputStream.toByteArray()
            val compressBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            outputStream.close()

            // 转换成文件
            val file = File.createTempFile("img_" + System.currentTimeMillis(), ".jpg")
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(byteArray)
            fileOutputStream.flush()
            fileOutputStream.close()

            return UploadFileBean.create(compressBitmap, file)
        } catch (_: Exception) {
        } finally {
        }
        return null
    }

}
