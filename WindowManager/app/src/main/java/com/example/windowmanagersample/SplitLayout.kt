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

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.widget.FrameLayout
import androidx.window.DisplayFeature.TYPE_FOLD
import androidx.window.DisplayFeature.TYPE_HINGE
import androidx.window.WindowLayoutInfo
import com.example.windowmanagersample.databinding.SplitLayoutContentBinding
import com.example.windowmanagersample.databinding.SplitLayoutControlBinding

/**
 * An example of split-layout for two views, separated by a display feature that goes across the
 * window. When both start and end views are added, it checks if there are display features that
 * separate the area in two (e.g. fold or hinge) and places them side-by-side or top-bottom.
 */
class SplitLayout : FrameLayout {
    private var windowLayoutInfo: WindowLayoutInfo? = null

    private lateinit var contentBinding: SplitLayoutContentBinding
    private lateinit var controlBinding: SplitLayoutControlBinding

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun updateWindowLayout(windowLayoutInfo: WindowLayoutInfo) {
        this.windowLayoutInfo = windowLayoutInfo
        requestLayout()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentBinding = SplitLayoutContentBinding.bind(findViewById(R.id.content_layout))
        controlBinding = SplitLayoutControlBinding.bind(findViewById(R.id.control_layout))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (windowLayoutInfo == null) return

        val splitPositions = splitViewPositions(contentBinding.root, controlBinding.root)

        if (splitPositions != null) {
            val startPosition = splitPositions[0]
            val startWidthSpec = MeasureSpec.makeMeasureSpec(startPosition.width(), EXACTLY)
            val startHeightSpec = MeasureSpec.makeMeasureSpec(startPosition.height(), EXACTLY)
            contentBinding.root.measure(startWidthSpec, startHeightSpec)
            contentBinding.root.layout(
                startPosition.left, startPosition.top, startPosition.right,
                startPosition.bottom
            )

            val endPosition = splitPositions[1]
            val endWidthSpec = MeasureSpec.makeMeasureSpec(endPosition.width(), EXACTLY)
            val endHeightSpec = MeasureSpec.makeMeasureSpec(endPosition.height(), EXACTLY)
            controlBinding.root.measure(endWidthSpec, endHeightSpec)
            controlBinding.root.layout(
                endPosition.left, endPosition.top, endPosition.right,
                endPosition.bottom
            )
        } else {
            super.onLayout(changed, left, top, right, bottom)
        }
    }

    /**
     * Get the position of the split for this view.
     * @return A rect that defines of split, or {@code null} if there is no split.
     */
    private fun splitViewPositions(startView: View, endView: View): Array<Rect>? {

        // Calculate the area for view's content with padding
        val paddedWidth = width - paddingLeft - paddingRight
        val paddedHeight = height - paddingTop - paddingBottom

        for (feature in windowLayoutInfo?.displayFeatures!!) {
            // Only a hinge or a fold can split the area in two
            if (feature.type != TYPE_FOLD && feature.type != TYPE_HINGE) {
                continue
            }

            val splitRect = getFeatureBoundsInWindow(feature, this) ?: continue

            if (feature.bounds.left == 0) { // Horizontal layout
                val topRect = Rect(
                    paddingLeft, paddingTop,
                    paddingLeft + paddedWidth, splitRect.top
                )
                val bottomRect = Rect(
                    paddingLeft, splitRect.bottom,
                    paddingLeft + paddedWidth, paddingTop + paddedHeight
                )

                if (measureAndCheckMinSize(topRect, startView) &&
                    measureAndCheckMinSize(bottomRect, endView)
                ) {
                    return arrayOf(topRect, bottomRect)
                }
            } else if (feature.bounds.top == 0) { // Vertical layout
                val leftRect = Rect(
                    paddingLeft, paddingTop,
                    splitRect.left, paddingTop + paddedHeight
                )
                val rightRect = Rect(
                    splitRect.right, paddingTop,
                    paddingLeft + paddedWidth, paddingTop + paddedHeight
                )

                if (measureAndCheckMinSize(leftRect, startView) &&
                    measureAndCheckMinSize(rightRect, endView)
                ) {
                    return arrayOf(leftRect, rightRect)
                }
            }
        }

        // We have tried to fit the children and measured them previously.
        // Since they didn't fit, there is no split that is possible with theses views
        return null
    }

    /**
     * Measure a child view and see it if will fit in the provided rect.
     * <p>Note: This method calls [View.measure] on the child view, which updates
     * its stored values for measured with and height. If the view will end up with different
     * values, it should be measured again.
     */
    private fun measureAndCheckMinSize(rect: Rect, childView: View): Boolean {
        val widthSpec = MeasureSpec.makeMeasureSpec(rect.width(), AT_MOST)
        val heightSpec = MeasureSpec.makeMeasureSpec(rect.height(), AT_MOST)
        childView.measure(widthSpec, heightSpec)
        return childView.measuredWidthAndState and MEASURED_STATE_TOO_SMALL == 0 &&
                childView.measuredHeightAndState and MEASURED_STATE_TOO_SMALL == 0
    }
}