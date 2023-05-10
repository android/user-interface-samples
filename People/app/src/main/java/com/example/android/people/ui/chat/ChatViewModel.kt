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

package com.example.android.people.ui.chat

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.android.people.data.ChatRepository
import com.example.android.people.data.DefaultChatRepository

class ChatViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: ChatRepository = DefaultChatRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val chatId = MutableLiveData<Long>()

    private val _photoUri = MutableLiveData<Uri?>()
    val photo: LiveData<Uri?> = _photoUri

    private var _photoMimeType: String? = null

    /**
     * We want to update the notification when the corresponding chat screen is open. Setting this
     * to `true` updates the current notification, removing the unread message(s) badge icon and
     * suppressing further notifications.
     *
     * We do want to keep on showing and updating the notification when the chat screen is opened
     * as an expanded bubble. [ChatFragment] should set this to false if it is launched in
     * BubbleActivity. Otherwise, the expanding a bubble would remove the notification and the
     * bubble.
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
    val contact = chatId.switchMap { id -> repository.findContact(id) }

    /**
     * The list of all the messages in this chat.
     */
    val messages = chatId.switchMap { id -> repository.findMessages(id) }

    /**
     * Whether the "Show as Bubble" button should be shown.
     */
    val showAsBubbleVisible = chatId.map { id -> repository.canBubble(id) }

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
            repository.sendMessage(id, text, _photoUri.value, _photoMimeType)
        }
        _photoUri.value = null
        _photoMimeType = null
    }

    fun showAsBubble() {
        chatId.value?.let { id ->
            repository.showAsBubble(id)
        }
    }

    fun setPhoto(uri: Uri, mimeType: String) {
        _photoUri.value = uri
        _photoMimeType = mimeType
    }

    override fun onCleared() {
        chatId.value?.let { id -> repository.deactivateChat(id) }
    }
}
