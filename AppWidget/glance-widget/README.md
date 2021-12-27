# Pre-requisites

* Android SDK 23
* [Jetpack Compose](https://developer.android.com/jetpack/compose/interop/adding#setup)

Note: The app will compile with SDK 21, but it might not work as expected

# Getting Started

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

## Setup 

Glance is currently only available as SNAPSHOT. Those are builds that are periodically generated in [androidx.dev](androidx.dev) repo.

To include them in your project, declare the [androidx.dev](https://androidx.dev) snapshot repo in your root build.gradle file:

```groovy
// main/root build.gradle file
allprojects {
 repositories {
   // …
   maven { url 'https://androidx.dev/snapshots/latest/artifacts/repository' }
 }
}
```

If you are using Kotlin Gradle DSL, add this repo to your root `build.gradle.kts`:
```kotlin
allprojects {
    repositories {
        // ...
        maven { url = URI("https://androidx.dev/snapshots/latest/artifacts/repository") }
    }
}
```

Alternatively, if your project is using settings repositories configuration, add the following in your settings.gradle file:

```groovy
// settings.gradle
dependencyResolutionManagement {
 // …
 repositories {
   // …
   maven { url 'https://androidx.dev/snapshots/latest/artifacts/repository' }
 }
}
```

Then, add the glance and glance-appwidget dependencies in your module's build.gradle:

```groovy
dependencies {
   implementation "androidx.glance:glance-appwidget:1.0.0-SNAPSHOT"
}
```

If you are using Kotlin Gradle DSL:
```kotlin
dependencies {
   implementation("androidx.glance:glance-appwidget:1.0.0-SNAPSHOT")
}

```

# AppWidget sample for Glance

This sample demonstrates how to build widgets using Glance, a new API powered by Compose runtime.

## Build a first widget

You need to prepare at least the following components to build a widget using Glance.

1. AppWidgetProviderInfo metadata

[`first_glance_widget_info.xml`](src/main/res/xml/first_glance_widget_info.xml)
```xml
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:description="@string/app_widget_description"
    android:minWidth="180dp"
    android:minHeight="50dp"
    android:initialLayout="@layout/widget_loading"
    android:previewImage="@drawable/first_glance_widget_preview"
    android:previewLayout="@layout/widget_first_glance_preview"
    android:resizeMode="horizontal|vertical"
    android:targetCellWidth="3"
    android:targetCellHeight="1"
    android:widgetCategory="home_screen" />
```

Define the metadata as an XML file in a same format of how you can create a widget without Glance
and have a reference of the metadata from the AndroidManifest.

```xml
<receiver
    android:name=".FirstGlanceWidgetReceiver"
    android:enabled="@bool/glance_appwidget_available"
    android:exported="false">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>

    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/first_glance_widget_info" />
</receiver>
```

2. GlanceAppWidget

Create a class that extends `GlanceAppWidget`. Then override the `Content` method that is marked as
`Composable` with the UI you want to have for you widget. 

[`FirstGlanceWidget.kt`](src/main/java/com/example/android/glancewidget/FirstGlanceWidget.kt\#L46)
```kotlin
class FirstGlanceWidget : GlanceAppWidget() {

    @Composable
    override fun Content() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(8.dp)
        ) {
            Text(
                text = "First Glance widget",
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(fontWeight = FontWeight.Bold),
            )
            ... 
        }
    }
}
```

3. GlanceAppWidgetReceiver

Create a class that extends `GlanceAppWidgetReceiver`, then override the `glanceAppWidget` property
that points to the instance of `GlanceAppWidget`.

[`FirstGlanceWidget.kt`](src/main/java/com/example/android/glancewidget/FirstGlanceWidget.kt\#L38)
```kotlin
class FirstGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = FirstGlanceWidget()
}
```

## Scrollable layout

`LazyColumn` composable allows you to define a set of items inside a scrollable container in the
widget.

[`TodoListGlanceWidget.kt`](src/main/java/com/example/android/glancewidget/TodoListGlanceWidget.kt\#L92)

```kotlin
LazyColumn {
    items(groceryStringIds) {
        ...
        CheckBox(
            ...
        )
    }
}
```

Note: Glance translates a LazyColumn into an actual [ListView](https://developer.android.com/reference/android/widget/ListView),
thus same limitations and restrictions of the [collection of RemoteViews](https://developer.android.com/guide/topics/appwidgets/collections)
apply.

## State management

Intrinsically, a widget is stateless because a widget lives in a different process from the app
process and the underlying UI content may be restored or updated by the system
(See https://developer.android.com/guide/topics/appwidgets/advanced#update-widgets for more details
on when and how an widget is updated). 

Thus, the state of the UI content of the widget, such as the checked state of the checkboxes or the
date of a weather widget needs to be stored in a persistent storage.

`GlanceStateDefinition` defines where and how the underlying data is stored to retrieve the state
of the UI content.

By overriding the `stateDefinition` field in the `GlanceAppWidget`, you can configure the underlying
data store. `PreferencesGlanceStateDefinition` is used for its implementation, which stores the 
underlying data using [Preference](https://developer.android.com/jetpack/androidx/releases/preference) androidx
library.

[`TodoListGlanceWidget.kt`](src/main/java/com/example/android/glancewidget/TodoListGlanceWidget.kt\#L72)

```kotlin
class TodoListGlanceWidget : GlanceAppWidget() {
    ...
    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition
    ...
}
```

Then you can store the data to the underlying storage by following code

[`TodoListGlanceWidget.kt`](src/main/java/com/example/android/glancewidget/TodoListGlanceWidget.kt\#L117)

```kotlin
updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
    it.toMutablePreferences()
        .apply { this[booleanPreferencesKey(toggledStringId)] = checked }
}
TodoListGlanceWidget().update(context, glanceId)
```

or read the value  by the following code

[`TodoListGlanceWidget.kt`](src/main/java/com/example/android/glancewidget/TodoListGlanceWidget.kt\#L83)

```kotlin
class TodoListGlanceWidget : GlanceAppWidget() {

    @Composable
    override fun Content() {
        ...
        val prefs = currentState<Preferences>()
        val checked = prefs[booleanPreferencesKey(keyOfCheckedState)] ?: false
        ...
    }
}
```

# Support

- Stack Overflow: http://stackoverflow.com/questions/tagged/glance+glance-appwidget

If you've found an error in this sample, please file an issue:
https://github.com/android/user-interface

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.
