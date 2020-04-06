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

import android.graphics.Rect
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * A [View.OnTouchListener] which we can set on a scrolling view, to control the IME inset
 * and visibility. When set on a scrolling view (such as a [RecyclerView]), it will track scrolling
 * gestures, and trigger a request to control the IME insets via
 * [SimpleImeAnimationController.startControlRequest] once the user is over-scrolling the view.
 *
 * Once in control, the listener will inset the IME in/off screen based on the user's scroll
 * position, using [SimpleImeAnimationController.updateInsetBy].
 */
class InsetsAnimationOverscrollingTouchListener : View.OnTouchListener {
    private var isHandling = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var lastWindowY = 0

    private val bounds = Rect()

    private val simpleController = SimpleImeAnimationController()

    private var velocityTracker: VelocityTracker? = null

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (velocityTracker == null) {
            // Obtain a VelocityTracker if we don't have one yet
            velocityTracker = VelocityTracker.obtain()
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                velocityTracker?.addMovement(event)

                lastTouchX = event.x
                lastTouchY = event.y

                v.copyBoundsInWindow(bounds)
                lastWindowY = bounds.top
            }
            MotionEvent.ACTION_MOVE -> {
                // Since the view is likely to be translated/moved as the WindowInsetsAnimation
                // progresses, we need to make sure we account for that change in our touch
                // handling. We do that by keeping track of the view's Y position in the window,
                // and detecting the difference between the current bounds.
                v.copyBoundsInWindow(bounds)
                val windowOffsetY = bounds.top - lastWindowY

                // We then make a copy of the MotionEvent, and offset it with the calculated
                // windowOffsetY. We can then pass it to the VelocityTracker.
                val vtev = MotionEvent.obtain(event)
                vtev.offsetLocation(0f, windowOffsetY.toFloat())
                velocityTracker?.addMovement(vtev)

                val dx = vtev.x - lastTouchX
                val dy = vtev.y - lastTouchY

                if (!isHandling) {
                    // If we're not currently handling the touch gesture, lets check if we should
                    // start handling, by seeing if the gesture is majorly vertical, and
                    // larger than the touch slop
                    isHandling = dy.absoluteValue > dx.absoluteValue &&
                            dy.absoluteValue >= ViewConfiguration.get(v.context).scaledTouchSlop
                }

                if (isHandling) {
                    if (simpleController.isInsetAnimationInProgress()) {
                        // If we currently have control, we can update the IME insets to 'scroll'
                        // the IME in
                        simpleController.updateInsetBy(dy.roundToInt())
                    } else if (!simpleController.isInsetAnimationRequestPending() && dy < 0 &&
                        v.canScrollVertically(-1) && !simpleController.isImeShownAtStart) {
                        // If we don't currently have control (and a request isn't pending),
                        // the IME is not shown, the user is scrolling up, and the view can't
                        // scroll up any more (i.e. over-scrolling), we can start to control
                        // the IME insets
                        simpleController.startControlRequest(v)
                    }

                    // Lastly we record the event X, Y, and view's Y window position, for the
                    // next touch event
                    lastTouchY = event.y
                    lastTouchX = event.x
                    lastWindowY = bounds.top
                }
            }
            MotionEvent.ACTION_UP -> {
                velocityTracker?.addMovement(event)

                // Calculate the current velocityY, over 1000 milliseconds
                velocityTracker?.computeCurrentVelocity(1000)
                val velocityY = velocityTracker?.yVelocity

                // If we received a ACTION_UP event, end any current WindowInsetsAnimation passing
                // in the calculated Y velocity
                simpleController.finish(velocityY)

                // Reset our touch handling state
                reset()
            }
            MotionEvent.ACTION_CANCEL -> {
                // If we received a ACTION_CANCEL event, cancel any current WindowInsetsAnimation
                simpleController.cancel()
                // Reset our touch handling state
                reset()
            }
        }

        return false
    }

    /**
     * Resets all of our internal state.
     */
    private fun reset() {
        // Clear all of our internal state
        isHandling = false
        lastTouchX = 0f
        lastTouchY = 0f
        lastWindowY = 0
        bounds.setEmpty()

        velocityTracker?.recycle()
        velocityTracker = null
    }
}
