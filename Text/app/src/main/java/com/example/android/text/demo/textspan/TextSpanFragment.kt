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

package com.example.android.text.demo.textspan

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.text.backgroundColor
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.Fragment
import com.example.android.text.R
import com.example.android.text.databinding.TextSpanFragmentBinding
import com.example.android.text.ui.viewBindings

class TextSpanFragment : Fragment(R.layout.text_span_fragment) {

    private val binding by viewBindings(TextSpanFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // `buildSpannedString` in the 'core-ktx' library is useful for quickly building a rich
        // text. The
        // [androidx.core.text](https://developer.android.com/reference/kotlin/androidx/core/text/package-summary)
        // package provides several DSL methods for applying text styles.
        binding.message.text = buildSpannedString {
            color(Color.RED) {
                append("H")
            }
            append("ello, ")
            bold {
                color(Color.BLUE) {
                    append("W")
                }
                append("orld")
            }
            backgroundColor(Color.YELLOW) {
                append("!")
            }
        }
    }
}
