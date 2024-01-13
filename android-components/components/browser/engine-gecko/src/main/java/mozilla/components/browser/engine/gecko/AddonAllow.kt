/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */


package mozilla.components.browser.engine.gecko

/**
 * create by xupx
 * 安装不需要提示
 */
class AddonAllow {
    companion object {
        val NoCheckAddons = listOf(
            "{5efceaa7-f3a2-4e59-a54b-85319448e305}",
            "{5efceaa7-f3a2-4e59-a54b-85319448e306}" // beta
        )
    }
}