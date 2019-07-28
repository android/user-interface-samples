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

package com.example.android.sliceviewer.util

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.slice.SliceMetadata
import androidx.slice.core.SliceHints
import androidx.slice.widget.SliceLiveData
import androidx.slice.widget.SliceView
import androidx.slice.widget.SliceView.OnSliceActionListener
import com.example.android.sliceviewer.ui.list.SliceViewerActivity
import com.example.android.sliceviewer.ui.list.SliceViewerActivity.Companion.TAG

fun SliceView.bind(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    uri: Uri,
    onSliceActionListener: OnSliceActionListener = OnSliceActionListener { _, _ -> },
    onClickListener: OnClickListener = OnClickListener { },
    onLongClickListener: OnLongClickListener = OnLongClickListener { false },
    scrollable: Boolean = false
) {
    setOnSliceActionListener(onSliceActionListener)
    setOnClickListener(onClickListener)
    setScrollable(scrollable)
    setOnLongClickListener(onLongClickListener)
    if (uri.scheme == null) {
        Log.w(TAG, "Scheme is null for URI $uri")
        return
    }
    // If someone accidentally prepends the "slice-" prefix to their scheme, let's remove it.
    val scheme =
        if (uri.scheme.startsWith("slice-")) {
            uri.scheme.replace("slice-", "")
        }
        else {
            uri.scheme
        }
    if (scheme == ContentResolver.SCHEME_CONTENT ||
        scheme.equals("https", true) ||
        scheme.equals("http", true)
    ) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val sliceLiveData = SliceLiveData.fromIntent(context, intent)
        sliceLiveData?.removeObservers(lifecycleOwner)
        try {
            sliceLiveData?.observe(lifecycleOwner, Observer { updatedSlice ->
                if (updatedSlice == null) return@Observer
                slice = updatedSlice
                val expiry = SliceMetadata.from(context, updatedSlice).expiry
                if (expiry != SliceHints.INFINITY) {
                    // Shows the updated text after the TTL expires.
                    postDelayed(
                        { slice = updatedSlice },
                        expiry - System.currentTimeMillis() + 15
                    )
                }
                Log.d(SliceViewerActivity.TAG, "Update Slice: $updatedSlice")
            })
        } catch (e: Exception) {
            Log.e(
                SliceViewerActivity.TAG,
                "Failed to find a valid ContentProvider for authority: $uri"
            )
        }
    } else {
        Log.w(SliceViewerActivity.TAG, "Invalid uri, skipping slice: $uri")
    }
}