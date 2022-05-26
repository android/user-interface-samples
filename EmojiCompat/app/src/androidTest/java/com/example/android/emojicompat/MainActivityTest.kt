/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.example.android.emojicompat

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @Rule @JvmField
    val rule = ActivityTestRule(MainActivity::class.java)

    @Test
    @MediumTest
    @Throws(Exception::class)
    fun allTextsDisplayed() {
        arrayOf(R.string.emoji_text_view,
                R.string.emoji_edit_text,
                R.string.emoji_button,
                R.string.regular_text_view,
                R.string.custom_text_view).forEach {
            val text = rule.activity.getString(it, MainActivity.EMOJI)
            onView(withText(text)).check(matches(isDisplayed()))
        }
    }

}
