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

import com.example.android.appwidget.R
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import kotlin.random.Random

object WeatherRepo {

    /**
     * Request the WeatherInfo of a given location
     */
    suspend fun getWeatherInfo(delay: Long = Random.nextInt(1, 3) * 1000L): WeatherInfo {
        // Simulate network loading
        if (delay > 0) {
            delay(delay)
        }
        return WeatherInfo.Available(
            placeName = "Tokyo",
            currentData = getRandomWeatherData(Instant.now()),
            hourlyForecast = (1..4).map {
                getRandomWeatherData(Instant.now().plusSeconds(it * 3600L))
            },
            dailyForecast = (1..4).map {
                getRandomWeatherData(Instant.now().plusSeconds(it * 86400L))
            }
        )
    }

    /**
     * Fake the weather data
     */
    private fun getRandomWeatherData(instant: Instant): WeatherData {
        val dateTime = instant.atZone(ZoneId.systemDefault())
        return WeatherData(
            icon = R.drawable.ic_partly_cloudy,
            status = R.string.mostly_cloudy,
            temp = Random.nextInt(5, 35),
            maxTemp = Random.nextInt(5, 35),
            minTemp = Random.nextInt(5, 35),
            day = dateTime.dayOfWeek.name,
            hour = "${dateTime.hour % 12}:${if (dateTime.hour >= 12) "pm" else "am"}",
        )
    }
}

