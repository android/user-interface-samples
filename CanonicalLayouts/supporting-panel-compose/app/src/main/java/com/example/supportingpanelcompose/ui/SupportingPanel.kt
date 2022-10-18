/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.supportingpanelcompose.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.window.layout.DisplayFeature
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.VerticalTwoPaneStrategy

@Composable
fun SupportingPanel(
    main: @Composable () -> Unit,
    supporting: @Composable () -> Unit,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>
) {
    TwoPane(
        first = main,
        second = supporting,
        strategy = when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> VerticalTwoPaneStrategy(0.5f)
            WindowWidthSizeClass.Medium -> HorizontalTwoPaneStrategy(0.5f)
            else -> HorizontalTwoPaneStrategy(0.3f)
        },
        displayFeatures = displayFeatures
    )
}