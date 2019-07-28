/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.example.android.elevationbasic;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.common.logger.Log;

public class ElevationBasicFragment extends Fragment {

    private final static String TAG = "ElevationBasicFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflates an XML containing two shapes: the first has a fixed elevation
         * and the second ones raises when tapped.
         */
        View rootView = inflater.inflate(R.layout.elevation_basic, container, false);

        View shape2 = rootView.findViewById(R.id.floating_shape_2);

        /**
         * Sets a {@Link View.OnTouchListener} that responds to a touch event on shape2.
         */
        shape2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getActionMasked();
                /* Raise view on ACTION_DOWN and lower it on ACTION_UP. */
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "ACTION_DOWN on view.");
                        view.setTranslationZ(120);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "ACTION_UP on view.");
                        view.setTranslationZ(0);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        return rootView;
    }
}