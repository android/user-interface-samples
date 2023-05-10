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

package com.example.android.appwidget.rv.weather

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.core.util.SizeFCompat
import androidx.core.widget.updateAppWidget
import com.example.android.appwidget.R

/**
 * Implementation of the weather forecast app widget that demonstrates the flexible layouts based
 * on the size of the device.
 */
class WeatherForecastAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val supportedSizes = listOf(
                SizeFCompat(180.0f, 110.0f),
                SizeFCompat(270.0f, 110.0f),
                SizeFCompat(270.0f, 280.0f)
            )
            appWidgetManager.updateAppWidget(appWidgetId, supportedSizes) {
                val layoutId = when (it) {
                    supportedSizes[0] -> R.layout.widget_weather_forecast_small
                    supportedSizes[1] -> R.layout.widget_weather_forecast_medium
                    else -> R.layout.widget_weather_forecast_large
                }
                RemoteViews(context.packageName, layoutId)
            }
        }
    }
}

