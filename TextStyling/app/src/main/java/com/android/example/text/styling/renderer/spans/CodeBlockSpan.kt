/*
 * Copyright (C) 2018 The Android Open Source Project
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
package com.android.example.text.styling.renderer.spans

import android.graphics.Color
import android.graphics.Typeface
import androidx.annotation.ColorInt
import android.text.TextPaint
import android.text.style.BackgroundColorSpan

/**
 * To draw a code block, we set a font for the text and a background color.
 *
 * The same effect can be achieved if on a text block, we set two spans: [FontSpan] and
 * [BackgroundColorSpan]
 */
class CodeBlockSpan(
        font: Typeface?,
        @param:ColorInt private val backgroundColor: Int = Color.LTGRAY
) : FontSpan(font) {

    // Since we're only changing the background color, it will not affect the measure state, so
    // just override the update draw state.
    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
        textPaint.bgColor = backgroundColor
    }
}
