/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * Gecko version and release channel constants used by this version of Android Components.
 */
object Gecko {
    /**
     * GeckoView Version.
     */
    //const val version = "125.0.20240425211020"
    const val version = "136.0.20250317200840"
    // 133.0.20241202233018
    // const val version = "134.0.20250120135430"
    //const val version = "136.0.20250317200840"

    /**
     * GeckoView channel
     */
    val channel = GeckoChannel.RELEASE
}

/**
 * Enum for GeckoView release channels.
 */
enum class GeckoChannel(
    val artifactName: String,
) {
    NIGHTLY("geckoview-nightly-omni"),
    BETA("geckoview-beta-omni"),
    RELEASE("geckoview-omni"),
}
