/*
* Copyright 2013 The Android Open Source Project
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
package com.example.android.advancedimmersivemode.tests;

import com.example.android.advancedimmersivemode.*;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

/**
* Tests for AdvancedImmersiveMode sample.
*/
public class SampleTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mTestActivity;
    private AdvancedImmersiveModeFragment mTestFragment;

    public SampleTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mTestActivity = getActivity();
        mTestFragment = (AdvancedImmersiveModeFragment)
            mTestActivity.getSupportFragmentManager().getFragments().get(1);
    }

    /**
    * Test if the test fixture has been set up correctly.
    */
    public void testPreconditions() {
        assertNotNull("mTestActivity is null", mTestActivity);
        assertNotNull("mTestFragment is null", mTestFragment);
    }


    /**
     * Verify that the UI flags actually changed when the toggle method is called.
     */
    @UiThreadTest
    public void testFlagsChanged() {
        int uiFlags = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        mTestFragment.mImmersiveModeCheckBox.setChecked(true);
        mTestFragment.mHideNavCheckbox.setChecked(true);
        mTestFragment.mHideStatusBarCheckBox.setChecked(true);
        mTestFragment.toggleUiFlags();
        int newUiFlags = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        assertTrue("UI Flags didn't toggle.", uiFlags != newUiFlags);
    }
}