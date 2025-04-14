/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.DialogLoginFragmentLayoutBinding
import org.mozilla.fenix.immersive_transalte.utils.SimpleEventBus

class EmailVerifyFragmentDialog(
    private val username: String,
    private val password: String,
) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogLoginFragmentLayoutBinding
    private lateinit var activityEmailFragment: EmailVerifyFragment

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
        activityEmailFragment = EmailVerifyFragment.newInstance(
            isDialog = true,
            email = username,
            password = password,
            onGotoLogin = {
                dismiss()
            },
        )
        childFragmentManager
            .beginTransaction()
            .add(R.id.fl_container, activityEmailFragment)
            .commitNowAllowingStateLoss()

        binding.ivClose.setOnClickListener {
            dismiss()
        }
        SimpleEventBus.register(SimpleEventBus.EVENT_LOGIN, onLogin)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { d ->
            val bottomSheet = (d as BottomSheetDialog).findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                it.setBackgroundColor(Color.TRANSPARENT)
                /*WindowCompat.setDecorFitsSystemWindows(dialog.window!!, false)
                ViewCompat.setOnApplyWindowInsetsListener(bottomSheet) { view, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    view.setPadding(0, 0, 0, systemBars.bottom)
                    insets
                }*/
            }
        }
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        SimpleEventBus.unregister(SimpleEventBus.EVENT_LOGIN, onLogin)
    }

    private val onLogin: (obj: Any?) -> Unit = {
        dismiss()
    }
}
