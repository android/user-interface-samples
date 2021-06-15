/*
 * Copyright 2020 The Android Open Source Project
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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.WindowInfoRepo
import androidx.window.windowInfoRepository
import com.example.windowmanagersample.databinding.ActivitySplitLayoutBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/** Demo of [SplitLayout]. */
class SplitLayoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplitLayoutBinding
    private lateinit var windowInfoRepo: WindowInfoRepo

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplitLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        windowInfoRepo = windowInfoRepository()
        // Create a new coroutine since repeatOnLifecycle is a suspend function
        lifecycleScope.launch {
            // The block passed to repeatOnLifecycle is executed when the lifecycle
            // is at least STARTED and is cancelled when the lifecycle is STOPPED.
            // It automatically restarts the block when the lifecycle is STARTED again.
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Safely collect from windowInfoRepo when the lifecycle is STARTED
                // and stops collection when the lifecycle is STOPPED
                windowInfoRepo.windowLayoutInfo()
                    // Throttle first event 10ms to allow the UI to pickup the posture
                    .throttleFirst(10)
                    .collect { newLayoutInfo ->
                        // New posture information
                        val splitLayout = binding.splitLayout
                        splitLayout.updateWindowLayout(newLayoutInfo)
                    }
            }
        }
    }
}
