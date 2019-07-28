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
package com.example.android.elevationbasic.tests;

import com.example.android.elevationbasic.ElevationBasicFragment;
import com.example.android.elevationbasic.MainActivity;
import com.example.android.elevationbasic.R;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

/**
* Tests for ElevationBasic sample.
*/
public class SampleTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mTestActivity;
    private ElevationBasicFragment mTestFragment;

    private View mShape1;
    private View mShape2;

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
        mTestFragment = (ElevationBasicFragment)
            mTestActivity.getSupportFragmentManager().getFragments().get(1);
        mShape1 = mTestActivity.findViewById(R.id.floating_shape);
        mShape2 = mTestActivity.findViewById(R.id.floating_shape_2);
    }

    /**
    * Test if the test fixture has been set up correctly.
    */
    public void testPreconditions() {
        assertNotNull("mTestActivity is null", mTestActivity);
        assertNotNull("mTestFragment is null", mTestFragment);
    }

    /**
     * Test if the initial Z values of the shapes are correct.
     */
    public void testInitialShapeZ() {
        assertTrue(mShape1.getZ() > 0f);
        assertEquals(mShape2.getZ(), 0f);
    }
}