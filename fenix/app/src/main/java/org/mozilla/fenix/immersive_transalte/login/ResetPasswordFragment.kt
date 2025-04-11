/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
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
import org.mozilla.fenix.databinding.FragmentForgetPasswordLayoutBinding
import org.mozilla.fenix.immersive_transalte.base.widget.ProcessDialog
import org.mozilla.fenix.immersive_transalte.net.ApiErrorMappingHelper
import org.mozilla.fenix.immersive_transalte.net.service.MemberService
import org.mozilla.fenix.immersive_transalte.utils.SimpleEventBus
import kotlin.math.max

class ResetPasswordFragment : Fragment() {

    companion object {
        fun newInstance(isDialog: Boolean): ResetPasswordFragment {
            val fragment = ResetPasswordFragment()
            fragment.isDialog = isDialog
            return fragment
        }
    }

    private var isDialog = false
    private lateinit var binding: FragmentForgetPasswordLayoutBinding
    private val scope = MainScope()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentForgetPasswordLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.vTopHolder.visibility = if (isDialog) View.VISIBLE else View.GONE
        binding.tvGetVerifyCode.setOnClickListener {
            getVerifyCode()
        }
        binding.btnResetPwd.setOnClickListener {
            handleResetPwd()
        }
        binding.btnGotoLogin.setOnClickListener {
            SimpleEventBus.postEvent(SimpleEventBus.EVENT_RESET_PWD)
        }

        binding.etEmail.addTextChangedListener {
            checkResetEnable()
        }
        binding.etVerifyCode.addTextChangedListener {
            checkResetEnable()
        }
        binding.etPwd.addTextChangedListener {
            checkResetEnable()
        }
        binding.etPwdConfirm.addTextChangedListener {
            checkResetEnable()
        }
    }

    private fun checkResetEnable() {
        val email = binding.etEmail.text.toString().trim()
        val verifyCode = binding.etVerifyCode.text.toString().trim()
        val password = binding.etPwd.text.toString().trim()
        val confirmPassword = binding.etPwdConfirm.text.toString().trim()

        val isSamePwd = TextUtils.equals(password, confirmPassword)
        binding.tvPwdInvalid.visibility = if (isSamePwd) View.INVISIBLE else View.VISIBLE

        val isEnable = !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(verifyCode)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(confirmPassword)

        binding.btnResetPwd.isEnabled = isSamePwd && isEnable
    }

    private fun handleResetPwd() {
        val email = binding.etEmail.text.toString().trim()
        val verifyCode = binding.etVerifyCode.text.toString().trim()
        val password = binding.etPwd.text.toString().trim()
        // val confirmPassword = binding.etPwdConfirm.toString().trim()
        scope.launch(Dispatchers.Main) {
            showProcessDialog()

            // 重置密码
            val resetResult = withContext(Dispatchers.IO) {
                MemberService.resetPassword(email, password, verifyCode)
            }
            if (!resetResult.isOk()) {
                hideProcessDialog()
                ApiErrorMappingHelper.ResetPwd.errorToast(resetResult)
                return@launch
            }
            hideProcessDialog()

            binding.clReset.visibility = View.GONE
            binding.clGotoLogin.visibility = View.VISIBLE

            // 登录逻辑
            /*val loginResult = withContext(Dispatchers.IO) {
                MemberService.webLogin(email, password).data?.data
            }
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
            handlerLoginSuccess()*/
        }
    }

    private fun handlerLoginSuccess() {
        /*if (!isDialog) {
        }*/
        SimpleEventBus.postEvent(SimpleEventBus.EVENT_LOGIN)
    }

    private fun getVerifyCode() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            return
        }
        scope.launch(Dispatchers.Main) {
            showProcessDialog()
            val verifyCodeResult = withContext(Dispatchers.IO) {
                MemberService.getResetPwdCode(email)
            }
            hideProcessDialog()
            if (!verifyCodeResult.isOk()) {
                ApiErrorMappingHelper.FindPassCode.errorToast(verifyCodeResult)
                return@launch
            }
            startVerifyCountdown()
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    private val countDownTime = 30

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

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
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
