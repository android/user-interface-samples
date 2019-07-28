/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.clippingbasic;

import com.example.android.common.logger.Log;

import android.graphics.Outline;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.TextView;

/**
 * This sample shows how to clip a {@link View} using an {@link Outline}.
 */
public class ClippingBasicFragment extends Fragment {

    private final static String TAG = "ClippingBasicFragment";

    /* Store the click count so that we can show a different text on every click. */
    private int mClickCount = 0;

    /* The {@Link Outline} used to clip the image with. */
    private ViewOutlineProvider mOutlineProvider;

    /* An array of texts. */
    private String[] mSampleTexts;

    /* A reference to a {@Link TextView} that shows different text strings when clicked. */
    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mOutlineProvider = new ClipOutlineProvider();
        mSampleTexts = getResources().getStringArray(R.array.sample_texts);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.clipping_basic_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* Set the initial text for the TextView. */
        mTextView = (TextView) view.findViewById(R.id.text_view);
        changeText();


        final View clippedView = view.findViewById(R.id.frame);

        /* Sets the OutlineProvider for the View. */
        clippedView.setOutlineProvider(mOutlineProvider);

        /* When the button is clicked, the text is clipped or un-clipped. */
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View bt) {
                // Toggle whether the View is clipped to the outline
                if (clippedView.getClipToOutline()) {
                    /* The Outline is set for the View, but disable clipping. */
                    clippedView.setClipToOutline(false);

                    Log.d(TAG, String.format("Clipping to outline is disabled"));
                    ((Button) bt).setText(R.string.clip_button);
                } else {
                    /* Enables clipping on the View. */
                    clippedView.setClipToOutline(true);

                    Log.d(TAG, String.format("Clipping to outline is enabled"));
                    ((Button) bt).setText(R.string.unclip_button);
                }
            }
        });

        /* When the text is clicked, a new string is shown. */
        view.findViewById(R.id.text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickCount++;

                // Update the text in the TextView
                changeText();

                // Invalidate the outline just in case the TextView changed size
                clippedView.invalidateOutline();
            }
        });
    }

    private void changeText() {
        // Compute the position of the string in the array using the number of strings
        //  and the number of clicks.
        String newText = mSampleTexts[mClickCount % mSampleTexts.length];

        /* Once the text is selected, change the TextView */
        mTextView.setText(newText);
        Log.d(TAG, String.format("Text was changed."));


    }

    /**
     * A {@link ViewOutlineProvider} which clips the view with a rounded rectangle which is inset
     * by 10%
     */
    private class ClipOutlineProvider extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            final int margin = Math.min(view.getWidth(), view.getHeight()) / 10;
            outline.setRoundRect(margin, margin, view.getWidth() - margin,
                    view.getHeight() - margin, margin / 2);
        }

    }
}