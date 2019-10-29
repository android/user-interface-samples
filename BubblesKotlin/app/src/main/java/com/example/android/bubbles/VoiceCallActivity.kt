/*
 * Copyright (C) 2019 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bubbles

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * A dummy voice call screen. It only shows the icon and the name.
 */
class VoiceCallActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAME = "name"
        const val EXTRA_ICON = "icon"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voice_call_activity)
        val name = intent.getStringExtra(EXTRA_NAME)
        val icon = intent.getIntExtra(EXTRA_ICON, 0)
        if (name == null || icon == 0) {
            finish()
            return
        }
        val textName: TextView = findViewById(R.id.name)
        textName.text = name
        val imageIcon: ImageView = findViewById(R.id.icon)
        Glide.with(imageIcon).load(icon).apply(RequestOptions.circleCropTransform()).into(imageIcon)
    }
}
