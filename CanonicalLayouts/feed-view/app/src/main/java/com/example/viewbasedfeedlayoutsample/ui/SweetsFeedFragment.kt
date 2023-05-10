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

package com.example.viewbasedfeedlayoutsample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viewbasedfeedlayoutsample.R
import com.example.viewbasedfeedlayoutsample.data.DataProvider
import com.example.viewbasedfeedlayoutsample.viewModel.SweetsFeedState
import kotlin.math.roundToInt

/**
 * A fragment representing a list of Items.
 */
class SweetsFeedFragment : Fragment() {
    private val state: SweetsFeedState by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sweets_feed, container, false)

        // Number of columns of the feed. Its value can be changed according to window size.
        val columnCount = resources.getInteger(R.integer.column_count)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MySweetsRecyclerViewAdapter(DataProvider.sweets) {
                    state.selectedSweets = it
                    findNavController().navigate(SweetsFeedFragmentDirections.showSweetsDetails())
                }
                addItemDecoration(getItemDecoration(columnCount = columnCount))
            }
        }
        return view
    }

    private fun getItemDecoration(columnCount: Int) = if (columnCount == 1) {
        SweetsFeedItemDecoration(
            horizontalSep =
            resources.getDimension(R.dimen.horizontal_item_sep).roundToInt(),
            verticalSep = resources.getDimension(R.dimen.vertical_item_sep).roundToInt()
        )
    } else {
        SweetsFeedItemDecorationInGrid(
            horizontalSep =
            resources.getDimension(R.dimen.horizontal_item_sep).roundToInt(),
            verticalSep = resources.getDimension(R.dimen.vertical_item_sep).roundToInt(),
            columnCount = columnCount
        )
    }
}
