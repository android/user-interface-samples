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

package com.example.android.people.data

import androidx.core.net.toUri

abstract class Contact(
    val id: Long,
    val name: String,
    val icon: String
) {

    companion object {
        val CONTACTS = listOf(
            object : Contact(1L, "Cat", "cat.jpg") {
                override fun reply(text: String) = buildReply().apply { this.text = "Meow" }
            },
            object : Contact(2L, "Dog", "dog.jpg") {
                override fun reply(text: String) = buildReply().apply { this.text = "Woof woof!!" }
            },
            object : Contact(3L, "Parrot", "parrot.jpg") {
                override fun reply(text: String) = buildReply().apply { this.text = text }
            },
            object : Contact(4L, "Sheep", "sheep.jpg") {
                override fun reply(text: String) = buildReply().apply {
                    this.text = "Look at me!"
                    photo = "content://com.example.android.people/photo/sheep_full.jpg".toUri()
                    photoMimeType = "image/jpeg"
                }
            }
        )
    }

    val iconUri = "content://com.example.android.people/icon/$id".toUri()

    val shortcutId = "contact_$id"

    fun buildReply() = Message.Builder().apply {
        sender = this@Contact.id
        timestamp = System.currentTimeMillis()
    }

    abstract fun reply(text: String): Message.Builder

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (id != other.id) return false
        if (name != other.name) return false
        if (icon != other.icon) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + icon.hashCode()
        return result
    }
}
