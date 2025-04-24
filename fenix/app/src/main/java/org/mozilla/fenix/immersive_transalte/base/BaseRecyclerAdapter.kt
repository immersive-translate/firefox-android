/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.base

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerAdapter<T>(val context: Context) :
    RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder>() {
    private var data: MutableList<T>? = null
    protected val layoutInflater: LayoutInflater get() = LayoutInflater.from(context)

    open fun setData(data: MutableList<T>) {
        this.data = data
        notifyItemRangeChanged(0, data.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        data?.let {
            it.clear()
            notifyDataSetChanged()
        }
    }

    fun addData(newData: List<T>) {
        if (newData.isEmpty()) {
            return
        }
        if (data == null) {
            data = ArrayList()
        }
        val startPoi = data!!.size
        data!!.addAll(newData)
        notifyItemRangeInserted(startPoi, newData.size)
    }

    fun removeData(poi: Int) {
        data!!.removeAt(poi)
        notifyItemRemoved(poi)
    }

    fun getEntity(poi: Int): T? {
        if (poi < 0 || poi >= itemCount) {
            return null
        }
        return data!![poi]
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        // bind item|footer holder
        onBindItemViewHolder(holder, position)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    abstract fun onBindItemViewHolder(holder: BaseViewHolder?, position: Int)

    open class BaseViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
