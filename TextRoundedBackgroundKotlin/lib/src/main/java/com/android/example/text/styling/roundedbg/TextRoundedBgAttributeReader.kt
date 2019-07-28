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
package com.android.example.text.styling.roundedbg

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.res.getDrawableOrThrow

/**
 * Reads default attributes that [TextRoundedBgHelper] needs from resources. The attributes read
 * are:
 *
 * - chHorizontalPadding: the padding to be applied to left & right of the background
 * - chVerticalPadding: the padding to be applied to top & bottom of the background
 * - chDrawable: the drawable used to draw the background
 * - chDrawableLeft: the drawable used to draw left edge of the background
 * - chDrawableMid: the drawable used to draw for whole line
 * - chDrawableRight: the drawable used to draw right edge of the background
 */
class TextRoundedBgAttributeReader(context: Context, attrs: AttributeSet?) {

    val horizontalPadding: Int
    val verticalPadding: Int
    val drawable: Drawable
    val drawableLeft: Drawable
    val drawableMid: Drawable
    val drawableRight: Drawable

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.TextRoundedBgHelper,
            0,
            R.style.RoundedBgTextView
        )
        horizontalPadding = typedArray.getDimensionPixelSize(
            R.styleable.TextRoundedBgHelper_roundedTextHorizontalPadding,
            0
        )
        verticalPadding = typedArray.getDimensionPixelSize(
            R.styleable.TextRoundedBgHelper_roundedTextVerticalPadding,
            0
        )
        drawable = typedArray.getDrawableOrThrow(
            R.styleable.TextRoundedBgHelper_roundedTextDrawable
        )
        drawableLeft = typedArray.getDrawableOrThrow(
            R.styleable.TextRoundedBgHelper_roundedTextDrawableLeft
        )
        drawableMid = typedArray.getDrawableOrThrow(
            R.styleable.TextRoundedBgHelper_roundedTextDrawableMid
        )
        drawableRight = typedArray.getDrawableOrThrow(
            R.styleable.TextRoundedBgHelper_roundedTextDrawableRight
        )
        typedArray.recycle()
    }
}
