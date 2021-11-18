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
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import androidx.window.rxjava2.layout.windowLayoutInfoObservable
import com.example.windowmanagersample.databinding.ActivityRxBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Demo activity that shows all display features and current device state on the screen. */
class RxActivity : AppCompatActivity() {

    private val stateLog: StringBuilder = StringBuilder()
    private var disposable: Disposable? = null

    private lateinit var binding: ActivityRxBinding
    private lateinit var observable: Observable<WindowLayoutInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create a new observable
        observable = WindowInfoTracker.getOrCreate(this@RxActivity)
            .windowLayoutInfoObservable(this@RxActivity)

        stateLog.clear()
        stateLog.append(getString(R.string.state_update_log)).append("\n")
    }

    override fun onStart() {
        super.onStart()

        // Subscribe to receive WindowLayoutInfo updates
        disposable?.dispose()
        disposable = observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { newLayoutInfo ->
                updateStateLog(newLayoutInfo)
                updateCurrentState(newLayoutInfo)
            }
    }

    override fun onStop() {
        super.onStop()

        // Dispose the WindowLayoutInfo observable
        disposable?.dispose()
    }

    /** Updates the device state and display feature positions. */
    private fun updateCurrentState(layoutInfo: WindowLayoutInfo) {
        // Cleanup previously added feature views
        val rootLayout = binding.featureContainerLayout
        rootLayout.removeAllViews()

        // Update the UI with the current state
        val stateStringBuilder = StringBuilder()

        stateStringBuilder.append(getString(R.string.window_layout))
            .append(": ")

        // Add views that represent display features
        for (displayFeature in layoutInfo.displayFeatures) {
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
                        if (foldFeature.orientation == FoldingFeature.Orientation.HORIZONTAL) {
                            getString(R.string.screen_is_horizontal)
                        } else {
                            getString(R.string.screen_is_vertical)
                        }
                    )
                    .append(" - ")
                    .append(
                        if (foldFeature.occlusionType == FoldingFeature.OcclusionType.NONE) {
                            getString(R.string.occlusion_is_full)
                        } else {
                            getString(R.string.occlusion_is_none)
                        }
                    )
            }
            featureView.foreground = ColorDrawable(color)

            rootLayout.addView(featureView, lp)
            featureView.id = View.generateViewId()
        }

        binding.currentState.text = stateStringBuilder.toString()
        Log.i("FoldingFeature", stateStringBuilder.toString())
    }

    /** Adds the current state to the text log of changes on screen. */
    private fun updateStateLog(layoutInfo: WindowLayoutInfo) {
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
}
