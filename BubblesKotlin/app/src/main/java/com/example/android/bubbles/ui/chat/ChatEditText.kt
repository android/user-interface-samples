/*
 * Copyright (C) 2020 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bubbles.ui.chat

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import com.example.android.bubbles.R

typealias OnImageAddedListener = (contentUri: Uri, mimeType: String, label: String) -> Unit

private val SUPPORTED_MIME_TYPES = arrayOf(
    "image/jpeg",
    "image/png",
    "image/gif"
)

/**
 * A custom EditText with the ability to handle image pasting from a software keyboard.
 */
class ChatEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var onImageAddedListener: OnImageAddedListener? = null

    override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection {
        val inputConnection = super.onCreateInputConnection(editorInfo)
        EditorInfoCompat.setContentMimeTypes(editorInfo, SUPPORTED_MIME_TYPES)
        return InputConnectionCompat.createWrapper(
            inputConnection,
            editorInfo
        ) { inputContentInfo, flags, opts ->
            // Request permission if it's missing.
            if ((flags and InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                try {
                    inputContentInfo.requestPermission()
                } catch (e: Exception) {
                    return@createWrapper false
                }
            }
            val mimeType = SUPPORTED_MIME_TYPES.find { inputContentInfo.description.hasMimeType(it) }
            if (mimeType == null) {
                false
            } else {
                onImageAddedListener?.invoke(
                    inputContentInfo.contentUri,
                    mimeType,
                    inputContentInfo.description.label.toString()
                )
                true
            }
        }
    }

    /**
     * Sets a listener to be called when a new image is selected on a software keyboard.
     */
    fun setOnImageAddedListener(listener: OnImageAddedListener?) {
        onImageAddedListener = listener
    }
}
