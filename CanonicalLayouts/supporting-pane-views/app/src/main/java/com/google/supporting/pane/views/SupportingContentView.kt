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

package com.google.supporting.pane.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.supporting.pane.views.databinding.SupportingContentBinding


class SupportingContentView @JvmOverloads
constructor(ctx: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    private val adapter = SupportAdapter { onItemClicked(it) }
    private var itemClickListener: (String) -> Unit = {}

    init {
        // get the inflater service from the android system
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        with(SupportingContentBinding.inflate(inflater, this, true)){
            supportList.adapter = adapter
            supportList.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        }
    }

    fun updateItems(newItems: List<String>){
        adapter.updateItems(newItems)
    }

    fun setOnItemClickListener(f: (String) -> Unit) {
        itemClickListener = f
    }

    private fun onItemClicked(item: String){
        itemClickListener(item)
    }

}