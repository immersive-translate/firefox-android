/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.base.widget

import android.app.Dialog
import android.content.Context
import android.view.View
import org.mozilla.fenix.R

class ProcessDialog(context: Context?) : Dialog(context!!, R.style.process_dialog_style) {
    private var root_view: View? = null

    init {
        root_view = layoutInflater.inflate(R.layout.dialog_process_layout, null)
        root_view?.let {
            setContentView(it)
        }
    }

}
