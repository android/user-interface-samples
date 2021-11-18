/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.windowmanagersample

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.window.layout.WindowMetricsCalculator
import com.example.windowmanagersample.databinding.ActivityWindowMetricsBinding
import com.example.windowmanagersample.infolog.InfoLogAdapter

class WindowMetricsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWindowMetricsBinding

    private val adapter = InfoLogAdapter()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowMetricsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerView.adapter = adapter
        adapter.append("onCreate", "triggered")

        logCurrentWindowMetrics("onCreate")

        val container: ViewGroup = binding.root

        // Add a utility view to the container to hook into
        // View.onConfigurationChanged.
        // This is required for all activities, even those that don't
        // handle configuration changes.
        // We also can't use Activity.onConfigurationChanged, since there
        // are situations where that won't be called when the configuration
        // changes.
        // View.onConfigurationChanged is called in those scenarios.
        container.addView(object : View(this) {
            override fun onConfigurationChanged(newConfig: Configuration?) {
                super.onConfigurationChanged(newConfig)
                logCurrentWindowMetrics("Config.Change")
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun logCurrentWindowMetrics(tag: String) {
        val windowMetrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)
        val width = windowMetrics.bounds.width()
        val height = windowMetrics.bounds.height()
        adapter.append(tag, "width: $width, height: $height")
        runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }
}
