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

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.android.advancedimmersivemode.AdvancedImmersiveModeFragment;
import com.example.android.advancedimmersivemode.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for AdvancedImmersiveMode sample.
 */
@RunWith(AndroidJUnit4.class)
public class SampleTests {
    private static final String TAG = "SampleTests";

    private AdvancedImmersiveModeFragment mTestFragment;
    private ActivityScenario<MainActivity> mScenario;

    @Before
    public void setUp() {
        mScenario = ActivityScenario.launch(MainActivity.class);
    }

    /**
     * Test if the test fixture has been set up correctly.
     */
    public void testPreconditions() {
        assertNotNull("scenario is null", mScenario);
    }

    /**
     * Verify that the UI flags actually changed when the toggle method is called.
     */
    @Test
    public void testFlagsChanged() {
        mScenario.onActivity(activity -> {
            mTestFragment = (AdvancedImmersiveModeFragment) activity.getSupportFragmentManager()
                    .getFragments().get(1);
            assertNotNull("mTestFragment is null", mTestFragment);
            Log.d(TAG, "testFlagsChanged: mTestFragment found");

            int uiFlags = activity.getWindow().getDecorView().getSystemUiVisibility();
            mTestFragment.mImmersiveModeCheckBox.setChecked(true);
            mTestFragment.mHideNavCheckbox.setChecked(true);
            mTestFragment.mHideStatusBarCheckBox.setChecked(true);
            mTestFragment.toggleUiFlags();
            int newUiFlags = activity.getWindow().getDecorView().getSystemUiVisibility();
            assertTrue("UI Flags didn't toggle.", uiFlags != newUiFlags);
        });
    }

    @After
    public void tearDown() {
        Log.d(TAG, "tearDown: Test case complete");
    }
}