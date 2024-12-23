/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.imts

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.FrameLayout
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

class LanguageSelectView : FrameLayout {
    private lateinit var binding: LanguageListLayoutBinding

    private lateinit var languageAdapter: LanguageAdapter
    private var jsonArray: JSONArray? = null
    private var onItemClickListener: OnItemClickListener? = null
    private var onDismissLister: OnDismissLister? = null

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
        binding = LanguageListLayoutBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)
        visibility = GONE

        languageAdapter = LanguageAdapter(context)
        binding.listView.adapter = languageAdapter
        binding.listView.setOnItemClickListener(
            object : AdapterView.OnItemClickListener {
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
            },
        )
    }

    private var isProcessing = false

    fun show() {
        if (visibility == VISIBLE || isProcessing) {
            return
        }
        isProcessing = true
        MainScope().launch(Dispatchers.Main) {
            jsonArray = withContext(Dispatchers.IO) {
                LanguageJson.getLanguages()
            }
            languageAdapter.setData(jsonArray)
            isProcessing = false
            visibility = VISIBLE
        }
    }

    fun dismiss() {
        visibility = GONE
        onDismissLister?.onDismiss()
    }

    private val handler = Handler(Looper.getMainLooper())

    fun onSearch(words: String) {
        if (visibility == GONE || jsonArray == null) {
            return
        }
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ filter(words) }, 100)
    }

    /**
     * 匹配 lang
     */
    private fun filter(words: String) {
        MainScope().launch(Dispatchers.Main) {
            val filterArray = JSONArray()
            withContext(Dispatchers.IO) {
                for (poi in 0 until jsonArray!!.length()) {
                    val jsonObject: JSONObject? = jsonArray?.getJSONObject(poi)
                    jsonObject?.let {
                        try {
                            val language = it.getString("language").lowercase()
                            if (language.contains(words) || language == words) {
                                filterArray.put(it)
                            }
                        } finally {
                        }
                    }
                }
            }
            languageAdapter.setData(filterArray)
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
                    inflater, parent, false,
                )
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

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnDismissListener(onDismissLister: OnDismissLister) {
        this.onDismissLister = onDismissLister
    }

    interface OnItemClickListener {
        fun onItemClick(o: JSONObject)
    }

    interface OnDismissLister {
        fun onDismiss()
    }

    private class ViewHolder {
        lateinit var itemBinding: LanguageItemLayoutBinding
    }
}
