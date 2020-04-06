
Jetpack WindowManager Sample
===================================

This sample demonstrates how to use the new Jetpack Window Manager library.
This library allows an application to support new device form factors as well as
provide a common API surface for new Window Manager features throughout old and
new platform versions. The initial release as well as this sample focuses on
foldable devices, and will be extended to support more display types and window
features.

Introduction
------------

New foldable devices are appearing on the market that provide a set of unique
hardware features. Optimizing your app for these new devices and form-factors
allow you to bring a differentiating experience and allow your users to take
full advantage of whatever device they are on. The Jetpack Window Manager
library allows you to handle all of these devices through a common API as well
as through different versions of Android.

You can determine what `DisplayFeatures`s are available on the device and their
`Rect` location. The first version of the library includes support for two types
of features: `TYPE_FOLD` and `TYPE_HINGE`. For a `TYPE_FOLD`, the bounding
rectangle is expected to be zero-high or zero-wide indicating that there is no
inaccessible region but still reporting the position on screen.

Besides `DisplayFeature`s, the app can also determine the `DeviceState` for the
current configuration of the device. These are defined as different postures:
`POSTURE_UNKNOWN`, `POSTURE_CLOSED`, `POSTURE_HALF_OPENED`, `POSTURE_OPENED`,
`POSTURE_FLIPPED`. Each device can decide what subset of postures to report and
when, based on their specific hardware.

We also have two callbacks to be alerted of new `DeviceState` changes as well
as `WindowLayoutInfo` changes.

``` java
//DeviceState changes
windowManager.registerDeviceStateChangeCallback(
    mainThreadExecutor /* Executor */,
    callback /* Consumer<DeviceState> */)

//Layout state changes
windowManager.registerLayoutChangeCallback(
    mainThreadExecutor /* Executor */,
    callback /* Consumer<WindowLayoutInfo> */)

```
With these you can move your views around the `DisplayFeature`s that are
available to provide a different UX. You can see an example of this in the
`SplitLayoutActivity` class.

This is an initial Alpha release of the library so the API surface may change
and grow with time. Any feedback is greatly appreciated on things you would
like to see added or changed!

For more information on the Jetpack Window Manager library, see the
[Jetpack Window Manager release page][1].

[1]: https://developer.android.com/jetpack/androidx/releases/window

Pre-requisites
--------------

- Android SDK 28
- Android Build Tools v28.0.3
- Android Support Repository

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
