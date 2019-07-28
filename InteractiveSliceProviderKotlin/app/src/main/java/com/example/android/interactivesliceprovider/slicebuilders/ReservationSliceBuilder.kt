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
import com.example.android.interactivesliceprovider.SliceActionsBroadcastReceiver
import com.example.android.interactivesliceprovider.R
import com.example.android.interactivesliceprovider.R.drawable
import com.example.android.interactivesliceprovider.SliceBuilder

class ReservationSliceBuilder(
    val context: Context,
    sliceUri: Uri
) : SliceBuilder(sliceUri) {

    override fun buildSlice() = list(context, sliceUri, ListBuilder.INFINITY) {
        header {
            title = "Upcoming trip to Seattle"
            subtitle = "Feb 1 - 19 | 2 guests"
            primaryAction = SliceAction.create(
                    SliceActionsBroadcastReceiver.getIntent(
                            context,
                            InteractiveSliceProvider.ACTION_TOAST,
                            "Primary Action for Reservation Slice"
                    ),
                    IconCompat.createWithResource(context, drawable.ic_location),
                    ListBuilder.ICON_IMAGE,
                    "Primary"
            )
        }
        addAction(
            SliceAction.create(
                SliceActionsBroadcastReceiver.getIntent(
                    context,
                    InteractiveSliceProvider.ACTION_TOAST, "show location on map"
                ),
                IconCompat.createWithResource(context, drawable.ic_location),
                ListBuilder.ICON_IMAGE,
                "Show reservation location"
            )
        )
        addAction(
            SliceAction.create(
                SliceActionsBroadcastReceiver.getIntent(
                    context, InteractiveSliceProvider.ACTION_TOAST, "contact host"
                ),
                IconCompat.createWithResource(context, drawable.ic_text),
                ListBuilder.ICON_IMAGE,
                "Contact host"
            )
        )
        gridRow {
            cell {
                addImage(
                    IconCompat.createWithResource(
                        context,
                        R.drawable.reservation
                    ),
                    ListBuilder.LARGE_IMAGE
                )
                setContentDescription("Image of your reservation in Seattle")
            }
        }
        gridRow {
            cell {
                addTitleText("Check In")
                addText("12:00 PM, Feb 1")
            }
            cell {
                addTitleText("Check Out")
                addText("11:00 AM, Feb 19")
            }
        }
    }

    companion object {
        const val TAG = "ListSliceBuilder"
    }
}