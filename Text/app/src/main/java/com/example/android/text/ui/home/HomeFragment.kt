/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.example.android.text.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.transition.Fade
import androidx.transition.Transition
import com.example.android.text.R
import com.example.android.text.databinding.HomeFragmentBinding
import com.example.android.text.demo.Demos
import com.example.android.text.ui.viewBindings

private const val TransitionDuration = 300L

/**
 * Shows the list of demos in this sample.
 */
class HomeFragment : Fragment(R.layout.home_fragment) {

    private val binding by viewBindings(HomeFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        exitTransition = createTransition(true)
        reenterTransition = createTransition(false)
        binding.demoList.adapter = DemoListAdapter(
            onDemoClick = { demo ->
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    addToBackStack(null)
                    replace(R.id.content, demo.fragment().apply {
                        enterTransition = createTransition(false)
                        returnTransition = createTransition(true)
                    })
                }
                activity?.title = demo.title
            }
        ).apply {
            submitList(Demos)
        }
    }

    private fun createTransition(isLeaving: Boolean): Transition {
        return Fade().apply {
            if (isLeaving) {
                duration = TransitionDuration / 3
                startDelay = 0L
                interpolator = FastOutLinearInInterpolator()
            } else {
                duration = TransitionDuration / 3 * 2
                startDelay = TransitionDuration / 3
                interpolator = LinearOutSlowInInterpolator()
            }
        }
    }
}
