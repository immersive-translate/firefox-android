/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.report

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.support.ktx.android.util.dpToPx
import org.mozilla.fenix.databinding.UploadImageViewLayoutBinding
import org.mozilla.fenix.immersive_transalte.bean.UploadFileBean
import org.mozilla.fenix.immersive_transalte.net.service.HomePageService

class UploadImageView : FrameLayout {
    private lateinit var binding: UploadImageViewLayoutBinding

    private var fileBean: UploadFileBean? = null
    private lateinit var uploadUrl: String
    private var uploadState = 0 // 1: loading, 2: success, 3: failed

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    ) {
        init(context)
    }

    private fun init(context: Context) {
        binding = UploadImageViewLayoutBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

        binding.ivImage.apply {
            shapeAppearanceModel = ShapeAppearanceModel.Builder()
                .setAllCorners(
                    CornerFamily.ROUNDED,
                    10F.dpToPx(context.resources.displayMetrics),
                )
                .build()
        }

        binding.ivRetry.setOnClickListener {
            uploadImage()
        }

        binding.ivDelete.setOnClickListener {
            callback?.onDelete(this@UploadImageView)
        }
    }

    fun setImage(fileBean: UploadFileBean) {
        this.fileBean = fileBean
        binding.ivImage.setImageBitmap(fileBean.bitmap)
        uploadImage()
    }

    fun isUploading(): Boolean {
        return uploadState == 1
    }

    fun isUploadSuccess(): Boolean {
        return uploadState == 2
    }

    fun isUploadFailed(): Boolean {
        return uploadState == 3
    }

    fun getImageUrl(): String {
        return uploadUrl
    }

    private fun uploadImage() {
        binding.flProgress.visibility = VISIBLE
        binding.progress.visibility = VISIBLE
        binding.ivRetry.visibility = GONE
        uploadState = 1
        MainScope().launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                HomePageService.uploadImage(fileBean!!.bitmapFile)
            }
            if (!isAttachedToWindow) {
                return@launch
            }
            val url = response?.data?.data?.objectKey
            if (!TextUtils.isEmpty(url)) {
                uploadState = 2
                uploadUrl = url!!
                binding.flProgress.visibility = GONE
                callback?.onUpload(true)
            } else {
                uploadState = 3
                binding.progress.visibility = GONE
                binding.ivRetry.visibility = VISIBLE
                callback?.onUpload(false)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        fileBean?.bitmapFile?.delete()
    }

    private var callback: Callback? = null

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {
        fun onDelete(view: View)
        fun onUpload(isSuccess: Boolean)
    }
}
