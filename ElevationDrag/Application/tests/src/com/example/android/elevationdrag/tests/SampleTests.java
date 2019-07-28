/*
* Copyright (C) 2013 The Android Open Source Project
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
package com.example.android.elevationdrag.tests;

import com.example.android.elevationdrag.*;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.Gravity;
import android.view.View;

/**
* Tests for ElevationDrag sample.
*/
public class SampleTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mTestActivity;
    private ElevationDragFragment mTestFragment;

    private View mFloatingShape;
    private View mDragFrame;

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
        mTestFragment = (ElevationDragFragment)
            mTestActivity.getSupportFragmentManager().getFragments().get(1);

        mFloatingShape = mTestActivity.findViewById(R.id.circle);
        mDragFrame = mTestActivity.findViewById(R.id.main_layout);
    }

    /**
    * Test if the test fixture has been set up correctly.
    */
    public void testPreconditions() {
        //Try to add a message to add context to your assertions. These messages will be shown if
        //a tests fails and make it easy to understand why a test failed
        assertNotNull("mTestActivity is null", mTestActivity);
        assertNotNull("mTestFragment is null", mTestFragment);
        assertNotNull("mFloatingShape is null", mFloatingShape);
        assertNotNull("mDragFrame is null", mDragFrame);
        // Check that the view is not raised yet.
        assertEquals(mFloatingShape.getZ(), 0f);
    }

    /**
     * Test that the floating shape can be dragged and that it's raised while dragging.
     */
    public void testDrag() {
        final float initialX = mFloatingShape.getX();
        // Drag the shape to the left edge.
        TouchUtils.dragViewToX(this,
                mFloatingShape,
                Gravity.CENTER,
                0);

        // Check that the view is dragging and that it's been raised.
        // We need to use runOnMainSync here as fake dragging uses waitForIdleSync().
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                // Check that the view has moved.
                float deltaX = mFloatingShape.getX() - initialX;
                assertTrue(Math.abs(deltaX) > 0f);

                // Check that the view is raised.
                assertTrue(mFloatingShape.getZ() > 0f);
            }
        });
    }
}
