/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.home.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import org.mozilla.fenix.databinding.WebFeaturedItemLayoutBinding
import org.mozilla.fenix.immersive_transalte.base.widget.FixLineFlowLayout
import org.mozilla.fenix.immersive_transalte.home.bean.FeatureItem

class FeaturedWebsLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FixLineFlowLayout(context, attrs, defStyleAttr) {
    private val inflater = LayoutInflater.from(context)
    private var featureItems: MutableList<FeatureItem> = mutableListOf()

    fun setFeatureItems(featureItems: MutableList<FeatureItem>) {
        this.featureItems = featureItems
        removeAllViews()
        featureItems.forEachIndexed { _, featureItem ->
            addView(featureItem)
        }
        requestLayout()
    }

    private fun addView(item: FeatureItem) {
        val binding = WebFeaturedItemLayoutBinding.inflate(inflater)
        binding.ivIcon.setImageResource(item.iconId)
        binding.tvTitle.setText(item.title)
        binding.flContainer.setOnClickListener {
            onItemClickListener?.onItemClick(item)
        }

        /*val layoutParams = binding.llContent.layoutParams as FrameLayout.LayoutParams
        if (index % maxItemsPerRow == 0) {
            layoutParams.gravity = android.view.Gravity.START
        } else if (index % maxItemsPerRow == maxItemsPerRow - 1) {
            layoutParams.gravity = android.view.Gravity.END
        } else {
            layoutParams.gravity = android.view.Gravity.CENTER
        }*/

        addView(binding.root)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(item: FeatureItem)
    }
}
