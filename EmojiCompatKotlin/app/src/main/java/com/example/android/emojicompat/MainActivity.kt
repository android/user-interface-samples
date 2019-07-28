/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.example.android.emojicompat

import android.os.Bundle
import androidx.emoji.text.EmojiCompat
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    companion object {
        // [U+1F469] (WOMAN) + [U+200D] (ZERO WIDTH JOINER) + [U+1F4BB] (PERSONAL COMPUTER)
        private val WOMAN_TECHNOLOGIST = "\uD83D\uDC69\u200D\uD83D\uDCBB"

        // [U+1F469] (WOMAN) + [U+200D] (ZERO WIDTH JOINER) + [U+1F3A4] (MICROPHONE)
        private val WOMAN_SINGER = "\uD83D\uDC69\u200D\uD83C\uDFA4"

        @JvmField
        val EMOJI = WOMAN_TECHNOLOGIST + " " + WOMAN_SINGER
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TextView variant provided by EmojiCompat library
        val emojiTextView: TextView = findViewById(R.id.emoji_text_view)
        emojiTextView.text = getString(R.string.emoji_text_view, EMOJI)

        // EditText variant provided by EmojiCompat library
        val emojiEditText: TextView = findViewById(R.id.emoji_edit_text)
        emojiEditText.text = getString(R.string.emoji_edit_text, EMOJI)

        // Button variant provided by EmojiCompat library
        val emojiButton: TextView = findViewById(R.id.emoji_button)
        emojiButton.text = getString(R.string.emoji_button, EMOJI)

        // Regular TextView without EmojiCompat support; you have to manually process the text
        val regularTextView: TextView = findViewById(R.id.regular_text_view)
        EmojiCompat.get().registerInitCallback(InitCallback(regularTextView))

        // Custom TextView
        val customTextView: TextView = findViewById(R.id.emoji_custom_text_view)
        customTextView.text = getString(R.string.custom_text_view, EMOJI)
    }

    private class InitCallback(regularTextView: TextView) : EmojiCompat.InitCallback() {

        val regularTextViewRef = WeakReference(regularTextView)

        override fun onInitialized() {
            val regularTextView = regularTextViewRef.get()
            if (regularTextView != null) {
                val compat = EmojiCompat.get()
                val context = regularTextView.context
                regularTextView.text = compat.process(
                        context.getString(R.string.regular_text_view, EMOJI))
            }
        }

    }

}
