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
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import androidx.slice.builders.cell
import androidx.slice.builders.gridRow
import androidx.slice.builders.header
import androidx.slice.builders.list
import com.example.android.interactivesliceprovider.InteractiveSliceProvider
import com.example.android.interactivesliceprovider.R.drawable
import com.example.android.interactivesliceprovider.SliceActionsBroadcastReceiver
import com.example.android.interactivesliceprovider.SliceBuilder
import com.example.android.interactivesliceprovider.data.DataRepository

class GridSliceBuilder(
    val context: Context,
    sliceUri: Uri,
    val repo: DataRepository
) : SliceBuilder(sliceUri) {

    override fun buildSlice() = list(context, sliceUri, ListBuilder.INFINITY) {
        val data = repo.getGridData()
        header {
            // Second argument for title/subtitle informs system we are waiting for data to load.
            setTitle(data.title, data.title.isEmpty())
            setSubtitle(data.subtitle, data.subtitle.isEmpty())
            primaryAction = SliceAction.create(
                    SliceActionsBroadcastReceiver.getIntent(
                            context,
                            InteractiveSliceProvider.ACTION_TOAST,
                            "Primary Action for Grid Slice"
                    ),
                    IconCompat.createWithResource(context, drawable.ic_home),
                    ListBuilder.ICON_IMAGE,
                    "Primary"
            )
        }
        gridRow {
            cell {
                addImage(
                    IconCompat.createWithResource(context, drawable.ic_home),
                    ListBuilder.ICON_IMAGE
                )
                addTitleText("Home")
                addText(data.home, data.home.isEmpty())
            }
            cell {
                addImage(
                    IconCompat.createWithResource(context, drawable.ic_work),
                    ListBuilder.ICON_IMAGE
                )
                addTitleText("Work")
                addText(data.work, data.work.isEmpty())
            }
            cell {
                addImage(
                    IconCompat.createWithResource(context, drawable.ic_school),
                    ListBuilder.ICON_IMAGE
                )
                addTitleText("School")
                addText(data.school, data.school.isEmpty())
            }
        }
    }

    companion object {
        const val TAG = "GridSliceBuilder"
    }
}