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
            installFlow.collect {
                delay(1500)
                block(it)
            }
        }
    }
}
