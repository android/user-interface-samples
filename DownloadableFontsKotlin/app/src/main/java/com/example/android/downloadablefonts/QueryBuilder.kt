/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.example.android.downloadablefonts

/**
 * Builder class for constructing a query for downloading a font.
 */
internal class QueryBuilder(val familyName: String,
                            val width: Float? = null,
                            val weight: Int? = null,
                            val italic: Float? = null,
                            val besteffort: Boolean? = null) {

    fun build(): String {
        if (weight == null && width == null && italic == null && besteffort == null) {
            return familyName
        }
        val builder = StringBuilder()
        builder.append("name=").append(familyName)
        weight?.let { builder.append("&weight=").append(weight) }
        width?.let { builder.append("&width=").append(width) }
        italic?.let { builder.append("&italic=").append(italic) }
        besteffort?.let { builder.append("&besteffort=").append(besteffort) }
        return builder.toString()
    }
}
