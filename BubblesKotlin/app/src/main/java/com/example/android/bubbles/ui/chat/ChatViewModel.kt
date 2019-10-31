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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.bubbles.data.ChatRepository
import com.example.android.bubbles.data.Contact
import com.example.android.bubbles.data.DefaultChatRepository
import com.example.android.bubbles.data.Message

class ChatViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: ChatRepository = DefaultChatRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val chatId = MutableLiveData<Long>()

    /**
     * We want to dismiss a notification when the corresponding chat screen is open. Setting this to `true` dismisses
     * the current notification and suppresses further notifications.
     *
     * We do want to keep on showing and updating the notification when the chat screen is opened as an expanded bubble.
     * [ChatFragment] should set this to false if it is launched in BubbleActivity. Otherwise, the expanding a bubble
     * would remove the notification and the bubble.
     */
    var foreground = false
        set(value) {
            field = value
            chatId.value?.let { id ->
                if (value) {
                    repository.activateChat(id)
                } else {
                    repository.deactivateChat(id)
                }
            }
        }

    /**
     * The contact of this chat.
     */
    val contact: LiveData<Contact?> = Transformations.switchMap(chatId) { id ->
        repository.findContact(id)
    }

    /**
     * The list of all the messages in this chat.
     */
    val messages: LiveData<List<Message>> = Transformations.switchMap(chatId) { id ->
        repository.findMessages(id)
    }

    /**
     * Whether the "Show as Bubble" button should be shown.
     */
    val showAsBubbleVisible: LiveData<Boolean> = object: LiveData<Boolean>() {
        override fun onActive() {
            // We hide the "Show as Bubble" button if we are not allowed to show the bubble.
            value = repository.canBubble()
        }
    }

    fun setChatId(id: Long) {
        chatId.value = id
        if (foreground) {
            repository.activateChat(id)
        } else {
            repository.deactivateChat(id)
        }
    }

    fun send(text: String) {
        val id = chatId.value
        if (id != null && id != 0L) {
            repository.sendMessage(id, text)
        }
    }

    fun showAsBubble() {
        chatId.value?.let { id ->
            repository.showAsBubble(id)
        }
    }

    override fun onCleared() {
        chatId.value?.let { id -> repository.deactivateChat(id) }
    }
}
