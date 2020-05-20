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

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.people.R
import com.example.android.people.databinding.MainFragmentBinding
import com.example.android.people.getNavigationController
import com.example.android.people.ui.viewBindings

/**
 * The main chat list screen.
 */
class MainFragment : Fragment(R.layout.main_fragment) {

    private val binding by viewBindings(MainFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.slide_top)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navigationController = getNavigationController()
        navigationController.updateAppBar(false)
        val viewModel: MainViewModel by viewModels()

        val contactAdapter = ContactAdapter { id ->
            navigationController.openChat(id, null)
        }
        viewModel.contacts.observe(viewLifecycleOwner, Observer { contacts ->
            contactAdapter.submitList(contacts)
        })
        binding.contacts.run {
            layoutManager = LinearLayoutManager(view.context)
            setHasFixedSize(true)
            adapter = contactAdapter
        }
    }
}
