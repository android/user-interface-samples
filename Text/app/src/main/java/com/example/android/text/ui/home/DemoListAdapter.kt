/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.example.android.text.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.text.databinding.DemoItemBinding
import com.example.android.text.demo.Demo

internal class DemoListAdapter(
    private val onDemoClick: (Demo) -> Unit
) : ListAdapter<Demo, DemoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoViewHolder {
        return DemoViewHolder(parent, onDemoClick)
    }

    override fun onBindViewHolder(holder: DemoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private val DiffCallback = object : DiffUtil.ItemCallback<Demo>() {
    override fun areItemsTheSame(oldItem: Demo, newItem: Demo) = oldItem.title == newItem.title
    override fun areContentsTheSame(oldItem: Demo, newItem: Demo) = oldItem.title == newItem.title
}

internal class DemoViewHolder(
    parent: ViewGroup,
    onDemoClick: (Demo) -> Unit,
    private val binding: DemoItemBinding = DemoItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )
) : RecyclerView.ViewHolder(binding.root) {

    private var _demo: Demo? = null

    init {
        binding.demo.setOnClickListener { _demo?.let { onDemoClick(it) } }
    }

    fun bind(demo: Demo) {
        _demo = demo
        binding.title.text = demo.title
    }
}
