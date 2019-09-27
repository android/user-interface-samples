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

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.example.android.wearable.wear.wearnotifications.R;
import com.example.android.wearable.wear.wearnotifications.StandaloneMainActivity;

/**
 * Template class meant to include functionality for your Messaging App. (This project's main focus
 * is on Notification Styles.)
 */
public class MessagingMainActivity extends Activity {

    private static final String TAG = "MessagingMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_main);

        // Cancel Notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(StandaloneMainActivity.NOTIFICATION_ID);

        // TODO: Handle and display message/conversation from your database

        // NOTE: You can retrieve the EXTRA_REMOTE_INPUT_DRAFT sent by the system when a user
        // inadvertently closes a messaging notification to pre-populate the reply text field so
        // the user can finish their reply.
    }
}
