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

package com.google.supporting.pane.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.supporting.pane.fragments.databinding.ItemSupportBinding

class SupportAdapter(val onItemClick: (String) -> Unit) : RecyclerView.Adapter<SupportViewHolder>() {

    private val items = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportViewHolder {
        val binding = ItemSupportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SupportViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SupportViewHolder, position: Int) {
        holder.bind(items[position]) {
            onItemClick(it)
        }
    }

    fun updateItems(newItems: List<String>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

class SupportViewHolder(private val binding: ItemSupportBinding) : ViewHolder(binding.root) {

    fun bind(label: String, onClick: (String) -> Unit) {
        binding.supportItem.text = label
        binding.root.setOnClickListener { onClick(label) }
    }
}