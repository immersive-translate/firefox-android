/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.utils

import java.security.MessageDigest

object MD5Util {

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
