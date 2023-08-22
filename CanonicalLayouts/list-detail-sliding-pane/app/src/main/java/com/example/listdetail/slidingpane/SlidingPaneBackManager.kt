/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.listdetail.slidingpane

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.slidingpanelayout.widget.SlidingPaneLayout

class SlidingPaneBackManager(private val slidingPaneLayout: SlidingPaneLayout) :
    OnBackPressedCallback(
        slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
    ), SlidingPaneLayout.PanelSlideListener {

    init {
        slidingPaneLayout.addPanelSlideListener(this)
        slidingPaneLayout.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateEnabledState()
        }
    }

    override fun handleOnBackPressed() {
        slidingPaneLayout.closePane()
    }

    private fun updateEnabledState() {
        // Only intercept the back button when the sliding pane layout is slideable
        // (in other words, only one of the two panes is visible) and when the sliding pane layout
        // is open (in other words, when the detail pane is open)
        isEnabled = slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
    }

    override fun onPanelSlide(panel: View, slideOffset: Float) {}

    override fun onPanelOpened(panel: View) {
        updateEnabledState()
    }

    override fun onPanelClosed(panel: View) {
        updateEnabledState()
    }
}