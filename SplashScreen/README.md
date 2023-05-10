Android SplashScreen Sample
===========================

Android 12 adds the new SplashScreen API that allows developers to add a customized animated screen
at the app launch.

## Introduction

Showing a splash screen is an effective way to improve the perceived performance of an app launch.
With the SplashScreen API introduced in Android 12 (API level 31), apps can specify an animated icon
to be shown on its splash screen.

The 'core-splashscreen' library ports the API back to Android Lollipop (API Level 21). We use this
library in this sample. See [Usage of the core-splashscreen library][1] for the documentation.

[1]: https://developer.android.com/reference/androidx/core/splashscreen/SplashScreen

## About the sample

This sample exports 3 launcher icons.

- "Default Splash Screen" shows the default splash screen without any customization.
- "Animated Splash Screen" shows an animated icon on the splash screen.
- "Custom Splash Screen" shows the splash screen with a custom animation to the app content.

## Screenshots

<img src="screenshots/main.gif" width="320" height="676" alt="Screenshot"/>

## Getting Started

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

## Support

- Stack Overflow: http://stackoverflow.com/questions/tagged/android

If you've found an error in this sample, please file an issue:
https://github.com/android/user-interface

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.
