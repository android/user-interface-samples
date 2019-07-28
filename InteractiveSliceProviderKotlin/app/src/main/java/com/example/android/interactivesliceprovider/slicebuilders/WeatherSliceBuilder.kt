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
import androidx.slice.builders.cell
import androidx.slice.builders.gridRow
import androidx.slice.builders.header
import androidx.slice.builders.list
import com.example.android.interactivesliceprovider.InteractiveSliceProvider
import com.example.android.interactivesliceprovider.SliceActionsBroadcastReceiver
import com.example.android.interactivesliceprovider.R.drawable
import com.example.android.interactivesliceprovider.SliceBuilder

class WeatherSliceBuilder(
    val context: Context,
    sliceUri: Uri
) : SliceBuilder(sliceUri) {

    override fun buildSlice(): Slice {
        val action = SliceAction.create(
            SliceActionsBroadcastReceiver.getIntent(
                context,
                InteractiveSliceProvider.ACTION_TOAST,
                "open weather app"
            ),
            IconCompat.createWithResource(context, drawable.ic_location),
            ListBuilder.ICON_IMAGE,
            "Weather is happening!"
        )
        return list(context, sliceUri, ListBuilder.INFINITY) {
            header {
                title = "Weather"
                subtitle = "Weather Slice Example"
                primaryAction = action
            }
            gridRow {
                primaryAction = action
                cell {
                    addImage(
                        IconCompat.createWithResource(context, drawable.weather_1),
                        ListBuilder.SMALL_IMAGE
                    )
                    addText("MON")
                    addTitleText("69\u00B0")
                }
                cell {
                    addImage(
                        IconCompat.createWithResource(context, drawable.weather_2),
                        ListBuilder.SMALL_IMAGE
                    )
                    addText("TUE")
                    addTitleText("71\u00B0")
                }
                cell {
                    addImage(
                        IconCompat.createWithResource(context, drawable.weather_3),
                        ListBuilder.SMALL_IMAGE
                    )
                    addText("WED")
                    addTitleText("76\u00B0")
                }
                cell {
                    addImage(
                        IconCompat.createWithResource(context, drawable.weather_4),
                        ListBuilder.SMALL_IMAGE
                    )
                    addText("THU")
                    addTitleText("72\u00B0")
                }
                cell {
                    addImage(
                        IconCompat.createWithResource(
                            context,
                            drawable.weather_1
                        ),
                        ListBuilder.SMALL_IMAGE
                    )
                    addText("FRI")
                    addTitleText("68\u00B0")
                }
            }
        }
    }

    companion object {
        const val TAG = "ListSliceBuilder"
    }
}