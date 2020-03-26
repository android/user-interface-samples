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

import android.graphics.Insets
import android.graphics.Rect
import android.os.CancellationSignal
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowInsets
import android.view.WindowInsetsAnimationControlListener
import android.view.WindowInsetsAnimationController
import android.view.animation.LinearInterpolator
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * A [View.OnTouchListener] which we can set on a scrolling view, to control the IME inset
 * and visibility. When set on a scrolling view (such as a [RecyclerView]), it will track scrolling
 * gestures, and trigger a request to control the IME insets via the
 * [android.view.WindowInsetsController.controlWindowInsetsAnimation] function once the user
 * is over-scrolling the view.
 *
 * Once in control, the listener will inset the IME in/off screen based on the user's scroll
 * position.
 */
internal class InsetsAnimationOverscrollingTouchListener() : View.OnTouchListener {
    private var insetsAnimationController: WindowInsetsAnimationController? = null
    private var currentControlRequest: WindowInsetsAnimationControlListener? = null

    private var startImeInsets = Insets.NONE
    private var isImeShownAtStart = false

    private var isHandling = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var lastWindowY = 0

    private val bounds = Rect()

    private var cancellationSignal: CancellationSignal? = null

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
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

                val dx = event.x - lastTouchX
                val dy = event.y - lastTouchY + windowOffsetY

                if (!isHandling) {
                    // If we're not currently handling the touch gesture, lets check if we should
                    // start handling, by seeing if the gesture is majorly vertical, and
                    // larger than the touch slop
                    isHandling = dy.absoluteValue > dx.absoluteValue &&
                            dy.absoluteValue >= ViewConfiguration.get(v.context).scaledTouchSlop
                }

                if (isHandling) {
                    if (currentControlRequest != null) {
                        // If we currently have control, we can update the IME insets to 'scroll'
                        // the IME in
                        updateImeInsets(dy)
                    } else if (!isImeShownAtStart && dy < 0 && v.canScrollVertically(-1)) {
                        // If we don't currently have control, the IME is not shown,
                        // the user is scrolling up, and the view can't scroll up any more
                        // (i.e. over-scrolling), we can start to control the IME insets
                        startControlRequest(v)
                    }

                    // Lastly we record the event X, Y, and view's Y window position, for the
                    // next touch event
                    lastTouchY = event.y
                    lastTouchX = event.x
                    lastWindowY = bounds.top
                }
            }
            MotionEvent.ACTION_UP -> {
                // If we received a ACTION_UP event, end any current WindowInsetsAnimation
                finish()
            }
            MotionEvent.ACTION_CANCEL -> {
                // If we received a ACTION_CANCEL event, cancel any current WindowInsetsAnimation
                cancel()
            }
        }

        return false
    }

    /**
     * This function updates the IME insets with the given dy value.
     */
    private fun updateImeInsets(dy: Float) {
        val controller = insetsAnimationController ?: return

        val hiddenBottom = controller.hiddenStateInsets.bottom
        val shownBottom = controller.shownStateInsets.bottom

        val startBottom = if (isImeShownAtStart) shownBottom else hiddenBottom
        val endBottom = if (isImeShownAtStart) hiddenBottom else shownBottom

        // Here we calculate the new bottom inset, using the start and current values, and
        // appending the scroll dy, and then coercing it within the limits.
        val insetBottom = (startImeInsets.bottom + controller.currentInsets.bottom - dy)
            .roundToInt()
            .coerceIn(startBottom, endBottom)

        // Finally update the insets in the WindowInsetsAnimationController using
        // setInsetsAndAlpha().
        controller.setInsetsAndAlpha(
            // Here we update the animating insets. This is passed through to views via their
            // WindowInsetsAnimation.Callback.
            Insets.of(0, 0, 0, insetBottom),
            // This controls the alpha value. We don't want to alter the alpha so use 1f
            1f,
            // Finally we calculate the animation progress fraction. This is the value which
            // controls how much the IME is inset by (aka how much of the IME is visible).
            (insetBottom - startBottom) / (endBottom - startBottom).toFloat()
        )
    }

    /**
     * This starts a control request.
     */
    private fun startControlRequest(v: View) {
        // Keep track of the IME insets, and the IME's visibility, at the start of the request
        startImeInsets = v.rootWindowInsets.getInsets(WindowInsets.Type.ime())
        isImeShownAtStart = v.rootWindowInsets.isVisible(WindowInsets.Type.ime())

        // To take control of the an WindowInsetsAnimation, we need to pass in a listener to
        // controlWindowInsetsAnimation() below. This listener keeps track of the current request,
        // and stores the current WindowInsetsAnimationController
        val listener = object : WindowInsetsAnimationControlListener {
            override fun onReady(controller: WindowInsetsAnimationController, types: Int) {
                if (currentControlRequest == this) {
                    onRequestReady(controller)
                } else {
                    finish()
                }
            }

            /**
             * If the request is cancelled, we should reset our internal state
             */
            override fun onCancelled() = reset()
        }

        // Finally we make a controlWindowInsetsAnimation() request:
        cancellationSignal = v.windowInsetsController?.controlWindowInsetsAnimation(
            // We're only catering for IME animations in this listener
            WindowInsets.Type.ime(),
            // Animation duration. This is not used by the system, and is only passed to any
            // WindowInsetsAnimation.Callback set on views. We pass in -1 to indicate that we're
            // not starting a finite animation, and that this is completely controlled by
            // the user's touch.
            -1,
            // The time interpolator used in calculating the animation progress. The fraction value
            // we passed into setInsetsAndAlpha() which be passed into this interpolator before
            // being used by the system to inset the IME. LinearInterpolator is a good type
            // to use for scrolling gestures.
            linearInterpolator,
            // The WindowInsetsAnimationControlListener we created above
            listener
        )

        currentControlRequest = listener
    }

    private fun onRequestReady(controller: WindowInsetsAnimationController) {
        insetsAnimationController = controller
    }

    /**
     * Cancel the current [WindowInsetsAnimationController]. We finish the animation, reverting
     * back to the state at the start of the gesture.
     */
    private fun cancel() {
        insetsAnimationController?.finish(isImeShownAtStart)
        cancellationSignal?.cancel()
        reset()
    }

    /**
     * Finish the current [WindowInsetsAnimationController]. We finish the animation, toggling
     * the IME's visibility (to the end state) if the user has scrolled more than 50%.
     */
    private fun finish() {
        insetsAnimationController?.run {
            finish(if (currentFraction >= 0.5f) !isImeShownAtStart else isImeShownAtStart)
        }
        cancellationSignal?.cancel()
        reset()
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

        insetsAnimationController = null
        currentControlRequest = null
        startImeInsets = Insets.NONE
        isImeShownAtStart = false
    }
}

/**
 * A LinearInterpolator instance we can re-use across listeners.
 */
private val linearInterpolator = LinearInterpolator()
