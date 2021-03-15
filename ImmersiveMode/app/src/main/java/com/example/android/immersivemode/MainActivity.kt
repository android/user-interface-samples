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

package com.example.android.immersivemode

import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

/**
 * Behaviors of immersive mode.
 */
enum class BehaviorOption(
    val title: String,
    val value: Int
) {
    // Swipe from the edge to show a hidden bar. Gesture navigation works regardless of visibility
    // of the navigation bar.
    Default(
        "BEHAVIOR_DEFAULT",
        WindowInsetsController.BEHAVIOR_DEFAULT
    ),
    // "Sticky immersive mode". Swipe from the edge to temporarily reveal the hidden bar.
    ShowTransientBarsBySwipe(
        "BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE",
        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    )
}

/**
 * Type of system bars to hide or show.
 */
enum class TypeOption(
    val title: String,
    val value: Int
) {
    // Both the status bar and the navigation bar
    SystemBars(
        "systemBars()",
        WindowInsets.Type.systemBars()
    ),
    // The status bar only.
    StatusBar(
        "statusBars()",
        WindowInsets.Type.statusBars()
    ),
    // The navigation bar only
    NavigationBar(
        "navigationBars()",
        WindowInsets.Type.navigationBars()
    )
}

class MainActivity : AppCompatActivity() {

    private lateinit var behaviorSpinner: Spinner
    private lateinit var typeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        behaviorSpinner = findViewById(R.id.behavior)
        behaviorSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            BehaviorOption.values().map { it.title }
        )

        typeSpinner = findViewById(R.id.type)
        typeSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            TypeOption.values().map { it.title }
        )

        val hideButton: Button = findViewById(R.id.hide)
        hideButton.setOnClickListener { controlWindowInsets(true) }
        val showButton: Button = findViewById(R.id.show)
        showButton.setOnClickListener { controlWindowInsets(false) }
    }

    private fun controlWindowInsets(hide: Boolean) {
        // WindowInsetsController can hide or show specified system bars.
        val insetsController = window.decorView.windowInsetsController ?: return
        // The behavior of the immersive mode.
        val behavior = BehaviorOption.values()[behaviorSpinner.selectedItemPosition].value
        // The type of system bars to hide or show.
        val type = TypeOption.values()[typeSpinner.selectedItemPosition].value
        insetsController.systemBarsBehavior = behavior
        if (hide) {
            insetsController.hide(type)
        } else {
            insetsController.show(type)
        }
    }
}
