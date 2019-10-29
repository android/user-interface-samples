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
import androidx.fragment.app.transaction
import com.example.android.bubbles.ui.chat.ChatFragment
import com.example.android.bubbles.ui.photo.PhotoFragment

/**
 * Entry point of the app when it is launched as an expanded Bubble.
 */
class BubbleActivity : AppCompatActivity(), NavigationController {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bubble_activity)
        val id = intent.data.lastPathSegment.toLongOrNull() ?: return
        if (savedInstanceState == null) {
            supportFragmentManager.transaction(now = true) {
                replace(R.id.container, ChatFragment.newInstance(id, false))
            }
        }
    }

    override fun openChat(id: Long) {
        throw UnsupportedOperationException("BubbleActivity always shows a single chat thread.")
    }

    override fun openPhoto(photo: Int) {
        // In an expanded Bubble, you can navigate between Fragments just like you would normally do in a normal
        // Activity. Just make sure you don't block onBackPressed().
        supportFragmentManager.transaction {
            addToBackStack(null)
            replace(R.id.container, PhotoFragment.newInstance(photo))
        }
    }

    override fun updateAppBar(showContact: Boolean, hidden: Boolean, body: (name: TextView, icon: ImageView) -> Unit) {
        // The expanded bubble does not have an app bar. Ignore.
    }
}
