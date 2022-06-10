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

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.android.text.databinding.EditTextDialogFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditTextDialogFragment private constructor() : DialogFragment() {

    companion object {
        const val REQUEST_EDIT_TEXT =
            "com.example.android.text.demo.linebreak.EditTextDialogFragment:edit_text"
        const val RESULT_ARG_TEXT = "text"

        private const val ARG_INITIAL_TEXT = "initial_text"

        fun newInstance(initialText: String): EditTextDialogFragment {
            return EditTextDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_TEXT, initialText)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = EditTextDialogFragmentBinding.inflate(layoutInflater)
        binding.input.setText(requireArguments().getString(ARG_INITIAL_TEXT))
        if (binding.input.requestFocus()) {
            binding.input.selectAll()
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                setFragmentResult(REQUEST_EDIT_TEXT, Bundle().apply {
                    putString(RESULT_ARG_TEXT, binding.input.text.toString())
                })
            }
            .create()
            .also { dialog ->
                // Show the software keyboard.
                dialog.window?.run {
                    clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    )
                    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }
            }
    }
}
