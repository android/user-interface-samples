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

package com.example.android.sliceviewer.domain

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit

/**
 * Simple CRUD local data source for persisting list of slice URIs.
 */
class LocalUriDataSource(
    private val sharedPrefs: SharedPreferences
) : UriDataSource {
    override fun getAllUris(): MutableList<Uri> {
        return sharedPrefs.getStringSet(
            KEY_URI, setOf<String>()
        ).map {
            Uri.parse(it)
        }.toMutableList()
    }

    override fun addUri(uri: Uri) {
        save(getAllUris().apply { add(uri) })
    }

    override fun removeFromPosition(index: Int) {
        save(getAllUris().apply { removeAt(index) })
    }

    private fun save(list: List<Uri>) {
        sharedPrefs.edit {
            putStringSet(KEY_URI, list.map { it.toString() }.toSet())
        }
    }

    companion object {
        const val SHARED_PREFS_NAME = "shared_prefs"
        const val KEY_URI = "com.example.android.sliceviewer.sliceuris"
    }
}