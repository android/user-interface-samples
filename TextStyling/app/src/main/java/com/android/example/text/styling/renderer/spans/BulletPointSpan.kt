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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Path.Direction
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import androidx.core.graphics.withTranslation

/**
 * Creating a bullet span with bigger bullets than [android.text.style.BulletSpan]
 * and with a left margin.
 */
class BulletPointSpan(
        @Px private val gapWidth: Int = DEFAULT_GAP_WIDTH,
        @ColorInt private val color: Int = Color.BLACK,
        private val useColor: Boolean = color != Color.BLACK
) : LeadingMarginSpan {

    // By default, lazy is thread safe. This is good if this property can be accessed from different
    // threads, but impacts performance otherwise. As this property is initialized in a draw method,
    // it's important to be as fast as possible.
    private val bulletPath: Path by lazy(LazyThreadSafetyMode.NONE) { Path() }

    override fun getLeadingMargin(first: Boolean): Int {
        return (2 * DEFAULT_BULLET_RADIUS + 2 * gapWidth).toInt()
    }

    /**
     * Using a similar drawing mechanism with [android.text.style.BulletSpan] but adding
     * margins before the bullet.
     */
    override fun drawLeadingMargin(
            canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
            lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence, lineStart: Int,
            lineEnd: Int, isFirstLine: Boolean, layout: Layout
    ) {
        if ((text as Spanned).getSpanStart(this) == lineStart) {
            paint.withCustomColor {
                if (canvas.isHardwareAccelerated) {
                    // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                    bulletPath.addCircle(0.0f, 0.0f, 1.2f * DEFAULT_BULLET_RADIUS, Direction.CW)

                    canvas.withTranslation(
                            getCircleXLocation(currentMarginLocation, paragraphDirection),
                            getCircleYLocation(lineTop, lineBottom)
                    ) {
                        drawPath(bulletPath, paint)
                    }
                } else {
                    canvas.drawCircle(
                            getCircleXLocation(currentMarginLocation, paragraphDirection),
                            getCircleYLocation(lineTop, lineBottom),
                            DEFAULT_BULLET_RADIUS,
                            paint
                    )
                }
            }
        }
    }

    private fun getCircleYLocation(lineTop: Int, lineBottom: Int) =
            (lineTop + lineBottom) / 2.0f

    private fun getCircleXLocation(currentMarginLocation: Int, paragraphDirection: Int) =
            gapWidth + currentMarginLocation + paragraphDirection * DEFAULT_BULLET_RADIUS

    companion object {
        private const val DEFAULT_GAP_WIDTH = 2
        @VisibleForTesting
        const val DEFAULT_BULLET_RADIUS = 15.0f
    }

    // When a custom color is used for bullets, the default style and colors need to be saved to
    // then be set again after the draw finishes. This extension hides the boilerplate.
    private inline fun Paint.withCustomColor(block: () -> Unit) {
        val oldStyle = style
        val oldColor = if (useColor) color else Color.TRANSPARENT

        if (useColor) {
            color = this@BulletPointSpan.color
        }

        style = Paint.Style.FILL

        block()

        if (useColor) {
            color = oldColor
        }

        style = oldStyle
    }
}
