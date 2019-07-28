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
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import androidx.slice.builders.header
import androidx.slice.builders.list
import com.example.android.interactivesliceprovider.InteractiveSliceProvider
import com.example.android.interactivesliceprovider.R
import com.example.android.interactivesliceprovider.SliceActionsBroadcastReceiver
import com.example.android.interactivesliceprovider.R.drawable
import com.example.android.interactivesliceprovider.SliceBuilder

class NoteSliceBuilder(
    val context: Context,
    sliceUri: Uri
) : SliceBuilder(sliceUri) {

    override fun buildSlice() = list(context, sliceUri, ListBuilder.INFINITY) {
        setAccentColor(ContextCompat.getColor(context, R.color.slice_accent_color))
        header {
            title = "Create new note"
            primaryAction = SliceAction.create(
                    SliceActionsBroadcastReceiver.getIntent(
                            context,
                            InteractiveSliceProvider.ACTION_TOAST,
                            "Primary Action for Note Slice"
                    ),
                    IconCompat.createWithResource(context, drawable.ic_create),
                    ListBuilder.ICON_IMAGE,
                    "Primary"
            )
        }
        addAction(
            SliceAction.create(
                SliceActionsBroadcastReceiver.getIntent(
                    context, InteractiveSliceProvider.ACTION_TOAST, "create note"
                ),
                IconCompat.createWithResource(context, drawable.ic_create),
                ListBuilder.ICON_IMAGE,
                "Create note"
            )
        )
        addAction(
            SliceAction.create(
                SliceActionsBroadcastReceiver.getIntent(
                    context, InteractiveSliceProvider.ACTION_TOAST, "voice note"
                ),
                IconCompat.createWithResource(context, drawable.ic_voice),
                ListBuilder.ICON_IMAGE,
                "Voice note"
            )
        )
        addAction(
            SliceAction.create(
                InteractiveSliceProvider.getPendingIntent(
                    context, "android.media.action.IMAGE_CAPTURE"
                ),
                IconCompat.createWithResource(context, drawable.ic_camera),
                ListBuilder.ICON_IMAGE,
                "Photo note"
            )
        )
    }

    companion object {
        const val TAG = "NoteSliceBuilder"
    }
}
