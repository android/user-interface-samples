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
import androidx.slice.Slice
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import androidx.slice.builders.header
import androidx.slice.builders.list
import androidx.slice.builders.row
import com.example.android.interactivesliceprovider.InteractiveSliceProvider
import com.example.android.interactivesliceprovider.R
import com.example.android.interactivesliceprovider.SliceActionsBroadcastReceiver
import com.example.android.interactivesliceprovider.SliceBuilder
import com.example.android.interactivesliceprovider.data.DataRepository

class ListSliceBuilder(
    val context: Context,
    sliceUri: Uri,
    val repo: DataRepository
) : SliceBuilder(sliceUri) {

    override fun buildSlice(): Slice {
        val listData = repo.getListData()
        return list(context, sliceUri, 6_000) {
            header {
                title = "Times to Destinations"
                subtitle = "List Slice Type"
                primaryAction = SliceAction.create(
                        SliceActionsBroadcastReceiver.getIntent(
                                context,
                                InteractiveSliceProvider.ACTION_TOAST,
                                "Primary Action for List Slice"
                        ),
                        IconCompat.createWithResource(context, R.drawable.ic_work),
                        ListBuilder.ICON_IMAGE,
                        "Primary"
                )
            }
            row {
                title = "Work"
                // Second argument for subtitle informs system we are waiting for data to load.
                setSubtitle(listData.work, listData.work.isEmpty())
                addEndItem(
                    IconCompat.createWithResource(context, R.drawable.ic_work),
                    ListBuilder.ICON_IMAGE
                )
            }
            row {
                title = "Home"
                // Second argument for subtitle informs system we are waiting for data to load.
                setSubtitle(listData.home, listData.home.isEmpty())
                addEndItem(
                    IconCompat.createWithResource(context, R.drawable.ic_home),
                    ListBuilder.ICON_IMAGE
                )
            }
            row {
                title = "School"
                // Second argument for subtitle informs system we are waiting for data to load.
                setSubtitle(listData.school, listData.school.isEmpty())
                addEndItem(
                    IconCompat.createWithResource(context, R.drawable.ic_school),
                    ListBuilder.ICON_IMAGE
                )
            }
        }
    }

    companion object {
        const val TAG = "ListSliceBuilder"
    }
}