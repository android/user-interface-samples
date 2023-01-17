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

package com.example.android.people.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.LocusId
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.people.R
import com.example.android.people.databinding.MainFragmentBinding
import com.example.android.people.getNavigationController
import com.example.android.people.ui.chat.PermissionRequest
import com.example.android.people.ui.chat.PermissionStatus
import com.example.android.people.ui.viewBindings

/**
 * The main chat list screen.
 */
class MainFragment : Fragment(R.layout.main_fragment) {

    private val binding by viewBindings(MainFragmentBinding::bind)

    @SuppressLint("InlinedApi") // POST_NOTIFICATIONS is automatically granted on API<33.
    private val permissionRequest = PermissionRequest(this, Manifest.permission.POST_NOTIFICATIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.slide_top)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navigationController = getNavigationController()
        navigationController.updateAppBar(false)
        val viewModel: MainViewModel by viewModels()

        // Show a header message asking the user to grant the notification permission.
        val headerAdapter = HeaderAdapter { permissionRequest.launch() }

        // Show the contact list.
        val contactAdapter = ContactAdapter { id ->
            navigationController.openChat(id, null)
        }
        binding.contacts.run {
            layoutManager = LinearLayoutManager(view.context)
            setHasFixedSize(true)
            adapter = contactAdapter
        }
        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            contactAdapter.submitList(contacts)
        }

        // Deal with updates of the permission status.
        permissionRequest.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                // We have the permission now. Hide the permission header.
                is PermissionStatus.Granted -> binding.contacts.adapter = contactAdapter
                // We don't have the permission. Show the permission header.
                is PermissionStatus.Denied -> {
                    val config = ConcatAdapter.Config.Builder().setStableIdMode(ConcatAdapter.Config.StableIdMode.SHARED_STABLE_IDS).build()
                    binding.contacts.adapter = ConcatAdapter(config, headerAdapter, contactAdapter)
                    headerAdapter.shouldShowRationale = status.shouldShowRationale
                }
            }
        }

        // Differentiate the main view from a chat view (ChatFragment) for  content capture.
        // See https://developer.android.com/reference/androidx/core/content/LocusIdCompat
        requireActivity().setLocusContext(LocusId("mainFragment"), null)
    }
}
