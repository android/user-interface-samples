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
package com.example.android.clippingbasic.tests;

import com.example.android.clippingbasic.*;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;

/**
* Tests for ClippingBasic sample.
*/
public class SampleTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mTestActivity;
    private ClippingBasicFragment mTestFragment;

    public SampleTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Starts the activity under test using the default Intent with:
        // action = {@link Intent#ACTION_MAIN}
        // flags = {@link Intent#FLAG_ACTIVITY_NEW_TASK}
        // All other fields are null or empty.
        mTestActivity = getActivity();
        mTestFragment = (ClippingBasicFragment)
            mTestActivity.getSupportFragmentManager().getFragments().get(1);
    }

    /**
    * Test if the test fixture has been set up correctly.
    */
    public void testPreconditions() {
        assertNotNull("mTestActivity is null", mTestActivity);
        assertNotNull("mTestFragment is null", mTestFragment);
        assertNotNull("Clipped frame is null", mTestActivity.findViewById(R.id.frame));
        assertNotNull("Text view is null", mTestActivity.findViewById(R.id.text_view));
    }

    /**
     * Triggers a click on the button and tests if the view is clipped afterwards.
     */
    public void testClipping() {
        View clippedView = mTestActivity.findViewById(R.id.frame);

        // Initially, the view is not clipped.
        assertFalse(clippedView.getClipToOutline());

        // Trigger a click on the button to activate clipping.
        TouchUtils.clickView(this, mTestActivity.findViewById(R.id.button));

        // Check that the view has been clipped.
        assertTrue(clippedView.getClipToOutline());
    }
}