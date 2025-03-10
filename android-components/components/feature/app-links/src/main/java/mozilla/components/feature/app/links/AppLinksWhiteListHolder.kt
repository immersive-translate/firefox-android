package mozilla.components.feature.app.links

import android.net.Uri

object AppLinksWhiteListHolder {
    private val whiteList = mutableListOf<String>()

    /**
     * Initialize the white list of app links.
     */
    init {
        whiteList.add("youtube")
        /*whiteList.add("tiktok")
        whiteList.add("snssdk1180")
        whiteList.add("aweme")*/
    }

    /**
     * Update the white list of app links.
     */
    fun updateWhiteList(urls: List<String>) {
        whiteList.clear()
        whiteList.addAll(urls)
    }

    /**
     * Check if the given uri is in the white list.
     */
    fun contains(uri: Uri): Boolean {
        uri.host?.let {
            val parts = it.split(".")
            if (parts.size >= 2) {
                val domain = parts[parts.size - 2]
                if (whiteList.contains(domain)) {
                    return true
                }
            }
            if (whiteList.contains(it)) {
                return true
            }
        }
        uri.scheme?.let {
            if (whiteList.contains(it)) {
                return true
            }
        }
        return false
    }

}
