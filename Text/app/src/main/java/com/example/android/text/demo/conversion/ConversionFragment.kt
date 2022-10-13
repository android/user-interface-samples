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

package com.example.android.text.demo.conversion

import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.android.text.R
import com.example.android.text.databinding.ConversionFragmentBinding
import com.example.android.text.ui.viewBindings

/**
 * Search-as-you-type is a very useful feature when user wants to search for something quickly.
 * However, the feature cannot be easily implemented in non-alphabet based languages, such as
 * Japanese and Chinese.
 *
 * Android 13 introduces Conversion Suggestion API that allows apps to access pieces of text before
 * they are committed. With this API, apps can provide search-as-you-type feature in non-alphabet
 * languages.
 */
@RequiresApi(33)
class ConversionFragment : Fragment(R.layout.conversion_fragment) {

    private val binding by viewBindings(ConversionFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // The ConversionEditText can return multiple search queries as user types.
        // In this sample, we just show the search queries.
        binding.edit.doOnSearchQueries { searchQueries ->
            binding.searchQueries.text = searchQueries.joinToString("\n")
        }
    }
}
