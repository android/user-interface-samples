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

package com.example.windowmanagersample

import android.os.Bundle
import androidx.core.util.Consumer
import androidx.core.view.doOnLayout
import androidx.window.DeviceState
import androidx.window.WindowLayoutInfo
import androidx.window.WindowManager
import com.example.windowmanagersample.databinding.ActivitySplitLayoutBinding

/** Demo of [SplitLayout]. */
class SplitLayoutActivity : BaseSampleActivity() {

    private lateinit var windowManager: WindowManager
    private val layoutStateChangeCallback = LayoutStateChangeCallback()

    private lateinit var binding: ActivitySplitLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplitLayoutBinding.inflate(layoutInflater)
        val view = binding.rootLayout
        setContentView(view)
        windowManager = WindowManager(this, getTestBackend())
        binding.rootLayout.doOnLayout {
            binding.splitLayout.updateWindowLayout(windowManager.windowLayoutInfo)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        windowManager.registerLayoutChangeCallback(mainThreadExecutor, layoutStateChangeCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        windowManager.unregisterLayoutChangeCallback(layoutStateChangeCallback)
    }

    inner class LayoutStateChangeCallback : Consumer<WindowLayoutInfo> {
        override fun accept(newLayoutInfo: WindowLayoutInfo) {
            binding.splitLayout.updateWindowLayout(newLayoutInfo)
        }
    }

    inner class DeviceStateChangeCallback : Consumer<DeviceState> {
        override fun accept(newDeviceState: DeviceState) {
//            binding.splitLayout.updateWindowLayout(newDeviceState)
        }
    }
}