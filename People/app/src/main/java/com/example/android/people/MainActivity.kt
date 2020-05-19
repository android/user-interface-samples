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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.example.android.people.data.Contact
import com.example.android.people.databinding.MainActivityBinding
import com.example.android.people.ui.chat.ChatFragment
import com.example.android.people.ui.main.MainFragment
import com.example.android.people.ui.photo.PhotoFragment
import com.example.android.people.ui.viewBindings

/**
 * Entry point of the app when it is launched as a full app.
 */
class MainActivity : AppCompatActivity(R.layout.main_activity), NavigationController {

    companion object {
        private const val FRAGMENT_CHAT = "chat"
    }

    private val binding by viewBindings(MainActivityBinding::bind)

    private lateinit var transition: Transition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        transition = TransitionInflater.from(this).inflateTransition(R.transition.app_bar)
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
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
        when (intent.action) {
            // Invoked when a dynamic shortcut is clicked.
            Intent.ACTION_VIEW -> {
                val id = intent.data?.lastPathSegment?.toLongOrNull()
                if (id != null) {
                    openChat(id, null)
                }
            }
            // Invoked when a text is shared through Direct Share.
            Intent.ACTION_SEND -> {
                val shortcutId = intent.getStringExtra(Intent.EXTRA_SHORTCUT_ID)
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                val contact = Contact.CONTACTS.find { it.shortcutId == shortcutId }
                if (contact != null) {
                    openChat(contact.id, text)
                }
            }
        }
    }

    override fun updateAppBar(
        showContact: Boolean,
        hidden: Boolean,
        body: (name: TextView, icon: ImageView) -> Unit
    ) {
        if (hidden) {
            binding.appBar.visibility = View.GONE
        } else {
            binding.appBar.visibility = View.VISIBLE
            TransitionManager.beginDelayedTransition(binding.appBar, transition)
            if (showContact) {
                supportActionBar?.setDisplayShowTitleEnabled(false)
                binding.name.visibility = View.VISIBLE
                binding.icon.visibility = View.VISIBLE
            } else {
                supportActionBar?.setDisplayShowTitleEnabled(true)
                binding.name.visibility = View.GONE
                binding.icon.visibility = View.GONE
            }
        }
        body(binding.name, binding.icon)
    }

    override fun openChat(id: Long, prepopulateText: String?) {
        supportFragmentManager.popBackStack(FRAGMENT_CHAT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.commit {
            addToBackStack(FRAGMENT_CHAT)
            replace(R.id.container, ChatFragment.newInstance(id, true, prepopulateText))
        }
    }

    override fun openPhoto(photo: Uri) {
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.container, PhotoFragment.newInstance(photo))
        }
    }
}
