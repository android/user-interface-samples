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

package com.example.android.text.ui.utils

import android.view.View
import android.widget.Adapter
import android.widget.AdapterView

fun <T : Adapter> AdapterView<T>.doOnItemSelected(
    onNothingSelected: () -> Unit = {},
    onItemSelected: (view: View, position: Int, id: Long) -> Unit
) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (view != null) {
                onItemSelected(view, position, id)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            onNothingSelected()
        }
    }
}
