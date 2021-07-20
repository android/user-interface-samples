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

import android.graphics.Rect
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.FoldingFeature
import androidx.window.FoldingFeature.State.Companion.HALF_OPENED
import androidx.window.FoldingFeature.Type.Companion.FOLD
import androidx.window.WindowLayoutInfo
import androidx.window.testing.WindowLayoutInfoPublisherRule
import androidx.window.windowInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toCollection
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
    fun testDeviceOpen_Vertical(): Unit = testScope.runBlockingTest {
        activityRule.scenario.onActivity { activity ->
            val bounds = activity.windowInfoRepository().currentWindowMetrics.bounds
            val center = bounds.centerX()
            val feature = FoldingFeature(
                Rect(center, 0, center, bounds.height()),
                FOLD,
                HALF_OPENED
            )
            val expected = WindowLayoutInfo.Builder().setDisplayFeatures(listOf(feature)).build()

            val values = mutableListOf<WindowLayoutInfo>()
            val value = testScope.async {
                activity.windowInfoRepository().windowLayoutInfo.take(1).toCollection(values)
            }
            publisherRule.overrideWindowLayoutInfo(expected)
            runBlockingTest {
                val newValues = value.await().toList()
                Assert.assertEquals(
                    listOf(expected),
                    newValues
                )
                delay(5000)
            }
        }
        onView(withId(R.id.start_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.end_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testDeviceOpen_Horizontal(): Unit = testScope.runBlockingTest {
        activityRule.scenario.onActivity { activity ->
            val bounds = activity.windowInfoRepository().currentWindowMetrics.bounds
            val center = bounds.centerY()
            val feature = FoldingFeature(
                Rect(0, center, bounds.height(), center),
                FOLD,
                HALF_OPENED
            )
            val expected = WindowLayoutInfo.Builder().setDisplayFeatures(listOf(feature)).build()

            val values = mutableListOf<WindowLayoutInfo>()
            val value = testScope.async {
                activity.windowInfoRepository().windowLayoutInfo.take(1).toCollection(values)
            }
            publisherRule.overrideWindowLayoutInfo(expected)
            runBlockingTest {
                val newValues = value.await().toList()
                Assert.assertEquals(
                    listOf(expected),
                    newValues
                )
                delay(5000)
            }
        }
        onView(withId(R.id.start_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.end_layout)).check(matches(isDisplayed()))
    }
}
