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
package com.android.example.text.styling.parser

import java.util.Collections.emptyList
import java.util.regex.Pattern

/**
 * The role of this parser is just to showcase ways of working with text. It should not be
 * expected to support complex markdown elements.
 *
 * Parse text and extract markdown elements:
 *
 *  * Paragraphs starting with “> ” are transformed into quotes. Quotes can't contain
 * other markdown elements.
 *  *  Text enclosed in “`” will be transformed into inline code block.
 *  * Lines starting with “+ ” or “* ” will be transformed into bullet points. Bullet
 * points can contain nested markdown elements, like code.
 *
 */
object Parser {

    /**
     * Parse a text and extract the [TextMarkdown].
     *
     * @param string string to be parsed into markdown elements
     * @return the [TextMarkdown]
     */
    fun parse(string: String): TextMarkdown {
        val parents = mutableListOf<Element>()

        val patternQuote = Pattern.compile(QUOTE_REGEX)
        val pattern = Pattern.compile(BULLET_POINT_CODE_BLOCK_REGEX)

        val matcher = patternQuote.matcher(string)
        var lastStartIndex = 0

        while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()
            // we found a quote
            if (lastStartIndex < startIndex) {
                // check what was before the quote
                val text = string.subSequence(lastStartIndex, startIndex)
                parents.addAll(findElements(text, pattern))
            }
            // a quote can only be a paragraph long, so look for end of line
            val endOfQuote = getEndOfParagraph(string, endIndex)
            lastStartIndex = endOfQuote
            val quotedText = string.subSequence(endIndex, endOfQuote)
            parents.add(Element(Element.Type.QUOTE, quotedText, emptyList<Element>()))
        }

        // check if there are any other element after the quote
        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.addAll(findElements(text, pattern))
        }

        return TextMarkdown(parents)
    }

    private fun getEndOfParagraph(string: CharSequence, endIndex: Int): Int {
        var endOfParagraph = string.indexOf(LINE_SEPARATOR, endIndex)
        if (endOfParagraph == -1) {
            // we don't have an end of line, so the quote is the last element in the text
            // so we can consider that the end of the quote is the end of the text
            endOfParagraph = string.length
        } else {
            // add the line separator as part of the element
            endOfParagraph += LINE_SEPARATOR.length
        }
        return endOfParagraph
    }

    private fun findElements(string: CharSequence, pattern: Pattern): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = pattern.matcher(string)
        var lastStartIndex = 0

        while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()
            // we found a mark
            val mark = string.subSequence(startIndex, endIndex)
            if (lastStartIndex < startIndex) {
                // check what was before the mark
                parents.addAll(findElements(string.subSequence(lastStartIndex, startIndex),
                        pattern))
            }
            val text: CharSequence
            // check what kind of mark this was
            when (mark) {
                BULLET_PLUS, BULLET_STAR -> {
                    // every bullet point is max until a new line or end of text
                    var endOfBulletPoint = getEndOfParagraph(string, endIndex)
                    text = string.subSequence(endIndex, endOfBulletPoint)
                    lastStartIndex = endOfBulletPoint
                    // also see what else we have in the text
                    val subMarks = findElements(text, pattern)
                    val bulletPoint = Element(Element.Type.BULLET_POINT, text, subMarks)
                    parents.add(bulletPoint)
                }
                CODE_BLOCK -> {
                    // a code block is set between two "`" so look for the other one
                    // if another "`" is not found, then this is not a code block
                    var markEnd = string.indexOf(CODE_BLOCK, endIndex)
                    if (markEnd == -1) {
                        // we don't have an end of code block so this is just text
                        markEnd = string.length
                        text = string.substring(startIndex, markEnd)
                        parents.add(Element(Element.Type.TEXT, text, emptyList()))
                        lastStartIndex = markEnd
                    } else {
                        // we found the end of the code block
                        text = string.substring(endIndex, markEnd)
                        parents.add(Element(Element.Type.CODE_BLOCK, text,
                                kotlin.collections.emptyList()))
                        // adding 1 so we can ignore the ending "`" for the code block
                        lastStartIndex = markEnd + 1
                    }
                }
            }
        }

        // check if there's any more text left
        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element(Element.Type.TEXT, text, emptyList<Element>()))
        }

        return parents
    }

    private const val BULLET_PLUS = "+ "
    private const val BULLET_STAR = "* "
    private const val QUOTE_REGEX = "(?m)^> "
    private const val BULLET_POINT_STAR = "(?m)^\\$BULLET_STAR"
    private const val BULLET_POINT_PLUS = "(?m)^\\$BULLET_PLUS"
    private const val BULLET_POINT_REGEX = "($BULLET_POINT_STAR|$BULLET_POINT_PLUS)"
    private const val CODE_BLOCK = "`"
    private const val BULLET_POINT_CODE_BLOCK_REGEX = "($BULLET_POINT_REGEX|$CODE_BLOCK)"

    private val LINE_SEPARATOR = System.getProperty("line.separator")
}
