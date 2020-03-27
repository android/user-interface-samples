/*
 *
 *  * Copyright 2020 The Android Open Source Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.example.windowmanagersample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.windowmanagersample.BaseSampleActivity.Companion.BACKEND_TYPE_DEVICE_DEFAULT
import com.example.windowmanagersample.BaseSampleActivity.Companion.BACKEND_TYPE_EXTRA
import com.example.windowmanagersample.BaseSampleActivity.Companion.BACKEND_TYPE_MID_SCREEN_FOLD
import com.example.windowmanagersample.databinding.ActivityWindowDemosBinding


/**
 * Main activity that launches WindowManager demos. Allows the user to choose the backend to use
 * with the [androidx.window.WindowManager] library interface, which can be helpful if the test
 * device does not report any display features.
 */
class WindowDemosActivity : AppCompatActivity() {
    private var selectedBackend = BACKEND_TYPE_DEVICE_DEFAULT

    private lateinit var binding: ActivityWindowDemosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowDemosBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backendRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.device_default_radio_button ->
                    selectedBackend = BACKEND_TYPE_DEVICE_DEFAULT
                R.id.mid_fold_radio_button ->
                    selectedBackend = BACKEND_TYPE_MID_SCREEN_FOLD
            }
        }
        binding.featuresActivityButton.setOnClickListener { showDisplayFeatures() }

        binding.splitLayoutActivityButton.setOnClickListener { showSplitLayout() }

        if (savedInstanceState != null) {
            selectedBackend = savedInstanceState.getInt(
                BACKEND_TYPE_EXTRA,
                BACKEND_TYPE_DEVICE_DEFAULT
            )
        }
        when (selectedBackend) {
            BACKEND_TYPE_DEVICE_DEFAULT ->
                binding.backendRadioGroup.check(R.id.device_default_radio_button)
            BACKEND_TYPE_MID_SCREEN_FOLD ->
                binding.backendRadioGroup.check(R.id.mid_fold_radio_button)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BACKEND_TYPE_EXTRA, selectedBackend)
    }

    private fun showDisplayFeatures() {
        val intent = Intent(this, DisplayFeaturesActivity::class.java)
        intent.putExtra(BACKEND_TYPE_EXTRA, selectedBackend)
        startActivity(intent)
    }

    private fun showSplitLayout() {
        val intent = Intent(this, SplitLayoutActivity::class.java)
        intent.putExtra(BACKEND_TYPE_EXTRA, selectedBackend)
        startActivity(intent)
    }
}