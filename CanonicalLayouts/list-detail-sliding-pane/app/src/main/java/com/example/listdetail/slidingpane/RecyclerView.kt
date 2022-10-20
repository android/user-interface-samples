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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listdetail.slidingpane.databinding.ListItemBinding

class Adapter(
    private val items: List<DefinedWord>,
    private val layoutInflater: LayoutInflater,
    private val onItemClickListener: (DefinedWord) -> Unit
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ListItemBinding,
        val onItemClickListener: (DefinedWord) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(definedWord: DefinedWord) {
            binding.root.text = definedWord.word
            binding.root.isSelected = false
            binding.root.setOnClickListener {
                onItemClickListener(definedWord)
                binding.root.isSelected = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(layoutInflater)
        return ViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(items[position])
    }

    override fun getItemCount(): Int = items.size

}

