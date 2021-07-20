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
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.FoldingFeature.Orientation.Companion.HORIZONTAL
import androidx.window.FoldingFeature.Orientation.Companion.VERTICAL
import androidx.window.FoldingFeature.State.Companion.FLAT
import androidx.window.FoldingFeature.State.Companion.HALF_OPENED
import androidx.window.WindowLayoutInfo
import androidx.window.testing.FoldingFeature
import androidx.window.testing.WindowLayoutInfoPublisherRule
import androidx.window.windowInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class DisplayFeaturesActivityTest {
    private val activityRule = ActivityScenarioRule(DisplayFeaturesActivity::class.java)
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
            val feature = FoldingFeature(
                activity = activity,
                state = FLAT,
                orientation = HORIZONTAL
            )
            val expected = WindowLayoutInfo.Builder().setDisplayFeatures(listOf(feature)).build()

            val values = mutableListOf<WindowLayoutInfo>()
            val value = testScope.async {
                activity.windowInfoRepository().windowLayoutInfo.take(1).toCollection(values)
            }
            publisherRule.overrideWindowLayoutInfo(expected)
            runBlockingTest {
                val newValues = value.await().toList()
                assertEquals(
                    listOf(expected),
                    newValues
                )
            }
        }
        onView(withId(R.id.state_update_log)).check(matches(withSubstring("state=FLAT")))
        onView(withId(R.id.current_state)).check(matches(withSubstring("is not separated")))
        onView(withId(R.id.current_state)).check(matches(withSubstring("Hinge is horizontal")))
    }

    @Test
    fun testDeviceOpen_TableTop(): Unit = testScope.runBlockingTest {
        activityRule.scenario.onActivity { activity ->
            val feature =
                FoldingFeature(activity = activity, state = HALF_OPENED, orientation = HORIZONTAL)
            val expected = WindowLayoutInfo.Builder().setDisplayFeatures(listOf(feature)).build()

            val values = mutableListOf<WindowLayoutInfo>()
            val value = testScope.async {
                activity.windowInfoRepository().windowLayoutInfo.take(1).toCollection(values)
            }
            publisherRule.overrideWindowLayoutInfo(expected)
            runBlockingTest {
                val newValues = value.await().toList()
                assertEquals(
                    listOf(expected),
                    newValues
                )
            }
        }
        onView(withId(R.id.state_update_log)).check(matches(withSubstring("state=HALF_OPENED")))
        onView(withId(R.id.current_state)).check(matches(withSubstring("are separated")))
        onView(withId(R.id.current_state)).check(matches(withSubstring("Hinge is horizontal")))
    }

    @Test
    fun testDeviceOpen_Book(): Unit = testScope.runBlockingTest {
        activityRule.scenario.onActivity { activity ->
            val feature =
                FoldingFeature(activity = activity, state = HALF_OPENED, orientation = VERTICAL)
            val expected = WindowLayoutInfo.Builder().setDisplayFeatures(listOf(feature)).build()

            val values = mutableListOf<WindowLayoutInfo>()
            val value = testScope.async {
                activity.windowInfoRepository().windowLayoutInfo.take(1).toCollection(values)
            }
            publisherRule.overrideWindowLayoutInfo(expected)
            runBlockingTest {
                val newValues = value.await().toList()
                assertEquals(
                    listOf(expected),
                    newValues
                )
            }
        }
        onView(withId(R.id.state_update_log)).check(matches(withSubstring("state=HALF_OPENED")))
        onView(withId(R.id.current_state)).check(matches(withSubstring("are separated")))
        onView(withId(R.id.current_state)).check(matches(withSubstring("Hinge is vertical")))
    }
}
