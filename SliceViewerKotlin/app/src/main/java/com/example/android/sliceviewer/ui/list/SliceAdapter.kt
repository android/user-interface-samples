/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sliceviewer.ui.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.slice.widget.SliceView
import androidx.slice.widget.SliceView.SliceMode
import com.example.android.sliceviewer.R
import com.example.android.sliceviewer.R.layout
import com.example.android.sliceviewer.util.bind
import com.example.android.sliceviewer.util.convertToSliceViewerScheme

class SliceAdapter(
    val lifecycleOwner: LifecycleOwner,
    val selectedMode: LiveData<Int>
) : ListAdapter<Uri, SliceViewHolder>(
    SlicesDiff
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(layout.slice_row, parent, false)
        return SliceViewHolder(itemView, selectedMode, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: SliceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SliceViewHolder(
    view: View,
    @SliceMode val selectedMode: LiveData<Int>,
    private val lifecycleOwner: LifecycleOwner
) : ViewHolder(view) {

    private val context: Context = view.context
    private val sliceView: SliceView = view.findViewById(R.id.slice)
    private val uriValue: TextView = view.findViewById(R.id.uri_value)
    private val uriGroup: ViewGroup = view.findViewById(R.id.uri_group)

    // Context, LifecycleOwner, onSliceActionListener, OnClickListener, scrollable, OnLongClickListener,
    fun bind(uri: Uri) {
        sliceView.bind(
            context = context,
            lifecycleOwner = lifecycleOwner,
            uri = uri,
            scrollable = false
        )

        uriGroup.setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri.convertToSliceViewerScheme()))
        }
        selectedMode.observe(lifecycleOwner, Observer {
            sliceView.mode = it ?: SliceView.MODE_LARGE
        })
        uriValue.text = uri.toString()
    }
}

object SlicesDiff : DiffUtil.ItemCallback<Uri>() {
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri) = oldItem === newItem

    override fun areContentsTheSame(oldItem: Uri, newItem: Uri) = oldItem == newItem
}