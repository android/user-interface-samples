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

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText

/**
 * This EditText uses the Conversion Suggestion API introduced in Android 13 and returns possible
 * search queries as user types.
 */
@RequiresApi(33)
class ConversionEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var inputConnection: ConversionInputConnection? = null
    private var textWatcher: TextWatcher? = null

    private var onSearchQueries: ((List<String>) -> Unit)? = null

    /**
     * Text currently shown on the EditText. This includes both the committed text and
     * pre-committed text.
     */
    private var currentText: String? = null

    /** Text in an ongoing conversion session. */
    private var currentComposingText: String? = null

    /** Suggestions for an ongoing conversion session. */
    private var currentSuggestions: List<String> = emptyList()

    /**
     * Passes the search queries.
     *
     * @param action A callback to receive the search queries. The caller should execute search by
     * combining these search queries by OR condition.
     */
    fun doOnSearchQueries(action: (searchQueries: List<String>) -> Unit) {
        onSearchQueries = action
    }

    /**
     * Builds search queries based on the current state of this EditText and passes them to the
     * callback.
     */
    private fun postSearchQueries() {
        val text = currentText ?: return
        val suggestions = currentSuggestions
        if (suggestions.isEmpty()) {
            // No conversion suggestions has been available. Simply returns the text as is.
            onSearchQueries?.invoke(listOf(text))
        } else {
            val first = suggestions.first()
            var index = text.lastIndexOf(first)
            if (index < 0) {
                // The first suggestion is not included in the text on the screen.
                // Try to find it in the ongoing conversion.
                currentComposingText?.let { composingText ->
                    index = text.lastIndexOf(composingText)
                }
            }
            if (index < 0) { // Not found
                // Neither the current text nor the ongoing conversion contain the suggestion.
                // Simply pass the suggestions from the IME as they are.
                onSearchQueries?.invoke(suggestions)
            } else {
                // The current text contains the first suggestion.
                // Obtain the committed text.
                val head = text.substring(0, index)
                // Prepend the committed text to the conversion suggestions.
                onSearchQueries?.invoke(suggestions.map { head + it })
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Use TextWatcher to monitor the text shown on this EditText.
        textWatcher = doOnTextChanged { text, _, _, _ ->
            currentText = text?.toString()
            postSearchQueries()
        }
    }

    override fun onDetachedFromWindow() {
        textWatcher?.let { removeTextChangedListener(it) }
        super.onDetachedFromWindow()
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        // Use our custom InputConnection to access conversion suggestions before they are
        // committed.
        return ConversionInputConnection(
            super.onCreateInputConnection(outAttrs),
            onSuggestions = { text, suggestions ->
                currentComposingText = text.toString()
                currentSuggestions = suggestions
                postSearchQueries()
            }
        ).also {
            inputConnection = it
        }
    }
}
