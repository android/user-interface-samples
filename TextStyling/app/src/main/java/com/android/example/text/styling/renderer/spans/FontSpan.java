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
package com.android.example.text.styling.renderer.spans;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Span that changes the typeface of the text used to the one provided. The style set before will
 * be kept.
 */
public class FontSpan extends MetricAffectingSpan {

    @Nullable
    private final Typeface font;

    public FontSpan(@Nullable final Typeface font) {
        this.font = font;
    }

    @Override
    public void updateMeasureState(TextPaint textPaint) {
        update(textPaint);
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        update(textPaint);
    }

    @SuppressLint("WrongConstant")
    private void update(TextPaint textPaint) {
        Typeface old = textPaint.getTypeface();
        int oldStyle = old != null ? old.getStyle() : 0;

        // Typeface is already cached at the system level
        // keep the style set before
        Typeface font = Typeface.create(this.font, oldStyle);
        textPaint.setTypeface(font);
    }
}
