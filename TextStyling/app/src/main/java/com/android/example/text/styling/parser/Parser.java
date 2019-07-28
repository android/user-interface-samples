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
package com.android.example.text.styling.parser;

import androidx.annotation.NonNull;

import com.android.example.text.styling.parser.Element.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The role of this parser is just to showcase ways of working with text. It should not be
 * expected to support complex markdown elements.
 *
 * Parse a text and extract markdown elements:
 * <ul>
 * <li>Paragraphs starting with “> ” are transformed into quotes. Quotes can't contain
 * other markdown elements</li>
 * <li> Text enclosed in “```” will be transformed into inline code block</li>
 * <li>Lines starting with “+ ” or “* ” will be transformed into bullet points. Bullet
 * points can contain nested markdown elements, like code.</li>
 * </ul>
 */
public class Parser {
    private static final String BULLET_PLUS = "+ ";
    private static final String BULLET_STAR = "* ";
    private static final String QUOTE_REGEX = "(?m)^> ";
    private static final String BULLET_POINT_STAR = "(?m)^\\* ";
    private static final String BULLET_POINT_PLUS = "(?m)^\\+ ";
    private static final String BULLET_POINT_REGEX = "(" + BULLET_POINT_STAR + "|" +
            BULLET_POINT_PLUS + ")";
    private static final String CODE_BLOCK = "`";
    private static final String BULLET_POINT_CODE_BLOCK_REGEX = "(" + BULLET_POINT_REGEX + "|" +
            CODE_BLOCK + ")";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Parse a text and extract the {@link TextMarkdown}.
     *
     * @param string string to be parsed into markdown elements
     * @return the {@link TextMarkdown}
     */
    @NonNull
    public TextMarkdown parse(@NonNull final String string) {
        List<Element> parents = new ArrayList<>();

        Pattern quotePattern = Pattern.compile(QUOTE_REGEX);
        Pattern pattern = Pattern.compile(BULLET_POINT_CODE_BLOCK_REGEX);

        Matcher matcher = quotePattern.matcher(string);
        int lastStartIndex = 0;

        while (matcher.find(lastStartIndex)) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();
            // we found a quote
            if (lastStartIndex < startIndex) {
                // check what was before the quote
                String text = string.substring(lastStartIndex, startIndex);
                parents.addAll(findElements(text, pattern));
            }
            // a quote can only be a paragraph long, so look for end of line
            int endOfQuote = getEndOfParagraph(string, endIndex);
            lastStartIndex = endOfQuote;
            String quotedText = string.substring(endIndex, endOfQuote);
            parents.add(new Element(Type.QUOTE, quotedText, Collections.<Element>emptyList()));
        }
        // check if there are any other element after the quote
        if (lastStartIndex < string.length()) {
            String text = string.substring(lastStartIndex, string.length());
            parents.addAll(findElements(text, pattern));
        }

        return new TextMarkdown(parents);
    }

    private static int getEndOfParagraph(@NonNull String string, int endIndex) {
        int endOfParagraph = string.indexOf(LINE_SEPARATOR, endIndex);
        if (endOfParagraph == -1) {
            // we don't have an end of line, so the quote is the last element in the text
            // so we can consider that the end of the quote is the end of the text
            endOfParagraph = string.length();
        } else {
            // add the new line as part of the element
            endOfParagraph += LINE_SEPARATOR.length();
        }
        return endOfParagraph;
    }

    @NonNull
    private static List<Element> findElements(@NonNull final String string,
            @NonNull final Pattern pattern) {
        List<Element> parents = new ArrayList<>();
        Matcher matcher = pattern.matcher(string);
        int lastStartIndex = 0;

        while (matcher.find(lastStartIndex)) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();
            // we found a mark
            String mark = string.substring(startIndex, endIndex);
            if (lastStartIndex < startIndex) {
                // check what was before the mark
                parents.addAll(findElements(string.substring(lastStartIndex, startIndex), pattern));
            }
            String text;
            // check what kind of mark this was
            switch (mark) {
                case BULLET_PLUS:
                case BULLET_STAR:
                    // every bullet point is max until a new line or end of text
                    int endOfBulletPoint = getEndOfParagraph(string, endIndex);
                    text = string.substring(endIndex, endOfBulletPoint);
                    lastStartIndex = endOfBulletPoint;
                    // also see what else we have in the text
                    List<Element> subMarks = findElements(text, pattern);
                    Element bulletPoint = new Element(Type.BULLET_POINT, text, subMarks);
                    parents.add(bulletPoint);
                    break;
                case CODE_BLOCK:
                    // a code block is set between two "`" so look for the other one
                    // if another "`" is not found, then this is not a code block
                    int markEnd = string.indexOf(CODE_BLOCK, endIndex);
                    if (markEnd == -1) {
                        // we don't have an end of code block so this is just text
                        markEnd = string.length();
                        text = string.substring(startIndex, markEnd);
                        parents.add(new Element(Type.TEXT, text, Collections.<Element>emptyList()));
                        lastStartIndex = markEnd;
                    } else {
                        // we found the end of the code block
                        text = string.substring(endIndex, markEnd);
                        parents.add(new Element(Type.CODE_BLOCK, text,
                                Collections.<Element>emptyList()));
                        // adding 1 so we can ignore the ending "`" for the code block
                        lastStartIndex = markEnd + 1;
                    }
                    break;
            }
        }
        // check if there's any more text left
        if (lastStartIndex < string.length()) {
            String text = string.substring(lastStartIndex, string.length());
            parents.add(new Element(Type.TEXT, text, Collections.<Element>emptyList()));
        }
        return parents;
    }
}
