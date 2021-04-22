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

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PREFS_MAIN = "main"
private const val PREF_NIGHT_MODE = "night_mode"

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // SharedPreferences for saving theme settings. Consider using
    // [DataStore][https://developer.android.com/topic/libraries/architecture/datastore] or
    // [Room][https://developer.android.com/training/data-storage/room] in real-life apps.
    private var _prefs: SharedPreferences? = null

    /**
     * The current night mode setting saved and persisted by the system.
     */
    val nightMode: LiveData<Int>
        get() = _nightMode
    private val _nightMode = MutableLiveData<Int>()

    /**
     * True when the initial data in ViewModel is ready, and the app is ready to draw its main
     * content.
     */
    var isReady = false
        private set

    init {
        viewModelScope.launch {
            val nightMode = withContext(Dispatchers.IO) {
                val prefs = application
                    .getSharedPreferences(PREFS_MAIN, Context.MODE_PRIVATE).also {
                        _prefs = it
                    }
                prefs.getInt(PREF_NIGHT_MODE, UiModeManager.MODE_NIGHT_AUTO)
            }
            _nightMode.value = nightMode

            // We have finished loading the initial essential data for showing the app content.
            isReady = true
        }
    }

    fun updateNightMode(nightMode: Int) {
        val prefs = _prefs ?: return
        val uiModeManager =
            getApplication<Application>().getSystemService(UiModeManager::class.java)
        // Sets and persists the night mode setting for this app. This allows the system to know
        // if the app wants to be displayed in dark mode before it launches so that the splash
        // screen can be displayed accordingly.
        // You don't need to do this if your app doesn't provide in-app dark mode setting.
        uiModeManager.setApplicationNightMode(nightMode)
        prefs.edit { putInt(PREF_NIGHT_MODE, nightMode) }
        _nightMode.value = nightMode
    }
}
