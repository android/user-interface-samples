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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.ColorInt;
import androidx.annotation.Px;
import androidx.annotation.VisibleForTesting;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;

/**
 * Creating a bullet span with bigger bullets than {@link BulletSpan} and with a left margin
 */
public class BulletPointSpan implements LeadingMarginSpan {

    @VisibleForTesting
    static final float BULLET_RADIUS = 15.0f;

    private final @Px int gapWidth;
    private final @ColorInt int color;

    private static Path bulletPath;

    public BulletPointSpan(@Px int gapWidth, @ColorInt int color) {
        this.gapWidth = gapWidth;
        this.color = color;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return (int) (2 * BULLET_RADIUS + 2 * gapWidth);
    }

    @Override
    public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir,
            int top, int baseline, int bottom,
            CharSequence text, int start, int end,
            boolean first, Layout l) {
        if (((Spanned) text).getSpanStart(this) == start) {
            Paint.Style style = paint.getStyle();
            int oldcolor = paint.getColor();
            paint.setColor(color);

            paint.setStyle(Paint.Style.FILL);

            final float y = (top + bottom) / 2f;

            if (canvas.isHardwareAccelerated()) {
                if (bulletPath == null) {
                    bulletPath = new Path();
                    bulletPath.addCircle(0.0f, 0.0f, BULLET_RADIUS, Path.Direction.CW);
                }

                canvas.save();
                canvas.translate(gapWidth + x + dir * BULLET_RADIUS, y);
                canvas.drawPath(bulletPath, paint);
                canvas.restore();
            } else {
                canvas.drawCircle(gapWidth + x + dir * BULLET_RADIUS, y, BULLET_RADIUS, paint);
            }

            // restore
            paint.setColor(oldcolor);
            paint.setStyle(style);
        }
    }
}
