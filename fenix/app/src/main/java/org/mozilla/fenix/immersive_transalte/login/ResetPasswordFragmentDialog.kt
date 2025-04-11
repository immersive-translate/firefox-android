/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.login

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.DialogLoginFragmentLayoutBinding
import org.mozilla.fenix.immersive_transalte.DeviceUtil
import org.mozilla.fenix.immersive_transalte.utils.SimpleEventBus

class ResetPasswordFragmentDialog : AppCompatDialogFragment() {
    private lateinit var binding: DialogLoginFragmentLayoutBinding
    private lateinit var resetPwdFragment: ResetPasswordFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogLoginFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("CommitTransaction")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resetPwdFragment = ResetPasswordFragment.newInstance(true)
        childFragmentManager
            .beginTransaction()
            .add(R.id.fl_container, resetPwdFragment)
            .commitNowAllowingStateLoss()

        binding.ivClose.setOnClickListener {
            dismiss()
        }
        SimpleEventBus.register(SimpleEventBus.EVENT_RESET_PWD, onResetSuccess)
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val displaySize = DeviceUtil.getDeviceRealSize(binding.root.context)
            it.window?.setLayout(displaySize.width, displaySize.height)
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // it.window?.setBackgroundDrawable(ColorDrawable(0x6F000000))
            it.window?.setGravity(Gravity.BOTTOM)
            it.window?.setWindowAnimations(R.style.BottomDialogAnimation)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        SimpleEventBus.unregister(SimpleEventBus.EVENT_RESET_PWD, onResetSuccess)
    }

    private val onResetSuccess: (obj: Any?) -> Unit = {
        dismiss()
        EmailLoginFragmentDialog().show(parentFragmentManager, "DialogEmailLoginFragment")
    }
}
