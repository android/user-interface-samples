/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wearable.wear.wearnotifications;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.BigPictureStyle;
import androidx.core.app.NotificationCompat.BigTextStyle;
import androidx.core.app.NotificationCompat.InboxStyle;
import androidx.core.app.NotificationCompat.MessagingStyle;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;
import androidx.wear.ambient.AmbientMode;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.android.wearable.wear.common.mock.MockDatabase;
import com.example.android.wearable.wear.common.util.NotificationUtil;
import com.example.android.wearable.wear.wearnotifications.handlers.BigPictureSocialIntentService;
import com.example.android.wearable.wear.wearnotifications.handlers.BigPictureSocialMainActivity;
import com.example.android.wearable.wear.wearnotifications.handlers.BigTextIntentService;
import com.example.android.wearable.wear.wearnotifications.handlers.BigTextMainActivity;
import com.example.android.wearable.wear.wearnotifications.handlers.InboxMainActivity;
import com.example.android.wearable.wear.wearnotifications.handlers.MessagingIntentService;
import com.example.android.wearable.wear.wearnotifications.handlers.MessagingMainActivity;

/**
 * Demonstrates best practice for {@link NotificationCompat} Notifications created by local
 * standalone Wear apps. All {@link NotificationCompat} examples use
 * {@link NotificationCompat.Style}.
 */
public class StandaloneMainActivity extends Activity implements
        AmbientMode.AmbientCallbackProvider {

    private static final String TAG = "StandaloneMainActivity";

    public static final int NOTIFICATION_ID = 888;

    /*
     * Used to represent each major {@link NotificationCompat.Style} in the
     * {@link WearableRecyclerView}. These constants are also used in a switch statement when one
     * of the items is selected to create the appropriate {@link Notification}.
     */
    private static final String BIG_TEXT_STYLE = "BIG_TEXT_STYLE";
    private static final String BIG_PICTURE_STYLE = "BIG_PICTURE_STYLE";
    private static final String INBOX_STYLE = "INBOX_STYLE";
    private static final String MESSAGING_STYLE = "MESSAGING_STYLE";

    /*
    Collection of major {@link NotificationCompat.Style} to create {@link CustomRecyclerAdapter}
    for {@link WearableRecyclerView}.
    */
    private static final String[] NOTIFICATION_STYLES =
            {BIG_TEXT_STYLE, BIG_PICTURE_STYLE, INBOX_STYLE, MESSAGING_STYLE};

    private NotificationManagerCompat mNotificationManagerCompat;

    // Needed for {@link SnackBar} to alert users when {@link Notification} are disabled for app.
    private FrameLayout mMainFrameLayout;
    private WearableRecyclerView mWearableRecyclerView;
    private CustomRecyclerAdapter mCustomRecyclerAdapter;

    /**
     * Ambient mode controller attached to this display. Used by Activity to see if it is in
     * ambient mode.
     */
    private AmbientMode.AmbientController mAmbientController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_main);

        // Enables Ambient mode.
        mAmbientController = AmbientMode.attachAmbientSupport(this);

        mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        mMainFrameLayout = findViewById(R.id.mainFrameLayout);
        mWearableRecyclerView = findViewById(R.id.recycler_view);

        // Aligns the first and last items on the list vertically centered on the screen.
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        // Customizes scrolling so items farther away form center are smaller.
        ScalingScrollLayoutCallback scalingScrollLayoutCallback =
                new ScalingScrollLayoutCallback();
        mWearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, scalingScrollLayoutCallback));

        // Improves performance because we know changes in content do not change the layout size of
        // the RecyclerView.
        mWearableRecyclerView.setHasFixedSize(true);

        // Specifies an adapter (see also next example).
        mCustomRecyclerAdapter = new CustomRecyclerAdapter(
                NOTIFICATION_STYLES,
                // Controller passes selected data from the Adapter out to this Activity to trigger
                // updates in the UI/Notifications.
                new Controller(this));

        mWearableRecyclerView.setAdapter(mCustomRecyclerAdapter);
    }

    // Called by WearableRecyclerView when an item is selected (check onCreate() for
    // initialization).
    public void itemSelected(String data) {

        Log.d(TAG, "itemSelected()");

        boolean areNotificationsEnabled = mNotificationManagerCompat.areNotificationsEnabled();

        // If notifications are disabled, allow user to enable.
        if (!areNotificationsEnabled) {
            // Because the user took an action to create a notification, we create a prompt to let
            // the user re-enable notifications for this application again.
            Snackbar snackbar = Snackbar
                    .make(
                            mMainFrameLayout,
                            "", // Not enough space for both text and action text.
                            Snackbar.LENGTH_LONG)
                    .setAction("Enable Notifications", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Links to this app's notification settings.
                            openNotificationSettingsForApp();
                        }
                    });
            snackbar.show();
            return;
        }

        String notificationStyle = data;

        switch (notificationStyle) {
            case BIG_TEXT_STYLE:
                generateBigTextStyleNotification();
                break;

            case BIG_PICTURE_STYLE:
                generateBigPictureStyleNotification();
                break;

            case INBOX_STYLE:
                generateInboxStyleNotification();
                break;

            case MESSAGING_STYLE:
                generateMessagingStyleNotification();
                break;

            default:
                // continue below.
        }
    }

    /*
     * Generates a BIG_TEXT_STYLE Notification that supports both Wear 1.+ and Wear 2.0.
     *
     * IMPORTANT NOTE:
     * This method includes extra code to replicate Notification Styles behavior from Wear 1.+ and
     * phones on Wear 2.0, i.e., the notification expands on click. To see the specific code in the
     * method, search for "REPLICATE_NOTIFICATION_STYLE_CODE".
     *
     * Notification Styles behave slightly different on Wear 2.0 when they are launched by a
     * native/local Wear app, i.e., they will NOT expand when the user taps them but will instead
     * take the user directly into the local app for the richest experience. In contrast, a bridged
     * Notification launched from the phone will expand with the style details (whether there is a
     * local app or not).
     *
     * If you want to see the new behavior, please review the generateBigPictureStyleNotification()
     * and generateMessagingStyleNotification() methods.
     */
    private void generateBigTextStyleNotification() {

        Log.d(TAG, "generateBigTextStyleNotification()");

        // Main steps for building a BIG_TEXT_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the BIG_TEXT_STYLE
        //      3. Set up main Intent for notification
        //      4. Create additional Actions for the Notification
        //      5. Build and issue the notification

        // 0. Get your data (everything unique per Notification).
        MockDatabase.BigTextStyleReminderAppData bigTextStyleReminderAppData =
                MockDatabase.getBigTextStyleData();

        // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).
        String notificationChannelId =
                NotificationUtil.createNotificationChannel(this, bigTextStyleReminderAppData);

        // 2. Build the BIG_TEXT_STYLE
        BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                // Overrides ContentText in the big form of the template.
                .bigText(bigTextStyleReminderAppData.getBigText())
                // Overrides ContentTitle in the big form of the template.
                .setBigContentTitle(bigTextStyleReminderAppData.getBigContentTitle())
                // Summary line after the detail section in the big form of the template
                // Note: To improve readability, don't overload the user with info. If Summary Text
                // doesn't add critical information, you should skip it.
                .setSummaryText(bigTextStyleReminderAppData.getSummaryText());


        // 3. Set up main Intent for notification.
        Intent mainIntent = new Intent(this, BigTextMainActivity.class);

        PendingIntent mainPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        // 4. Create additional Actions (Intents) for the Notification.

        // In our case, we create two additional actions: a Snooze action and a Dismiss action.

        // Snooze Action.
        Intent snoozeIntent = new Intent(this, BigTextIntentService.class);
        snoozeIntent.setAction(BigTextIntentService.ACTION_SNOOZE);

        PendingIntent snoozePendingIntent = PendingIntent.getService(this, 0, snoozeIntent, 0);
        NotificationCompat.Action snoozeAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_alarm_white_48dp,
                        "Snooze",
                        snoozePendingIntent)
                        .build();

        // Dismiss Action.
        Intent dismissIntent = new Intent(this, BigTextIntentService.class);
        dismissIntent.setAction(BigTextIntentService.ACTION_DISMISS);

        PendingIntent dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0);
        NotificationCompat.Action dismissAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_cancel_white_48dp,
                        "Dismiss",
                        dismissPendingIntent)
                        .build();


        // 5. Build and issue the notification.

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. Later, we use the same global builder to get back the notification
        // we built here for the snooze action, that is, canceling the notification and relaunching
        // it several seconds later.

        // Notification Channel Id is ignored for Android pre O (26).
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), notificationChannelId);

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);

        notificationCompatBuilder
                // BIG_TEXT_STYLE sets title and content.
                .setStyle(bigTextStyle)
                .setContentTitle(bigTextStyleReminderAppData.getContentTitle())
                .setContentText(bigTextStyleReminderAppData.getContentText())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_alarm_white_48dp))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                .setCategory(Notification.CATEGORY_REMINDER)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(bigTextStyleReminderAppData.getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(bigTextStyleReminderAppData.getChannelLockscreenVisibility())

                // Adds additional actions specified above.
                .addAction(snoozeAction)
                .addAction(dismissAction);

        /* REPLICATE_NOTIFICATION_STYLE_CODE:
         * You can replicate Notification Style functionality on Wear 2.0 (24+) by not setting the
         * main content intent, that is, skipping the call setContentIntent(). However, you need to
         * still allow the user to open the native Wear app from the Notification itself, so you
         * add an action to launch the app.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            // Enables launching app in Wear 2.0 while keeping the old Notification Style behavior.
            NotificationCompat.Action mainAction = new NotificationCompat.Action.Builder(
                    R.drawable.ic_launcher,
                    "Open",
                    mainPendingIntent)
                    .build();

            notificationCompatBuilder.addAction(mainAction);

        } else {
            // Wear 1.+ still functions the same, so we set the main content intent.
            notificationCompatBuilder.setContentIntent(mainPendingIntent);
        }


        Notification notification = notificationCompatBuilder.build();

        mNotificationManagerCompat.notify(NOTIFICATION_ID, notification);

        // Close app to demonstrate notification in steam.
        finish();
    }

    /*
     * Generates a BIG_PICTURE_STYLE Notification that supports both Wear 1.+ and Wear 2.0.
     *
     * This example Notification is a social post. It allows updating the notification with
     * comments/responses via RemoteInput and the BigPictureSocialIntentService on 24+ (N+) and
     * Wear devices.
     *
     * IMPORTANT NOTE:
     * Notification Styles behave slightly different on Wear 2.0 when they are launched by a
     * native/local Wear app, i.e., they will NOT expand when the user taps them but will instead
     * take the user directly into the local app for the richest experience. In contrast, a bridged
     * Notification launched from the phone will expand with the style details (whether there is a
     * local app or not).
     *
     * If you want to enable an action on your Notification without launching the app, you can do so
     * with the setHintDisplayActionInline() feature (shown below), but this only allows one action.
     *
     * If you wish to replicate the original experience of a bridged notification, please review the
     * generateBigTextStyleNotification() method above to see how.
     */
    private void generateBigPictureStyleNotification() {

        Log.d(TAG, "generateBigPictureStyleNotification()");

        // Main steps for building a BIG_PICTURE_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the BIG_PICTURE_STYLE
        //      3. Set up main Intent for notification
        //      4. Set up RemoteInput, so users can input (keyboard and voice) from notification
        //      5. Build and issue the notification

        // 0. Get your data (everything unique per Notification).
        MockDatabase.BigPictureStyleSocialAppData bigPictureStyleSocialAppData =
                MockDatabase.getBigPictureStyleData();

        // 1. Build the BIG_PICTURE_STYLE.
        BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                // Provides the bitmap for the BigPicture notification.
                .bigPicture(
                        BitmapFactory.decodeResource(
                                getResources(),
                                bigPictureStyleSocialAppData.getBigImage()))
                // Overrides ContentTitle in the big form of the template.
                .setBigContentTitle(bigPictureStyleSocialAppData.getBigContentTitle())
                // Summary line after the detail section in the big form of the template.
                .setSummaryText(bigPictureStyleSocialAppData.getSummaryText());

        // 2. Create/Retrieve Notification Channel for O and beyond devices (26+).
        String notificationChannelId =
                NotificationUtil.createNotificationChannel(this, bigPictureStyleSocialAppData);

        // 3. Set up main Intent for notification.
        Intent mainIntent = new Intent(this, BigPictureSocialMainActivity.class);

        PendingIntent mainPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // 4. Set up a RemoteInput Action, so users can input (keyboard, drawing, voice) directly
        // from the notification without entering the app.

        // Create the RemoteInput.
        String replyLabel = getString(R.string.reply_label);
        RemoteInput remoteInput =
                new RemoteInput.Builder(BigPictureSocialIntentService.EXTRA_COMMENT)
                        .setLabel(replyLabel)
                        // List of quick response choices for any wearables paired with the phone.
                        .setChoices(bigPictureStyleSocialAppData.getPossiblePostResponses())
                        .build();

        // Create PendingIntent for service that handles input.
        Intent replyIntent = new Intent(this, BigPictureSocialIntentService.class);
        replyIntent.setAction(BigPictureSocialIntentService.ACTION_COMMENT);
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
                        // Add WearableExtender to enable inline actions.
                        .extend(inlineActionForWear2)
                        .build();

        // 5. Build and issue the notification

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. Later, we use the same global builder to get back the notification
        // we built here for a comment on the post.

        // Notification Channel Id is ignored for Android pre O (26).
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), notificationChannelId);

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);

        notificationCompatBuilder
                // BIG_PICTURE_STYLE sets title and content.
                .setStyle(bigPictureStyle)
                .setContentTitle(bigPictureStyleSocialAppData.getContentTitle())
                .setContentText(bigPictureStyleSocialAppData.getContentText())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_person_black_48dp))
                .setContentIntent(mainPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                .setSubText(Integer.toString(1))
                .addAction(replyAction)
                .setCategory(Notification.CATEGORY_SOCIAL)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(bigPictureStyleSocialAppData.getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(bigPictureStyleSocialAppData.getChannelLockscreenVisibility())
                // Notifies system that the main launch intent is an Activity.
                .extend(new NotificationCompat.WearableExtender()
                        .setHintContentIntentLaunchesActivity(true));

        // If the phone is in "Do not disturb mode, the user will still be notified if
        // the sender(s) is starred as a favorite.
        for (String name : bigPictureStyleSocialAppData.getParticipants()) {
            notificationCompatBuilder.addPerson(name);
        }

        Notification notification = notificationCompatBuilder.build();
        mNotificationManagerCompat.notify(NOTIFICATION_ID, notification);

        // Close app to demonstrate notification in steam.
        finish();
    }

    /*
     * Generates a INBOX_STYLE Notification that supports both Wear 1.+ and Wear 2.0.
     */
    private void generateInboxStyleNotification() {

        Log.d(TAG, "generateInboxStyleNotification()");


        // Main steps for building a INBOX_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the INBOX_STYLE
        //      3. Set up main Intent for notification
        //      4. Build and issue the notification

        // 0. Get your data (everything unique per Notification).
        MockDatabase.InboxStyleEmailAppData inboxStyleEmailAppData =
                MockDatabase.getInboxStyleData();

        // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).
        String notificationChannelId =
                NotificationUtil.createNotificationChannel(this, inboxStyleEmailAppData);

        // 2. Build the INBOX_STYLE
        InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                // This title is slightly different than regular title, since I know INBOX_STYLE is
                // available.
                .setBigContentTitle(inboxStyleEmailAppData.getBigContentTitle())
                .setSummaryText(inboxStyleEmailAppData.getSummaryText());

        // Add each summary line of the new emails, you can add up to 5.
        for (String summary : inboxStyleEmailAppData.getIndividualEmailSummary()) {
            inboxStyle.addLine(summary);
        }

        // 3. Set up main Intent for notification.
        Intent mainIntent = new Intent(this, InboxMainActivity.class);

        PendingIntent mainPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // 4. Build and issue the notification.

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. However, we don't need to update this notification later, so we
        // will not need to set a global builder for access to the notification later.

        // Notification Channel Id is ignored for Android pre O (26).
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), notificationChannelId);

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);

        notificationCompatBuilder
                // INBOX_STYLE sets title and content.
                .setStyle(inboxStyle)
                .setContentTitle(inboxStyleEmailAppData.getContentTitle())
                .setContentText(inboxStyleEmailAppData.getContentText())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_person_black_48dp))
                .setContentIntent(mainPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                // Sets large number at the right-hand side of the notification for Wear 1.+.
                .setSubText(Integer.toString(inboxStyleEmailAppData.getNumberOfNewEmails()))

                .setCategory(Notification.CATEGORY_EMAIL)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(inboxStyleEmailAppData.getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(inboxStyleEmailAppData.getChannelLockscreenVisibility())
                // Notifies system that the main launch intent is an Activity.
                .extend(new NotificationCompat.WearableExtender()
                        .setHintContentIntentLaunchesActivity(true));

        // If the phone is in "Do not disturb mode, the user will still be notified if
        // the sender(s) is starred as a favorite.
        for (String name : inboxStyleEmailAppData.getParticipants()) {
            notificationCompatBuilder.addPerson(name);
        }

        Notification notification = notificationCompatBuilder.build();
        mNotificationManagerCompat.notify(NOTIFICATION_ID, notification);

        // Close app to demonstrate notification in steam.
        finish();
    }

    /*
     * Generates a MESSAGING_STYLE Notification that supports both Wear 1.+ and Wear 2.0. For
     * devices on API level 24 (Wear 2.0) and after, displays MESSAGING_STYLE. Otherwise, displays
     * a basic BIG_TEXT_STYLE.
     *
     * IMPORTANT NOTE:
     * Notification Styles behave slightly different on Wear 2.0 when they are launched by a
     * native/local Wear app, i.e., they will NOT expand when the user taps them but will instead
     * take the user directly into the local app for the richest experience. In contrast, a bridged
     * Notification launched from the phone will expand with the style details (whether there is a
     * local app or not).
     *
     * If you want to enable an action on your Notification without launching the app, you can do so
     * with the setHintDisplayActionInline() feature (shown below), but this only allows one action.
     *
     * If you wish to replicate the original experience of a bridged notification, please review the
     * generateBigTextStyleNotification() method above to see how.
     */
    private void generateMessagingStyleNotification() {

        Log.d(TAG, "generateMessagingStyleNotification()");

        // Main steps for building a MESSAGING_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the MESSAGING_STYLE
        //      3. Set up main Intent for notification
        //      4. Set up RemoteInput (users can input directly from notification)
        //      5. Build and issue the notification

        // 0. Get your data (everything unique per Notification).
        MockDatabase.MessagingStyleCommsAppData messagingStyleCommsAppData =
                MockDatabase.getMessagingStyleData(getApplicationContext());

        // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).
        String notificationChannelId =
                NotificationUtil.createNotificationChannel(this, messagingStyleCommsAppData);

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
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        // 4. Set up a RemoteInput Action, so users can input (keyboard, drawing, voice) directly
        // from the notification without entering the app.

        // Create the RemoteInput specifying this key.
        String replyLabel = getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(MessagingIntentService.EXTRA_REPLY)
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

        // Because we want this to be a new notification (not updating current notification), we
        // create a new Builder. Later, we update this same notification, so we need to save this
        // Builder globally (as outlined earlier).

        // Notification Channel Id is ignored for Android pre O (26).
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), notificationChannelId);

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);

        notificationCompatBuilder
                // MESSAGING_STYLE sets title and content for Wear 1.+ and Wear 2.0 devices.
                .setStyle(messagingStyle)
                .setContentTitle(contentTitle)
                .setContentText(messagingStyleCommsAppData.getContentText())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_person_black_48dp))
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

        Notification notification = notificationCompatBuilder.build();
        mNotificationManagerCompat.notify(NOTIFICATION_ID, notification);

        // Close app to demonstrate notification in steam.
        finish();
    }

    /**
     * Helper method for the SnackBar action, i.e., if the user has this application's notifications
     * disabled, this opens up the dialog to turn them back on after the user requests a
     * Notification launch.
     *
     * IMPORTANT NOTE: You should not do this action unless the user takes an action to see your
     * Notifications like this sample demonstrates. Spamming users to re-enable your notifications
     * is a bad idea.
     */
    private void openNotificationSettingsForApp() {
        // Links to this app's notification settings
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);
        startActivity(intent);
    }

    @Override
    public AmbientMode.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }

    private class MyAmbientCallback extends AmbientMode.AmbientCallback {
        /** Prepares the UI for ambient mode. */
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);

            Log.d(TAG, "onEnterAmbient() " + ambientDetails);
            // In our case, the assets are already in black and white, so we don't update UI.
        }

        /** Restores the UI to active (non-ambient) mode. */
        @Override
        public void onExitAmbient() {
            super.onExitAmbient();

            Log.d(TAG, "onExitAmbient()");
            // In our case, the assets are already in black and white, so we don't update UI.
        }
    }

}
