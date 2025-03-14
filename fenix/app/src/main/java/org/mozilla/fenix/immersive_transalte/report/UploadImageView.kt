/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.report

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import mozilla.components.support.ktx.android.util.dpToPx

import org.mozilla.fenix.R

class UploadImageView : FrameLayout {
    private lateinit var displayImageView: ShapeableImageView
    private lateinit var deleteView: ImageView
    private lateinit var bitmap: Bitmap

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
        val size = 70.dpToPx(context.resources.displayMetrics)
        layoutParams = LayoutParams(size, size)
        (layoutParams as MarginLayoutParams).leftMargin = 8.dpToPx(context.resources.displayMetrics)

        displayImageView = ShapeableImageView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            shapeAppearanceModel = ShapeAppearanceModel.Builder()
                .setAllCorners(
                    CornerFamily.ROUNDED,
                    10F.dpToPx(context.resources.displayMetrics),
                )
                .build()
        }
        addView(displayImageView)

        // ic_delete
        deleteView = ImageView(context).apply {
            val deleteSize = 14.dpToPx(context.resources.displayMetrics)
            layoutParams = LayoutParams(deleteSize, deleteSize)
            (layoutParams as LayoutParams).apply {
                gravity = Gravity.END or Gravity.TOP
                topMargin = 6.dpToPx(context.resources.displayMetrics)
                marginEnd = 6.dpToPx(context.resources.displayMetrics)
            }
            setImageResource(R.drawable.ic_delete)
            setOnClickListener {
                deleteCallback?.invoke(this@UploadImageView)
            }
        }
        addView(deleteView)
    }

    fun setImage(bitmap: Bitmap) {
        this.bitmap = bitmap
        displayImageView.setImageBitmap(bitmap)
    }

    fun getBitmap(): Bitmap {
        return bitmap
    }

    private var deleteCallback: ((View) -> Unit)? = null
    fun setDeleteClickListener(deleteCallback: (View) -> Unit) {
        this.deleteCallback = deleteCallback
    }
}
