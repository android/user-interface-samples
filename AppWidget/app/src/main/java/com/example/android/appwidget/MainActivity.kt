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

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.appwidget.buttons.ButtonsAppWidget
import com.example.android.appwidget.list.ListAppWidget
import com.example.android.appwidget.weather.WeatherForecastAppWidget

class MainActivity : AppCompatActivity() {

    private val widgetManager by lazy {
        AppWidgetManager.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val titleId = if (widgetManager.isRequestPinAppWidgetSupported) {
            R.string.placeholder_main_activity_pin
        } else {
            R.string.placeholder_main_activity_pin_unavailable
        }
        findViewById<TextView>(R.id.pin_title).setText(titleId)
        findViewById<Button>(R.id.sample_buttons).setup(ButtonsAppWidget::class.java)
        findViewById<Button>(R.id.sample_list).setup(ListAppWidget::class.java)
        findViewById<Button>(R.id.sample_weather).setup(WeatherForecastAppWidget::class.java)
    }

    private fun Button.setup(targetClass: Class<out AppWidgetProvider>) {
        isEnabled = widgetManager.isRequestPinAppWidgetSupported
        setOnClickListener {
            pinAppWidget(targetClass)
        }
    }

    private fun pinAppWidget(receiverClass: Class<out AppWidgetProvider>) {
        val successCallback = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, WidgetPinnedReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        widgetManager.requestPinAppWidget(
            ComponentName.createRelative(this, receiverClass.name),
            null,
            successCallback
        )
    }

    class WidgetPinnedReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(
                context,
                "Widget pinned. Go to homescreen.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}