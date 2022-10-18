/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.listdetail.slidingpane

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.listdetail.slidingpane.databinding.FragmentDetailBinding

class DetailFragment : Fragment(R.layout.fragment_detail){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val binding = FragmentDetailBinding.bind(view)
        binding.label.text = args?.getCharSequence(LABEL)
        binding.definition.text = args?.getCharSequence(DEFINITION)
    }

    companion object {
        const val LABEL = "Fruit"
        const val DEFINITION = "Definition"
    }
}