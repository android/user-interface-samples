/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.views_app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.views_app.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PREFERENCE_NAME = "shared_preference"
        const val PREFERENCE_MODE = Context.MODE_PRIVATE

        const val FIRST_TIME_MIGRATION = "first_time_migration"
        const val SELECTED_LANGUAGE = "selected_language"

        const val STATUS_DONE = "status_done"
    }

    /**
     * This is a sample code that explains the use of getter and setter APIs for Locales introduced
     * in the Per-App language preferences. Here is an example use of the AndroidX Support Library
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /* NOTE: If you were handling the locale storage on you own earlier, you will need to add a
        one time migration for switching this storage from a custom way to the AndroidX storage.

        This can be done in the following manner. Lets say earlier the locale preference was
        stored in a SharedPreference */

        // Check if the migration has already been done or not
        if (getString(FIRST_TIME_MIGRATION) != STATUS_DONE) {
            // Fetch the selected language from wherever it was stored. In this case its SharedPref
            getString(SELECTED_LANGUAGE)?.let {
                // Set this locale using the AndroidX library that will handle the storage itself
                val localeList = LocaleListCompat.forLanguageTags(it)
                AppCompatDelegate.setApplicationLocales(localeList)
                // Set the migration flag to ensure that this is executed only once
                putString(FIRST_TIME_MIGRATION, STATUS_DONE)
            }
        }

        // Fetching the current application locale using the AndroidX support Library
        val currentLocaleName = if (!AppCompatDelegate.getApplicationLocales().isEmpty) {
            // Fetches the current Application Locale from the list
            AppCompatDelegate.getApplicationLocales()[0]?.displayName
        } else {
            // Fetches the default System Locale
            Locale.getDefault().displayName
        }

        // Displaying the selected locale on screen
        binding.tvSelectedLanguage.text = currentLocaleName

        // Setting app language to "English" in-app using the AndroidX support library
        binding.btnSelectEnglish.setOnClickListener {
            val localeList = LocaleListCompat.forLanguageTags("en")
            AppCompatDelegate.setApplicationLocales(localeList)
        }

        // Setting app language to "Hindi" in-app using the AndroidX support library
        binding.btnSelectHindi.setOnClickListener {
            val localeList = LocaleListCompat.forLanguageTags("hi")
            AppCompatDelegate.setApplicationLocales(localeList)
        }

        // Setting app language to "Arabic" in-app using the AndroidX support Library
        // NOTE: Here the screen orientation is reversed to RTL
        binding.btnSelectArabic.setOnClickListener {
            val localeList = LocaleListCompat.forLanguageTags("ar")
            AppCompatDelegate.setApplicationLocales(localeList)
        }

        // Setting app language to "Japanese" in-app using the AndroidX support library
        binding.btnSelectJapanese.setOnClickListener {
            val localeList = LocaleListCompat.forLanguageTags("ja")
            AppCompatDelegate.setApplicationLocales(localeList)
        }

        // Setting app language to "Spanish" in-app using the AndroidX support library
        binding.btnSelectSpanish.setOnClickListener {
            val localeList = LocaleListCompat.forLanguageTags("es")
            AppCompatDelegate.setApplicationLocales(localeList)
        }

        // Setting app language to Traditional Chinese in-app using the AndroidX support Library
        binding.btnSelectXxYy.setOnClickListener {
            val localeList = LocaleListCompat.forLanguageTags("zh-Hant")
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }

    private fun putString(key: String, value: String) {
        val editor = getSharedPreferences(PREFERENCE_NAME, PREFERENCE_MODE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun getString(key: String): String? {
        val preference = getSharedPreferences(PREFERENCE_NAME, PREFERENCE_MODE)
        return preference.getString(key, null)
    }
}
