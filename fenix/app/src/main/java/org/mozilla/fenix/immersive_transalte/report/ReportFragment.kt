/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.report

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.support.ktx.android.content.getColorFromAttr
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentReportLayoutBinding
import org.mozilla.fenix.ext.showToolbar
import org.mozilla.fenix.immersive_transalte.base.widget.ProcessDialog
import org.mozilla.fenix.immersive_transalte.net.service.HomePageService
import org.mozilla.fenix.immersive_transalte.user.UserManager
import org.mozilla.fenix.immersive_transalte.utils.ToastUtil


enum class ReportType {
    BUG, FEATURE
}

class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportLayoutBinding
    private var reportType = ReportType.BUG
    private val scope = MainScope()
    private val bmpLimitKB = 1024 // 1M

    private val imageLimitCount = 3
    private val imageList = mutableListOf<UploadImageView>()

    private var pickImagesLauncher: ActivityResultLauncher<Intent?> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                handleSelectedImages(result.data!!)
            }
        }

    private fun handleSelectedImages(data: Intent) {
        // 限制最多 3 张
        val remainCount = imageLimitCount - imageList.size
        if (remainCount <= 0) {
            return
        }
        val imageUris = mutableListOf<Uri>()
        if (data.clipData != null) { // 多选情况
            val count = minOf(data.clipData!!.itemCount, remainCount)
            for (i in 0 until count) {
                imageUris.add(data.clipData!!.getItemAt(i).uri)
            }
        } else if (data.data != null) { // 单选情况
            imageUris.add(data.data!!)
        }
        imageUris.forEach { uri ->
            compressImage(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentReportLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvReportBug.setOnClickListener {
            changeReportType(ReportType.BUG)
        }
        binding.tvReportFeat.setOnClickListener {
            changeReportType(ReportType.FEATURE)
        }
        binding.ivUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            pickImagesLauncher.launch(intent)
        }
        binding.btnCommit.setOnClickListener {
            commit()
        }

        UserManager.getUserEmail(binding.root.context)?.let {
            if (!TextUtils.isEmpty(it)) {
                binding.etEmail.setText(it)
            }
        }
        refreshUI()
    }

    private fun changeReportType(type: ReportType) {
        if (reportType == type) return
        reportType = type
        refreshUI()
    }

    private fun refreshUI() {
        val context = binding.root.context
        if (reportType == ReportType.BUG) {
            binding.tvReportBug.setBackgroundResource(R.drawable.report_tab_item_bg)
            binding.tvReportFeat.setBackgroundColor(0x0)
            binding.tvReportBug.setTextColor(context.getColorFromAttr(R.attr.normal_color_222222))
            binding.tvReportFeat.setTextColor(context.getColorFromAttr(R.attr.normal_color_666666))
            binding.tvReportType.text = HtmlCompat.fromHtml(
                "<font color='#FF5B5B'>*</font>${context.getString(R.string.report_type_bug)}",
                0,
            )
            binding.etDescription.hint = context.getString(R.string.report_bug_input_hint)
        } else {
            binding.tvReportBug.setBackgroundColor(0x0)
            binding.tvReportFeat.setBackgroundResource(R.drawable.report_tab_item_bg)
            binding.tvReportBug.setTextColor(context.getColorFromAttr(R.attr.normal_color_666666))
            binding.tvReportFeat.setTextColor(context.getColorFromAttr(R.attr.normal_color_222222))
            binding.tvReportType.text = HtmlCompat.fromHtml(
                "<font color='#FF5B5B'>*</font>${context.getString(R.string.report_type_feat)}",
                0,
            )
            binding.etDescription.hint = context.getString(R.string.report_feat_input_hint)
        }
    }

    private fun compressImage(uri: Uri) {
        scope.launch(Dispatchers.Main) {
            // 创建 bitmap
            val fileBean = withContext(Dispatchers.IO) {
                BitmapUtil.compress(requireContext(), uri, bmpLimitKB)
            }
            fileBean?.let {
                val imageView = UploadImageView(requireContext())
                imageView.setCallback(object : UploadImageView.Callback {
                    override fun onDelete(view: View) {
                        binding.llUpload.removeView(imageView)
                        imageList.remove(imageView)
                        binding.ivUpload.visibility =
                            if (imageList.size < imageLimitCount) View.VISIBLE else View.GONE
                    }

                    override fun onUpload(isSuccess: Boolean) {
                        if (!isSuccess) {
                            showUploadFailed()
                        }
                    }
                })
                imageView.setImage(it)
                binding.llUpload.addView(imageView, 0)
                imageList.add(imageView)
                binding.ivUpload.visibility =
                    if (imageList.size < imageLimitCount) View.VISIBLE else View.GONE
            }
        }
    }

    private fun commit() {
        val desc = binding.etDescription.text?.toString()?.trim()
        if (TextUtils.isEmpty(desc)) {
            ToastUtil.toast(
                requireContext(), R.string.report_input_remind,
                false, Gravity.BOTTOM,
            )
            return
        }

        val appFeedBack = if (reportType == ReportType.BUG) "appBug" else "appFeedBack"
        val email = binding.etEmail.text?.toString()?.trim() ?: ""

        val urls = mutableListOf<String>()
        if (imageList.isNotEmpty()) {
            imageList.forEach { imageView ->
                if (!imageView.isUploadSuccess()) {
                    return
                }
                urls.add(imageView.getImageUrl())
            }
        }

        scope.launch(Dispatchers.Main) {
            showProcessDialog()
            // 上报数据
            val reportResult = withContext(Dispatchers.IO) {
                HomePageService.reportProblem(appFeedBack, desc!!, email, urls)
            }
            hideProcessDialog()
            if (reportResult.isOk()) {
                showCommitSuccess()
            } else {
                showCommitFailed()
            }
        }
    }

    private fun showCommitSuccess() {
        CommitRemindDialog(
            requireContext(),
            R.drawable.ic_commit_success,
            R.string.report_commit_success,
        ) {
            if (!isDetached) {
                activity?.supportFragmentManager?.popBackStack()
            }
        }.show()
    }

    private fun showCommitFailed() {
        CommitRemindDialog(
            requireContext(),
            R.drawable.ic_commit_failure,
            R.string.report_commit_failure,
        ) {}.show()
    }

    private fun showUploadFailed() {
        CommitRemindDialog(
            requireContext(),
            R.drawable.ic_commit_failure,
            R.string.report_upload_img_failure,
        ) {}.show()
    }

    override fun onResume() {
        super.onResume()
        showToolbar(getString(R.string.preferences_report))
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        processDialog?.dismiss()
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
