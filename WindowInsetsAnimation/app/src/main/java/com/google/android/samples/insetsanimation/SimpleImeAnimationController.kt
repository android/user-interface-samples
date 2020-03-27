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
import android.os.CancellationSignal
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimationControlListener
import android.view.WindowInsetsAnimationController
import android.view.animation.LinearInterpolator
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.dynamicanimation.animation.springAnimationOf
import androidx.dynamicanimation.animation.withSpringForceProperties
import kotlin.math.roundToInt

/**
 * A wrapper around the new [WindowInsetsAnimationController] APIs in Android 11, to simplify
 * the usage for common use-cases. See [InsetsAnimationOverscrollingTouchListener] for an example
 * of how to use this.
 *
 * @see InsetsAnimationOverscrollingTouchListener
 */
class SimpleImeAnimationController {
    private var insetsAnimationController: WindowInsetsAnimationController? = null
    private var pendingRequestCancellationSignal: CancellationSignal? = null

    /* To take control of the an WindowInsetsAnimation, we need to pass in a listener to
       controlWindowInsetsAnimation() in startControlRequest(). The listener created here
       keeps track of the current WindowInsetsAnimationController and resets our state. */
    private val animationControlListener: WindowInsetsAnimationControlListener by lazy {
        object : WindowInsetsAnimationControlListener {
            /**
             * Once the request is ready, call our [onRequestReady] function
             */
            override fun onReady(
                controller: WindowInsetsAnimationController,
                types: Int
            ) = onRequestReady(controller)

            /**
             * If the request is finished, we should reset our internal state
             */
            override fun onFinished(controller: WindowInsetsAnimationController) = reset()

            /**
             * If the request is cancelled, we should reset our internal state
             */
            override fun onCancelled(controller: WindowInsetsAnimationController?) = reset()
        }
    }

    /**
     * True if the IME was shown at the start of the current animation.
     */
    var isImeShownAtStart = false
        private set

    private var currentSpringAnimation: SpringAnimation? = null

    /**
     * Start a control request to the [view]s [android.view.WindowInsetsController]. This should
     * be called once the view is in a position to take control over the position of the IME.
     */
    fun startControlRequest(view: View) {
        check(!isInsetAnimationInProgress()) {
            "Animation in progress. Can not start a new request to controlWindowInsetsAnimation()"
        }

        // Keep track of the IME insets, and the IME visibility, at the start of the request
        isImeShownAtStart = view.rootWindowInsets.isVisible(WindowInsets.Type.ime())

        // Create a cancellation signal, which we pass to controlWindowInsetsAnimation() below
        pendingRequestCancellationSignal = CancellationSignal()

        // Finally we make a controlWindowInsetsAnimation() request:
        view.windowInsetsController?.controlWindowInsetsAnimation(
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
            // A cancellation signal, which allows us to cancel the request to control
            pendingRequestCancellationSignal,
            // The WindowInsetsAnimationControlListener
            animationControlListener
        )
    }

    /**
     * Update the inset position of the IME by the given [dy] value. This value will be coerced
     * into the hidden and shown inset values.
     *
     * This function should only be called if [isInsetAnimationInProgress] returns true.
     */
    fun updateInsetBy(dy: Int) {
        val controller = insetsAnimationController
            ?: throw IllegalStateException("Current WindowInsetsAnimationController is null." +
                    "This should only be called if isAnimationInProgress() returns true")

        // Call updateInsetTo() with the new inset value
        updateInsetTo(controller.currentInsets.bottom - dy)
    }

    /**
     * Update the inset position of the IME to be the given [inset] value. This value will be
     * coerced into the hidden and shown inset values.
     *
     * This function should only be called if [isInsetAnimationInProgress] returns true.
     */
    fun updateInsetTo(inset: Int) {
        val controller = insetsAnimationController
            ?: throw IllegalStateException("Current WindowInsetsAnimationController is null." +
                    "This should only be called if isAnimationInProgress() returns true")

        val hiddenBottom = controller.hiddenStateInsets.bottom
        val shownBottom = controller.shownStateInsets.bottom
        val startBottom = if (isImeShownAtStart) shownBottom else hiddenBottom
        val endBottom = if (isImeShownAtStart) hiddenBottom else shownBottom

        // We coerce the given inset within the limits of the hidden and shown insets
        val coercedBottom = inset.coerceIn(hiddenBottom, shownBottom)

        // Finally update the insets in the WindowInsetsAnimationController using
        // setInsetsAndAlpha().
        controller.setInsetsAndAlpha(
            // Here we update the animating insets. This is what controls where the IME is displayed.
            // It is also passed through to views via their WindowInsetsAnimation.Callback.
            Insets.of(0, 0, 0, coercedBottom),
            // This controls the alpha value. We don't want to alter the alpha so use 1f
            1f,
            // Finally we calculate the animation progress fraction. This value is passed through
            // to any WindowInsetsAnimation.Callbacks, but it is not used by the system.
            (coercedBottom - startBottom) / (endBottom - startBottom).toFloat()
        )
    }

    /**
     * Return [true] if an inset animation is in progress.
     */
    fun isInsetAnimationInProgress(): Boolean {
        return insetsAnimationController != null
    }

    /**
     * Return [true] if a request to control an inset animation is in progress.
     */
    fun isInsetAnimationRequestPending(): Boolean {
        return pendingRequestCancellationSignal != null
    }

    /**
     * Cancel the current [WindowInsetsAnimationController]. We immediately finish the animation,
     * reverting back to the state at the start of the gesture.
     */
    fun cancel() {
        insetsAnimationController?.finish(isImeShownAtStart)
        pendingRequestCancellationSignal?.cancel()

        // Cancel the current spring animation
        currentSpringAnimation?.cancel()

        reset()
    }

    /**
     * Finish the current [WindowInsetsAnimationController]. We finish the animation,
     * animating to the end state if necessary.
     *
     * @param velocityY the velocity of the touch gesture which caused this call to [finish].
     * Can be `null` if velocity is not available.
     */
    fun finish(velocityY: Float? = null) {
        val controller = insetsAnimationController
        if (controller != null) {
            val current = controller.currentInsets.bottom
            val shown = controller.shownStateInsets.bottom
            val hidden = controller.hiddenStateInsets.bottom

            when (current) {
                // The current inset matches either the shown/hidden inset, finish() immediately
                shown -> controller.finish(true)
                hidden -> controller.finish(false)
                else -> {
                    // If the current IME animation is part-way complete, we animate it to
                    // it's final state
                    val frac = controller.currentFraction
                    animateImeToVisibility(
                        if (frac >= SCROLL_THRESHOLD) !isImeShownAtStart else isImeShownAtStart,
                        velocityY
                    )
                }
            }
        } else {
            // Otherwise we cancel any pending request CancellationSignal
            pendingRequestCancellationSignal?.cancel()
        }
    }

    private fun onRequestReady(controller: WindowInsetsAnimationController) {
        // The request is ready, so clear out the pending cancellation signal
        pendingRequestCancellationSignal = null
        // Store the current WindowInsetsAnimationController
        insetsAnimationController = controller
    }

    /**
     * Resets all of our internal state.
     */
    private fun reset() {
        // Clear all of our internal state
        insetsAnimationController = null
        pendingRequestCancellationSignal = null

        isImeShownAtStart = false

        currentSpringAnimation?.cancel()
        currentSpringAnimation = null
    }

    /**
     * Animate the IME to a given visibility.
     *
     * @param visible `true` to animate the IME to it's fully shown state, `false` to it's
     * fully hidden state.
     * @param velocityY the velocity of the touch gesture which caused this call. Can be `null`
     * if velocity is not available.
     */
    private fun animateImeToVisibility(
        visible: Boolean,
        velocityY: Float?
    ) {
        val controller = insetsAnimationController
            ?: throw IllegalStateException("Controller should not be null")

        val end = when {
            visible -> controller.shownStateInsets.bottom
            else -> controller.hiddenStateInsets.bottom
        }

        val animator = springAnimationOf(
            setter = { updateInsetTo(it.roundToInt()) },
            getter = { controller.currentInsets.bottom.toFloat() },
            finalPosition = end.toFloat()
        ).withSpringForceProperties {
            // Tweak the damping value, to remove any bounciness.
            dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            // The stiffness value controls the strength of the spring animation, which
            // controls the speed. Medium (the default) is a good value, but feel free to
            // play around with this value.
            stiffness = SpringForce.STIFFNESS_MEDIUM
        }.apply {
            if (velocityY != null) {
                setStartVelocity(velocityY)
            }
        }
        animator.addEndListener { _, _, _, _ ->
            // Once the animation has ended, finish the controller
            finish()
        }
        animator.start()
        // Keep track of the Spring animation so we can cancel it if needed
        currentSpringAnimation = animator
    }
}

/**
 * Scroll threshold for determining whether to animating to the end state, or to the start state.
 */
private const val SCROLL_THRESHOLD = 0.2f

/**
 * A LinearInterpolator instance we can re-use across listeners.
 */
private val linearInterpolator = LinearInterpolator()
