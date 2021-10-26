/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.windowmanagersample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.PositionAssertions.isBottomAlignedWith
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf
import androidx.test.espresso.assertion.PositionAssertions.isLeftAlignedWith
import androidx.test.espresso.assertion.PositionAssertions.isRightAlignedWith
import androidx.test.espresso.assertion.PositionAssertions.isTopAlignedWith
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.layout.FoldingFeature.Orientation.Companion.HORIZONTAL
import androidx.window.layout.FoldingFeature.Orientation.Companion.VERTICAL
import androidx.window.layout.FoldingFeature.State.Companion.HALF_OPENED
import androidx.window.testing.layout.FoldingFeature
import androidx.window.testing.layout.TestWindowLayoutInfo
import androidx.window.testing.layout.WindowLayoutInfoPublisherRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplitLayoutActivityTest {
    private val activityRule = ActivityScenarioRule(SplitLayoutActivity::class.java)
    private val publisherRule = WindowLayoutInfoPublisherRule()

    @get:Rule
    val testRule: TestRule

    init {
        testRule = RuleChain.outerRule(publisherRule).around(activityRule)
    }

    @Test
    fun testDeviceOpen_Flat() {
        activityRule.scenario.onActivity {
            val expected = TestWindowLayoutInfo(listOf())
            publisherRule.overrideWindowLayoutInfo(expected)
        }

        // Checks that the two views are overlapped if there's no FoldingFeature.
        onView(withId(R.id.start_layout)).check(isBottomAlignedWith(withId(R.id.end_layout)))
        onView(withId(R.id.start_layout)).check(isTopAlignedWith(withId(R.id.end_layout)))
        onView(withId(R.id.start_layout)).check(isLeftAlignedWith(withId(R.id.end_layout)))
        onView(withId(R.id.start_layout)).check(isRightAlignedWith(withId(R.id.end_layout)))
    }

    @Test
    fun testDeviceOpen_Vertical() {
        activityRule.scenario.onActivity { activity ->
            val feature = FoldingFeature(
                activity = activity,
                orientation = VERTICAL,
                state = HALF_OPENED
            )
            val expected = TestWindowLayoutInfo(listOf(feature))
            publisherRule.overrideWindowLayoutInfo(expected)
        }

        // Checks that start_layout is on the left of end_layout with a vertical folding feature.
        // This requires to run the test on a big enough screen to fit both views on screen
        onView(withId(R.id.start_layout)).check(isCompletelyLeftOf(withId(R.id.end_layout)))
    }

    @Test
    fun testDeviceOpen_Horizontal() {
        activityRule.scenario.onActivity { activity ->
            val feature = FoldingFeature(
                activity = activity,
                orientation = HORIZONTAL,
                state = HALF_OPENED
            )
            val expected = TestWindowLayoutInfo(listOf(feature))
            publisherRule.overrideWindowLayoutInfo(expected)
        }

        // Checks that start_layout is above of end_layout with a horizontal folding feature.
        // This requires to run the test on a big enough screen to fit both views on screen
        onView(withId(R.id.start_layout)).check(isCompletelyAbove(withId(R.id.end_layout)))
    }
}
