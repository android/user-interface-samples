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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.viewbasedfeedlayoutsample.data.Sweets
import com.example.viewbasedfeedlayoutsample.databinding.FragmentSweetsFeedItemBinding

/**
 * [RecyclerView.Adapter] that can display a [Sweets].
 */
class MySweetsRecyclerViewAdapter(
    private val values: List<Sweets>,
    private val onSweetsSelected: (Sweets) -> Unit = {}
) : RecyclerView.Adapter<MySweetsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentSweetsFeedItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.descriptionView.text =
            holder.descriptionView.context.getString(item.description)
        holder.thumbnailView.load(item.imageUrl)
        holder.setClickListener { onSweetsSelected(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSweetsFeedItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val descriptionView: TextView = binding.description
        val thumbnailView: ImageView = binding.thumbnail
        private val root = binding.root

        fun setClickListener(listener: (View) -> Unit) {
            root.setOnClickListener(listener)
        }

        override fun toString(): String {
            return super.toString() + " '" + descriptionView.text + "'"
        }
    }
}
