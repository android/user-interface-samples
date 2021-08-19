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
import androidx.window.layout.WindowInfoRepository.Companion.windowInfoRepository
import androidx.window.layout.WindowLayoutInfo
import androidx.window.testing.layout.FoldingFeature
import androidx.window.testing.layout.WindowLayoutInfoPublisherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SplitLayoutActivityTest {
    private val activityRule = ActivityScenarioRule(SplitLayoutActivity::class.java)
    private val publisherRule = WindowLayoutInfoPublisherRule()

    private val testScope = TestCoroutineScope()

    @get:Rule
    val testRule: TestRule

    init {
        testRule = RuleChain.outerRule(publisherRule).around(activityRule)
    }

    @Test
    fun testDeviceOpen_Flat(): Unit = testScope.runBlockingTest {
        activityRule.scenario.onActivity { activity ->
            val expected = WindowLayoutInfo.Builder().setDisplayFeatures(listOf()).build()

            val value = testScope.async {
                activity.windowInfoRepository().windowLayoutInfo.first()
            }
            publisherRule.overrideWindowLayoutInfo(expected)
            runBlockingTest {
                Assert.assertEquals(
                    expected,
                    value.await()
                )
            }
        }

        // Checks that the two views are overlapped if there's no FoldingFeature.
        onView(withId(R.id.start_layout)).check(isBottomAlignedWith(withId(R.id.end_layout)))
        onView(withId(R.id.start_layout)).check(isTopAlignedWith(withId(R.id.end_layout)))
        onView(withId(R.id.start_layout)).check(isLeftAlignedWith(withId(R.id.end_layout)))
        onView(withId(R.id.start_layout)).check(isRightAlignedWith(withId(R.id.end_layout)))
    }

    @Test
    fun testDeviceOpen_Vertical(): Unit = testScope.runBlockingTest {
        activityRule.scenario.onActivity { activity ->
            val feature = FoldingFeature(
                activity = activity,
                orientation = VERTICAL,
                state = HALF_OPENED
            )
            val expected =
                WindowLayoutInfo.Builder().setDisplayFeatures(listOf(feature)).build()

            val value = testScope.async {
                activity.windowInfoRepository().windowLayoutInfo.first()
            }
            publisherRule.overrideWindowLayoutInfo(expected)
            runBlockingTest {
                Assert.assertEquals(
                    expected,
                    value.await()
                )
            }
        }

        // Checks that start_layout is on the left of end_layout with a vertical folding feature.
        // This requires to run the test on a big enough screen to fit both views on screen
        onView(withId(R.id.start_layout)).check(isCompletelyLeftOf(withId(R.id.end_layout)))
    }

    @Test
    fun testDeviceOpen_Horizontal(): Unit = testScope.runBlockingTest {
        activityRule.scenario.onActivity { activity ->
            val feature = FoldingFeature(
                activity = activity,
                orientation = HORIZONTAL,
                state = HALF_OPENED
            )
            val expected =
                WindowLayoutInfo.Builder().setDisplayFeatures(listOf(feature)).build()

            val value = testScope.async {
                activity.windowInfoRepository().windowLayoutInfo.first()
            }
            publisherRule.overrideWindowLayoutInfo(expected)
            runBlockingTest {
                val newValues = value.await()
                Assert.assertEquals(
                    expected,
                    newValues
                )
            }
        }

        // Checks that start_layout is above of end_layout with a horizontal folding feature.
        // This requires to run the test on a big enough screen to fit both views on screen
        onView(withId(R.id.start_layout)).check(isCompletelyAbove(withId(R.id.end_layout)))
    }
}
