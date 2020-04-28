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

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat

/**
 * A [LinearLayout] which acts as a [nested scroll parent][NestedScrollingParent3] to automatically
 * control the IME inset and visibility. This class tracks scrolling, overscrolling, and
 * flinging gestures on child scrolling views, such as a
 * [androidx.recyclerview.widget.RecyclerView].
 *
 * This class triggers a request to control the IME insets via
 * [SimpleImeAnimationController.startControlRequest] once it detect a scroll in an appropriate direction
 * to [onNestedPreScroll] and [onNestedScroll]. Once in control, the class will inset (move)
 * the IME in/off screen based on the user's scroll position, using
 * [SimpleImeAnimationController.insetBy].
 *
 * The class supports both animating the IME onto screen (from not visible), and animating it
 * off-screen (from visible). This can be customize through the [scrollImeOnScreenWhenNotVisible]
 * and [scrollImeOffScreenWhenVisible] properties.
 *
 * Note: all of the nested scrolling logic could be extracted to a `CoordinatorLayout.Behavior`
 * if desired.
 */
class InsetsAnimationLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {

    private val nestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private var currentNestedScrollingChild: View? = null

    private val imeAnimController = SimpleImeAnimationController()

    private var dropNextY = 0
    private val startViewLocation = IntArray(2)

    /**
     * Set to true to allow scrolling the IME off screen (from being visible),
     * by an downwards scroll. Defaults to `true`.
     */
    var scrollImeOffScreenWhenVisible = true

    /**
     * Set to true to allow scrolling the IME on screen (from not being visible),
     * by an upwards scroll. Defaults to `true`.
     */
    var scrollImeOnScreenWhenNotVisible = true

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        // We only want to track vertical scrolls, which are driven from a direct touch event
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && type == ViewCompat.TYPE_TOUCH
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type)
        currentNestedScrollingChild = child
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (imeAnimController.isInsetAnimationRequestPending()) {
            // We're waiting for a controller to become ready. Consume and no-op the scroll
            consumed[0] = dx
            consumed[1] = dy
            return
        }

        var deltaY = dy
        if (dropNextY != 0) {
            consumed[1] = dropNextY
            deltaY -= dropNextY
            dropNextY = 0
        }

        if (deltaY < 0) {
            // If the user is scrolling down...

            if (imeAnimController.isInsetAnimationInProgress()) {
                // If we currently have control, we can update the IME insets using insetBy().
                //
                // The negation on the deltaY and the return value is necessary since nested
                // scrolling uses dy values, from 0 (top) to infinity (bottom), meaning that
                // positive values indicate a downwards motion. IME insets are different, as they
                // treat values from from 0 (bottom) to IME-height (top). Since we're using
                // insetBy() with delta values, we can just pass in a simple negation and let it
                // handle the min/max positions.
                consumed[1] -= imeAnimController.insetBy(-deltaY)
            } else if (scrollImeOffScreenWhenVisible &&
                !imeAnimController.isInsetAnimationRequestPending() &&
                rootWindowInsets.isVisible(WindowInsets.Type.ime())) {
                // If we're not in control, the IME is currently open, and,
                // 'scroll IME away when visible' is enabled, we start a control request
                startControlRequest()

                // We consume the scroll to stop the list scrolling while we wait for a controller
                consumed[1] = deltaY
            }
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (dyUnconsumed > 0) {
            // If the user is scrolling up, and the scrolling view isn't consuming the scroll...

            if (imeAnimController.isInsetAnimationInProgress()) {
                // If we currently have control, we can update the IME insets
                consumed[1] = -imeAnimController.insetBy(-dyUnconsumed)
            } else if (scrollImeOnScreenWhenNotVisible &&
                !imeAnimController.isInsetAnimationRequestPending() &&
                !rootWindowInsets.isVisible(WindowInsets.Type.ime())) {
                // If we don't currently have control, the IME is not shown,
                // the user is scrolling up, and the view can't scroll up any more
                // (i.e. over-scrolling), we can start to control the IME insets
                startControlRequest()

                // We consume the scroll to stop the list scrolling while we wait for a controller
                consumed[1] = dyUnconsumed
            }
        }
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        if (imeAnimController.isInsetAnimationInProgress()) {
            // If we have an IME animation in progress, from the user scrolling, we can
            // animate to the end state using the velocity
            imeAnimController.animateToFinish(velocityY)
            // Indicate that we reacted to the fling
            return true
        } else {
            // Otherwise we may need to start a control request and immediately fling
            // using the velocityY
            val imeVisible = rootWindowInsets.isVisible(WindowInsets.Type.ime())
            when {
                velocityY > 0 && scrollImeOnScreenWhenNotVisible && !imeVisible -> {
                    // If the fling is in a upwards direction, and the IME is not visible,
                    // start an control request with an immediate fling
                    imeAnimController.startAndFling(this, velocityY)
                    // Indicate that we reacted to the fling
                    return true
                }
                velocityY < 0 && scrollImeOffScreenWhenVisible && imeVisible -> {
                    // If the fling is in a downwards direction, and the IME is visible,
                    // start an control request with an immediate fling
                    imeAnimController.startAndFling(this, velocityY)
                    // Indicate that we reacted to the fling
                    return true
                }
            }
        }

        // Otherwise, return false to indicate that we did not
        // react to the fling
        return false
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        nestedScrollingParentHelper.onStopNestedScroll(target, type)

        if (imeAnimController.isInsetAnimationInProgress() &&
            !imeAnimController.isInsetAnimationFinishing()) {
            imeAnimController.animateToFinish()
        }
        reset()
    }

    override fun dispatchWindowInsetsAnimationPrepare(animation: WindowInsetsAnimation) {
        super.dispatchWindowInsetsAnimationPrepare(animation)

        // We suppressed layout in startControlRequest(), so we need to un-suppress it now
        suppressLayout(false)
    }

    /**
     * This starts a control request.
     */
    private fun startControlRequest() {
        // Suppress layout, so that nothing interrupts or is re-laid out while the IME
        // animation starts. This needs to be done before controlWindowInsetsAnimation()
        suppressLayout(true)

        // Now record the current location of the nested scrolling view. This allows
        // us to track any changes in the location as the animation prepares and starts
        currentNestedScrollingChild?.getLocationInWindow(startViewLocation)

        // Now we can start the control request
        imeAnimController.startControlRequest(
            view = this,
            onRequestReady = { onControllerReady() }
        )
    }

    private fun onControllerReady() {
        val scrollingChild = currentNestedScrollingChild
        if (scrollingChild != null) {
            // Dispatch an IME insets update now, to trigger any WindowInsetsAnimation.Callbacks
            // in the hierarchy, allowing them to setup for the animation
            imeAnimController.insetBy(0)

            // Now calculate the difference in the view's Y in the window. We store that to
            // find the offset at the next nested scroll
            val location = tempIntArray2
            scrollingChild.getLocationInWindow(location)
            dropNextY = location[1] - startViewLocation[1]
        }
    }

    /**
     * Resets all of our internal state.
     */
    private fun reset() {
        // Clear all of our internal state
        dropNextY = 0
        startViewLocation.fill(0)
        // Just to make sure we do not suppress layout forever
        suppressLayout(false)
    }

    /**
     * Overrides of necessary nested scroll APIs.
     *
     * The nested scrolling APIs have had a number of revisions, with each revision extending the
     * previous revision. The latest,
     * [androidx.core.view.NestedScrollingParent3], extends
     * [androidx.core.view.NestedScrollingParent2], which extends
     * [androidx.core.view.NestedScrollingParent].
     *
     * These classes are all Java interfaces containing various method overloads, usually to add
     * extra parameters. AndroidX Core, the library which bundles the interfaces, is built with
     * JDK 7, meaning it can not use default methods to automatically proxy the methods from the
     * older revisions to the new versions. This means that we need to do that proxying
     * ourselves, as below.
     */

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            tempIntArray2
        )
    }

    override fun onStopNestedScroll(target: View) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH)
    }
}

private val tempIntArray2 = IntArray(2)
