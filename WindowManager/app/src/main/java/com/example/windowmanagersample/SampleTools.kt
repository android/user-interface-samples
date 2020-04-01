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

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import androidx.window.DisplayFeature

/**
 * Get the bounds of the display feature translated to the View's coordinate space and current
 * position in the window. This will also include view padding in the calculations.
 */
fun getFeatureBoundsInWindow(
    displayFeature: DisplayFeature,
    view: View,
    includePadding: Boolean = true
): Rect? {
    // The the location of the view in window to be in the same coordinate space as the feature.
    val viewLocationInWindow = IntArray(2)
    view.getLocationInWindow(viewLocationInWindow)

    // Intersect the feature rectangle in window with view rectangle to clip the bounds.
    val viewRect = Rect(
        viewLocationInWindow[0], viewLocationInWindow[1],
        viewLocationInWindow[0] + view.width, viewLocationInWindow[1] + view.height
    )

    // Include padding if needed
    if (includePadding) {
        viewRect.left += view.paddingLeft
        viewRect.top += view.paddingTop
        viewRect.right -= view.paddingRight
        viewRect.bottom -= view.paddingBottom
    }

    val featureRectInView = Rect(displayFeature.bounds)
    val intersects = featureRectInView.intersect(viewRect)

    //Checks to see if the display feature overlaps with our view at all
    if ((featureRectInView.width() == 0 && featureRectInView.height() == 0) ||
        !intersects
    ) {
        return null
    }

    // Offset the feature coordinates to view coordinate space start point
    featureRectInView.offset(-viewLocationInWindow[0], -viewLocationInWindow[1])

    return featureRectInView
}

/**
 * Get the layout params for placing a rectangle indicating a display feature inside a
 * [FrameLayout].
 */
fun getLayoutParamsForFeature(displayFeature: DisplayFeature, viewGroup: ViewGroup):
        MarginLayoutParams? {
    val featureRectInView = getFeatureBoundsInWindow(displayFeature, viewGroup) ?: return null

    val lp = MarginLayoutParams(featureRectInView.width(), featureRectInView.height())
    lp.leftMargin = featureRectInView.left
    lp.topMargin = featureRectInView.top

    // Make sure that zero-wide and zero-high features are still shown
    if (featureRectInView.left == featureRectInView.right) {
        lp.width = 1
    }
    if (featureRectInView.top == featureRectInView.bottom) {
        lp.height = 1
    }

    return lp
}