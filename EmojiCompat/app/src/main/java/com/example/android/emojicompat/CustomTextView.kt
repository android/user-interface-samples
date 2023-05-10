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

import android.content.Context
import androidx.emoji.widget.EmojiTextViewHelper
import androidx.appcompat.widget.AppCompatTextView
import android.text.InputFilter
import android.util.AttributeSet


/**
 * A sample implementation of custom TextView.

 *
 * You can use [EmojiTextViewHelper] to make your custom TextView compatible with
 * EmojiCompat.
 */
class CustomTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    : AppCompatTextView(context, attrs, defStyleAttr) {

    private var mEmojiTextViewHelper: EmojiTextViewHelper? = null

    /**
     * Returns the [EmojiTextViewHelper] for this TextView.
     *
     * This property can be called from super constructors through [#setFilters] or [#setAllCaps].
     */
    private val emojiTextViewHelper: EmojiTextViewHelper
        get() {
            if (mEmojiTextViewHelper == null) {
                mEmojiTextViewHelper = EmojiTextViewHelper(this)
            }
            return mEmojiTextViewHelper as EmojiTextViewHelper
        }

    init {
        emojiTextViewHelper.updateTransformationMethod()
    }

    override fun setFilters(filters: Array<InputFilter>) {
        super.setFilters(emojiTextViewHelper.getFilters(filters))
    }

    override fun setAllCaps(allCaps: Boolean) {
        super.setAllCaps(allCaps)
        emojiTextViewHelper.setAllCaps(allCaps)
    }

}
