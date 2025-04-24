/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.databinding.FragmentWebTranslateLayoutBinding
import org.mozilla.fenix.immersive_transalte.home.adapter.WebTranslateAdapter
import org.mozilla.fenix.immersive_transalte.home.bean.WebItem
import org.mozilla.fenix.immersive_transalte.home.data.WebDataProvider

class WebTranslateFragment : Fragment() {
    private lateinit var binding: FragmentWebTranslateLayoutBinding
    private lateinit var webTranslateAdapter: WebTranslateAdapter

    private val itemData = mutableListOf<WebItem>().apply {
        add(WebItem(WebItem.TYPE_TRANSLATE))
        add(WebItem(WebItem.TYPE_FEATURED_WEBS))
        addAll(WebDataProvider.getWebLinks())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWebTranslateLayoutBinding.inflate(
            inflater, container, false,
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webTranslateAdapter = WebTranslateAdapter(view.context)
        binding.recyclerView.layoutManager = LinearLayoutManager(view.context)
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = webTranslateAdapter
        webTranslateAdapter.setData(itemData)
        webTranslateAdapter.setCallback(
            object : WebTranslateAdapter.Callback {
                override fun onUrlClick(url: String?) {
                    url?.let { openUrl(it) }
                }
            },
        )
        binding.flClose.setOnClickListener {
            NavHostFragment.findNavController(this).popBackStack()
        }
    }

    private fun openUrl(url: String) {
        if (activity is HomeActivity) {
            (activity as HomeActivity).openToBrowserAndLoad(
                searchTermOrURL = url,
                newTab = true,
                from = BrowserDirection.FromGlobal,
                isReturnHome = false,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // showToolbar("")
    }
}
