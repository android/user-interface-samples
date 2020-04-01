/*
 *
 *  * Copyright 2020 The Android Open Source Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.example.windowmanagersample

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.util.Consumer
import androidx.core.view.doOnLayout
import androidx.window.DeviceState
import androidx.window.DisplayFeature
import androidx.window.WindowLayoutInfo
import androidx.window.WindowManager
import com.example.windowmanagersample.backend.MidScreenFoldBackend
import com.example.windowmanagersample.databinding.ActivityDisplayFeaturesBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Demo activity that shows all display features and current device state on the screen.
 * Updates as new layout information or device state changes occur
 */
class DisplayFeaturesActivity : BaseSampleActivity() {

    private lateinit var windowManager: WindowManager
    private val stateLog: StringBuilder = StringBuilder()

    private val deviceStateChangeCallback = DeviceStateChangeCallback()
    private val layoutStateChangeCallback = LayoutStateChangeCallback()

    private lateinit var binding: ActivityDisplayFeaturesBinding

    private var windowBackend: MidScreenFoldBackend? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        windowBackend = getTestBackend()
        windowManager = WindowManager(this, windowBackend)

        //if our windowBackend isn't null, we're using the test implementation so enable our
        // DeviceState toggle
        if (windowBackend != null) {
            binding.deviceStateToggleButton.visibility = View.VISIBLE
            binding.deviceStateToggleButton.setOnClickListener {
                windowBackend?.toggleDeviceHalfOpenedState()
            }
        }

        stateLog.clear()
        stateLog.append(getString(R.string.state_update_log)).append("\n")

        windowManager.registerDeviceStateChangeCallback(
            mainThreadExecutor,
            deviceStateChangeCallback
        )

        window.decorView.doOnLayout {
            updateStateAndFeatureViews()
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

    /**
     * Update the device state and display feature positions. Needs to be called after window
     * layout, so that view position in the window could be evaluated correctly.
     */
    internal fun updateStateAndFeatureViews() {
        // Cleanup previously added feature views
        binding.featureContainerLayout.removeAllViews()

        // Update the UI with the current state
        val stateStringBuilder = StringBuilder()
        // Update the current state string
        stateStringBuilder.append(getString(R.string.device_state))
            .append(": ")
            .append(windowManager.deviceState)
            .append("\n")

        stateStringBuilder.append(getString(R.string.window_layout))
            .append(": ")
            .append(windowManager.windowLayoutInfo)

        // Add views that represent display features
        for (displayFeature in windowManager.windowLayoutInfo.displayFeatures) {
            val lp = getLayoutParamsForFeature(
                displayFeature,
                binding.featureContainerLayout
            ) ?: continue

            val featureView = View(this)
            val color = when (displayFeature.type) {
                DisplayFeature.TYPE_FOLD -> getColor(R.color.color_feature_fold)
                DisplayFeature.TYPE_HINGE -> getColor(R.color.color_feature_hinge)
                else -> getColor(R.color.color_feature_unknown)
            }
            featureView.foreground = ColorDrawable(color)

            binding.featureContainerLayout.addView(featureView, lp)
            featureView.id = View.generateViewId()
        }
        binding.currentState.text = stateStringBuilder.toString()
    }

    /** Add the current state to the text log of changes on screen. */
    internal fun updateStateLog(info: Any) {
        stateLog.append(getCurrentTimeString())
            .append(" ")
            .append(info)
            .append("\n")
        binding.stateUpdateLog.text = stateLog
    }

    private fun getCurrentTimeString(): String {
        val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
        val currentDate = sdf.format(Date())
        return currentDate.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.unregisterDeviceStateChangeCallback(deviceStateChangeCallback)
    }

    inner class DeviceStateChangeCallback : Consumer<DeviceState> {
        override fun accept(newDeviceState: DeviceState) {
            updateStateLog(newDeviceState)
            updateStateAndFeatureViews()
        }
    }

    inner class LayoutStateChangeCallback : Consumer<WindowLayoutInfo> {
        override fun accept(newLayoutInfo: WindowLayoutInfo) {
            updateStateLog(newLayoutInfo)
            updateStateAndFeatureViews()
        }
    }
}
