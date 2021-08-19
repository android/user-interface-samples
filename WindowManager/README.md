
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

You can determine what [`DisplayFeature`][0]'s are available on the device
and their [`bounds`][1].

Jetpack WindowManager introduces a new [`WindowInfoRepository`][2] interface
that can be used to receive `DisplayFeature` changes. You can collect the flow
taking into consideration the application lifecycle as in:

``` java
private lateinit var windowInfoRepository: WindowInfoRepository

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    windowInfoRepository = windowInfoRepository()

    // Create a new coroutine since repeatOnLifecycle is a suspend function
    lifecycleScope.launch(Dispatchers.Main) {
        // The block passed to repeatOnLifecycle is executed when the lifecycle
        // is at least STARTED and is cancelled when the lifecycle is STOPPED.
        // It automatically restarts the block when the lifecycle is STARTED again.
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            // Safely collect from windowInfoRepo when the lifecycle is STARTED
            // and stops collection when the lifecycle is STOPPED
            windowInfoRepository.windowLayoutInfo
                .collect { newLayoutInfo ->
                    updateStateLog(newLayoutInfo)
                    updateCurrentState(newLayoutInfo)
                }
        }
    }

    // Other initialization

}
```

The [`WindowLayoutInfo`][3] contains a list of the instances of
`DisplayFeature` that are located within the window.

The [`FoldingFeature`][4] class implements the `DisplayFeature` interface,
which includes information about the feature's state that can be retrieved
using the methods:

  * [`isSeparating`][20]
  * [`orientation`][21]
  * [`occlusionMode`][22]

You can see an example of this in the `DisplayFeaturesActivity` class.
It is also access the folding posture if needed using its [`state`][23] that
can be:

  * [`FLAT`][24]
  * [`HALF_OPENED`][25]


[0]: https://developer.android.com/reference/androidx/window/layout/DisplayFeature
[1]: https://developer.android.com/reference/androidx/window/layout/DisplayFeature#bounds()
[2]: https://developer.android.com/reference/androidx/window/layout/WindowInfoRepository
[3]: https://developer.android.com/reference/androidx/window/layout/WindowLayoutInfo
[4]: https://developer.android.com/reference/androidx/window/layout/FoldingFeature

[20]: https://developer.android.com/reference/androidx/window/layout/FoldingFeature#isSeparating()
[21]: https://developer.android.com/reference/androidx/window/layout/FoldingFeature#orientation()
[22]: https://developer.android.com/reference/androidx/window/layout/FoldingFeature#occlusionMode()
[23]: https://developer.android.com/reference/androidx/window/layout/FoldingFeature#state()
[24]: https://developer.android.com/reference/androidx/window/layout/FoldingFeature.Companion#FLAT()
[25]: https://developer.android.com/reference/androidx/window/layout/FoldingFeature.State.Companion#HALF_OPENED()

`WindowInfoRepositoryCallbackAdapter`
-------------------------------------

To use this library from Java or if you prefer to use a callback interface, it
is available the artifact `window:window-java` that makes available the 
[`WindowInfoRepositoryCallbackAdapter`][30] that allows to
register/unregister a callback to receive updates on the device's postgure
from the library as shown in the `SplitLayoutActivity` class included in this
sample.  
To create a new `WindowInfoRepositoryCallbackAdapter`, you can use the code:

```java
windowInfoRepository = new WindowInfoRepositoryCallbackAdapter(WindowInfoRepository.getOrCreate(this));
```

[30]: https://developer.android.com/reference/androidx/window/java/layout/WindowInfoRepositoryCallbackAdapter

`WindowMetrics`
---------------

The WindowManager library includes a new [`WindowMetrics`][40] API to get
information about your current window state and the maximum window size for
the current state of the system.

The API results don’t include information about the system insets such as the
status bar or action bar, since those values aren’t available before the first
layout pass. These bounds also don’t react to any changes in layout params
that might occur when your layout is inflated. If you are looking for specific
information for laying out views you should get the width/height from the
`Configuration` object or the `DecorView`.

To access these APIs, you can use, when working in Kotlin, a
`WindowInfoRepository`:

``` java
val windowInfoRepository = windowInfoRepository()
```

From here you now have access to the WindowMetrics APIs collecting events from
the [`currentWindowMetrics`][41] Flow.

If you want to retrieve the information sinchronously, you can use the
[`WindowMetricsCalculator`][42]

``` java
val windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)
```

[40]: https://developer.android.com/reference/androidx/window/layout/WindowMetrics
[41]: https://developer.android.com/reference/androidx/window/layout/WindowInfoRepository#currentWindowMetrics()
[42]: https://developer.android.com/reference/androidx/window/layout/WindowMetricsCalculator

Notes
-----

Any feedback to the library's API surface is greatly appreciated on things
you would like to see added or changed!

For more information on the Jetpack Window Manager library, see the
[Jetpack Window Manager release page][99].

[99]: https://developer.android.com/jetpack/androidx/releases/window

Pre-requisites
--------------

- Android SDK 30
- Android Studio Artic Fox | 2020.3.1

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
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more
details.

License
-------

Copyright 2020 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
