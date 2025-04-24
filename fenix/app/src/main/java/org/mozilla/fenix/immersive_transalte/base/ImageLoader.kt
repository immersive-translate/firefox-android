/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.base

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageLoader {

    fun display(
        ctx: Context,
        imageView: ImageView,
        imageUrl: String?,
    ) {
        imageUrl?.let {
            Glide.with(ctx)
                .load(imageUrl)
                .into(imageView)
        }
    }

}
