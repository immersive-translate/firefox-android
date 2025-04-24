/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.text.HtmlCompat
import mozilla.components.support.ktx.android.content.getColorFromAttr
import mozilla.components.support.ktx.android.util.dpToPx
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.VideoFeaturedWebsItemLayoutBinding
import org.mozilla.fenix.databinding.VideoTranslateItemLayoutBinding
import org.mozilla.fenix.databinding.WebLinkItemLayoutBinding
import org.mozilla.fenix.immersive_transalte.base.BaseRecyclerAdapter
import org.mozilla.fenix.immersive_transalte.base.BaseRecyclerAdapter.BaseViewHolder
import org.mozilla.fenix.immersive_transalte.base.ImageLoader
import org.mozilla.fenix.immersive_transalte.home.bean.FeatureItem
import org.mozilla.fenix.immersive_transalte.home.bean.WebItem
import org.mozilla.fenix.immersive_transalte.home.data.VideoDataProvider
import org.mozilla.fenix.immersive_transalte.home.widget.FeaturedWebsLayout

class VideoTranslateAdapter(
    context: Context,
) : BaseRecyclerAdapter<WebItem>(context) {
    private var firstLinkItem: WebItem? = null

    override fun setData(data: MutableList<WebItem>) {
        firstLinkItem = data.first { it.type == WebItem.TYPE_LINKS }
        super.setData(data)
    }

    override fun getItemViewType(position: Int): Int {
        getEntity(position)?.let {
            return it.type
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            WebItem.TYPE_TRANSLATE -> {
                val binding = VideoTranslateItemLayoutBinding.inflate(
                    layoutInflater, parent, false,
                )
                VideoTranslateViewHolder(binding)
            }

            WebItem.TYPE_FEATURED_WEBS -> {
                val binding = VideoFeaturedWebsItemLayoutBinding.inflate(
                    layoutInflater, parent, false,
                )
                VideoFeaturedWebsViewHolder(binding) { item ->
                    callback?.onUrlClick(item.url)
                }
            }

            else -> {
                val binding = WebLinkItemLayoutBinding.inflate(
                    layoutInflater, parent, false,
                )
                VideoLinksViewHolder(binding) { item ->
                    callback?.onUrlClick(item.url)
                }
            }
        }
    }

    override fun onBindItemViewHolder(holder: BaseViewHolder?, position: Int) {
        when (holder) {
            is VideoTranslateViewHolder -> {
                holder.run { bindView() }
            }

            is VideoFeaturedWebsViewHolder -> {
                holder.run { bindView() }
            }

            is VideoLinksViewHolder -> {
                val item = getEntity(position)
                holder.run { bindView(item, item == firstLinkItem) }
            }
        }
    }

    private var callback: Callback? = null

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {
        fun onUrlClick(url: String?)
    }
}


private class VideoTranslateViewHolder(
    val viewBinding: VideoTranslateItemLayoutBinding,
) : BaseViewHolder(viewBinding) {

    fun bindView() {
        val context = binding.root.context
        val color = intColorToArgbString(context.getColorFromAttr(R.attr.normal_color_EA4C89))
        var desc = context.getString(R.string.video_ts_desc)
        desc = desc.replace("#1", "<font color='${color}'>")
        desc = desc.replace("#2", "</font>")
        viewBinding.tvDesc.text = HtmlCompat.fromHtml(desc, 0)
    }

    private fun intColorToArgbString(color: Int): String {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        return String.format("#%02X%02X%02X", r, g, b)
    }
}

private class VideoFeaturedWebsViewHolder(
    val viewBinding: VideoFeaturedWebsItemLayoutBinding,
    val onLinkClick: ((FeatureItem) -> Unit)? = null,
) : BaseViewHolder(viewBinding) {
    companion object {
        var isShowMore = false
    }

    init {
        viewBinding.tvMore.setOnClickListener {
            isShowMore = !isShowMore
            refreshView()
        }
        viewBinding.ivMore.setOnClickListener {
            isShowMore = !isShowMore
            refreshView()
        }
        viewBinding.featuredWebs.setOnItemClickListener(
            object : FeaturedWebsLayout.OnItemClickListener {
                override fun onItemClick(item: FeatureItem) {
                    onLinkClick?.invoke(item)
                }
            },
        )
    }

    private fun refreshView() {
        val resId = if (isShowMore) R.drawable.chevron_right_up
        else R.drawable.chevron_right_down
        viewBinding.ivMore.setImageResource(resId)

        val items = VideoDataProvider.getFeaturedWebs(isShowMore)
        viewBinding.featuredWebs.setFeatureItems(items)
    }

    fun bindView() {
        refreshView()
    }
}

private class VideoLinksViewHolder(
    val viewBinding: WebLinkItemLayoutBinding,
    val onLinkClick: ((WebItem) -> Unit)? = null,
) : BaseViewHolder(viewBinding) {

    fun bindView(webItem: WebItem?, isFirstLink: Boolean) {
        webItem?.let { item ->
            val context = viewBinding.root.context
            viewBinding.tvLinksTitle.visibility = if (isFirstLink) View.VISIBLE else View.GONE
            viewBinding.tvTitle.text = item.title
            (viewBinding.cardContent.layoutParams as MarginLayoutParams).let {
                val topMargin = if (!isFirstLink) 32 else 16
                it.topMargin = topMargin.dpToPx(context.resources.displayMetrics)
            }
            ImageLoader.display(
                viewBinding.root.context,
                viewBinding.ivImage,
                item.imageUrl,
            )
            viewBinding.root.setOnClickListener {
                onLinkClick?.invoke(item)
            }
        }
    }

}
