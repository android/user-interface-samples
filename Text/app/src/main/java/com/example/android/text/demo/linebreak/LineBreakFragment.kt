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

package com.example.android.text.demo.linebreak

import android.graphics.text.LineBreakConfig
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.android.text.R
import com.example.android.text.databinding.LineBreakFragmentBinding
import com.example.android.text.ui.utils.doOnChange
import com.example.android.text.ui.utils.doOnItemSelected
import com.example.android.text.ui.viewBindings

@RequiresApi(33)
enum class LineBreakWordStyle(val value: Int, val label: String) {
    Phrase(LineBreakConfig.LINE_BREAK_WORD_STYLE_PHRASE, "Phrase"),
    None(LineBreakConfig.LINE_BREAK_WORD_STYLE_NONE, "None")
}

/**
 * Demonstrates the `android:lineBreakWordStyle` attribute introduced in Android 13 (API level 33).
 * Texts can now wrap by phrase (or bunsetsu) for Japanese.
 * Note that this feature works only when the system language is set to Japanese.
 */
class LineBreakFragment : Fragment(R.layout.line_break_fragment) {

    private val binding by viewBindings(LineBreakFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            binding.root.updatePadding(bottom = navigationBars.bottom)
            insets
        }
        binding.control.lineBreakWordStyle.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            android.R.id.text1,
            LineBreakWordStyle.values().map { it.label }
        )

        // Edit text.
        binding.control.edit.setOnClickListener {
            EditTextDialogFragment
                .newInstance(binding.message.text.toString())
                .show(childFragmentManager, null)
        }
        childFragmentManager.setFragmentResultListener(
            EditTextDialogFragment.REQUEST_EDIT_TEXT,
            viewLifecycleOwner
        ) { requestKey, result ->
            if (requestKey == EditTextDialogFragment.REQUEST_EDIT_TEXT) {
                binding.message.text = result.getString(EditTextDialogFragment.RESULT_ARG_TEXT)
            }
        }

        // Text size.
        binding.control.textSize.doOnChange { progress, fromUser ->
            if (fromUser) {
                binding.message.textSize = progressToTextSize(progress)
            }
        }

        // Change lineBreakWordStyle.
        binding.control.lineBreakWordStyle.doOnItemSelected { _, position, _ ->
            if (Build.VERSION.SDK_INT >= 33) {
                binding.message.lineBreakWordStyle = LineBreakWordStyle.values()[position].value
            }
        }
    }

    // SeekBar's progress is from 0 to 100. Convert it to 10f to 80f. The text size is in sp.
    private fun progressToTextSize(progress: Int) = progress.toFloat() / 100f * 70f + 10f
}

