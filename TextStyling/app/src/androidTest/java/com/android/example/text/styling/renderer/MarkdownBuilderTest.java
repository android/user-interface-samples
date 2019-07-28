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
package com.android.example.text.styling.renderer;

import android.content.Context;
import android.graphics.Typeface;
import androidx.test.InstrumentationRegistry;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.text.SpannedString;
import android.text.style.LeadingMarginSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.android.example.text.styling.R;
import com.android.example.text.styling.parser.Parser;
import com.android.example.text.styling.renderer.spans.BulletPointSpan;
import com.android.example.text.styling.renderer.spans.CodeBlockSpan;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MarkdownBuilderTest {

    private MarkdownBuilder builder;

    public MarkdownBuilderTest() {
        Context context = InstrumentationRegistry.getTargetContext();
        int bulletPointColor = ContextCompat.getColor(context, R.color.colorAccent);
        int codeBackgroundColor = ContextCompat.getColor(context, R.color.code_background);
        Typeface codeBlockTypeface = Typeface.DEFAULT;
        builder = new MarkdownBuilder(bulletPointColor, codeBackgroundColor, codeBlockTypeface,
                new Parser());
    }

    @Test
    public void builder() {
        SpannedString result = builder.markdownToSpans("Hello, world!");
        assertEquals("Hello, world!", result.toString());
    }

    @Test
    public void text() {
        SpannedString result = builder.markdownToSpans("Text");

        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(0, spans.length);
    }

    @Test
    public void textWithQuote() {
        SpannedString result = builder.markdownToSpans("Text\n> Quote");

        assertEquals("Text\nQuote", result.toString());
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(3, spans.length);

        StyleSpan styleSpan = (StyleSpan) spans[0];
        assertEquals(Typeface.ITALIC, styleSpan.getStyle());
        assertEquals(5, result.getSpanStart(styleSpan));
        assertEquals(10, result.getSpanEnd(styleSpan));
        LeadingMarginSpan leadingMarginSpan = (LeadingMarginSpan) spans[1];
        assertEquals(5, result.getSpanStart(leadingMarginSpan));
        assertEquals(10, result.getSpanEnd(leadingMarginSpan));
        RelativeSizeSpan relativeSizeSpan = (RelativeSizeSpan) spans[2];
        assertEquals(5, result.getSpanStart(relativeSizeSpan));
        assertEquals(10, result.getSpanEnd(relativeSizeSpan));
    }

    @Test
    public void textWithBulletPoints() {
        SpannedString result = builder.markdownToSpans("Points\n* one\n+ two");

        assertEquals("Points\none\ntwo", result.toString());
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(2, spans.length);

        BulletPointSpan bulletSpan = (BulletPointSpan) spans[0];
        assertEquals(7, result.getSpanStart(bulletSpan));
        assertEquals(11, result.getSpanEnd(bulletSpan));
        BulletPointSpan bulletSpan2 = (BulletPointSpan) spans[1];
        assertEquals(11, result.getSpanStart(bulletSpan2));
        assertEquals(14, result.getSpanEnd(bulletSpan2));
    }

    @Test
    public void textWithCode() {
        SpannedString result = builder.markdownToSpans("Text `code`");

        assertEquals("Text code", result.toString());
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);

        CodeBlockSpan codeSpan = (CodeBlockSpan) spans[0];
        assertEquals(5, result.getSpanStart(codeSpan));
        assertEquals(9, result.getSpanEnd(codeSpan));
    }
}