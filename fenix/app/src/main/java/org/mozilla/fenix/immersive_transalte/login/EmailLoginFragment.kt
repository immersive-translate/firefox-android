/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.login

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentEmailLoginLayoutBinding
import org.mozilla.fenix.immersive_transalte.base.widget.ProcessDialog
import org.mozilla.fenix.immersive_transalte.net.ApiErrorMappingHelper
import org.mozilla.fenix.immersive_transalte.net.service.MemberService
import org.mozilla.fenix.immersive_transalte.user.HwAgreementDialog
import org.mozilla.fenix.immersive_transalte.user.UserManager
import org.mozilla.fenix.immersive_transalte.utils.SimpleEventBus
import org.mozilla.fenix.settings.SupportUtils

class EmailLoginFragment : Fragment() {

    companion object {
        fun newInstance(
            isDialog: Boolean,
            onResetPwd: () -> Unit,
        ): EmailLoginFragment {
            val fragment = EmailLoginFragment()
            fragment.isDialog = isDialog
            fragment.onResetPwd = onResetPwd
            return fragment
        }
    }

    private var isDialog = false
    private var onResetPwd: (() -> Unit)? = null

    private lateinit var binding: FragmentEmailLoginLayoutBinding
    private val scope = MainScope()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEmailLoginLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initHwAgreement()
        binding.vTopHolder.visibility = if (isDialog) View.VISIBLE else View.GONE
        binding.cbPwdVisible.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 显示密码
                binding.etPwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                // 隐藏密码
                binding.etPwd.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        binding.btnCommit.setOnClickListener {
            if (!checkHwAgreement()) {
                showHwAgreementRemind {
                    handleLogin()
                }
                return@setOnClickListener
            }
            handleLogin()
        }
        binding.tvForgetPwd.setOnClickListener {
            if (isDialog) {
                onResetPwd?.invoke()
                return@setOnClickListener
            }
        }
    }

    private fun handleLogin() {
        val username = binding.etEmail.text.toString().trim()
        val password = binding.etPwd.text.toString().trim()
        if (TextUtils.isEmpty(username)) {
            return
        }
        if (TextUtils.isEmpty(password)) {
            return
        }
        showProcessDialog()
        scope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                MemberService.webLogin(username, password)
            }

            if (!result.isOk()) {
                // 新用户注册
                if (result.errorCode == 1001 || result.errorCode == 1009) {
                    gotoRegister(username, password)
                } else {
                    ApiErrorMappingHelper.WebLogin.errorToast(result)
                }
                hideProcessDialog()
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
            handlerLoginSuccess()
        }
    }

    private suspend fun gotoRegister(username: String, password: String) {
        val registerResult = withContext(Dispatchers.IO) {
            MemberService.register(username, password)
        }
        if (registerResult.isOk()) {
            EmailVerifyFragmentDialog(username, password).show(
                parentFragmentManager, "EmailVerifyFragmentDialog",
            )
        } else {
            ApiErrorMappingHelper.Register.errorToast(registerResult)
        }
    }

    private fun handlerLoginSuccess() {
        /*if (!isDialog) {
        }*/
        SimpleEventBus.postEvent(SimpleEventBus.EVENT_LOGIN)
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
        super.onDestroy()
        scope.cancel()
    }

    private fun checkHwAgreement(): Boolean {
        return binding.cbHwAgreement.isChecked
    }

    private fun showHwAgreementRemind(onAgree: (() -> Unit)? = null) {
        activity?.let {
            LoginHwPrivacyRemindDialog(
                it,
                onAgree = {
                    binding.cbHwAgreement.isChecked = true
                    onAgree?.invoke()
                },
                onShowCondition = {
                    showHwAgreementContent(SupportUtils.APP_LOGIN_HW_CONDITION_URL)
                },
                onShowPrivacy = {
                    showHwAgreementContent(SupportUtils.APP_LOGIN_HW_PRIVACY_URL)
                },
            ).show(binding.root)
        }
    }

    private fun showHwAgreementContent(url: String) {
        activity?.let {
            HwAgreementDialog(
                context = it,
                url = url,
            ).show(binding.root)
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
}
