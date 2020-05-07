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
import android.view.WindowInsets.Type
import android.view.WindowInsetsAnimation

/**
 * A [WindowInsetsAnimation.Callback] which will request and clear focus on the given view,
 * depending on the [WindowInsets.Type.ime] visibility state when an IME [WindowInsetsAnimation]
 * has finished.
 *
 * This is primarily used when animating the [WindowInsets.Type.ime], so that the appropriate view
 * is focused for accepting input from the IME.
 *
 * @param view the view to request/clear focus
 * @param dispatchMode The dispatch mode for this callback.
 * See [WindowInsetsAnimation.Callback.getDispatchMode].
 */
class ControlFocusInsetsAnimationCallback(
    private val view: View,
    dispatchMode: Int = DISPATCH_MODE_STOP
) : WindowInsetsAnimation.Callback(dispatchMode) {

    override fun onProgress(
        insets: WindowInsets,
        runningAnimations: List<WindowInsetsAnimation>
    ): WindowInsets {
        // no-op and return the insets
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimation) {
        if (animation.typeMask and Type.ime() != 0) {
            // The animation has now finished, so we can check the view's focus state.
            // We post the check because the rootWindowInsets has not yet been updated, but will
            // be in the next message traversal
            view.post {
                checkFocus()
            }
        }
    }

    private fun checkFocus() {
        val imeVisible = view.rootWindowInsets.isVisible(Type.ime())
        if (imeVisible && view.rootView.findFocus() == null) {
            // If the IME will be visible, and there is not a currently focused view in
            // the hierarchy, request focus on our view
            view.requestFocus()
        } else if (!imeVisible && view.isFocused) {
            // If the IME will not be visible and our view is currently focused, clear the focus
            view.clearFocus()
        }
    }
}
