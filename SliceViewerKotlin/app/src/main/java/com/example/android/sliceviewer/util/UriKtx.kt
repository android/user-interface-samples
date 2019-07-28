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

package com.example.android.sliceviewer.util

import android.net.Uri

/**
 * Copy a URI but remove the "slice-" prefix from its scheme.
 */
fun Uri.convertToOriginalScheme(): Uri {
    var builder = Uri.Builder()
        .authority(authority)
        .path(path)
        .encodedQuery(query)
        .fragment(fragment)
    builder = when (scheme) {
        "slice-http" -> builder.scheme("http")
        "slice-https" -> builder.scheme("https")
        "slice-content" -> builder.scheme("content")
        else -> builder
    }
    return builder.build()
}

/**
 * Copy a URI but add a "slice-" prefix to its scheme.
 */
fun Uri.convertToSliceViewerScheme(): Uri {
    var builder = Uri.Builder()
        .authority(authority)
        .path(path)
        .encodedQuery(query)
        .fragment(fragment)
    builder = when (scheme) {
        "http" -> builder.scheme("slice-http")
        "https" -> builder.scheme("slice-https")
        "content" -> builder.scheme("slice-content")
        else -> builder
    }
    return builder.build()
}

/**
 * We have to have an explicit list of schemes in our manifest that our SingleSliceViewer listens
 * to. Right now, these are "http", "https", and "content"; likely the only schemes used in the vast
 * majority of cases.
 */
fun Uri.hasSupportedSliceScheme(): Boolean {
    return scheme != null && (scheme.equals("slice-http", true)
            || scheme.equals("slice-https", true)
            || scheme.equals("slice-content", true))
}