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
package com.android.example.text.styling.renderer

import android.graphics.Typeface
import androidx.annotation.ColorInt
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.LeadingMarginSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.text.buildSpannedString
import androidx.text.inSpans
import com.android.example.text.styling.parser.Element
import com.android.example.text.styling.parser.Parser
import com.android.example.text.styling.renderer.spans.BulletPointSpan
import com.android.example.text.styling.renderer.spans.CodeBlockSpan

/**
 * Renders the text as simple markdown, using spans.
 */
class MarkdownBuilder(
        @ColorInt private val bulletPointColor: Int,
        @ColorInt private val codeBackgroundColor: Int,
        private val codeBlockTypeface: Typeface?,
        private val parser: Parser
) {

    fun markdownToSpans(string: String): SpannedString {
        val markdown = parser.parse(string)

        return buildSpannedString {
            markdown.elements.forEach { it -> buildElement(it, this) }
        }
    }

    private fun buildElement(element: Element, builder: SpannableStringBuilder): CharSequence {
        return builder.apply {
            // apply different spans depending on the type of the element
            when (element.type) {
                Element.Type.CODE_BLOCK -> {
                    inSpans(CodeBlockSpan(codeBlockTypeface, codeBackgroundColor)) {
                        append(element.text)
                    }
                }
                Element.Type.QUOTE -> {
                    // You can set multiple spans for the same text
                    inSpans(StyleSpan(Typeface.ITALIC),
                            LeadingMarginSpan.Standard(40),
                            RelativeSizeSpan(1.1f)) {
                        append(element.text)
                    }
                }
                Element.Type.BULLET_POINT -> {
                    inSpans(BulletPointSpan(20, bulletPointColor)) {
                        for (child in element.elements) {
                            buildElement(child, builder)
                        }
                    }
                }
                Element.Type.TEXT -> append(element.text)
            }
        }
    }

}
