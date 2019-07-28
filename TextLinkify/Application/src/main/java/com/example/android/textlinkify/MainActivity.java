/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.textlinkify;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.widget.TextView;

/**
 * This sample demonstrates how clickable links can be added to a
 * {@link android.widget.TextView}.
 *
 * <p>This can be done in three ways:
 * <ul>
 * <li><b>Automatically:</b> Text added to a TextView can automatically be linkified by enabling
 * autoLinking. In XML, use the android:autoLink property, programatically call
 * {@link android.widget.TextView#setAutoLinkMask(int)} using an option from
 * {@link android.text.util.Linkify}</li>
 *
 * <li><b>Parsing a String as HTML:</b> See {@link android.text.Html#fromHtml(String)})</li>
 *
 * <li><b>Manually by constructing a {@link android.text.SpannableString}:</b> Consisting of
 * {@link android.text.style.StyleSpan} and {@link android.text.style.URLSpan} objects that
 * are contained within a {@link android.text.SpannableString}</li>
 * </ul></p>
 *
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sample_main);

        // BEGIN_INCLUDE(text_auto_linkify)
        /*
         *  text_auto_linkify shows the android:autoLink property, which
         *  automatically linkifies things like URLs and phone numbers
         *  found in the text. No java code is needed to make this
         *  work.
         *  This can also be enabled programmatically by calling
         *  .setAutoLinkMask(Linkify.ALL) before the text is set on the TextView.
         *
         *  See android.text.util.Linkify for other options, for example only
         *  auto-linking email addresses or phone numbers
         */
        // END_INCLUDE(text_auto_linkify)

        // BEGIN_INCLUDE(text_html_resource)
        /*
         * text_html_resource has links specified by putting anchor tags (<a>) in the string
         * resource. By default these links will appear but not
         * respond to user input. To make them active, you need to
         * call setMovementMethod() on the TextView object.
         */
        TextView textViewResource = (TextView) findViewById(R.id.text_html_resource);
        textViewResource.setText(
                Html.fromHtml(getResources().getString(R.string.link_text_manual)));
        textViewResource.setMovementMethod(LinkMovementMethod.getInstance());
        // END_INCLUDE(text_html_resource)

        // BEGIN_INCLUDE(text_html_program)
        /*
         * text_html_program shows creating text with links from HTML in the Java
         * code, rather than from a string resource. Note that for a
         * fixed string, using a (localizable) resource as shown above
         * is usually a better way to go; this example is intended to
         * illustrate how you might display text that came from a
         * dynamic source (eg, the network).
         */
        TextView textViewHtml = (TextView) findViewById(R.id.text_html_program);
        textViewHtml.setText(
                Html.fromHtml(
                        "<b>text_html_program: Constructed from HTML programmatically.</b>"
                                + "  Text with a <a href=\"http://www.google.com\">link</a> "
                                + "created in the Java source code using HTML."));
        textViewHtml.setMovementMethod(LinkMovementMethod.getInstance());
        // END_INCLUDE(text_html_program)

        // BEGIN_INCLUDE(text_spannable)
        /*
         * text_spannable illustrates constructing a styled string containing a
         * link without using HTML at all. Again, for a fixed string
         * you should probably be using a string resource, not a
         * hardcoded value.
         */
        SpannableString ss = new SpannableString(
                "text_spannable: Manually created spans. Click here to dial the phone.");

        /*
         * Make the first 38 characters bold by applying a StyleSpan with bold typeface.
         *
         * Characters 45 to 49 (the word "here") is made clickable by applying a URLSpan
         * pointing to a telephone number. Clicking it opens the "tel:" URL that starts the dialer.
         *
         * The SPAN_EXCLUSIVE_EXCLUSIVE flag defines this span as exclusive, which means
         * that it will not expand to include text inserted on either side of this span.
         */
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, 39,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new URLSpan("tel:4155551212"), 40 + 6, 40 + 10,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textViewSpan = (TextView) findViewById(R.id.text_spannable);
        textViewSpan.setText(ss);

        /*
         * Set the movement method to move between links in this TextView.
         * This means that the user traverses through links in this TextView, automatically
         * handling appropriate scrolling and key commands.
         */
        textViewSpan.setMovementMethod(LinkMovementMethod.getInstance());
        // END_INCLUDE(text_spannable)
    }

}
