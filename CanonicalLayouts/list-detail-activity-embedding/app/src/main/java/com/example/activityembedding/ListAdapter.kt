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
package com.example.activityembedding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.activityembedding.databinding.ListItemBinding

class CustomAdapter (
    val dataSet: List<DefinedWord>,
    val onItemClickListener: (DefinedWord) -> Unit
    ) : Adapter<CustomAdapter.ViewHolder>() {

    var lastSelectedItem : ListItemBinding? = null

    class ViewHolder(
        val binding : ListItemBinding,
        val onItemClickListener: (DefinedWord) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(definedWord: DefinedWord) {
            binding.textView.text = definedWord.word
            binding.root.isSelected = false
            binding.root.setOnClickListener {
                onItemClickListener(definedWord)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int) : ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(layoutInflater,parent, false)
        val primary = parent.context.getColorStateList(
            com.google.android.material.R.color.design_default_color_primary)
        val surface = parent.context.getColorStateList(
            com.google.android.material.R.color.design_default_color_surface
        )
        val onPrimary = parent.context.getColorStateList(
            com.google.android.material.R.color.design_default_color_on_primary
        )
        val onSurface = parent.context.getColorStateList(
            com.google.android.material.R.color.design_default_color_on_surface
        )

        return ViewHolder(binding, {
            onItemClickListener(it)
            binding.root.isSelected = true
            binding.root.setCardBackgroundColor(primary)
            binding.textView.setTextColor(onPrimary)

            if (null != lastSelectedItem) {
                lastSelectedItem!!.cardView.setCardBackgroundColor(surface)
                lastSelectedItem!!.cardView.invalidate()
                lastSelectedItem!!.textView.setTextColor(onSurface)
                lastSelectedItem!!.textView.invalidate()
            }
            lastSelectedItem = binding
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(dataSet.get(position))
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}




