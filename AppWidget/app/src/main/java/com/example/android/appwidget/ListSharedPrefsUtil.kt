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

package com.example.android.appwidget

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.LayoutRes
import androidx.core.content.edit

object ListSharedPrefsUtil {
    private const val PREFS_NAME = "com.example.android.appwidget.GroceryListWidget"
    private const val PREF_PREFIX_KEY = "appwidget_"

    internal fun saveWidgetLayoutIdPref(
        context: Context,
        appWidgetId: Int,
        @LayoutRes layoutId: Int
    ) {
        context.getSharedPreferences(name = PREFS_NAME, mode = 0).edit {
            putInt(PREF_PREFIX_KEY + appWidgetId, layoutId)
        }
    }

    internal fun loadWidgetLayoutIdPref(context: Context, appWidgetId: Int): Int =
        context.getSharedPreferences(name = PREFS_NAME, mode = 0)
            .getInt(PREF_PREFIX_KEY + appWidgetId, R.layout.widget_grocery_list)

    internal fun deleteWidgetLayoutIdPref(context: Context, appWidgetId: Int) {
        context.getSharedPreferences(name = PREFS_NAME, mode = 0).edit {
            remove(PREF_PREFIX_KEY + appWidgetId)
        }
    }

    // Wrapper for Context.getSharedPreferences to support named arguments
    private fun Context.getSharedPreferences(name: String, mode: Int): SharedPreferences {
        return getSharedPreferences(name, mode)
    }
}