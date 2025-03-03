package mozilla.components.feature.contextmenu.utils

import android.text.TextUtils
import java.security.MessageDigest

/**
 * hash 工具类
 */
object ImageIdUtil {

    fun getImageId(string: String): String? {
        getContent(string)?.let {
            val imtPrefix = "imt_"
            if (it.startsWith(imtPrefix)) {
                return it.replace(imtPrefix, "")
            }
        }
        return null
    }

    fun hasImageId(string: String): Boolean {
        return !TextUtils.isEmpty(getImageId(string))
    }

    fun isProImage(string: String):Boolean {
        return string.startsWith("https://store1.immersivetranslate.com")
    }

    private fun getContent(string: String): String? {
        val startIndex = string.indexOf(";")
        val endIndex = string.lastIndexOf(";")
        if (startIndex == endIndex || startIndex == -1 || endIndex == -1) {
            return null
        }
        try {
            return string.substring(startIndex + 1, endIndex)
        } catch (_: Exception) {
        }
        return null
    }

    fun getHashSha256(string: String): String {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(string.toByteArray())
            val digest = md.digest()
            return bytesToHex(digest)
        } catch (_: Exception) {
        }
        return ""
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

}
