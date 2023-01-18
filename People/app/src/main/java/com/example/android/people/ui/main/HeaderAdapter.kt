/*
 * Copyright (C) 2022 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.people.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.people.R
import com.example.android.people.databinding.ContactHeaderBinding

class HeaderAdapter(private val onClick: () -> Unit) : RecyclerView.Adapter<HeaderViewHolder>() {
    init {
        setHasStableIds(true)
    }

    private var cachedHolder: HeaderViewHolder? = null

    var shouldShowRationale = false
        set(value) {
            field = value
            cachedHolder?.let { holder ->
                onBindViewHolder(holder, 0)
            }
        }

    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        return HeaderViewHolder(parent).also {
            cachedHolder = it
            it.binding.grant.setOnClickListener { onClick() }
        }
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.binding.rationale.visibility =
            if (shouldShowRationale) View.VISIBLE else View.GONE
    }
}

class HeaderViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.contact_header, parent, false)
) {
    val binding = ContactHeaderBinding.bind(itemView)
}
