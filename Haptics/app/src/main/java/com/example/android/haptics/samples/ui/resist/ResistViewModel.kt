/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.haptics.samples.ui.resist

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.haptics.samples.R

/**
 * ViewModel that handles state logic for Resist route.
 */
class ResistViewModel(
    val messageToUser: String,
    val isLowTickSupported: Boolean,
) : ViewModel() {

    /**
     * Factory for ResistViewModel.
     */
    companion object {
        fun provideFactory(
            application: Application,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val vibrator = application.getSystemService(Vibrator::class.java)

                val isTickSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && vibrator.areAllPrimitivesSupported(
                    VibrationEffect.Composition.PRIMITIVE_TICK
                )
                val isLowTickSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && vibrator.areAllPrimitivesSupported(
                    VibrationEffect.Composition.PRIMITIVE_LOW_TICK
                )

                // The message to display to user if we detect designed experience will
                // not work at all or will be degraded.
                var messageToUser = ""
                if (!isTickSupported && !isLowTickSupported) {
                    messageToUser = application.getString(R.string.message_not_supported)
                } else if (!isLowTickSupported) {
                    messageToUser = application.getString(R.string.message_degraded_experience)
                }

                return ResistViewModel(
                    messageToUser = messageToUser,
                    isLowTickSupported = isLowTickSupported
                ) as T
            }
        }
    }
}
