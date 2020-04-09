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

package com.example.android.bubbles.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Person
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.example.android.bubbles.BubbleActivity
import com.example.android.bubbles.MainActivity
import com.example.android.bubbles.R

/**
 * Handles all operations related to [Notification].
 */
class NotificationHelper(private val context: Context) {

    companion object {
        /**
         * The notification channel for messages. This is used for showing Bubbles.
         */
        private const val CHANNEL_NEW_MESSAGES = "new_messages"

        private const val REQUEST_CONTENT = 1
        private const val REQUEST_BUBBLE = 2
    }

    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalStateException()

    private val shortcutManager: ShortcutManager =
        context.getSystemService() ?: throw IllegalStateException()

    fun setUpNotificationChannels() {
        if (notificationManager.getNotificationChannel(CHANNEL_NEW_MESSAGES) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_NEW_MESSAGES,
                    context.getString(R.string.channel_new_messages),
                    // The importance must be IMPORTANCE_HIGH to show Bubbles.
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.channel_new_messages_description)
                }
            )
        }
        updateShortcuts()
    }

    @WorkerThread
    fun updateShortcuts() {
        val shortcuts = Contact.CONTACTS.map { contact ->
            val icon = Icon.createWithAdaptiveBitmap(
                context.resources.assets.open(contact.icon).use { input ->
                    BitmapFactory.decodeStream(input)
                }
            )
            // Create a dynamic shortcut for each of the contacts.
            // The same shortcut ID will be used when we show a bubble notification.
            ShortcutInfo.Builder(context, contact.shortcutId)
                .setActivity(ComponentName(context, MainActivity::class.java))
                .setShortLabel(contact.name)
                .setIcon(icon)
                .setLongLived(true)
                .setCategories(setOf(ShortcutInfo.SHORTCUT_CATEGORY_CONVERSATION))
                .setIntent(
                    Intent(context, MainActivity::class.java)
                        .setAction(Intent.ACTION_VIEW)
                        .setData(
                            Uri.parse(
                                "https://android.example.com/chat/${contact.id}"
                            )
                        )
                )
                .setPerson(
                    Person.Builder()
                        .setName(contact.name)
                        .setIcon(icon)
                        .build()
                )
                .build()
        }
        shortcutManager.addDynamicShortcuts(shortcuts)
    }

    @WorkerThread
    fun showNotification(chat: Chat, fromUser: Boolean) {
        val icon = Icon.createWithAdaptiveBitmapContentUri(chat.contact.iconUri)
        val person = Person.Builder()
            .setName(chat.contact.name)
            .setIcon(icon)
            .build()
        val contentUri = "https://android.example.com/chat/${chat.contact.id}".toUri()
        val builder = Notification.Builder(context, CHANNEL_NEW_MESSAGES)
            // A notification can be shown as a bubble by calling setBubbleMetadata()
            .setBubbleMetadata(
                Notification.BubbleMetadata.Builder()
                    .createIntentBubble(
                        // The Intent to be used for the expanded bubble.
                        PendingIntent.getActivity(
                            context,
                            REQUEST_BUBBLE,
                            // Launch BubbleActivity as the expanded bubble.
                            Intent(context, BubbleActivity::class.java)
                                .setAction(Intent.ACTION_VIEW)
                                .setData(contentUri),
                            PendingIntent.FLAG_UPDATE_CURRENT
                        ),
                        // The icon of the bubble.
                        icon
                    )
                    // The height of the expanded bubble.
                    .setDesiredHeight(context.resources.getDimensionPixelSize(R.dimen.bubble_height))
                    .apply {
                        // When the bubble is explicitly opened by the user, we can show the bubble
                        // automatically in the expanded state. This works only when the app is in
                        // the foreground.
                        if (fromUser) {
                            setAutoExpandBubble(true)
                            setSuppressNotification(true)
                        }
                    }
                    .build()
            )
            // The user can turn off the bubble in system settings. In that case, this notification
            // is shown as a normal notification instead of a bubble. Make sure that this
            // notification works as a normal notification as well.
            .setContentTitle(chat.contact.name)
            .setSmallIcon(R.drawable.ic_message)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setShortcutId(chat.contact.shortcutId)
            .addPerson(person)
            .setShowWhen(true)
            // The content Intent is used when the user clicks on the "Open Content" icon button on
            // the expanded bubble, as well as when the fall-back notification is clicked.
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    REQUEST_CONTENT,
                    Intent(context, MainActivity::class.java)
                        .setAction(Intent.ACTION_VIEW)
                        .setData(contentUri),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

        if (fromUser) {
            // This is a Bubble explicitly opened by the user.
            builder
                .setStyle(
                    Notification.MessagingStyle(person)
                        .addMessage(
                            context.getString(R.string.chat_with_contact, chat.contact.name),
                            System.currentTimeMillis(),
                            person
                        )
                        .setGroupConversation(false)
                )
                .setContentText(context.getString(R.string.chat_with_contact, chat.contact.name))
        } else {
            // Let's add some more content to the notification in case it falls back to a normal
            // notification.
            val lastOutgoingId = chat.messages.last { !it.isIncoming }.id
            val newMessages = chat.messages.filter { message ->
                message.id > lastOutgoingId
            }
            builder
                .setStyle(
                    Notification.MessagingStyle(person)
                        .apply {
                            for (message in newMessages) {
                                addMessage(message.text, message.timestamp, person)
                            }
                        }
                        .setGroupConversation(false)
                )
                .setContentText(newMessages.joinToString("\n") { it.text })
                .setWhen(newMessages.last().timestamp)
        }

        notificationManager.notify(chat.contact.id.toInt(), builder.build())
    }

    fun dismissNotification(id: Long) {
        notificationManager.cancel(id.toInt())
    }

    fun canBubble(): Boolean {
        val channel = notificationManager.getNotificationChannel(CHANNEL_NEW_MESSAGES)
        return notificationManager.areBubblesAllowed() && channel.canBubble()
    }
}
