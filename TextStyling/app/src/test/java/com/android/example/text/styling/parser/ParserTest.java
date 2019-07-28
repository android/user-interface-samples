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

import static com.android.example.text.styling.parser.Element.Type.BULLET_POINT;
import static com.android.example.text.styling.parser.Element.Type.CODE_BLOCK;
import static com.android.example.text.styling.parser.Element.Type.QUOTE;
import static com.android.example.text.styling.parser.Element.Type.TEXT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;

/**
 * Tests for {@link Parser}
 */
public class ParserTest {

    private final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    public void quoteBeginingOfText() {
        String withQuote = "> This is a quote." + LINE_SEPARATOR + "This is not";

        List<Element> elements = new Parser().parse(withQuote).getElements();

        assertEquals(elements.size(), 2);
        assertEquals(elements.get(0).getType(), QUOTE);
        assertEquals(elements.get(0).getText(), "This is a quote." + LINE_SEPARATOR);
        assertEquals(elements.get(1).getType(), TEXT);
        assertEquals(elements.get(1).getText(), "This is not");
    }

    @Test
    public void quoteEndOfText() {
        String withQuote = "This is not a quote." + LINE_SEPARATOR + "> This is a quote";

        List<Element> elements = new Parser().parse(withQuote).getElements();

        assertEquals(elements.size(), 2);
        assertEquals(elements.get(0).getType(), TEXT);
        assertEquals(elements.get(0).getText(), "This is not a quote." + LINE_SEPARATOR);
        assertEquals(elements.get(1).getType(), QUOTE);
        assertEquals(elements.get(1).getText(), "This is a quote");
    }

    @Test
    public void simpleBulletPoints() {
        String bulletPoints = "Bullet points:" + LINE_SEPARATOR + "* One" + LINE_SEPARATOR + "+ Two" + LINE_SEPARATOR + "* Three";

        List<Element> elements = new Parser().parse(bulletPoints).getElements();

        assertEquals(elements.size(), 4);
        assertEquals(elements.get(0).getType(), TEXT);
        assertEquals(elements.get(0).getText(), "Bullet points:" + LINE_SEPARATOR);
        assertEquals(elements.get(1).getType(), BULLET_POINT);
        assertEquals(elements.get(1).getText(), "One" + LINE_SEPARATOR);
        assertEquals(elements.get(2).getType(), BULLET_POINT);
        assertEquals(elements.get(2).getText(), "Two" + LINE_SEPARATOR);
        assertEquals(elements.get(3).getType(), BULLET_POINT);
        assertEquals(elements.get(3).getText(), "Three");
    }

    @Test
    public void simpleCode() {
        String code = "Styling `Text` in `Java`";

        List<Element> elements = new Parser().parse(code).getElements();

        assertEquals(elements.size(), 4);
        assertEquals(elements.get(0).getType(), TEXT);
        assertEquals(elements.get(0).getText(), "Styling ");
        assertEquals(elements.get(1).getType(), CODE_BLOCK);
        assertEquals(elements.get(1).getText(), "Text");
        assertEquals(elements.get(2).getType(), TEXT);
        assertEquals(elements.get(2).getText(), " in ");
        assertEquals(elements.get(3).getType(), CODE_BLOCK);
        assertEquals(elements.get(3).getText(), "Java");
    }

    @Test
    public void codeWithExtraTick() {
        String code = "Styling `Text` in `Java";

        List<Element> elements = new Parser().parse(code).getElements();

        assertEquals(elements.size(), 4);
        assertEquals(elements.get(0).getType(), TEXT);
        assertEquals(elements.get(0).getText(), "Styling ");
        assertEquals(elements.get(1).getType(), CODE_BLOCK);
        assertEquals(elements.get(1).getText(), "Text");
        assertEquals(elements.get(2).getType(), TEXT);
        assertEquals(elements.get(2).getText(), " in ");
        assertEquals(elements.get(3).getType(), TEXT);
        assertEquals(elements.get(3).getText(), "`Java");
    }

    @Test
    public void quoteBulletPointsCode() {
        String text = "Complex:" + LINE_SEPARATOR + "> Quote" + LINE_SEPARATOR + "With points:" + LINE_SEPARATOR + "+ bullet `one`" + LINE_SEPARATOR + "* bullet `two` is `long`";

        List<Element> elements = new Parser().parse(text).getElements();

        assertEquals(elements.size(), 5);
        assertEquals(elements.get(0).getType(), TEXT);
        assertEquals(elements.get(0).getText(), "Complex:" + LINE_SEPARATOR);
        assertEquals(elements.get(1).getType(), QUOTE);
        assertEquals(elements.get(1).getText(), "Quote" + LINE_SEPARATOR);
        assertEquals(elements.get(2).getType(), TEXT);
        assertEquals(elements.get(2).getText(), "With points:" + LINE_SEPARATOR);
        // first bullet point
        assertEquals(elements.get(3).getType(), BULLET_POINT);
        assertEquals(elements.get(3).getText(), "bullet `one`" + LINE_SEPARATOR);
        List<Element> subElements1 = elements.get(3).getElements();
        assertEquals(subElements1.size(), 3);
        assertEquals(subElements1.get(0).getType(), TEXT);
        assertEquals(subElements1.get(0).getText(), "bullet ");
        assertEquals(subElements1.get(1).getType(), CODE_BLOCK);
        assertEquals(subElements1.get(1).getText(), "one");
        assertEquals(subElements1.get(2).getType(), TEXT);
        assertEquals(subElements1.get(2).getText(), "" + LINE_SEPARATOR);
        // second bullet point
        assertEquals(elements.get(4).getType(), BULLET_POINT);
        assertEquals(elements.get(4).getText(), "bullet `two` is `long`");
        List<Element> subElements2 = elements.get(4).getElements();
        assertEquals(subElements2.size(), 4);
        assertEquals(subElements2.get(0).getType(), TEXT);
        assertEquals(subElements2.get(0).getText(), "bullet ");
        assertEquals(subElements2.get(1).getType(), CODE_BLOCK);
        assertEquals(subElements2.get(1).getText(), "two");
        assertEquals(subElements2.get(2).getType(), TEXT);
        assertEquals(subElements2.get(2).getText(), " is ");
        assertEquals(subElements2.get(3).getType(), CODE_BLOCK);
        assertEquals(subElements2.get(3).getText(), "long");
    }
}