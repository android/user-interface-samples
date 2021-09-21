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

package com.example.windowmanagersample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.window.layout.FoldingFeature.Orientation.HORIZONTAL;
import static androidx.window.layout.FoldingFeature.Orientation.VERTICAL;
import static androidx.window.layout.FoldingFeature.State.FLAT;
import static androidx.window.layout.FoldingFeature.State.HALF_OPENED;
import static org.mockito.Mockito.verify;

import androidx.core.util.Consumer;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.window.java.layout.WindowInfoRepositoryCallbackAdapter;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoRepository;
import androidx.window.layout.WindowLayoutInfo;
import androidx.window.testing.layout.DisplayFeatureTesting;
import androidx.window.testing.layout.WindowLayoutInfoPublisherRule;
import java.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class DisplayFeaturesActivityJavaTest {

    private ActivityScenarioRule<DisplayFeaturesActivity> activityRule =
            new ActivityScenarioRule<>(DisplayFeaturesActivity.class);
    private WindowLayoutInfoPublisherRule publisherRule = new WindowLayoutInfoPublisherRule();

    @Mock private Consumer<WindowLayoutInfo> testConsumer;

    @Rule public TestRule testRule;

    public DisplayFeaturesActivityJavaTest() {
        testRule = RuleChain.outerRule(publisherRule).around(activityRule);
    };

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDeviceOpen_Flat() {
        activityRule
                .getScenario()
                .onActivity(
                        activity -> {
                            FoldingFeature feature =
                                    DisplayFeatureTesting.createFoldingFeature(
                                            activity, -1, 0, FLAT, HORIZONTAL);

                            WindowLayoutInfo expected =
                                    new WindowLayoutInfo.Builder()
                                            .setDisplayFeatures(Collections.singletonList(feature))
                                            .build();

                            WindowInfoRepositoryCallbackAdapter adapter =
                                    new WindowInfoRepositoryCallbackAdapter(
                                            WindowInfoRepository.getOrCreate(activity));

                            adapter.addWindowLayoutInfoListener(Runnable::run, testConsumer);

                            publisherRule.overrideWindowLayoutInfo(expected);

                            verify(testConsumer).accept(expected);
                        });

        onView(withId(R.id.state_update_log)).check(matches(withSubstring("state = FLAT")));
        onView(withId(R.id.current_state)).check(matches(withSubstring("is not separated")));
        onView(withId(R.id.current_state)).check(matches(withSubstring("Hinge is horizontal")));
    }

    @Test
    public void testDeviceOpen_TableTop() {
        activityRule
                .getScenario()
                .onActivity(
                        activity -> {
                            FoldingFeature feature =
                                    DisplayFeatureTesting.createFoldingFeature(
                                            activity,
                                            -1,
                                            0,
                                            HALF_OPENED,
                                            FoldingFeature.Orientation.HORIZONTAL);

                            WindowLayoutInfo expected =
                                    new WindowLayoutInfo.Builder()
                                            .setDisplayFeatures(Collections.singletonList(feature))
                                            .build();

                            WindowInfoRepositoryCallbackAdapter adapter =
                                    new WindowInfoRepositoryCallbackAdapter(
                                            WindowInfoRepository.getOrCreate(activity));

                            adapter.addWindowLayoutInfoListener(Runnable::run, testConsumer);

                            publisherRule.overrideWindowLayoutInfo(expected);

                            verify(testConsumer).accept(expected);
                        });

        onView(withId(R.id.state_update_log)).check(matches(withSubstring("state = HALF_OPENED")));
        onView(withId(R.id.current_state)).check(matches(withSubstring("are separated")));
        onView(withId(R.id.current_state)).check(matches(withSubstring("Hinge is horizontal")));
    }

    @Test
    public void testDeviceOpen_Book() { // : Unit = testScope.runBlockingTest {
        activityRule
                .getScenario()
                .onActivity(
                        activity -> {
                            FoldingFeature feature =
                                    DisplayFeatureTesting.createFoldingFeature(
                                            activity, -1, 0, HALF_OPENED, VERTICAL);

                            WindowLayoutInfo expected =
                                    new WindowLayoutInfo.Builder()
                                            .setDisplayFeatures(Collections.singletonList(feature))
                                            .build();

                            WindowInfoRepositoryCallbackAdapter adapter =
                                    new WindowInfoRepositoryCallbackAdapter(
                                            WindowInfoRepository.getOrCreate(activity));

                            adapter.addWindowLayoutInfoListener(Runnable::run, testConsumer);

                            publisherRule.overrideWindowLayoutInfo(expected);

                            verify(testConsumer).accept(expected);
                        });

        onView(withId(R.id.state_update_log)).check(matches(withSubstring("state = HALF_OPENED")));
        onView(withId(R.id.current_state)).check(matches(withSubstring("are separated")));
        onView(withId(R.id.current_state)).check(matches(withSubstring("Hinge is vertical")));
    }
}
