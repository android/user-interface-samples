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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.LocusIdCompat
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.example.android.people.BubbleActivity
import com.example.android.people.MainActivity
import com.example.android.people.R
import com.example.android.people.ReplyReceiver

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
        updateShortcuts(null)
    }

    @WorkerThread
    fun updateShortcuts(importantContact: Contact?) {
        var shortcuts = Contact.CONTACTS.map { contact ->
            val icon = IconCompat.createWithAdaptiveBitmap(
                context.resources.assets.open(contact.icon).use { input ->
                    BitmapFactory.decodeStream(input)
                }
            )
            // Create a dynamic shortcut for each of the contacts.
            // The same shortcut ID will be used when we show a bubble notification.
            ShortcutInfoCompat.Builder(context, contact.shortcutId)
                .setLocusId(LocusIdCompat(contact.shortcutId))
                .setActivity(ComponentName(context, MainActivity::class.java))
                .setShortLabel(contact.name)
                .setIcon(icon)
                .setLongLived(true)
                .setCategories(setOf("com.example.android.bubbles.category.TEXT_SHARE_TARGET"))
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
        // Move the important contact to the front of the shortcut list.
        if (importantContact != null) {
            shortcuts = shortcuts.sortedByDescending { it.id == importantContact.shortcutId }
        }
        // Truncate the list if we can't show all of our contacts.
        val maxCount = ShortcutManagerCompat.getMaxShortcutCountPerActivity(context)
        if (shortcuts.size > maxCount) {
            shortcuts = shortcuts.take(maxCount)
        }
        for (shortcut in shortcuts) {
            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
        }
    }

    private fun flagUpdateCurrent(mutable: Boolean): Int {
        return if (mutable) {
            if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
    }

    @WorkerThread
    fun showNotification(chat: Chat, fromUser: Boolean, update: Boolean = false) {
        updateShortcuts(chat.contact)
        val icon = IconCompat.createWithAdaptiveBitmapContentUri(chat.contact.iconUri)
        val user = Person.Builder().setName(context.getString(R.string.sender_you)).build()
        val person = Person.Builder().setName(chat.contact.name).setIcon(icon).build()
        val contentUri = "https://android.example.com/chat/${chat.contact.id}".toUri()

        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_BUBBLE,
            // Launch BubbleActivity as the expanded bubble.
            Intent(context, BubbleActivity::class.java)
                .setAction(Intent.ACTION_VIEW)
                .setData(contentUri),
            flagUpdateCurrent(mutable = true)
        )
        // Let's add some more content to the notification in case it falls back to a normal
        // notification.
        val messagingStyle = NotificationCompat.MessagingStyle(user)
        val lastId = chat.messages.last().id
        for (message in chat.messages) {
            val m = NotificationCompat.MessagingStyle.Message(
                message.text,
                message.timestamp,
                if (message.isIncoming) person else null
            ).apply {
                if (message.photoUri != null) {
                    setData(message.photoMimeType, message.photoUri)
                }
            }
            if (message.id < lastId) {
                messagingStyle.addHistoricMessage(m)
            } else {
                messagingStyle.addMessage(m)
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_NEW_MESSAGES)
            // A notification can be shown as a bubble by calling setBubbleMetadata()
            .setBubbleMetadata(
                NotificationCompat.BubbleMetadata.Builder(pendingIntent, icon)
                    // The height of the expanded bubble.
                    .setDesiredHeight(context.resources.getDimensionPixelSize(R.dimen.bubble_height))
                    .apply {
                        // When the bubble is explicitly opened by the user, we can show the bubble
                        // automatically in the expanded state. This works only when the app is in
                        // the foreground.
                        if (fromUser) {
                            setAutoExpandBubble(true)
                        }
                        if (fromUser || update) {
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
            // This ID helps the intelligence services of the device to correlate this notification
            // with the corresponding dynamic shortcut.
            .setLocusId(LocusIdCompat(chat.contact.shortcutId))
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
                    flagUpdateCurrent(mutable = false)
                )
            )
            // Direct Reply
            .addAction(
                NotificationCompat.Action
                    .Builder(
                        IconCompat.createWithResource(context, R.drawable.ic_send),
                        context.getString(R.string.label_reply),
                        PendingIntent.getBroadcast(
                            context,
                            REQUEST_CONTENT,
                            Intent(context, ReplyReceiver::class.java).setData(contentUri),
                            flagUpdateCurrent(mutable = true)
                        )
                    )
                    .addRemoteInput(
                        RemoteInput.Builder(ReplyReceiver.KEY_TEXT_REPLY)
                            .setLabel(context.getString(R.string.hint_input))
                            .build()
                    )
                    .setAllowGeneratedReplies(true)
                    .build()
            )
            // Let's add some more content to the notification in case it falls back to a normal
            // notification.
            .setStyle(messagingStyle)
            .setWhen(chat.messages.last().timestamp)
        // Don't sound/vibrate if an update to an existing notification.
        if (update) {
            builder.setOnlyAlertOnce(true)
        }
        notificationManager.notify(chat.contact.id.toInt(), builder.build())
    }

    private fun dismissNotification(id: Long) {
        notificationManager.cancel(id.toInt())
    }

    fun canBubble(contact: Contact): Boolean {
        val channel = notificationManager.getNotificationChannel(
            CHANNEL_NEW_MESSAGES,
            contact.shortcutId
        )
        return notificationManager.areBubblesAllowed() || channel?.canBubble() == true
    }

    fun updateNotification(chat: Chat, chatId: Long, prepopulatedMsgs: Boolean) {
        if (!prepopulatedMsgs) {
            // Update notification bubble metadata to suppress notification so that the unread
            // message badge icon on the collapsed bubble is removed.
            showNotification(chat, fromUser = false, update = true)
        } else {
            dismissNotification(chatId)
        }
    }
}

