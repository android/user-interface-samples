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

package com.example.android.people

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.people.databinding.VoiceCallActivityBinding
import com.example.android.people.ui.viewBindings

/**
 * A dummy voice call screen. It only shows the icon and the name.
 */
class VoiceCallActivity : AppCompatActivity(R.layout.voice_call_activity) {

    companion object {
        const val EXTRA_NAME = "name"
        const val EXTRA_ICON_URI = "iconUri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra(EXTRA_NAME)
        val icon = intent.getParcelableExtra<Uri>(EXTRA_ICON_URI)
        if (name == null || icon == null) {
            finish()
            return
        }
        val binding: VoiceCallActivityBinding by viewBindings(VoiceCallActivityBinding::bind)
        binding.name.text = name
        Glide.with(binding.icon)
            .load(icon)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.icon)
    }
}
