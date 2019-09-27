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

import androidx.core.app.NotificationCompat;

/**
 * We use a Singleton for a global copy of the NotificationCompat.Builder to update active
 * Notifications from other Services/Activities.
 *
 * You have two options for updating your notifications:
 *
 *  1. Use a new NotificationCompatBuilder to create the Notification. This approach requires you
 *  to get *ALL* the information and pass it to the builder. We get all the information from a Mock
 *  Database and this is the approach used in the MainActivity.
 *
 *  2. Use an existing NotificationCompatBuilder to create a Notification. This approach requires
 *  you to store a reference to the original builder. The benefit is you only need the new/updated
 *  information for an existing notification. We use this approach in the IntentService handlers to
 *  update existing notifications.
 *
 *  IMPORTANT NOTE 1: You shouldn't save/modify the resulting Notification object using
 *  its member variables and/or legacy APIs. If you want to retain anything from update
 *  to update, retain the Builder as option 2 outlines.
 *
 *  IMPORTANT NOTE 2: If the global Notification Builder is lost because the process is killed, you
 *  should have a way to recreate the Notification Builder from a persistent state. (We do this as
 *  well in the sample, check the IntentServices.)
 */
public final class GlobalNotificationBuilder {

    private static NotificationCompat.Builder sGlobalNotificationCompatBuilder = null;

    /*
     * Empty constructor - We don't initialize builder because we rely on a null state to let us
     * know the Application's process was killed.
     */
    private GlobalNotificationBuilder() { }

    public static void setNotificationCompatBuilderInstance(NotificationCompat.Builder builder) {
        sGlobalNotificationCompatBuilder = builder;
    }

    public static NotificationCompat.Builder getNotificationCompatBuilderInstance() {
        return sGlobalNotificationCompatBuilder;
    }
}
