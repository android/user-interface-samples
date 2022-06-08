/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.example.android.text

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commitNow
import com.example.android.text.databinding.MainActivityBinding
import com.example.android.text.ui.home.HomeFragment
import com.example.android.text.ui.viewBindings

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private val binding by viewBindings(MainActivityBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // Set up the toolbar.
        setSupportActionBar(binding.toolbar)
        updateNavigationIcon()
        supportFragmentManager.addOnBackStackChangedListener { updateNavigationIcon() }
        binding.toolbar.setNavigationOnClickListener { supportFragmentManager.popBackStack() }

        // Adjust for edge-to-edge display.
        ViewCompat.setOnApplyWindowInsetsListener(binding.coordinator) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.appBar.updatePadding(top = systemBars.top)
            insets
        }

        // Show the demo list.
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                replace(R.id.content, HomeFragment())
            }
        }
    }

    private fun updateNavigationIcon() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            setTitle(R.string.app_name)
        }
    }
}
