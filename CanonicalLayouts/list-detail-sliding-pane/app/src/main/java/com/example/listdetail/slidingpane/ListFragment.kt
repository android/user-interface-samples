/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.listdetail.slidingpane

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout.LOCK_MODE_LOCKED
import com.example.listdetail.slidingpane.databinding.FragmentListBinding

class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var backManager: SlidingPaneBackManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentListBinding.bind(view)

        backManager = SlidingPaneBackManager(binding.slidingPaneLayout)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backManager)

        val navHost =
            childFragmentManager.findFragmentById(R.id.detail_container) as NavHostFragment
        val controller = navHost.navController

        /**
         * Locks the SlidingPaneLayout so that it cannot be slided, but only opened and closed
         * through navigation
         */
        binding.slidingPaneLayout.lockMode = LOCK_MODE_LOCKED

        binding.list.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.list.adapter = Adapter(sampleWords, layoutInflater) {
            controller.navigate(
                R.id.detailFragment, bundleOf(
                    DetailFragment.LABEL to it.word,
                    DetailFragment.DEFINITION to it.definition
                )
            )

            binding.root.open()
        }
    }
}