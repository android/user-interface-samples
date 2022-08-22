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

import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.view.inputmethod.TextAttribute
import androidx.annotation.RequiresApi

/**
 * @param inputConnection The parent input connection.
 * @param onSuggestions Called when a new set of conversion suggestions is available.
 */
@RequiresApi(33)
internal class ConversionInputConnection(
    inputConnection: InputConnection?,
    private val onSuggestions: (text: CharSequence, suggestions: List<String>) -> Unit
) : InputConnectionWrapper(inputConnection, false) {

    override fun setComposingText(
        text: CharSequence,
        newCursorPosition: Int,
        textAttribute: TextAttribute?
    ): Boolean {
        if (textAttribute == null || text.isEmpty()) {
            // No conversion suggestion is available. Just return the text.
            onSuggestions.invoke(text, emptyList())
        } else {
            // Received conversion suggestions from the IME. Pass them over to the callback.
            onSuggestions.invoke(text, textAttribute.textConversionSuggestions)
        }
        return super.setComposingText(text, newCursorPosition, textAttribute)
    }

    override fun commitText(
        text: CharSequence,
        newCursorPosition: Int,
        textAttribute: TextAttribute?
    ): Boolean {
        if (text.isNotEmpty()) {
            onSuggestions(text, emptyList())
        }
        return super.commitText(text, newCursorPosition, textAttribute)
    }

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        if (!text.isNullOrEmpty()) {
            onSuggestions(text, emptyList())
        }
        return super.commitText(text, newCursorPosition)
    }
}
