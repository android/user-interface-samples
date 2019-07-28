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

package com.example.android.interactivesliceprovider.slicebuilders

import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import androidx.slice.builders.inputRange
import androidx.slice.builders.list
import com.example.android.interactivesliceprovider.InteractiveSliceProvider
import com.example.android.interactivesliceprovider.SliceActionsBroadcastReceiver
import com.example.android.interactivesliceprovider.R
import com.example.android.interactivesliceprovider.SliceBuilder

class InputRangeSliceBuilder(
    val context: Context,
    sliceUri: Uri
) : SliceBuilder(sliceUri) {

    override fun buildSlice(): Slice {
        val icon = IconCompat.createWithResource(context, R.drawable.ic_star_on)
        return list(context, sliceUri, ListBuilder.INFINITY) {
            setAccentColor(ContextCompat.getColor(context, R.color.slice_accent_color))
            inputRange {
                title = "Star rating"
                subtitle = "Rate from 0 to 10."
                min = 0
                thumb = icon
                inputAction =
                        SliceActionsBroadcastReceiver.getIntent(
                            context,
                            InteractiveSliceProvider.ACTION_TOAST_RANGE_VALUE, null
                        )
                max = 10
                value = 8
                primaryAction = SliceAction.create(
                    SliceActionsBroadcastReceiver.getIntent(
                        context, InteractiveSliceProvider.ACTION_TOAST, "open star rating"
                    ),
                    icon, ListBuilder.ICON_IMAGE, "Rate"
                )
                contentDescription = "Slider for star ratings."
            }
        }
    }

    companion object {
        const val TAG = "ListSliceBuilder"
    }
}