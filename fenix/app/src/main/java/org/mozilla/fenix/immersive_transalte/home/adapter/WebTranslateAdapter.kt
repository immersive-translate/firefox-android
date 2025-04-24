/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import mozilla.components.support.ktx.android.util.dpToPx
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.WebFeaturedWebsItemLayoutBinding
import org.mozilla.fenix.databinding.WebLinkItemLayoutBinding
import org.mozilla.fenix.databinding.WebTranslateItemLayoutBinding
import org.mozilla.fenix.immersive_transalte.base.BaseRecyclerAdapter
import org.mozilla.fenix.immersive_transalte.base.BaseRecyclerAdapter.BaseViewHolder
import org.mozilla.fenix.immersive_transalte.base.ImageLoader
import org.mozilla.fenix.immersive_transalte.home.data.WebDataProvider
import org.mozilla.fenix.immersive_transalte.home.bean.FeatureItem
import org.mozilla.fenix.immersive_transalte.home.bean.WebItem
import org.mozilla.fenix.immersive_transalte.home.widget.FeaturedWebsLayout

class WebTranslateAdapter(
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
                val binding = WebTranslateItemLayoutBinding.inflate(
                    layoutInflater, parent, false,
                )
                WebTranslateViewHolder(binding)
            }

            WebItem.TYPE_FEATURED_WEBS -> {
                val binding = WebFeaturedWebsItemLayoutBinding.inflate(
                    layoutInflater, parent, false,
                )
                WebFeaturedWebsViewHolder(binding) { item ->
                    callback?.onUrlClick(item.url)
                }
            }

            else -> {
                val binding = WebLinkItemLayoutBinding.inflate(
                    layoutInflater, parent, false,
                )
                WebLinksViewHolder(binding) { item ->
                    callback?.onUrlClick(item.url)
                }
            }
        }
    }

    override fun onBindItemViewHolder(holder: BaseViewHolder?, position: Int) {
        when (holder) {
            is WebTranslateViewHolder -> {
            }

            is WebFeaturedWebsViewHolder -> {
                holder.run { bindView() }
            }

            is WebLinksViewHolder -> {
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

private class WebTranslateViewHolder(
    binding: WebTranslateItemLayoutBinding,
) : BaseViewHolder(binding)

private class WebFeaturedWebsViewHolder(
    val viewBinding: WebFeaturedWebsItemLayoutBinding,
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

        val items = WebDataProvider.getFeaturedWebs(isShowMore)
        viewBinding.featuredWebs.setFeatureItems(items)
    }

    fun bindView() {
        refreshView()
    }
}

private class WebLinksViewHolder(
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
