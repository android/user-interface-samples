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

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.slice.widget.SliceView
import com.example.android.sliceviewer.domain.UriDataSource

class SliceViewModel(
    private val uriDataSource: UriDataSource
) : ViewModel() {

    val selectedMode = MutableLiveData<Int>().apply { value = SliceView.MODE_LARGE }

    val slices
        get() = uriDataSource.getAllUris()

    fun addSlice(uri: Uri) {
        uriDataSource.addUri(uri)
    }

    fun removeFromPosition(position: Int) {
        uriDataSource.removeFromPosition(position)
    }
}