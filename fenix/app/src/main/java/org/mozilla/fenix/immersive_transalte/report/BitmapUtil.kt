/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.report

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

object BitmapUtil {

    fun compress(context: Context, uri: Uri, limitKB: Int): Bitmap? {
        try {
            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            if (bitmap.byteCount / 1024F <= limitKB) return bitmap

            val outputStream = ByteArrayOutputStream()
            var quality = 90 // 从 90% 质量开始递减
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            val size = outputStream.toByteArray().size
            while (size != 0 && size / 1024F > limitKB && quality > 10) {
                outputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                quality -= 10
            }
            bitmap.recycle()

            val byteArray = outputStream.toByteArray()
            val compressBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            outputStream.close()

            return compressBitmap
        } catch (_: Exception) {
        } finally {
        }
        return null
    }

}
