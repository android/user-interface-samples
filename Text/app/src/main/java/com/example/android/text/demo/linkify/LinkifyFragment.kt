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

package com.example.android.text.demo.linkify

import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.core.text.util.LinkifyCompat
import androidx.fragment.app.Fragment
import com.example.android.text.R
import com.example.android.text.databinding.LinkifyFragmentBinding
import com.example.android.text.ui.viewBindings
import java.util.regex.Matcher
import java.util.regex.Pattern

class LinkifyFragment : Fragment(R.layout.linkify_fragment) {

    private val binding by viewBindings(LinkifyFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Linkify supports EMAIL_ADDRESSES, PHONE_NUMBERS, and WEB_URLS.
        LinkifyCompat.addLinks(binding.webUrls, Linkify.WEB_URLS)

        // Linkify a custom pattern using regex. This pattern represents a Twitter account.
        val pattern = Pattern.compile("@[a-zA-Z0-9_]{1,15}")
        LinkifyCompat.addLinks(
            binding.custom,
            pattern,
            "https://twitter.com/",
            null
        ) { _, url ->
            // Remove "@" at the beginning of the match.
            url.toString().substring(startIndex = 1)
        }
    }
}
