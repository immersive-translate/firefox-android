package mozilla.components.feature.contextmenu

/**
 * 保存 imageHash和imageUrl的映射关系
 */
object TranslateImageLinkHolder {
    private val imageLinks = HashMap<String, String>()

    fun save(imageHash: String, imageUrl: String) {
        imageLinks[imageHash] = imageUrl
    }

    fun remove(imageHash: String) {
        imageLinks.remove(imageHash)
    }

    fun get(imageHash: String): String? {
        return imageLinks[imageHash]
    }

}