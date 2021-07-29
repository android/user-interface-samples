/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.example.android.splashscreen

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.window.SplashScreenView
import androidx.core.animation.doOnEnd
import java.time.Duration
import java.time.Instant

/**
 * "Custom Splash Screen". This is similar to [AnimatedActivity], but also has a custom animation
 * for the splash screen exit.
 */
class CustomActivity : MainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback is called when the app is ready to draw its content and replace the splash
        // screen. We can customize the exit animation of the splash screen here.
        splashScreen.setOnExitAnimationListener { splashScreenView ->

            // The animated vector drawable is already animating at this point. Depending on the
            // duration of the app launch, the animation might not have finished yet.
            // Check the extension property to see how to calculate the remaining duration of the
            // icon animation.
            val remainingDuration = splashScreenView.iconAnimationRemainingDurationMillis

            // The callback gives us a `SplashScreenView` as its parameter. This is the view for the
            // entire splash screen.
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 200L

            // Make sure to call SplashScreenView.remove at the end of your custom animation.
            slideUp.doOnEnd { splashScreenView.remove() }

            // For the purpose of the demo, we wait for the icon animation to finish. Your app
            // should prioritize showing app content as soon as possible.
            slideUp.startDelay = remainingDuration
            slideUp.start()
        }
    }
}

/**
 * Calculates the remaining duration of the icon animation based on the total duration
 * ([SplashScreenView.getIconAnimationDuration]) and the start time
 * ([SplashScreenView.getIconAnimationStart])
 */
private val SplashScreenView.iconAnimationRemainingDurationMillis: Long
    get() {
        val duration = iconAnimationDuration
        val start = iconAnimationStart
        return if (duration != null && start != null) {
            (duration - Duration.between(start, Instant.now()))
                .toMillis()
                .coerceAtLeast(0L)
        } else {
            0L
        }
    }
