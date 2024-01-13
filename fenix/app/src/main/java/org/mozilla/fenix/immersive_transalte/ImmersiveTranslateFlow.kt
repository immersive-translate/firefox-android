/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


/**
 * created by xupx
 * on 2024-01-03
 */
object ImmersiveTranslateFlow {
    private val installFlow = MutableSharedFlow<Boolean>(
        replay = 1,
        extraBufferCapacity = 1,
    )

    fun emit(isInstalled: Boolean) {
        MainScope().launch {
            installFlow.emit(isInstalled)
        }
    }

    fun collect(block: (Boolean) -> Unit) {
        MainScope().launch {
            delay(100)
            installFlow.collect {
                block(it)
            }
        }
    }
}
