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

import android.content.Intent
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.transaction
import com.example.android.bubbles.ui.chat.ChatFragment
import com.example.android.bubbles.ui.main.MainFragment
import com.example.android.bubbles.ui.photo.PhotoFragment

/**
 * Entry point of the app when it is launched as a full app.
 */
class MainActivity : AppCompatActivity(), NavigationController {

    companion object {
        private const val FRAGMENT_CHAT = "chat"
    }

    private lateinit var appBar: ViewGroup
    private lateinit var name: TextView
    private lateinit var icon: ImageView

    private lateinit var transition: Transition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))
        transition = TransitionInflater.from(this).inflateTransition(R.transition.app_bar)
        appBar = findViewById(R.id.app_bar)
        name = findViewById(R.id.name)
        icon = findViewById(R.id.icon)
        if (savedInstanceState == null) {
            supportFragmentManager.transaction(now = true) {
                replace(R.id.container, MainFragment())
            }
            intent?.let(::handleIntent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            val id = intent.data.lastPathSegment.toLongOrNull()
            if (id != null) {
                openChat(id)
            }
        }
    }

    override fun updateAppBar(showContact: Boolean, hidden: Boolean, body: (name: TextView, icon: ImageView) -> Unit) {
        if (hidden) {
            appBar.visibility = View.GONE
        } else {
            appBar.visibility = View.VISIBLE
            TransitionManager.beginDelayedTransition(appBar, transition)
            if (showContact) {
                supportActionBar?.setDisplayShowTitleEnabled(false)
                name.visibility = View.VISIBLE
                icon.visibility = View.VISIBLE
            } else {
                supportActionBar?.setDisplayShowTitleEnabled(true)
                name.visibility = View.GONE
                icon.visibility = View.GONE
            }
        }
        body(name, icon)
    }

    override fun openChat(id: Long) {
        supportFragmentManager.popBackStack(FRAGMENT_CHAT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.transaction {
            addToBackStack(FRAGMENT_CHAT)
            replace(R.id.container, ChatFragment.newInstance(id, true))
        }
    }

    override fun openPhoto(photo: Int) {
        supportFragmentManager.transaction {
            addToBackStack(null)
            replace(R.id.container, PhotoFragment.newInstance(photo))
        }
    }
}

