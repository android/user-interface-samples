/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.example.android.text.demo

import android.os.Build
import androidx.fragment.app.Fragment
import com.example.android.text.demo.conversion.ConversionFragment
import com.example.android.text.demo.hyphenation.HyphenationFragment
import com.example.android.text.demo.linebreak.LineBreakFragment
import com.example.android.text.demo.linkify.LinkifyFragment
import com.example.android.text.demo.textspan.TextSpanFragment

class Demo(
    val title: String,
    val fragment: () -> Fragment
)

val Demos = buildList {
    add(
        Demo(
            title = "Text span",
            fragment = { TextSpanFragment() }
        )
    )
    add(
        Demo(
            title = "Linkify",
            fragment = { LinkifyFragment() }
        )
    )
    if (Build.VERSION.SDK_INT >= 23) {
        add(
            Demo(
                title = "Hyphenation",
                fragment = { HyphenationFragment() }
            )
        )
    }
    add(
        Demo(
            title = "Line break",
            fragment = { LineBreakFragment() }
        )
    )
    if (Build.VERSION.SDK_INT >= 33) {
        add(
            Demo(
                title = "Conversion suggestions",
                fragment = { ConversionFragment() }
            )
        )
    }
}
