/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.report

import android.app.Dialog
import android.content.Context
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.DialogCommitRemindLayoutBinding

class CommitRemindDialog(
    context: Context,
    iconResId: Int,
    msgResId: Int,
    private val onDismiss: (() -> Unit)?,
) : Dialog(context, R.style.process_dialog_style) {
    private val binding = DialogCommitRemindLayoutBinding.inflate(layoutInflater)
    private val dismissTime = 1500L

    init {
        setContentView(binding.root)
        binding.ivState.setImageResource(iconResId)
        binding.tvMessage.text = context.getString(msgResId)
        setCancelable(true)
    }

    override fun show() {
        super.show()
        binding.root.postDelayed(
            { dismiss() },
            dismissTime,
        )
    }

    override fun dismiss() {
        super.dismiss()
        onDismiss?.invoke()
    }

}
