/*
Copyright 2016 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.example.android.wearable.wear.wearnotifications.handlers;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.MessagingStyle;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;

import com.example.android.wearable.wear.common.mock.MockDatabase;
import com.example.android.wearable.wear.wearnotifications.GlobalNotificationBuilder;
import com.example.android.wearable.wear.wearnotifications.R;
import com.example.android.wearable.wear.wearnotifications.StandaloneMainActivity;

/**
 * Asynchronously handles updating messaging app posts (and active Notification) with replies from
 * user in a conversation. Notification for social app use MessagingStyle.
 */
public class MessagingIntentService extends IntentService {

    private static final String TAG = "MessagingIntentService";

    public static final String ACTION_REPLY =
            "com.example.android.wearable.wear.wearnotifications.handlers.action.REPLY";

    public static final String EXTRA_REPLY =
            "com.example.android.wearable.wear.wearnotifications.handlers.extra.REPLY";

    public MessagingIntentService() {
        super("MessagingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent(): " + intent);

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REPLY.equals(action)) {
                handleActionReply(getMessage(intent));
            }
        }
    }

    /** Handles action for replying to messages from the notification. */
    private void handleActionReply(CharSequence replyCharSequence) {
        Log.d(TAG, "handleActionReply(): " + replyCharSequence);

        if (replyCharSequence != null) {

            // TODO: Asynchronously save your message to Database and servers.

            /*
             * You have two options for updating your notification (this class uses approach #2):
             *
             *  1. Use a new NotificationCompatBuilder to create the Notification. This approach
             *  requires you to get *ALL* the information that existed in the previous
             *  Notification (and updates) and pass it to the builder. This is the approach used in
             *  the MainActivity.
             *
             *  2. Use the original NotificationCompatBuilder to create the Notification. This
             *  approach requires you to store a reference to the original builder. The benefit is
             *  you only need the new/updated information. In our case, the reply from the user
             *  which we already have here.
             *
             *  IMPORTANT NOTE: You shouldn't save/modify the resulting Notification object using
             *  its member variables and/or legacy APIs. If you want to retain anything from update
             *  to update, retain the Builder as option 2 outlines.
             */

            // Retrieves NotificationCompat.Builder used to create initial Notification
            NotificationCompat.Builder notificationCompatBuilder =
                    GlobalNotificationBuilder.getNotificationCompatBuilderInstance();

            // Recreate builder from persistent state if app process is killed
            if (notificationCompatBuilder == null) {
                // Note: New builder set globally in the method
                notificationCompatBuilder = recreateBuilderWithMessagingStyle();
            }

            // Since we are adding to the MessagingStyle, we need to first retrieve the
            // current MessagingStyle from the Notification itself.
            Notification notification = notificationCompatBuilder.build();
            MessagingStyle messagingStyle =
                    NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(
                            notification);

            // Add new message to the MessagingStyle. Set last parameter to null for responses
            // from user.
            messagingStyle.addMessage(replyCharSequence, System.currentTimeMillis(), (Person) null);

            // Updates the Notification
            notification = notificationCompatBuilder.setStyle(messagingStyle).build();

            // Pushes out the updated Notification
            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(StandaloneMainActivity.NOTIFICATION_ID, notification);
        }
    }

    /*
     * Extracts CharSequence created from the RemoteInput associated with the Notification.
     */
    private CharSequence getMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_REPLY);
        }
        return null;
    }

    /*
     * This recreates the notification from the persistent state in case the app process was killed.
     * It is basically the same code for creating the Notification from MainActivity.
     */
    private NotificationCompat.Builder recreateBuilderWithMessagingStyle() {

        // Main steps for building a MESSAGING_STYLE notification:
        //      0. Get your data
        //      1. Create Notification Channel for O and beyond devices (26+)
        //      2. Build the MESSAGING_STYLE
        //      3. Set up main Intent for notification
        //      4. Set up RemoteInput (users can input directly from notification)
        //      5. Build and issue the notification

        // 0. Get your data (everything unique per Notification).
        MockDatabase.MessagingStyleCommsAppData messagingStyleCommsAppData =
                MockDatabase.getMessagingStyleData(getApplicationContext());

        // 1. Retrieve Notification Channel for O and beyond devices (26+). We don't need to create
        //    the NotificationChannel, since it was created the first time this Notification was
        //    created.
        String notificationChannelId = messagingStyleCommsAppData.getChannelId();

        // 2. Build the Notification.Style (MESSAGING_STYLE).
        String contentTitle = messagingStyleCommsAppData.getContentTitle();

        MessagingStyle messagingStyle =
                new NotificationCompat.MessagingStyle(messagingStyleCommsAppData.getMe())
                        /*
                         * <p>This API's behavior was changed in SDK version
                         * {@link Build.VERSION_CODES#P}. If your application's target version is
                         * less than {@link Build.VERSION_CODES#P}, setting a conversation title to
                         * a non-null value will make {@link #isGroupConversation()} return
                         * {@code true} and passing {@code null} will make it return {@code false}.
                         * This behavior can be overridden by calling
                         * {@link #setGroupConversation(boolean)} regardless of SDK version.
                         * In {@code P} and above, this method does not affect group conversation
                         * settings.
                         *
                         * In our case, we use the same title.
                         */
                        .setConversationTitle(contentTitle);

        // Adds all Messages.
        // Note: Messages include the text, timestamp, and sender.
        for (MessagingStyle.Message message : messagingStyleCommsAppData.getMessages()) {
            messagingStyle.addMessage(message);
        }

        messagingStyle.setGroupConversation(messagingStyleCommsAppData.isGroupConversation());

        // 3. Set up main Intent for notification.
        Intent notifyIntent = new Intent(this, MessagingMainActivity.class);

        PendingIntent mainPendingIntent =
                PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 4. Set up a RemoteInput Action, so users can input (keyboard, drawing, voice) directly
        // from the notification without entering the app.

        // Create the RemoteInput specifying this key.
        String replyLabel = getString(R.string.reply_label);
        RemoteInput remoteInput =
                new RemoteInput.Builder(MessagingIntentService.EXTRA_REPLY)
                        .setLabel(replyLabel)
                        // Use machine learning to create responses based on previous messages.
                        .setChoices(messagingStyleCommsAppData.getReplyChoicesBasedOnLastMessage())
                        .build();

        // Create PendingIntent for service that handles input.
        Intent replyIntent = new Intent(this, MessagingIntentService.class);
        replyIntent.setAction(MessagingIntentService.ACTION_REPLY);
        PendingIntent replyActionPendingIntent = PendingIntent.getService(this, 0, replyIntent, 0);

        // Enable action to appear inline on Wear 2.0 (24+). This means it will appear over the
        // lower portion of the Notification for easy action (only possible for one action).
        final NotificationCompat.Action.WearableExtender inlineActionForWear2 =
                new NotificationCompat.Action.WearableExtender()
                        .setHintDisplayActionInline(true)
                        .setHintLaunchesActivity(false);

        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(
                                R.drawable.ic_reply_white_18dp,
                                replyLabel,
                                replyActionPendingIntent)
                        .addRemoteInput(remoteInput)
                        // Informs system we aren't bringing up our own custom UI for a reply
                        // action.
                        .setShowsUserInterface(false)
                        // Allows system to generate replies by context of conversation.
                        .setAllowGeneratedReplies(true)
                        // Add WearableExtender to enable inline actions.
                        .setSemanticAction(Action.SEMANTIC_ACTION_REPLY)
                        .extend(inlineActionForWear2)
                        .build();

        // 5. Build and issue the notification.

        // Notification Channel Id is ignored for Android pre O (26).
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(getApplicationContext(), notificationChannelId);

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);

        notificationCompatBuilder
                // MESSAGING_STYLE sets title and content for Wear 1.+ and Wear 2.0 devices.
                .setStyle(messagingStyle)
                .setContentTitle(contentTitle)
                .setContentText(messagingStyleCommsAppData.getContentText())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(
                        BitmapFactory.decodeResource(
                                getResources(), R.drawable.ic_person_black_48dp))
                .setContentIntent(mainPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                // Number of new notifications for API <24 (Wear 1.+) devices.
                .setSubText(Integer.toString(messagingStyleCommsAppData.getNumberOfNewMessages()))
                .addAction(replyAction)
                .setCategory(Notification.CATEGORY_MESSAGE)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(messagingStyleCommsAppData.getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(messagingStyleCommsAppData.getChannelLockscreenVisibility());

        // If the phone is in "Do not disturb" mode, the user may still be notified if the
        // sender(s) are in a group allowed through "Do not disturb" by the user.
        for (Person person : messagingStyleCommsAppData.getParticipants()) {
            notificationCompatBuilder.addPerson(person.getUri());
        }
        return notificationCompatBuilder;
    }
}
