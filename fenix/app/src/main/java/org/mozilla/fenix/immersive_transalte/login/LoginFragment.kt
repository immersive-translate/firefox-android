/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.json.JSONObject
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentLoginLayoutBinding
import org.mozilla.fenix.ext.showToolbar
import org.mozilla.fenix.immersive_transalte.base.widget.ProcessDialog
import org.mozilla.fenix.immersive_transalte.user.HwAgreementDialog
import org.mozilla.fenix.immersive_transalte.utils.FastClickUtil
import org.mozilla.fenix.immersive_transalte.utils.SimpleEventBus
import org.mozilla.fenix.immersive_transalte.utils.ToastUtil
import org.mozilla.fenix.settings.SupportUtils

class LoginFragment : Fragment() {

    companion object {
        fun newInstance(isDialog: Boolean): LoginFragment {
            val fragment = LoginFragment()
            fragment.isDialog = isDialog
            return fragment
        }
    }

    private var isDialog = false
    private lateinit var binding: FragmentLoginLayoutBinding
    private lateinit var loginManager: ThirdLoginManager
    private val scope = MainScope()
    private val fbLoginManager = FbLoginManager()
    private val fastClickUtil = FastClickUtil()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SimpleEventBus.register(SimpleEventBus.EVENT_WEB_LOGIN_GET_TOKEN, onGetAccessToken)
        SimpleEventBus.register(SimpleEventBus.EVENT_WX_LOGIN_GET_TOKEN, onGetWxToken)
        SimpleEventBus.register(SimpleEventBus.EVENT_LOGIN, onLoginSuccess)
        loginManager = ThirdLoginManager(view.context, scope, parentFragmentManager) { state ->
            when (state) {
                ThirdLoginManager.STATE_LOADING -> {
                    showProcessDialog()
                }

                ThirdLoginManager.STATE_LOGIN_SUCCESS -> {
                    hideProcessDialog()
                }

                ThirdLoginManager.STATE_LOGIN_FAIL -> {
                    hideProcessDialog()
                }
            }
        }
        initView()
    }

    private fun initView() {
        initHwAgreement()
        binding.vTopHolder.visibility = if (isDialog) View.VISIBLE else View.GONE

        binding.llLoginEmail.setOnClickListener {
            /*if (isDialog) {
                EmailLoginFragmentDialog().show(parentFragmentManager, "DialogLoginFragment")
            }*/
            if (!fastClickUtil.isFastClick()) {
                EmailLoginFragmentDialog().show(parentFragmentManager, "DialogLoginFragment")
            }
        }

        binding.llLoginGoogle.setOnClickListener {
            loginWithThird(ThirdLoginManager.TYPE_GOOGLE)
        }
        binding.llLoginApple.setOnClickListener {
            loginWithThird(ThirdLoginManager.TYPE_APPLE)
        }
        binding.llLoginFacebook.setOnClickListener {
            loginWithThird(ThirdLoginManager.TYPE_FACEBOOK)
        }

        binding.llLoginWx.setOnClickListener {
            loginWithThird(ThirdLoginManager.TYPE_WX)
        }
    }

    private fun loginWithThird(loginType: Int) {
        if (fastClickUtil.isFastClick()) {
            return
        }
        val ctx = binding.root.context
        val isFacebookSdk = (loginType == ThirdLoginManager.TYPE_FACEBOOK)
                && fbLoginManager.isInstall(ctx)
        if (!checkHwAgreement()) {
            showHwAgreementRemind {
                binding.root.postDelayed(
                    {
                        if (isFacebookSdk) {
                            fbLoginManager.requestLogin(this@LoginFragment)
                        } else {
                            loginManager.loginThird(loginType)
                        }
                    },
                    100,
                )
            }
            return
        }

        if (isFacebookSdk) {
            fbLoginManager.requestLogin(this@LoginFragment)
        } else {
            loginManager.loginThird(loginType)
        }
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
        if (!isDialog) {
            showToolbar(getString(R.string.preferences_login))
        }
    }

    override fun onDestroy() {
        scope.cancel()
        SimpleEventBus.unregister(SimpleEventBus.EVENT_WEB_LOGIN_GET_TOKEN, onGetAccessToken)
        SimpleEventBus.unregister(SimpleEventBus.EVENT_WX_LOGIN_GET_TOKEN, onGetWxToken)
        SimpleEventBus.unregister(SimpleEventBus.EVENT_LOGIN, onLoginSuccess)
        super.onDestroy()
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

    // get web token
    private val onGetAccessToken: (obj: Any?) -> Unit = {
        val jsonObject = it as? JSONObject
        jsonObject?.let { jo ->
            loginManager.loginWithAccessToken(jo)
        }
    }

    // get wx token
    private val onGetWxToken: (obj: Any?) -> Unit = {
        val wxToken = it as? String
        wxToken?.let { token ->
            val params: MutableMap<String, Any?> = mutableMapOf()
            params["code"] = token
            params["state"] = "WeChatApp_imt"
            loginManager.loginWithWxToken(params)
        }
    }

    // login success
    private val onLoginSuccess: (obj: Any?) -> Unit = {
        if (!isDialog) {
            NavHostFragment.findNavController(this).popBackStack()
            // activity?.supportFragmentManager?.popBackStack()
        }
        val toast = requireContext().getString(R.string.login_success)
        ToastUtil.toast(requireContext(), toast, true)
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fbLoginManager.handlerResult(requestCode, resultCode, data)
    }
}
