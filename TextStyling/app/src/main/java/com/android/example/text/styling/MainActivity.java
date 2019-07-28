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
package com.android.example.text.styling;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.android.example.text.styling.parser.Parser;
import com.android.example.text.styling.renderer.MarkdownBuilder;

/**
 * This sample demonstrates techniques for stying text; it is not intended to be a full markdown
 * parser.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.styled_text);

        // This is a simple markdown parser, where:
        // Paragraphs starting with “> ” are transformed into quotes. Quotes can't contain
        // other markdown elements
        // Text enclosed in “`” will be transformed into inline code block
        // Lines starting with “+ ” or “* ” will be transformed into bullet points. Bullet
        // points can contain nested markdown elements, like code.
        int bulletPointColor = ContextCompat.getColor(this, R.color.colorAccent);
        int codeBackgroundColor = ContextCompat.getColor(this, R.color.code_background);
        Typeface codeBlockTypeface = ResourcesCompat.getFont(this, R.font.inconsolata);

        CharSequence text = new MarkdownBuilder(bulletPointColor, codeBackgroundColor,
                codeBlockTypeface, new Parser())
                .markdownToSpans(getString(R.string.display_text));
        textView.setText(text);
    }
}
