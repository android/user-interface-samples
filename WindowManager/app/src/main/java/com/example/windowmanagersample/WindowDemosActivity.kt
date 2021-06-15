/*
 * Copyright 2020 The Android Open Source Project
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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.windowmanagersample.databinding.ActivityWindowDemosBinding

/**
 * Main activity that launches WindowManager demos.
 */
class WindowDemosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWindowDemosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowDemosBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.featuresActivityButton.setOnClickListener { showDisplayFeatures() }
        binding.splitLayoutActivityButton.setOnClickListener { showSplitLayout() }
    }

    private fun showDisplayFeatures() {
        val intent = Intent(this, DisplayFeaturesActivity::class.java)
        startActivity(intent)
    }

    private fun showSplitLayout() {
        val intent = Intent(this, SplitLayoutActivity::class.java)
        startActivity(intent)
    }
}
