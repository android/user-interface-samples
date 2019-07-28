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
import androidx.slice.builders.row
import com.example.android.interactivesliceprovider.MainActivity
import com.example.android.interactivesliceprovider.R
import com.example.android.interactivesliceprovider.SliceBuilder

class DefaultSliceBuilder(
        val context: Context,
        sliceUri: Uri
) : SliceBuilder(sliceUri) {

    override fun buildSlice() = list(context, sliceUri, ListBuilder.INFINITY) {
        val activityAction = SliceAction.create(
                MainActivity.getPendingIntent(context),
                IconCompat.createWithResource(context, R.mipmap.ic_launcher),
                ListBuilder.LARGE_IMAGE,
                "Open app"
        )
        return list(context, sliceUri, ListBuilder.INFINITY) {
            setAccentColor(ContextCompat.getColor(context, R.color.slice_accent_color))
            // The first row can be a header or a row. If it's a header then it'll be styled
            // slightly differently -- you might not always want the content in the top row to be
            // bigger or differentiated if that doesn't make sense for the content.
            header {
                title = "Default app launcher slice!"

                // Subtitle is displayed when the slice is large (or small if a summary isn't set).
                subtitle = "Subtitle displayed in large mode."

                // Summary (optional) is displayed when the slice is small. It allows developers
                // to provide a summary when the rest of the slice isn't visible (other rows
                // outside of the header / first row aren't shown in small mode).
                summary = "Summary displayed in small mode."

                // Content description (optional) is what accessibility (talk back) will use to
                // describe the slice when the header is focused. If this isn't set, talkback will
                // read whatever text is in the header (title / subtitle etc.)
                contentDescription = "Content Description used for accessibility."

                primaryAction = activityAction
            }



            row {
                subtitle = "Subtitle for row 2!"
                contentDescription = "Row 2 Content Description"
                // Places icon/action at the front of the slice.
                // Note, these sorts of icons aren't allowed on the first row in a slice (usually
                // reserved for the header).
                setTitleItem(activityAction)
                primaryAction = activityAction
                }
            row {
                title = "Try this third row!"
                subtitle = "Subtitle for row 3!"
                contentDescription = "Row 3 Content Description"
                addEndItem(activityAction)
                primaryAction = activityAction
            }
        }
    }

    companion object {
        const val TAG = "DefaultSliceBuilder"
    }
}