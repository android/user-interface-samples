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

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.util.Consumer
import androidx.core.view.doOnLayout
import androidx.window.DisplayFeature
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
        val view = binding.root
        setContentView(view)
        windowManager = WindowManager(this, getTestBackend())
        binding.root.doOnLayout {
            updateWindowLayout(windowManager.windowLayoutInfo)
        }
    }

    private fun updateWindowLayout(windowLayoutInfo: WindowLayoutInfo) {
        val splitPositions = splitViewPositions(
            binding.contentLayout.root,
            binding.controlLayout.root,
            windowLayoutInfo
        )

        if (splitPositions != null) {
            val startPosition = splitPositions[0]
            val startWidthSpec = View.MeasureSpec.makeMeasureSpec(
                startPosition.width(),
                View.MeasureSpec.EXACTLY
            )
            val startHeightSpec = View.MeasureSpec.makeMeasureSpec(
                startPosition.height(),
                View.MeasureSpec.EXACTLY
            )
            binding.contentLayout.root.measure(startWidthSpec, startHeightSpec)
            binding.contentLayout.root.layout(
                startPosition.left, startPosition.top, startPosition.right,
                startPosition.bottom
            )

            val endPosition = splitPositions[1]
            val endWidthSpec = View.MeasureSpec.makeMeasureSpec(
                endPosition.width(),
                View.MeasureSpec.EXACTLY
            )
            val endHeightSpec = View.MeasureSpec.makeMeasureSpec(
                endPosition.height(),
                View.MeasureSpec.EXACTLY
            )
            binding.controlLayout.root.measure(endWidthSpec, endHeightSpec)
            binding.controlLayout.root.layout(
                endPosition.left, endPosition.top, endPosition.right,
                endPosition.bottom
            )
        }
    }

    /**
     * Get the position of the split for this view.
     * @return A rect that defines of split, or {@code null} if there is no split.
     */
    private fun splitViewPositions(
        startView: View?,
        endView: View?,
        windowLayoutInfo: WindowLayoutInfo
    ): Array<Rect>? {
        if (startView == null || endView == null) {
            return null
        }

        // Calculate the area for view's content with padding
        val paddedWidth = binding.root.width - binding.root.paddingLeft - binding.root.paddingRight
        val paddedHeight =
            binding.root.height - binding.root.paddingTop - binding.root.paddingBottom

        for (feature in windowLayoutInfo.displayFeatures) {
            // Only a hinge or a fold can split the area in two
            if (feature.type != DisplayFeature.TYPE_FOLD && feature.type != DisplayFeature.TYPE_HINGE) {
                continue
            }

            val splitRect = getFeaturePositionInViewRect(feature, binding.root) ?: continue

            if (feature.bounds.left == 0) { // Horizontal layout
                val topRect = Rect(
                    startView.paddingLeft,
                    startView.paddingTop,
                    startView.paddingLeft + paddedWidth,
                    splitRect.top
                )
                val bottomRect = Rect(
                    endView.paddingLeft, splitRect.bottom,
                    endView.paddingLeft + paddedWidth,
                    endView.paddingTop + paddedHeight
                )

                if (measureAndCheckMinSize(topRect, startView) &&
                    measureAndCheckMinSize(bottomRect, endView)
                ) {
                    return arrayOf(topRect, bottomRect)
                }
            } else if (feature.bounds.top == 0) { // Vertical layout
                val leftRect = Rect(
                    startView.paddingLeft,
                    startView.paddingTop,
                    splitRect.left,
                    startView.paddingTop + paddedHeight
                )
                val rightRect = Rect(
                    splitRect.right,
                    endView.paddingTop,
                    endView.paddingLeft + paddedWidth,
                    endView.paddingTop + paddedHeight
                )

                if (measureAndCheckMinSize(leftRect, startView) &&
                    measureAndCheckMinSize(rightRect, endView)
                ) {
                    return arrayOf(leftRect, rightRect)
                }
            }
        }

        return null
    }

    /**
     * Measure a child view and see it if will fit in the provided rect.
     * <p>Note: This method calls [View.measure] on the child view, which updates
     * its stored values for measured with and height. If the view will end up with different
     * values, it should be measured again.
     */
    private fun measureAndCheckMinSize(rect: Rect, childView: View): Boolean {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(rect.width(), View.MeasureSpec.AT_MOST)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(rect.height(), View.MeasureSpec.AT_MOST)
        childView.measure(widthSpec, heightSpec)
        return childView.measuredWidthAndState and FrameLayout.MEASURED_STATE_TOO_SMALL == 0 &&
                childView.measuredHeightAndState and FrameLayout.MEASURED_STATE_TOO_SMALL == 0
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
            updateWindowLayout(newLayoutInfo)
        }
    }
}