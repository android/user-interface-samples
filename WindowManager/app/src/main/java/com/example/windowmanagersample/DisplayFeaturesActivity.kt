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

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.util.Consumer
import androidx.window.FoldingFeature
import androidx.window.WindowLayoutInfo
import androidx.window.WindowManager
import com.example.windowmanagersample.backend.MidScreenFoldBackend
import com.example.windowmanagersample.databinding.ActivityDisplayFeaturesBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Demo activity that shows all display features and current device state on the screen. */
class DisplayFeaturesActivity : BaseSampleActivity() {

    private lateinit var windowManager: WindowManager
    private val stateLog: StringBuilder = StringBuilder()

    private val displayFeatureViews = ArrayList<View>()

    // Store most recent values for the device state and window layout
    private val stateContainer = StateContainer()
    private lateinit var binding: ActivityDisplayFeaturesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        windowManager = getTestBackend()?.let { backend ->
            binding.deviceStateToggleButton.visibility = View.VISIBLE
            binding.deviceStateToggleButton.setOnClickListener {
                if (backend is MidScreenFoldBackend) {
                    backend.toggleDeviceHalfOpenedState(this)
                }
            }
            WindowManager(this, backend)
        } ?: WindowManager(this)

        stateLog.clear()
        stateLog.append(getString(R.string.state_update_log)).append("\n")
    }

    override fun onStart() {
        super.onStart()
        windowManager.registerLayoutChangeCallback(mainThreadExecutor, stateContainer)
    }

    override fun onStop() {
        super.onStop()
        windowManager.unregisterLayoutChangeCallback(stateContainer)
    }

    /** Updates the device state and display feature positions. */
    internal fun updateCurrentState(layoutInfo: WindowLayoutInfo?) {
        // Cleanup previously added feature views
        val rootLayout = binding.featureContainerLayout
        for (featureView in displayFeatureViews) {
            rootLayout.removeView(featureView)
        }
        displayFeatureViews.clear()

        // Update the UI with the current state
        val stateStringBuilder = StringBuilder()

        layoutInfo?.let { windowLayoutInfo ->
            stateStringBuilder.append(getString(R.string.window_layout))
                .append(": ")

            // Add views that represent display features
            for (displayFeature in windowLayoutInfo.displayFeatures) {
                val lp = getLayoutParamsForFeatureInFrameLayout(displayFeature, rootLayout)
                    ?: continue

                // Make sure that zero-wide and zero-high features are still shown
                if (lp.width == 0) {
                    lp.width = 1
                }
                if (lp.height == 0) {
                    lp.height = 1
                }

                val featureView = View(this)
                val foldFeature = displayFeature as? FoldingFeature

                val color = if (foldFeature != null) {
                    if (foldFeature.isSeparating) {
                        stateStringBuilder.append(getString(R.string.screens_are_separated))
                        getColor(R.color.color_feature_separating)
                    } else {
                        stateStringBuilder.append(getString(R.string.screens_are_not_separated))
                        getColor(R.color.color_feature_not_separating)
                    }
                } else {
                    getColor(R.color.color_feature_unknown)
                }
                if (foldFeature != null) {
                    stateStringBuilder
                        .append(" - ")
                        .append(
                            if (foldFeature.orientation == FoldingFeature.ORIENTATION_HORIZONTAL) {
                                getString(R.string.screen_is_horizontal)
                            } else {
                                getString(R.string.screen_is_vertical)
                            }
                        )
                }
                featureView.foreground = ColorDrawable(color)

                rootLayout.addView(featureView, lp)
                featureView.id = View.generateViewId()

                displayFeatureViews.add(featureView)
            }
        }

        binding.currentState.text = stateStringBuilder.toString()
    }

    /** Adds the current state to the text log of changes on screen. */
    internal fun updateStateLog(layoutInfo: WindowLayoutInfo) {
        stateLog.append(getCurrentTimeString())
            .append(" ")
            .append(layoutInfo)
            .append("\n")
        binding.stateUpdateLog.text = stateLog
    }

    private fun getCurrentTimeString(): String {
        val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
        val currentDate = sdf.format(Date())
        return currentDate.toString()
    }

    inner class StateContainer : Consumer<WindowLayoutInfo> {
        var lastLayoutInfo: WindowLayoutInfo? = null

        override fun accept(newLayoutInfo: WindowLayoutInfo) {
            updateStateLog(newLayoutInfo)
            lastLayoutInfo = newLayoutInfo
            updateCurrentState(lastLayoutInfo)
        }
    }
}
