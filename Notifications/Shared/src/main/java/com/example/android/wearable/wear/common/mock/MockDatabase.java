/*
 * Copyright (C) 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wearable.wear.common.mock;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.MessagingStyle;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.example.android.wearable.wear.wearnotifications.common.R;

import java.util.ArrayList;

/** Mock data for each of the Notification Style Demos. */
public final class MockDatabase {

    public static BigTextStyleReminderAppData getBigTextStyleData() {
        return BigTextStyleReminderAppData.getInstance();
    }

    public static BigPictureStyleSocialAppData getBigPictureStyleData() {
        return BigPictureStyleSocialAppData.getInstance();
    }

    public static InboxStyleEmailAppData getInboxStyleData() {
        return InboxStyleEmailAppData.getInstance();
    }

    public static MessagingStyleCommsAppData getMessagingStyleData(Context context) {
        return MessagingStyleCommsAppData.getInstance(context);
    }

    /** Represents data needed for BigTextStyle Notification. */
    public static class BigTextStyleReminderAppData extends MockNotificationData {

        private static BigTextStyleReminderAppData sInstance = null;

        // Unique data for this Notification.Style:
        private String mBigContentTitle;
        private String mBigText;
        private String mSummaryText;

        public static BigTextStyleReminderAppData getInstance() {
            if (sInstance == null) {
                sInstance = getSync();
            }

            return sInstance;
        }

        private static synchronized BigTextStyleReminderAppData getSync() {
            if (sInstance == null) {
                sInstance = new BigTextStyleReminderAppData();
            }

            return sInstance;
        }

        private BigTextStyleReminderAppData() {

            // Standard Notification values:
            // Title for API <16 (4.0 and below) devices.
            mContentTitle = "Don't forget to...";
            // Content for API <24 (4.0 and below) devices.
            mContentText = "Feed Dogs and check garage!";
            mPriority = NotificationCompat.PRIORITY_DEFAULT;

            // BigText Style Notification values:
            mBigContentTitle = "Don't forget to...";
            mBigText =
                    "... feed the dogs before you leave for work, and check the garage to "
                            + "make sure the door is closed.";
            mSummaryText = "Dogs and Garage";

            // Notification channel values (for devices targeting 26 and above):
            mChannelId = "channel_reminder_1";
            // The user-visible name of the channel.
            mChannelName = "Sample Reminder";
            // The user-visible description of the channel.
            mChannelDescription = "Sample Reminder Notifications";
            mChannelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            mChannelEnableVibrate = false;
            mChannelLockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC;
        }

        public String getBigContentTitle() {
            return mBigContentTitle;
        }

        public String getBigText() {
            return mBigText;
        }

        public String getSummaryText() {
            return mSummaryText;
        }

        @Override
        public String toString() {
            return getBigContentTitle() + getBigText();
        }
    }

    /** Represents data needed for BigPictureStyle Notification. */
    public static class BigPictureStyleSocialAppData extends MockNotificationData {

        private static BigPictureStyleSocialAppData sInstance = null;

        // Unique data for this Notification.Style:
        private int mBigImage;
        private String mBigContentTitle;
        private String mSummaryText;

        private CharSequence[] mPossiblePostResponses;

        private ArrayList<String> mParticipants;

        public static BigPictureStyleSocialAppData getInstance() {
            if (sInstance == null) {
                sInstance = getSync();
            }
            return sInstance;
        }

        private static synchronized BigPictureStyleSocialAppData getSync() {
            if (sInstance == null) {
                sInstance = new BigPictureStyleSocialAppData();
            }

            return sInstance;
        }

        private BigPictureStyleSocialAppData() {
            // Standard Notification values:
            // Title/Content for API <16 (4.0 and below) devices.
            mContentTitle = "Bob's Post";
            mContentText = "[Picture] Like my shot of Earth?";
            mPriority = NotificationCompat.PRIORITY_HIGH;

            // Style notification values:
            mBigImage = R.drawable.earth;
            mBigContentTitle = "Bob's Post";
            mSummaryText = "Like my shot of Earth?";

            // This would be possible responses based on the contents of the post.
            mPossiblePostResponses = new CharSequence[] {"Yes", "No", "Maybe?"};

            mParticipants = new ArrayList<>();
            mParticipants.add("Bob Smith");

            // Notification channel values (for devices targeting 26 and above):
            mChannelId = "channel_social_1";
            // The user-visible name of the channel.
            mChannelName = "Sample Social";
            // The user-visible description of the channel.
            mChannelDescription = "Sample Social Notifications";
            mChannelImportance = NotificationManager.IMPORTANCE_HIGH;
            mChannelEnableVibrate = true;
            mChannelLockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE;
        }

        public int getBigImage() {
            return mBigImage;
        }

        public String getBigContentTitle() {
            return mBigContentTitle;
        }

        public String getSummaryText() {
            return mSummaryText;
        }

        public CharSequence[] getPossiblePostResponses() {
            return mPossiblePostResponses;
        }

        public ArrayList<String> getParticipants() {
            return mParticipants;
        }

        @Override
        public String toString() {
            return getContentTitle() + " - " + getContentText();
        }
    }

    /** Represents data needed for InboxStyle Notification. */
    public static class InboxStyleEmailAppData extends MockNotificationData {

        private static InboxStyleEmailAppData sInstance = null;

        // Unique data for this Notification.Style:
        private int mNumberOfNewEmails;
        private String mBigContentTitle;
        private String mSummaryText;
        private ArrayList<String> mIndividualEmailSummary;

        private ArrayList<String> mParticipants;

        public static InboxStyleEmailAppData getInstance() {
            if (sInstance == null) {
                sInstance = getSync();
            }
            return sInstance;
        }

        private static synchronized InboxStyleEmailAppData getSync() {
            if (sInstance == null) {
                sInstance = new InboxStyleEmailAppData();
            }

            return sInstance;
        }

        private InboxStyleEmailAppData() {
            // Standard Notification values:
            // Title/Content for API <16 (4.0 and below) devices.
            mContentTitle = "5 new emails";
            mContentText = "from Jane, Jay, Alex +2 more";
            mNumberOfNewEmails = 5;
            mPriority = NotificationCompat.PRIORITY_DEFAULT;

            // Style notification values:
            mBigContentTitle = "5 new emails from Jane, Jay, Alex +2";
            mSummaryText = "New emails";

            // Add each summary line of the new emails, you can add up to 5.
            mIndividualEmailSummary = new ArrayList<>();
            mIndividualEmailSummary.add("Jane Faab  -   Launch Party is here...");
            mIndividualEmailSummary.add("Jay Walker -   There's a turtle on the server!");
            mIndividualEmailSummary.add("Alex Chang -   Check this out...");
            mIndividualEmailSummary.add("Jane Johns -   Check in code?");
            mIndividualEmailSummary.add("John Smith -   Movies later....");

            // If the phone is in "Do not disturb mode, the user will still be notified if
            // the user(s) is starred as a favorite.
            mParticipants = new ArrayList<>();
            mParticipants.add("Jane Faab");
            mParticipants.add("Jay Walker");
            mParticipants.add("Alex Chang");
            mParticipants.add("Jane Johns");
            mParticipants.add("John Smith");

            // Notification channel values (for devices targeting 26 and above):
            mChannelId = "channel_email_1";
            // The user-visible name of the channel.
            mChannelName = "Sample Email";
            // The user-visible description of the channel.
            mChannelDescription = "Sample Email Notifications";
            mChannelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            mChannelEnableVibrate = true;
            mChannelLockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE;
        }

        public int getNumberOfNewEmails() {
            return mNumberOfNewEmails;
        }

        public String getBigContentTitle() {
            return mBigContentTitle;
        }

        public String getSummaryText() {
            return mSummaryText;
        }

        public ArrayList<String> getIndividualEmailSummary() {
            return mIndividualEmailSummary;
        }

        public ArrayList<String> getParticipants() {
            return mParticipants;
        }

        @Override
        public String toString() {
            return getContentTitle() + " " + getContentText();
        }
    }

    /** Represents data needed for MessagingStyle Notification. */
    public static class MessagingStyleCommsAppData extends MockNotificationData {

        private static MessagingStyleCommsAppData sInstance = null;

        // Unique data for this Notification.Style:
        private ArrayList<MessagingStyle.Message> mMessages;
        // String of all mMessages.
        private String mFullConversation;
        // Name preferred when replying to chat.
        private Person mMe;
        private ArrayList<Person> mParticipants;

        private CharSequence[] mReplyChoicesBasedOnLastMessages;

        public static MessagingStyleCommsAppData getInstance(Context context) {
            if (sInstance == null) {
                sInstance = getSync(context);
            }
            return sInstance;
        }

        private static synchronized MessagingStyleCommsAppData getSync(Context context) {
            if (sInstance == null) {
                sInstance = new MessagingStyleCommsAppData(context);
            }

            return sInstance;
        }

        private MessagingStyleCommsAppData(Context context) {
            // Standard notification values:
            // Content for API <24 (M and below) devices.
            // Note: I am actually hardcoding these Strings based on info below. You would be
            // pulling these values from the same source in your database. I leave this up here, so
            // you can see the standard parts of a Notification first.
            mContentTitle = "3 Messages w/ Famous, Wendy";
            mContentText = "HEY, I see my house! :)";
            mPriority = NotificationCompat.PRIORITY_HIGH;

            // Create the users for the conversation.
            // Name preferred when replying to chat.
            mMe =
                    new Person.Builder()
                            .setName("Me MacDonald")
                            .setKey("1234567890")
                            .setUri("tel:1234567890")
                            .setIcon(
                                    IconCompat.createWithResource(context, R.drawable.me_macdonald))
                            .build();

            Person participant1 =
                    new Person.Builder()
                            .setName("Famous Fryer")
                            .setKey("9876543210")
                            .setUri("tel:9876543210")
                            .setIcon(
                                    IconCompat.createWithResource(context, R.drawable.famous_fryer))
                            .build();

            Person participant2 =
                    new Person.Builder()
                            .setName("Wendy Wonda")
                            .setKey("2233221122")
                            .setUri("tel:2233221122")
                            .setIcon(IconCompat.createWithResource(context, R.drawable.wendy_wonda))
                            .build();

            // If the phone is in "Do not disturb mode, the user will still be notified if
            // the user(s) is starred as a favorite.
            // Note: You don't need to add yourself, aka 'me', as a participant.
            mParticipants = new ArrayList<>();
            mParticipants.add(participant1);
            mParticipants.add(participant2);

            mMessages = new ArrayList<>();

            // For each message, you need the timestamp. In this case, we are using arbitrary longs
            // representing time in milliseconds.
            mMessages.add(
                    // When you are setting an image for a message, text does not display.
                    new MessagingStyle.Message("", 1528490641998l, participant1)
                            .setData("image/png", resourceToUri(context, R.drawable.earth)));

            mMessages.add(
                    new MessagingStyle.Message(
                            "Visiting the moon again? :P", 1528490643998l, mMe));

            mMessages.add(
                    new MessagingStyle.Message("HEY, I see my house!", 1528490645998l, participant2));

            // String version of the mMessages above.
            mFullConversation =
                    "Famous: [Picture of Moon]\n\n"
                            + "Me: Visiting the moon again? :P\n\n"
                            + "Wendy: HEY, I see my house! :)\n\n";

            // Responses based on the last messages of the conversation. You would use
            // Machine Learning to get these (https://developers.google.com/ml-kit/).
            mReplyChoicesBasedOnLastMessages =
                    new CharSequence[] {"Me too!", "How's the weather?", "You have good eyesight."};

            // Notification channel values (for devices targeting 26 and above):
            mChannelId = "channel_messaging_1";
            // The user-visible name of the channel.
            mChannelName = "Sample Messaging";
            // The user-visible description of the channel.
            mChannelDescription = "Sample Messaging Notifications";
            mChannelImportance = NotificationManager.IMPORTANCE_MAX;
            mChannelEnableVibrate = true;
            mChannelLockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE;
        }

        public ArrayList<MessagingStyle.Message> getMessages() {
            return mMessages;
        }

        public String getFullConversation() {
            return mFullConversation;
        }

        public Person getMe() {
            return mMe;
        }

        public int getNumberOfNewMessages() {
            return mMessages.size();
        }

        public ArrayList<Person> getParticipants() {
            return mParticipants;
        }

        public CharSequence[] getReplyChoicesBasedOnLastMessage() {
            return mReplyChoicesBasedOnLastMessages;
        }

        @Override
        public String toString() {
            return getFullConversation();
        }

        public boolean isGroupConversation() {
            return mParticipants.size() > 1;
        }
    }

    /** Represents standard data needed for a Notification. */
    public abstract static class MockNotificationData {

        // Standard notification values:
        protected String mContentTitle;
        protected String mContentText;
        protected int mPriority;

        // Notification channel values (O and above):
        protected String mChannelId;
        protected CharSequence mChannelName;
        protected String mChannelDescription;
        protected int mChannelImportance;
        protected boolean mChannelEnableVibrate;
        protected int mChannelLockscreenVisibility;

        // Notification Standard notification get methods:
        public String getContentTitle() {
            return mContentTitle;
        }

        public String getContentText() {
            return mContentText;
        }

        public int getPriority() {
            return mPriority;
        }

        // Channel values (O and above) get methods:
        public String getChannelId() {
            return mChannelId;
        }

        public CharSequence getChannelName() {
            return mChannelName;
        }

        public String getChannelDescription() {
            return mChannelDescription;
        }

        public int getChannelImportance() {
            return mChannelImportance;
        }

        public boolean isChannelEnableVibrate() {
            return mChannelEnableVibrate;
        }

        public int getChannelLockscreenVisibility() {
            return mChannelLockscreenVisibility;
        }
    }

    public static Uri resourceToUri(Context context, int resId) {
        return Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://"
                        + context.getResources().getResourcePackageName(resId)
                        + "/"
                        + context.getResources().getResourceTypeName(resId)
                        + "/"
                        + context.getResources().getResourceEntryName(resId));
    }
}
