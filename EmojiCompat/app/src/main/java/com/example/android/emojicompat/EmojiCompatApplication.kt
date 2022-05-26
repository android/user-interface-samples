/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.example.android.emojicompat

import android.app.Application
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.core.provider.FontRequest
import android.util.Log


/**
 * This application uses EmojiCompat.
 */
class EmojiCompatApplication : Application() {

    companion object {

        private val TAG = "EmojiCompatApplication"

        /** Change this to `false` when you want to use the downloadable Emoji font.  */
        private val USE_BUNDLED_EMOJI = true

    }

    override fun onCreate() {
        super.onCreate()

        val config: EmojiCompat.Config
        if (USE_BUNDLED_EMOJI) {
            // Use the bundled font for EmojiCompat
            config = BundledEmojiCompatConfig(applicationContext)
        } else {
            // Use a downloadable font for EmojiCompat
            val fontRequest = FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    "Noto Color Emoji Compat",
                    R.array.com_google_android_gms_fonts_certs)
            config = FontRequestEmojiCompatConfig(applicationContext, fontRequest)
                    .setReplaceAll(true)
                    .registerInitCallback(object : EmojiCompat.InitCallback() {
                        override fun onInitialized() {
                            Log.i(TAG, "EmojiCompat initialized")
                        }

                        override fun onFailed(throwable: Throwable?) {
                            Log.e(TAG, "EmojiCompat initialization failed", throwable)
                        }
                    })
        }
        EmojiCompat.init(config)
    }

}
