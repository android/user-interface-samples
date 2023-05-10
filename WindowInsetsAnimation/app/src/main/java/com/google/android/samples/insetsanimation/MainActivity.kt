/*
 * Copyright 2020 The Android Open Source Project
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

package com.google.android.samples.insetsanimation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.samples.insetsanimation.databinding.ActivityMainBinding

/**
 * The root activity for the sample. This Activity's layout contains a [ConversationFragment] which
 * is where the main entry point for this sample is.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tell the Window that our app is going to responsible for fitting for any system windows.
        // This is similar to the now deprecated:
        // view.setSystemUiVisibility(LAYOUT_STABLE | LAYOUT_FULLSCREEN | LAYOUT_FULLSCREEN)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}
