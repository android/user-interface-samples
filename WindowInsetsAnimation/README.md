WindowInsetsAnimation sample
============

This sample shows how to react to the on-screen keyboard (IME) changing visibility, and also controlling the IME's visibility. To do this, the sample uses the new [WindowInsetsAnimation](https://developer.android.com/reference/android/view/WindowInsetsAnimation) and [WindowInsetsAnimationController](https://developer.android.com/reference/android/view/WindowInsetsAnimationController) APIs in [Android 11](https://developer.android.com/11).

![Animation showing app in use](./images/demos.gif)

## Features

The app displays a fake instant-message style conversation, and has two key pieces of functionality:

### #1: Reacting to the IME coming on/off screen

When the IME is displayed due to a implicit event (such as the `EditText` being focused), the UI will react as the IME animates in by moving any relevant views in unison. This creates the effect of the IME pushing the app's UI up. You can see this in the demo above on the right. 

In terms of implementation, this is done using the new [`WindowInsetsAnimation.Callback`](https://developer.android.com/reference/android/view/WindowInsetsAnimation.Callback), which allows views to be notified when an insets animation is taking place. In this sample, we have provided an implementation called [`TranslateDeferringInsetsAnimationCallback`](./app/src/main/java/com/google/android/samples/insetsanimation/TranslateDeferringInsetsAnimationCallback.kt) which automatically moves the host view between it's position before and after the IME visibility change. This is used on both the text field and scrolling views, allowing them both to move in unison with the IME.

### #2: Controlling the IME

When the user scrolls up on the conversation list, to the end of the list's content, and keeps scrolling (aka over-scrolling) the sample takes control of the IME and animates it on/off screen as part of the scroll gesture. You can see this in the demo above on the left, as the IME scrolls on and off screen with the conversation.

In terms of implementation, this is done using the new [`WindowInsetsAnimationController`](https://developer.android.com/reference/android/view/WindowInsetsAnimationController) class in Android 11. A nested scrolling `LinearLayout` which automatically reacts to scrolls by insetting the IME on/off screen is provided in [`InsetsAnimationLinearLayout`](./app/src/main/java/com/google/android/samples/insetsanimation/InsetsAnimationLinearLayout.kt).

A [`View.OnTouchListener`](https://developer.android.com/reference/kotlin/android/view/View.OnTouchListener) implementation, for use with non-scrolling views is also provided as [`InsetsAnimationTouchListener`](./app/src/main/java/com/google/android/samples/insetsanimation/InsetsAnimationTouchListener.kt).

## Caveats

This sample is built and tested against Android 11 Developer Preview 4 (DP4), and may not work on earlier versions.

## Getting Started

Clone this repository, enter the top level directory and run `./gradlew tasks`
to get an overview of all the tasks available for this project.

Some important tasks are:

```
assembleDebug - Assembles all Debug builds.
installDebug - Installs the Debug build.
connectedAndroidTest - Installs and runs the tests for Debug build on connected
devices.
test - Run all unit tests.
```

## Support

If you've found an error in this sample, please file an issue:
https://github.com/android/user-interface/issues

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub.
