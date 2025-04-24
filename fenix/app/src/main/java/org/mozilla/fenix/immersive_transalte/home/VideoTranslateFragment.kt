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
import org.mozilla.fenix.databinding.FragmentVideoTranslateLayoutBinding
import org.mozilla.fenix.immersive_transalte.home.adapter.VideoTranslateAdapter
import org.mozilla.fenix.immersive_transalte.home.bean.WebItem
import org.mozilla.fenix.immersive_transalte.home.data.VideoDataProvider

class VideoTranslateFragment : Fragment() {
    private lateinit var binding: FragmentVideoTranslateLayoutBinding
    private lateinit var videoTranslateAdapter: VideoTranslateAdapter

    private val itemData = mutableListOf<WebItem>().apply {
        add(WebItem(WebItem.TYPE_TRANSLATE))
        add(WebItem(WebItem.TYPE_FEATURED_WEBS))
        addAll(VideoDataProvider.getWebLinks())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentVideoTranslateLayoutBinding.inflate(
            inflater, container, false,
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoTranslateAdapter = VideoTranslateAdapter(view.context)
        binding.recyclerView.layoutManager = LinearLayoutManager(view.context)
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = videoTranslateAdapter
        videoTranslateAdapter.setData(itemData)
        videoTranslateAdapter.setCallback(
            object : VideoTranslateAdapter.Callback {
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

}
