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

import android.graphics.Typeface;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.LeadingMarginSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.android.example.text.styling.parser.Element;
import com.android.example.text.styling.parser.Parser;
import com.android.example.text.styling.parser.TextMarkdown;
import com.android.example.text.styling.renderer.spans.BulletPointSpan;
import com.android.example.text.styling.renderer.spans.CodeBlockSpan;

/**
 * Renders the text as simple markdown, using spans.
 */
public class MarkdownBuilder {

    @ColorInt
    private final int bulletPointColor;

    @ColorInt
    private final int codeBackgroundColor;

    @Nullable
    private final Typeface codeBlockTypeface;

    @NonNull
    private final Parser parser;

    public MarkdownBuilder(@ColorInt final int bulletPointColor,
                           @ColorInt final int codeBackgroundColor,
                           final Typeface codeBlockTypeface,
                           @NonNull final Parser parser) {
        this.bulletPointColor = bulletPointColor;
        this.codeBackgroundColor = codeBackgroundColor;
        this.codeBlockTypeface = codeBlockTypeface;
        this.parser = parser;
    }

    public SpannedString markdownToSpans(@NonNull final String string) {
        TextMarkdown markdown = parser.parse(string);

        // In the SpannableStringBuilder, the text and the markup are mutable.
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int i = 0; i < markdown.getElements().size(); i++) {
            buildSpans(markdown.getElements().get(i), builder);
        }
        return new SpannedString(builder);
    }

    /**
     * Build the spans for an element and insert them in the builder
     *
     * @param element the element for which the spans are built
     * @param builder a {@link SpannableStringBuilder} that gathers all the spans
     */
    private void buildSpans(@NonNull final Element element,
                            @NonNull final SpannableStringBuilder builder) {
        // apply different spans depending on the type of the element
        switch (element.getType()) {
            case CODE_BLOCK:
                buildCodeBlockSpan(element, builder);
                break;
            case QUOTE:
                buildQuoteSpans(element, builder);
                break;
            case BULLET_POINT:
                buildBulletPointSpans(element, builder);
                break;
            case TEXT:
                builder.append(element.getText());
                break;
        }
    }

    private void buildBulletPointSpans(@NonNull final Element element,
                                       @NonNull final SpannableStringBuilder builder) {
        int startIndex = builder.length();
        for (Element child : element.getElements()) {
            buildSpans(child, builder);
        }
        builder.setSpan(new BulletPointSpan(20, bulletPointColor), startIndex,
                builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void buildQuoteSpans(@NonNull Element element, SpannableStringBuilder builder) {
        int startIndex = builder.length();
        builder.append(element.getText());
        // You can set multiple spans for the same text
        builder.setSpan(new StyleSpan(Typeface.ITALIC), startIndex, builder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new LeadingMarginSpan.Standard(40), startIndex, builder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new RelativeSizeSpan(1.1f), startIndex, builder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void buildCodeBlockSpan(@NonNull Element element, SpannableStringBuilder builder) {
        int startIndex = builder.length();
        builder.append(element.getText());
        builder.setSpan(new CodeBlockSpan(codeBlockTypeface, codeBackgroundColor), startIndex,
                builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
