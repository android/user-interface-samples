
Android BasicImmersiveMode Sample
===================================

Sample demonstrating the use of immersive mode to hide the system and navigation bars for
full screen applications.

Introduction
------------

'Immersive Mode' is a new UI mode which improves 'hide full screen' and 'hide nav bar' 
modes, by letting users swipe the bars in and out.

This sample demonstrates how to enable and disable immersive mode programmatically.

Immersive mode was introduced in Android 4.4 (Api Level 19). It is toggled using the 
SYSTEM_UI_FLAG_IMMERSIVE system ui flag. When combined with the SYSTEM_UI_FLAG_HIDE_NAVIGATION and SYSTEM_UI_FLAG_FULLSCREEN  flags, hides the navigation and status bars and lets your app capture all touch events on the screen.

Pre-requisites
--------------

- Android SDK 28
- Android Build Tools v28.0.3
- Android Support Repository

Screenshots
-------------

<img src="screenshots/1-activity.png" height="400" alt="Screenshot"/> <img src="screenshots/2-immersive.png" height="400" alt="Screenshot"/> 

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Stack Overflow: http://stackoverflow.com/questions/tagged/android

If you've found an error in this sample, please file an issue:
https://github.com/android/user-interface

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.
