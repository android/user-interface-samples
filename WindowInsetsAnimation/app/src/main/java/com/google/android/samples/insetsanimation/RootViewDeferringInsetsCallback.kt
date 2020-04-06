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

import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation

/**
 * A class which extends/implements both [WindowInsetsAnimation.Callback] and
 * [View.OnApplyWindowInsetsListener], which should be set on the root view in your layout.
 *
 * This class enables the root view is selectively defer handling any insets which match
 * [deferredInsetTypes], to enable better looking [WindowInsetsAnimation]s.
 *
 * An example is the following: when a [WindowInsetsAnimation] is started, the system will dispatch
 * a [WindowInsets] instance which contains the end state of the animation. For the scenario of
 * the IME being animated in, that means that the insets contains the IME height. If the view's
 * [View.OnApplyWindowInsetsListener] simply always applied the combination of
 * [WindowInsets.Type.ime] and [WindowInsets.Type.systemBars] using padding, the viewport of any
 * child views would then be smaller. This results in us animating a smaller (padded-in) view into
 * a larger viewport. Visually, this results in the views looking clipped.
 *
 * This class allows us to implement a different strategy for the above scenario, by selectively
 * deferring the [WindowInsets.Type.ime] insets until the [WindowInsetsAnimation] is ended.
 * For the above example, you would create a [RootViewDeferringInsetsCallback] like so:
 *
 * ```
 * val callback = RootViewDeferringInsetsCallback(
 *     persistentInsetTypes = WindowInsets.Type.systemBars(),
 *     deferredInsetTypes = WindowInsets.Type.ime()
 * )
 * ```
 *
 * This class is not limited to just IME animations, and can work with any [WindowInsets.Type]s.
 *
 * @param persistentInsetTypes the bitmask of any inset types which should always be handled
 * through padding the attached view
 * @param deferredInsetTypes the bitmask of insets types which should be deferred until after
 * any related [WindowInsetsAnimation]s have ended
 */
class RootViewDeferringInsetsCallback(
    val persistentInsetTypes: Int,
    val deferredInsetTypes: Int
) : WindowInsetsAnimation.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE),
    View.OnApplyWindowInsetsListener {
    init {
        require(persistentInsetTypes and deferredInsetTypes == 0) {
            "persistentInsetTypes and deferredInsetTypes can not contain any of " +
                    " same WindowInsets.Type values"
        }
    }

    private var view: View? = null
    private var lastWindowInsets: WindowInsets? = null

    private var deferredInsets = false

    override fun onApplyWindowInsets(v: View, windowInsets: WindowInsets): WindowInsets {
        // Store the view and insets for us in onEnd() below
        view = v
        lastWindowInsets = windowInsets

        val types = when {
            // When the deferred flag is enabled, we only use the systemBars() insets
            deferredInsets -> persistentInsetTypes
            // Otherwise we handle the combination of the the systemBars() and ime() insets
            else -> persistentInsetTypes or deferredInsetTypes
        }

        // Finally we apply the resolved insets by setting them as padding
        val typeInsets = windowInsets.getInsets(types)
        v.setPadding(typeInsets.left, typeInsets.top, typeInsets.right, typeInsets.bottom)

        // We return the new WindowInsets.CONSUMED to stop the insets being dispatched any
        // further into the view hierarchy. This replaces the deprecated
        // WindowInsets.consumeSystemWindowInsets() and related functions.
        return WindowInsets.CONSUMED
    }

    override fun onPrepare(animation: WindowInsetsAnimation) {
        if (animation.typeMask and deferredInsetTypes != 0) {
            // We defer the WindowInsets.Type.ime() insets if the IME is currently not visible.
            // This results in only the WindowInsets.Type.systemBars() being applied, allowing
            // the scrolling view to remain at it's larger size.
            deferredInsets = true
        }
    }

    override fun onProgress(
        insets: WindowInsets,
        runningAnims: List<WindowInsetsAnimation>
    ): WindowInsets {
        // This is a no-op. We don't actually want to handle any WindowInsetsAnimations
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimation) {
        if (deferredInsets && (animation.typeMask and deferredInsetTypes) != 0) {
            // If we deferred the IME insets and an IME animation has finished, we need to reset
            // the flag
            deferredInsets = false

            // And finally dispatch the deferred insets to the view now.
            // Ideally we would just call view.requestApplyInsets() and let the normal dispatch
            // cycle happen, but this happens too late resulting in a visual flicker.
            // Instead we manually dispatch the most recent WindowInsets to the view.
            if (lastWindowInsets != null) {
                view?.dispatchApplyWindowInsets(lastWindowInsets!!)
            }
        }
    }
}
