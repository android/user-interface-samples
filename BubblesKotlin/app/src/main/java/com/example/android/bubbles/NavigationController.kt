/*
 * Copyright (C) 2019 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bubbles

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment

/**
 * Common interface between [MainActivity] and [BubbleActivity].
 */
interface NavigationController {

    fun openChat(id: Long)

    fun openPhoto(@DrawableRes photo: Int)

    /**
     * Updates the appearance and functionality of the app bar.
     *
     * @param showContact Whether to show contact information instead the screen title.
     * @param hidden Whether to hide the app bar.
     * @param body Provide this function to update the content of the app bar.
     */
    fun updateAppBar(
        showContact: Boolean = true,
        hidden: Boolean = false,
        body: (name: TextView, icon: ImageView) -> Unit = { _, _ -> }
    )
}

fun Fragment.getNavigationController() = requireActivity() as NavigationController
