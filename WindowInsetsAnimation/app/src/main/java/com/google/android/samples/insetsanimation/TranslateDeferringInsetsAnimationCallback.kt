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
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation

/**
 * A [WindowInsetsAnimation.Callback] which will translate/move the given view during any
 * inset animations of the given inset type.
 *
 * This class works in tandem with [RootViewDeferringInsetsCallback] to support the deferring of
 * certain [WindowInsets.Type] values during a [WindowInsetsAnimation], provided in
 * [deferredInsetTypes]. The values passed into this constructor should match those which
 * the [RootViewDeferringInsetsCallback] is created with.
 *
 * @param view the view to translate from it's start to end state
 * @param persistentInsetTypes the bitmask of any inset types which were handled as part of the
 * layout
 * @param deferredInsetTypes the bitmask of insets types which should be deferred until after
 * any [WindowInsetsAnimation]s have ended
 * @param dispatchMode The dispatch mode for this callback.
 * See [WindowInsetsAnimation.Callback.getDispatchMode].
 */
class TranslateDeferringInsetsAnimationCallback(
    private val view: View,
    val persistentInsetTypes: Int,
    val deferredInsetTypes: Int,
    dispatchMode: Int = DISPATCH_MODE_STOP
) : WindowInsetsAnimation.Callback(dispatchMode) {
    init {
        require(persistentInsetTypes and deferredInsetTypes == 0) {
            "persistentInsetTypes and deferredInsetTypes can not contain any of " +
                    " same WindowInsets.Type values"
        }
    }

    override fun onProgress(
        insets: WindowInsets,
        runningAnimations: List<WindowInsetsAnimation>
    ): WindowInsets {
        // onProgress() is called when any of the running animations progress...

        // First we get the insets which are potentially deferred
        val typesInset = insets.getInsets(deferredInsetTypes)
        // Then we get the persistent inset types which are applied as padding during layout
        val otherInset = insets.getInsets(persistentInsetTypes)

        // Now that we subtract the two insets, to calculate the difference. We also coerce
        // the insets to be >= 0, to make sure we don't use negative insets.
        val diff = Insets.subtract(typesInset, otherInset).let {
            Insets.max(it, Insets.NONE)
        }

        // The resulting `diff` insets contain the values for us to apply as a translation
        // to the view
        view.translationX = (diff.left - diff.right).toFloat()
        view.translationY = (diff.top - diff.bottom).toFloat()

        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimation) {
        // Once the animation has ended, reset the translation values
        view.translationX = 0f
        view.translationY = 0f
    }
}
