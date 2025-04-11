/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.login

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentEmailVerifyLayoutBinding
import org.mozilla.fenix.immersive_transalte.base.widget.ProcessDialog
import org.mozilla.fenix.immersive_transalte.net.ApiErrorMappingHelper
import org.mozilla.fenix.immersive_transalte.net.service.MemberService
import org.mozilla.fenix.immersive_transalte.user.HwAgreementDialog
import org.mozilla.fenix.immersive_transalte.user.UserManager
import org.mozilla.fenix.immersive_transalte.utils.SimpleEventBus
import org.mozilla.fenix.settings.SupportUtils
import kotlin.math.max

class EmailVerifyFragment : Fragment() {

    companion object {
        fun newInstance(
            isDialog: Boolean,
            email: String,
            password: String,
            onGotoLogin: () -> Unit,
        ): EmailVerifyFragment {
            val fragment = EmailVerifyFragment()
            fragment.isDialog = isDialog
            fragment.email = email
            fragment.password = password
            fragment.onGotoLogin = onGotoLogin
            return fragment
        }
    }

    private var isDialog = false
    private lateinit var binding: FragmentEmailVerifyLayoutBinding
    private val scope = MainScope()
    private var email = ""
    private var password = ""
    private var onGotoLogin : (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEmailVerifyLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initHwAgreement()
        binding.vTopHolder.visibility = if (isDialog) View.VISIBLE else View.GONE
        binding.tvEmail.text = email
        binding.tvGetVerifyCode.setOnClickListener {
            getVerifyCode()
        }
        binding.btnVerifyEmail.setOnClickListener {
            handleVerifyEmail()
        }
        binding.tvLogin.setOnClickListener {
            if (isDialog) {
                onGotoLogin?.invoke()
            }
        }
        binding.etVerifyCode.addTextChangedListener {
            val code = binding.etVerifyCode.text.toString().trim()
            binding.btnVerifyEmail.isEnabled = code.isNotEmpty()
        }
        getVerifyCode()
    }

    private fun handleVerifyEmail() {
        val userEmail = email
        val verifyCode = binding.etVerifyCode.text.toString().trim()
        scope.launch(Dispatchers.Main) {
            showProcessDialog()
            val activeResult = withContext(Dispatchers.IO) {
                MemberService.activeEmail(userEmail, verifyCode)
            }

            if (!activeResult.isOk()) {
                hideProcessDialog()
                ApiErrorMappingHelper.AccountActive.errorToast(activeResult)
                return@launch
            }

            // 登录流程
            val result = withContext(Dispatchers.IO) {
                MemberService.webLogin(userEmail, password)
            }

            // toast 提示
            if (!result.isOk()) {
                hideProcessDialog()
                ApiErrorMappingHelper.WebLogin.errorToast(result)
                return@launch
            }

            val loginResult = result.data?.data
            val isLogin = loginResult?.hasToken() ?: false
            if (!isLogin) {
                hideProcessDialog()
                return@launch
            }

            loginResult!!.updateUserInfo()
            val user = loginResult.loginResult!!.user
            // 更新设备信息
            withContext(Dispatchers.IO) {
                MemberService.updateDeviceInfo(user)
                // 保存用户信息
                val context = binding.root.context
                UserManager.saveUser(context, user)
            }

            hideProcessDialog()
            // 登录成功同志
            SimpleEventBus.postEvent(SimpleEventBus.EVENT_LOGIN)
        }
    }

    private fun getVerifyCode() {
        scope.launch(Dispatchers.Main) {
            showProcessDialog()
            val verifyCodeResult = withContext(Dispatchers.IO) {
                MemberService.getActiveEmailVerifyCode(email)
            }
            hideProcessDialog()
            if (!verifyCodeResult.isOk()) {
                ApiErrorMappingHelper.ReActiveCode.errorToast(verifyCodeResult)
                return@launch
            }
            startVerifyCountdown()
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    private val countDownTime = 60

    private fun startVerifyCountdown() {
        binding.tvGetVerifyCode.isEnabled = false
        startTime = System.currentTimeMillis()
        countdown()
    }

    private fun countdown() {
        handler.postDelayed(
            {
                val btnText =
                    binding.root.context.getString(R.string.email_login_reset_verify_code_btn)
                var time = countDownTime - (System.currentTimeMillis() - startTime) / 1000
                time = max(0, time)
                val text = "${time}s"
                binding.tvGetVerifyCode.text = text
                if (time <= 0) {
                    handler.removeCallbacksAndMessages(null)
                    binding.tvGetVerifyCode.isEnabled = true
                    binding.tvGetVerifyCode.text = btnText
                } else {
                    countdown()
                }
            },
            500,
        )
    }

    private fun initHwAgreement() {
        val context = binding.root.context

        val hwAgreementDesc = context.getString(R.string.login_hw_agreement)
        val hwConditionText = context.getString(R.string.login_hw_condition)
        val hwPrivacyText = context.getString(R.string.login_hw_privacy)

        val contentText = String.format(hwAgreementDesc, hwConditionText, hwPrivacyText)
        val contentSpan = SpannableString(contentText)
        val textColor = ForegroundColorSpan(0xFF4181F0.toInt())

        // condition
        var startSpan = contentSpan.indexOf(hwConditionText)
        contentSpan.setSpan(
            textColor,
            startSpan,
            startSpan + hwConditionText.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )
        contentSpan.setSpan(
            conditionSpan,
            startSpan,
            startSpan + hwConditionText.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )

        // privacy
        startSpan = contentSpan.indexOf(hwPrivacyText)
        contentSpan.setSpan(
            textColor,
            startSpan,
            startSpan + hwPrivacyText.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )
        contentSpan.setSpan(
            privacySpan,
            startSpan,
            startSpan + hwPrivacyText.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )

        binding.tvHwAgreement.movementMethod = LinkMovementMethod.getInstance()
        binding.tvHwAgreement.text = contentSpan
        binding.tvHwAgreement.highlightColor = Color.TRANSPARENT
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun showHwAgreementContent(url: String) {
        activity?.let {
            HwAgreementDialog(
                context = it,
                url = url,
            ).show(binding.root)
        }
    }

    private var processDialog: ProcessDialog? = null
    private fun showProcessDialog() {
        processDialog = ProcessDialog(context)
        processDialog!!.show()
    }

    private fun hideProcessDialog() {
        if (processDialog != null && processDialog!!.isShowing) {
            processDialog!!.dismiss()
        }
    }

    private val conditionSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            showHwAgreementContent(SupportUtils.APP_LOGIN_HW_CONDITION_URL)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = 0xFF4181F0.toInt()
            ds.isUnderlineText = false
        }
    }

    private val privacySpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            showHwAgreementContent(SupportUtils.APP_LOGIN_HW_PRIVACY_URL)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = 0xFF4181F0.toInt()
            ds.isUnderlineText = false
        }
    }
}
