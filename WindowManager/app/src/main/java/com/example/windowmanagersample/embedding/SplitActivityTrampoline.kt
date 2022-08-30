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

package com.example.windowmanagersample.embedding

import android.content.Intent
import android.os.Bundle
import android.util.LayoutDirection
import androidx.window.core.ExperimentalWindowApi
import androidx.window.embedding.ActivityFilter
import androidx.window.embedding.SplitController
import androidx.window.embedding.SplitPlaceholderRule
import androidx.window.embedding.SplitRule

/**
 * Example trampoline activity that launches a split and finishes itself.
 */
@ExperimentalWindowApi
class SplitActivityTrampoline : SplitActivityBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityFilters = setOf(
            ActivityFilter(
                componentName(
                    "com.example.windowmanagersample.embedding.SplitActivityTrampolineTarget"
                ),
                null
            )
        )
        val placeholderIntent = Intent()
        placeholderIntent.component =
            componentName("com.example.windowmanagersample.embedding.SplitActivityPlaceholder")
        val placeholderRule = SplitPlaceholderRule(
            filters = activityFilters,
            placeholderIntent = placeholderIntent,
            isSticky = false,
            finishPrimaryWithSecondary = SplitRule.FINISH_ADJACENT,
            minWidth = minSplitWidth(),
            minSmallestWidth = 0,
            splitRatio = SPLIT_RATIO,
            layoutDirection = LayoutDirection.LOCALE
        )
        SplitController.getInstance().registerRule(placeholderRule)
        val activityIntent = Intent()
        activityIntent.component = componentName(
            "com.example.windowmanagersample.embedding.SplitActivityTrampolineTarget"
        )
        startActivity(activityIntent)

        finish()
    }
}
