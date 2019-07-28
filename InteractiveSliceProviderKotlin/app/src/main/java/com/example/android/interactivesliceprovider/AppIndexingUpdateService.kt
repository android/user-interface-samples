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
package com.example.android.interactivesliceprovider

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.android.interactivesliceprovider.data.model.AppIndexingMetadata
import com.google.firebase.appindexing.FirebaseAppIndex
import com.google.firebase.appindexing.Indexable

class AppIndexingUpdateService : JobIntentService() {

    private val TAG = AppIndexingUpdateService::class.java.simpleName

    companion object {
        private const val UNIQUE_JOB_ID = 42
        fun enqueueWork(context: Context) {
            enqueueWork(context, AppIndexingUpdateService::class.java, UNIQUE_JOB_ID, Intent())
        }
    }

    override fun onHandleWork(intent: Intent) {

        // Retrieve list of indexable data for each slice.
        val sliceIndexableDataList = getIndexableData()

        // Convert list of AppIndexingMetadata objects (custom class) to a list of Indexable
        // objects, so FirebaseAppIndex can consume them.
        val firebaseAppIndex = FirebaseAppIndex.getInstance()
        val appIndexDataList = mutableListOf<Indexable>()

        for(indexableData in sliceIndexableDataList) {
            appIndexDataList.add(
                    Indexable.Builder()
                            .setUrl(indexableData.httpUrl)
                            .setName(indexableData.name)
                            .setKeywords(*indexableData.keywords.toTypedArray())
                            .setMetadata(
                                    Indexable.Metadata.Builder()
                                            .setSliceUri(Uri.parse(indexableData.contentUri)))
                            .build()
            )
        }

        // If you are passing in multiple Indexables, we recommend passing them in together, so
        // we can handle the batching on our side. Make sure you test your complete Indexable list.
        // If one Indexable is invalid, the entire update() call will fail.
        firebaseAppIndex.update(*appIndexDataList.toTypedArray())
                .addOnSuccessListener { Log.d(TAG, "App Indexing succeeded.") }
                .addOnFailureListener { Log.d(TAG, "App Indexing failed: $it") }
    }

    /*
     * Retrieves full list of indexable data for each slice. Here is an example of what is included
     * for each item:
     *      httpUrl = https://interactivesliceprovider.android.example.com/wifi
     *      name = Wifi
     *      keywords = wifi
     *      contentUri = content://com.example.android.interactivesliceprovider/wifi
     */
    private fun getIndexableData(): List<AppIndexingMetadata> {

        // Retrieve data for each Indexable type (slices for this sample).

        // TODO: Move all data for Indexables to the data/domain layer.

        // To support a Slice in search, the Indexable must include the content URI mapping to the
        // correct Slice.
        val hostContentUri = "${application.resources.getString(R.string.host_slice_uri)}"

        // The resource string for scheme doesn't include "://" because android:scheme for the
        // data element in the manifest doesn't allow it. Therefore, we must add it here to via the
        // URI.Builder class to create a complete HTTPS URL.
        // In this case, it is the website scheme/host part of the URL.
        val hostHttpsUrl = Uri.Builder()
                .scheme(application.resources.getString(R.string.scheme_slice_url))
                .authority(applicationContext.resources.getString(R.string.host_slice_url))
                .build()
                .toString()

        val defaultPath = application.resources.getString(R.string.default_slice_path)

        val wifiPath = application.resources.getString(R.string.wifi_slice_path)
        val notePath = application.resources.getString(R.string.note_slice_path)

        val ridePath = application.resources.getString(R.string.ride_slice_path)
        val togglePath = application.resources.getString(R.string.toggle_slice_path)
        val galleryPath = application.resources.getString(R.string.gallery_slice_path)

        val weatherPath = application.resources.getString(R.string.weather_slice_path)
        val reservationPath = application.resources.getString(R.string.reservation_slice_path)
        val loadListPath = application.resources.getString(R.string.list_slice_path)

        val loadGridPath = application.resources.getString(R.string.grid_slice_path)
        val inputRangePath = application.resources.getString(R.string.input_slice_path)
        val rangePath = application.resources.getString(R.string.range_slice_path)

        return listOf(
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + defaultPath,
                        contentUri = hostContentUri + defaultPath,
                        name = "Default",
                        keywords = listOf("default", "defaultest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + wifiPath,
                        contentUri = hostContentUri + wifiPath,
                        name = "Wifi",
                        keywords = listOf("wifi", "wifitest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + notePath,
                        contentUri = hostContentUri + notePath,
                        name = "Note",
                        keywords = listOf("note", "notetest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + ridePath,
                        contentUri = hostContentUri + ridePath,
                        name = "Ride",
                        keywords = listOf("ride", "ridetest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + togglePath,
                        contentUri = hostContentUri + togglePath,
                        name = "Toggle",
                        keywords = listOf("toggle", "toggletest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + galleryPath,
                        contentUri = hostContentUri + galleryPath,
                        name = "Gallery",
                        keywords = listOf("gallery", "gallerytest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + weatherPath,
                        contentUri = hostContentUri + weatherPath,
                        name = "Weather",
                        keywords = listOf("weather", "weathertest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + reservationPath,
                        contentUri = hostContentUri + reservationPath,
                        name = "Reservation",
                        keywords = listOf("reservation", "reservationtest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + loadListPath,
                        contentUri = hostContentUri + loadListPath,
                        name = "Load List",
                        keywords = listOf("list", "loadlist", "loadlisttest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + loadGridPath,
                        contentUri = hostContentUri + loadGridPath,
                        name = "Load Grid",
                        keywords = listOf("grid", "loadgrid", "loadgridtest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + inputRangePath,
                        contentUri = hostContentUri + inputRangePath,
                        name = "Input Range",
                        keywords = listOf("input", "range", "inputrange", "inputrangetest1234")
                ),
                AppIndexingMetadata(
                        httpUrl = hostHttpsUrl + rangePath,
                        contentUri = hostContentUri + rangePath,
                        name = "Range",
                        keywords = listOf("range", "rangetest1234")
                )
        )
    }
}