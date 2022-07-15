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
package com.example.android.haptics.samples.ui.expand

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.haptics.samples.R

/**
 * ViewModel that handles state logic for Expand route.
 */
class ExpandViewModel(
    val messageToUser: String,
) : ViewModel() {

    /**
     * Factory for ExpandViewModel.
     */
    companion object {

        fun provideFactory(
            application: Application,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                var messageToUser = ""
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || !vibrator.areAllPrimitivesSupported(
                        VibrationEffect.Composition.PRIMITIVE_SLOW_RISE,
                        VibrationEffect.Composition.PRIMITIVE_QUICK_FALL,
                        VibrationEffect.Composition.PRIMITIVE_TICK
                    )
                ) {
                    messageToUser = application.getString(R.string.message_not_supported)
                }

                return ExpandViewModel(
                    messageToUser = messageToUser,
                ) as T
            }
        }
    }
}
