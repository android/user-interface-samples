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

package com.example.android.bubbles.ui.chat

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.bubbles.data.Chat
import com.example.android.bubbles.data.Contact
import com.example.android.bubbles.data.TestChatRepository
import com.example.android.bubbles.observedValue
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ChatViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dummyContacts = Contact.CONTACTS

    private lateinit var viewModel: ChatViewModel
    private lateinit var repository: TestChatRepository

    @Before
    fun createViewModel() {
        repository = TestChatRepository(dummyContacts.map { contact ->
            contact.id to Chat(contact)
        }.toMap())
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            viewModel = ChatViewModel(ApplicationProvider.getApplicationContext(), repository)
        }
    }

    @Test
    fun hasContactAndMessages() {
        viewModel.setChatId(1L)
        viewModel.foreground = true
        assertThat(viewModel.contact.observedValue()).isEqualTo(dummyContacts.find { it.id == 1L })
        assertThat(viewModel.messages.observedValue()).hasSize(2)
        assertThat(repository.activatedId).isEqualTo(1L)
    }

    @Test
    fun sendAndReceiveReply() {
        viewModel.setChatId(1L)
        viewModel.send("a")
        val messages = viewModel.messages.observedValue()
        assertThat(messages).hasSize(4)
        assertThat(messages[2].text).isEqualTo("a")
        assertThat(messages[3].text).isEqualTo("Meow")
    }

    @Test
    fun showAsBubble() {
        viewModel.setChatId(1L)
        assertThat(repository.bubbleId).isEqualTo(0L)
        viewModel.showAsBubble()
        assertThat(repository.bubbleId).isEqualTo(1L)
    }

}
