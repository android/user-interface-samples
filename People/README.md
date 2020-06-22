Android People Sample
=====================

Android 11 makes a number of improvements to the way conversations are handled.

## Conversation notifications

Conversations are real-time bidirectional communication among two or more people. On Android 11,
conversations are presented in their own are in the notification shade.

In order to make a notification considered conversational, use [MessagingStyle][1] and publish
[people][2] as shortcuts by calling [ShortcutManager.setDynamicShortcuts][3]. It is also recommended
that you set a [LocusId][4] to the notification so that the system can correlate this notification
with the corresponding dynamic shortcut.

[1]: https://developer.android.com/reference/android/app/Notification.MessagingStyle
[2]: https://developer.android.com/reference/android/app/Person
[3]: https://developer.android.com/reference/android/content/pm/ShortcutManager#addDynamicShortcuts(java.util.List%3Candroid.content.pm.ShortcutInfo%3E)
[4]: https://developer.android.com/reference/android/content/pm/ShortcutInfo.Builder#setLocusId(android.content.LocusId)

## Bubbles

Bubbles are built into the Notification system. They float on top of other app content and follow
the user wherever they go. Bubbles can be expanded to reveal app functionality and information, and
can be collapsed when not being used.

### API Guidelines

#### Conversation Requirements
First fulfill the notification [conversation requirements][2] -- this involves implementing long-lived dynamic or [sharing shortcuts][3] and attaching the shortcut id to your messaging style notification.

#### Configure Activity

For your activity to display as a bubble, it must be resizable. You should expect that the bubble activity will be flagged to act like [documentLaunchMode=”always”][4].

    <activity
        android:name=".bubbles.BubbleActivity"
        android:label="@string/title_activity_bubble"
        android:resizeableActivity="true"
    />

#### Create your Bubble Metadata

Define your [BubbleMetadata][5], defining the properties relevant to your bubble (e.g. The activity used in the expanded bubble, size, auto-expanded and etc...) and [set it on your notification][6].

For more information on bubbles, see the [Bubbles][1] developer guide.

[1]: https://developer.android.com/guide/topics/ui/bubbles
[2]: https://developer.android.com/preview/features/conversations
[3]: https://developer.android.com/training/sharing/receive#providing-direct-share-targets
[4]: https://developer.android.com/guide/topics/manifest/activity-element#dlmode
[5]: https://developer.android.com/reference/androidx/core/app/NotificationCompat.BubbleMetadata.Builder
[6]: https://developer.android.com/reference/kotlin/androidx/core/app/NotificationCompat.Builder#setBubbleMetadata(androidx.core.app.NotificationCompat.BubbleMetadata)

## Pre-requisites

- Android 11 SDK

Android 10 supported Bubbles as a developer-preview feature, but this sample only supports Bubbles
on Android 11 or later.

## Screenshots

<img src="screenshots/bubble.png" height="400" alt="Screenshot"/> 

## Getting Started

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

## Support

- Stack Overflow: http://stackoverflow.com/questions/tagged/android

If you've found an error in this sample, please file an issue:
https://github.com/android/user-interface

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.
