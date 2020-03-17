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
import android.view.ViewParent
import kotlin.IllegalArgumentException

/**
 * Function which calls the given lambda [f] on each of this view's ancestors.
 */
inline fun View.forEachAncestor(f: (ViewParent) -> Unit) {
    var parent = parent
    while (parent != null) {
        f(parent)
        parent = parent.parent
    }
}

private val tmpIntArr = IntArray(2)

/**
 * Function which updates the given [rect] with this view's position and bounds in its window.
 */
fun View.copyBoundsInWindow(rect: Rect) {
    if (isLaidOut && isAttachedToWindow) {
        rect.set(0, 0, width, height)
        getLocationInWindow(tmpIntArr)
        rect.offset(tmpIntArr[0], tmpIntArr[1])
    } else {
        throw IllegalArgumentException("Can not copy bounds as view is not laid out" +
                " or attached to window")
    }
}

/**
 * Stores the current [ViewGroup.getClipToPadding] value in a tag.
 */
fun ViewGroup.storeClipToPadding() = setTag(R.id.viewgroup_clip_padding, clipToPadding)

/**
 * Restores the [ViewGroup.getClipToPadding] value previously stored by [storeClipChildren].
 */
fun ViewGroup.restoreClipToPadding() {
    val stored = getTag(R.id.viewgroup_clip_padding)
    if (stored is Boolean) {
        clipToPadding = stored
    }
    // Clear the stored value
    setTag(R.id.viewgroup_clip_padding, null)
}

/**
 * Stores the current [ViewGroup.getClipChildren] value in a tag.
 */
fun ViewGroup.storeClipChildren() = setTag(R.id.viewgroup_clip_children, clipChildren)

/**
 * Restores the [ViewGroup.getClipChildren] value previously stored by [storeClipChildren].
 */
fun ViewGroup.restoreClipChildren() {
    val stored = getTag(R.id.viewgroup_clip_children)
    if (stored is Boolean) {
        clipChildren = stored
    }
    // Clear the stored value
    setTag(R.id.viewgroup_clip_children, null)
}


/**
 * Simple linear interpolation function.
 */
fun lerp(startValue: Int, endValue: Int, fraction: Float): Float {
    return startValue + (endValue - startValue) * fraction
}
