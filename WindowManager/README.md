
Jetpack WindowManager Sample
===================================

This sample demonstrates how to use the new Jetpack WindowManager library.
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
full advantage of whatever device they are on. The Jetpack WindowManager
library allows you to handle all of these devices through a common API as well
as through different versions of Android.

You can determine what `DisplayFeatures`s are available on the device and their
`Rect` location. The alpha02 release introduces a new `DisplayFeature` class with an updated callback contract to notify your application when a `DisplayFeature` changes. You can register/unregister the callback using these methods:
``` java
registerLayoutChangeCallback(@NonNull Executor executor, @NonNull Consumer<WindowLayoutInfo> callback)
unregisterLayoutChangeCallback(@NonNull Consumer<WindowLayoutInfo> callback)
```

The `WindowLayoutInfo` contains a list of the instances of `DisplayFeature` that are located within the window.

The `FoldingFeature` class implements the `DisplayFeature` interface, which includes information about these types of features:

```
TYPE_FOLD
TYPE_HINGE
```

And their possible folding states:
```
STATE_FLAT
STATE_HALF_OPENED
STATE_FLIPPED
```

To access the new state you can use the FoldingFeature information returned to the registered callback:
``` java
class LayoutStateChangeCallback : Consumer<WindowLayoutInfo> {
    override fun accept(newLayoutInfo: WindowLayoutInfo) {
        // TODO
        // Check newLayoutInfo. getDisplayFeatures()
        // to see if it is a FoldingFeature and retrieve the information
    }
}
```

You can see an example of this in the `SplitLayoutActivity` class.

WindowMetrics
-------------

The WindowManager library includes a new WindowMetrics API to get information about your current window state and the maximum window size for the current state of the system.

The API results don’t include information about the system insets such as the status bar or action bar, since those values aren’t available before the first layout pass. These bounds also don’t react to any changes in layout params that might occur when your layout is inflated. If you are looking for specific information for laying out views you should get the width/height from the Configuration object or the DecorView.

To access these APIs, you need to get an instance of the WindowManager object.

``` java
var windowManager = WindowManager(this /* context */)
```

From here you now have access to the WindowMetrics APIs and can easily call

``` java
windowManager.currentWindowMetrics
windowManager.maximumWindowMetrics
```

Notes
-----

This is the second Alpha release of the library so the API surface may still change
and grow with time. Any feedback is greatly appreciated on things you would
like to see added or changed!

For more information on the Jetpack Window Manager library, see the
[Jetpack Window Manager release page][1].

[1]: https://developer.android.com/jetpack/androidx/releases/window

Pre-requisites
--------------

- Android SDK 30
- Android Studio 4.1.2
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
