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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.slice.SliceManager
import com.google.firebase.appindexing.FirebaseAppIndex

import java.net.URLDecoder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Grants permission to all non-default slices.
        // IMPORTANT NOTE: This will not be needed when the API is launched publicly. It is only
        // required at the current time for the EAP.
        grantNonDefaultSlicePermission()


        val defaultUriEncoded = getResources().getString(R.string.default_slice_uri)

        // Decode for special characters that may appear in URI. Review Android documentation on
        // special characters for more information:
        // https://developer.android.com/guide/topics/resources/string-resource#FormattingAndStyling
        val defaultUriDecoded = URLDecoder.decode(defaultUriEncoded, "UTF-8")

        // Grants permission for default slice.
        grantSlicePermissions(defaultUriDecoded.toUri())

        setContentView(R.layout.activity_main)
    }

    private fun grantSlicePermissions(uri: Uri, notifyIndexOfChange: Boolean = true) {
        // Grant permissions to AGSA
        SliceManager.getInstance(this).grantSlicePermission(
            "com.google.android.googlequicksearchbox",
            uri
        )
        // grant permission to GmsCore
        SliceManager.getInstance(this).grantSlicePermission(
            "com.google.android.gms",
            uri
        )

        if (notifyIndexOfChange) {
            // Notify change. Ensure that it does not happen on every onCreate()
            // calls as notify change triggers reindexing which can clear usage
            // signals of your app and hence impact your app’s ranking. One way to
            // do this is to use shared preferences.
            val sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(applicationContext)

            if (!sharedPreferences.getBoolean(PREF_GRANT_SLICE_PERMISSION, false)) {
                contentResolver.notifyChange(uri, null /* content observer */)
                sharedPreferences.edit {
                    putBoolean(PREF_GRANT_SLICE_PERMISSION, true)
                }
            }
        }
    }

    /*
     * Grants permissions for non-default URLs, so they can be shown in google search on device.
     * IMPORTANT NOTE: As stated earlier, this will not be required soon (and not for launch), so
     * you can assume you won't need to loop through all your non-default slice URIs for the launch.
     */
    private fun grantNonDefaultSlicePermission () {

        val nonDefaultUris = listOf(
                applicationContext.resources.getString(R.string.wifi_slice_uri),
                applicationContext.resources.getString(R.string.note_slice_uri),
                applicationContext.resources.getString(R.string.ride_slice_uri),
                applicationContext.resources.getString(R.string.toggle_slice_uri),
                applicationContext.resources.getString(R.string.gallery_slice_uri),
                applicationContext.resources.getString(R.string.weather_slice_uri),
                applicationContext.resources.getString(R.string.reservation_slice_uri),
                applicationContext.resources.getString(R.string.list_slice_uri),
                applicationContext.resources.getString(R.string.grid_slice_uri),
                applicationContext.resources.getString(R.string.input_slice_uri),
                applicationContext.resources.getString(R.string.range_slice_uri)
        )

        for(nonDefaultUri in nonDefaultUris) {
            grantSlicePermissions(
                    Uri.parse(nonDefaultUri),
                    false
            )
        }
    }

    fun onClickIndexSlices (view: View) {
        val intent = Intent(this, AppIndexingUpdateReceiver::class.java)
        intent.action = FirebaseAppIndex.ACTION_UPDATE_INDEX
        sendBroadcast(intent)
    }

    companion object {
        private const val PREF_GRANT_SLICE_PERMISSION = "permission_slice_status"

        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(context, 0, intent, 0)
        }
    }
}