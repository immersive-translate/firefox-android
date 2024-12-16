/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.mozilla.fenix.onboarding.imts

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.PopupWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.mozilla.fenix.databinding.LanguageItemLayoutBinding
import org.mozilla.fenix.databinding.LanguageListLayoutBinding
import org.mozilla.fenix.immersive_transalte.LanguageJson

class LanguagePopWindow(
    context: Context,
    binding: LanguageListLayoutBinding,
) :
    PopupWindow(
        binding.root,
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    ) {
    private val languageAdapter: LanguageAdapter
    private var jsonArray: JSONArray? = null
    private var onItemClickListener: OnItemClickListener? = null

    init {
        isTouchable = true
        isOutsideTouchable = true
        isFocusable = false
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        languageAdapter = LanguageAdapter(context)
        binding.listView.adapter = languageAdapter
        binding.listView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                languageAdapter.getItem(position)?.let { jo ->
                    dismiss()
                    onItemClickListener?.onItemClick(jo)
                }
            }
        })
    }

    private var isProcessing = false
    fun show(view: View) {
        if (isShowing || isProcessing) {
            return
        }
        isProcessing = true
        MainScope().launch(Dispatchers.Main) {
            jsonArray = withContext(Dispatchers.IO) {
                LanguageJson.getLanguages()
            }
            languageAdapter.setData(jsonArray)
            showAsDropDown(view)
            isProcessing = false
        }
    }

    private class LanguageAdapter(context: Context?) : BaseAdapter() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var jsonArray: JSONArray? = null

        fun setData(jsonArray: JSONArray?) {
            this.jsonArray = jsonArray
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return if (jsonArray != null) jsonArray!!.length() else 0
        }

        override fun getItem(position: Int): JSONObject? {
            return if (jsonArray != null && position < jsonArray!!.length()) {
                jsonArray!!.getJSONObject(position)
            } else {
                null
            }
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val containerView: View
            val holder: ViewHolder
            if (convertView == null) {
                holder = ViewHolder()
                holder.itemBinding = LanguageItemLayoutBinding.inflate(
                    inflater, parent, false)
                containerView = holder.itemBinding.root
                containerView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                containerView = convertView
            }

            val jsonObject: JSONObject? = jsonArray?.getJSONObject(position)

            // val code = getJsonObjectString(jsonObject, "code")
            val lang = getJsonObjectString(jsonObject, "language")
            holder.itemBinding.tvLang.text = lang

            return containerView
        }

        fun getJsonObjectString(jsonObject: JSONObject?, key: String): String {
            if (jsonObject == null) {
                return ""
            }
            try {
                return jsonObject.getString(key)
            } catch (ignored: JSONException) {
            }
            return ""
        }
    }

    public fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(o : JSONObject)
    }

    private class ViewHolder {
        lateinit var itemBinding: LanguageItemLayoutBinding
    }
}
