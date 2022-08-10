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

import android.appwidget.AppWidgetProvider
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.android.appwidget.glance.weather.WeatherGlanceWidget
import com.example.android.appwidget.glance.weather.WeatherGlanceWidgetReceiver
import com.example.android.appwidget.glance.weather.WeatherRepo
import com.google.android.glance.tools.preview.GlancePreviewActivity

@OptIn(ExperimentalGlanceRemoteViewsApi::class)
class PreviewActivity : GlancePreviewActivity() {

    override suspend fun getGlancePreview(receiver: Class<out GlanceAppWidgetReceiver>): GlanceAppWidget {
        return when (receiver) {
            WeatherGlanceWidgetReceiver::class.java -> WeatherGlanceWidget()
            else -> throw IllegalArgumentException()
        }
    }

    override suspend fun getGlanceState(instance: GlanceAppWidget): Any? {
        return when (instance) {
            is WeatherGlanceWidget -> WeatherRepo.getWeatherInfo(delay = 0)
            else -> super.getGlanceState(instance)
        }
    }

    override fun getProviders(): List<Class<out AppWidgetProvider>> {
        return listOf(WeatherGlanceWidgetReceiver::class.java)
    }
}