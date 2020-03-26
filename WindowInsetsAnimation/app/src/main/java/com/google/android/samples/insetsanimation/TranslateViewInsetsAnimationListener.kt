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
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsAnimation

/**
 * A [WindowInsetsAnimation.Callback] which will translate the given view during any
 * inset animations of the given inset type.
 *
 * It does this by making a copy of the views bounds before the animation has start, and then as
 * the animation starts. As the animation progress, the listener updates the view's
 * translate properties so that visually the view is moving from the start to the end state.
 *
 * It does not however handle any changes in the view changing size.
 *
 * @param view the view to translate from it's start to end state
 * @param typeMask the bitmask of animation types to cater for
 */
internal class TranslateViewInsetsAnimationListener(
    private val view: View,
    private val typeMask: Int
) : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
    private val startBounds = Rect()
    private val endBounds = Rect()

    override fun onPrepare(animation: WindowInsetsAnimation) {
        // onPrepare() is called before the insets animation is about the start, AND before
        // the window has been laid out in the new state. We take a copy of the views bounds
        // and location in the window for use later.
        view.copyBoundsInWindow(startBounds)
    }

    override fun onStart(
        animation: WindowInsetsAnimation,
        bounds: WindowInsetsAnimation.Bounds
    ): WindowInsetsAnimation.Bounds {
        // onStart() is called at the start of the insets animation, and AFTER the window has been
        // laid out in the new state...

        // Similar to above, we take a copy of the views bounds in the end state.
        view.copyBoundsInWindow(endBounds)

        // For certain animations, like IME closed -> open, the view in the end state will be
        // smaller than the start state. This means we'll be moving around a smaller view on screen,
        // which can leave gaps. To combat this, we turn off clipping on all of our parent
        // ViewGroups, so that the view can draw outside of its bounds.
        // We store the current values for each property so that they can be stored later.
        view.forEachAncestor { parent ->
            if (parent is ViewGroup) {
                parent.storeClipChildren()
                parent.clipChildren = false

                parent.storeClipToPadding()
                parent.clipToPadding = false
            }
        }

        // Finally we translate the view back to where it was at the start of the animation.
        view.translationX = (startBounds.right - endBounds.right).toFloat()
        view.translationY = (startBounds.bottom - endBounds.bottom).toFloat()

        return bounds
    }

    override fun onProgress(
        insets: WindowInsets,
        runningAnimations: List<WindowInsetsAnimation>
    ): WindowInsets {
        // onProgress() is called when any of the running animations progress...

        // This listener is only concerned with animations which match our typeMask, so we find
        // the first anim
        // TODO: ideally we would look at all animations, not just the first in the list
        val filteredAnim = runningAnimations.firstOrNull { anim ->
            (anim.typeMask and typeMask) != 0
        }

        if (filteredAnim != null) {
            // If we have an animation which matches our typeMask, use its interpolatedFraction
            // to update our view's translation properties

            view.translationX = lerp(
                // Start value, which translates the view back to the same X position as
                // before the animation
                startBounds.right - endBounds.right,
                // End value, which is 0 to remove the translation
                0,
                filteredAnim.interpolatedFraction
            )

            view.translationY = lerp(
                // Start value, which translates the view back to the same Y position as
                // before the animation
                startBounds.bottom - endBounds.bottom,
                // End value, which is 0 to remove the translation
                0,
                filteredAnim.interpolatedFraction
            )
        }
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimation) {
        // onEnd() is called when an animation has ended...

        // Since there can be many animations running at once, and of different types, we check
        // the animation type to make sure it matches our typeMask
        if ((animation.typeMask and typeMask) != 0) {

            // Since we turned off clipping on all of our parent ViewGroups, we should restore
            // the previously values
            view.forEachAncestor { parent ->
                if (parent is ViewGroup) {
                    parent.restoreClipChildren()
                    parent.restoreClipToPadding()
                }
            }

            // Reset the views translate properties
            view.translationX = 0f
            view.translationY = 0f

            // ...and clear our internal state
            startBounds.setEmpty()
            endBounds.setEmpty()
        }
    }
}
