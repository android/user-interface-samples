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

package com.example.android.bubbles.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.bubbles.data.Chat
import com.example.android.bubbles.data.Contact
import com.example.android.bubbles.data.TestChatRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dummyContacts = Contact.CONTACTS

    private fun createViewModel(): MainViewModel {
        var viewModel: MainViewModel? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            viewModel = MainViewModel(
                ApplicationProvider.getApplicationContext(),
                TestChatRepository(dummyContacts.map { contact ->
                    contact.id to Chat(contact)
                }.toMap())
            )
        }
        return viewModel!!
    }

    @Test
    fun hasListOfContacts() {
        val viewModel = createViewModel()
        val contacts = viewModel.contacts.value
        assertThat(contacts).isEqualTo(dummyContacts)
    }

}
