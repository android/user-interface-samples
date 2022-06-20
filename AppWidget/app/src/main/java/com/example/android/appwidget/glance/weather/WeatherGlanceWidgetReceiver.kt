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

package com.example.android.appwidget.glance.weather

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Handle system events for AppWidgets with the provided GlanceAppWidget instance.
 *
 * Use this class to handle widget lifecycle specific events like onEnable/Disable.
 */
class WeatherGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WeatherGlanceWidget()

    /**
     * Called when the first instance of the widget is placed. Since all instances share the same
     * state, we don't need to enqueue a new one for subsequent instances.
     *
     * Note: if you would need to load different data for each instance you could enqueue onUpdate
     * method instead. It's safe to call multiple times because of the unique work + KEEP policy
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WeatherWorker.enqueue(context)
    }

    /**
     * Called when the last instance of this widget is removed.
     * Make sure to cancel all ongoing workers when user remove all widget instances
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WeatherWorker.cancel(context)
    }
}