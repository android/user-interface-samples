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
import androidx.test.InstrumentationRegistry
import androidx.core.content.ContextCompat
import android.text.style.LeadingMarginSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.android.example.text.styling.R
import com.android.example.text.styling.getColorCompat
import com.android.example.text.styling.parser.Parser
import com.android.example.text.styling.renderer.spans.BulletPointSpan
import com.android.example.text.styling.renderer.spans.CodeBlockSpan
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for [MarkdownBuilder] class
 */
class MarkdownBuilderTest {

    private val context = InstrumentationRegistry.getTargetContext()
    val bulletPointColor = context.getColorCompat(R.color.colorAccent)
    val codeBackgroundColor = context.getColorCompat(R.color.code_background)
    val codeBlockTypeface = Typeface.DEFAULT
    private val builder = MarkdownBuilder(bulletPointColor, codeBackgroundColor, codeBlockTypeface,
            Parser)

    @Test fun builder() {
        val result = builder.markdownToSpans("Hello, world!")
        assertEquals("Hello, world!", result.toString())
    }

    @Test fun text() {
        val result = builder.markdownToSpans("Text")

        val spans = result.getSpans<Any>(0, result.length, Any::class.java)
        assertEquals(0, spans.size.toLong())
    }

    @Test fun textWithQuote() {
        val result = builder.markdownToSpans("Text\n> Quote")

        assertEquals("Text\nQuote", result.toString())
        val spans = result.getSpans<Any>(0, result.length, Any::class.java)
        assertEquals(3, spans.size.toLong())

        val styleSpan = spans[0] as StyleSpan
        assertEquals(Typeface.ITALIC.toLong(), styleSpan.style.toLong())
        assertEquals(5, result.getSpanStart(styleSpan).toLong())
        assertEquals(10, result.getSpanEnd(styleSpan).toLong())
        val leadingMarginSpan = spans[1] as LeadingMarginSpan
        assertEquals(5, result.getSpanStart(leadingMarginSpan).toLong())
        assertEquals(10, result.getSpanEnd(leadingMarginSpan).toLong())
        val relativeSizeSpan = spans[2] as RelativeSizeSpan
        assertEquals(5, result.getSpanStart(relativeSizeSpan).toLong())
        assertEquals(10, result.getSpanEnd(relativeSizeSpan).toLong())
    }

    @Test fun textWithBulletPoints() {
        val result = builder.markdownToSpans("Points\n* one\n+ two")

        assertEquals("Points\none\ntwo", result.toString())
        val spans = result.getSpans<Any>(0, result.length, Any::class.java)
        assertEquals(2, spans.size.toLong())

        val bulletSpan = spans[0] as BulletPointSpan
        assertEquals(7, result.getSpanStart(bulletSpan).toLong())
        assertEquals(11, result.getSpanEnd(bulletSpan).toLong())
        val bulletSpan2 = spans[1] as BulletPointSpan
        assertEquals(11, result.getSpanStart(bulletSpan2).toLong())
        assertEquals(14, result.getSpanEnd(bulletSpan2).toLong())
    }

    @Test fun textWithCode() {
        val result = builder.markdownToSpans("Text `code`")

        assertEquals("Text code", result.toString())
        val spans = result.getSpans<Any>(0, result.length, Any::class.java)
        assertEquals(1, spans.size.toLong())

        val codeSpan = spans[0] as CodeBlockSpan
        assertEquals(5, result.getSpanStart(codeSpan).toLong())
        assertEquals(9, result.getSpanEnd(codeSpan).toLong())
    }
}