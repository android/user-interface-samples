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

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.SizeF
import android.widget.RemoteViews

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
            updateWeatherWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWeatherWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val viewMapping: Map<SizeF, RemoteViews> = mapOf(
            // Specify the minimum width and height in dp and a layout, which you want to use for the
            // specified size
            // In the following case:
            //   - R.layout.widget_weather_forecast_small is used from
            //     180dp (or minResizeWidth) x 110dp (or minResizeHeight) to 269dp (next cutoff point - 1) x 279dp (next cutoff point - 1)
            //   - R.layout.widget_weather_forecast_medium is used from 270dp x 110dp to 270dp x 279dp (next cutoff point - 1)
            //   - R.layout.widget_weather_forecast_large is used from
            //     270dp x 280dp to 570dp (specified as maxResizeWidth) x 450dp (specified as maxResizeHeight)
            SizeF(180.0f, 110.0f) to RemoteViews(
                context.packageName,
                R.layout.widget_weather_forecast_small
            ),
            SizeF(270.0f, 110.0f) to RemoteViews(
                context.packageName,
                R.layout.widget_weather_forecast_medium
            ),
            SizeF(270.0f, 280.0f) to RemoteViews(
                context.packageName,
                R.layout.widget_weather_forecast_large
            )
        )
        appWidgetManager.updateAppWidget(appWidgetId, RemoteViews(viewMapping))
    }
}

