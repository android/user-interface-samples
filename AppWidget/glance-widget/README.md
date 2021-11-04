
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


# Pre-requisites

* Android SDK 23
* [Jetpack Compose](https://developer.android.com/jetpack/compose/interop/adding#setup)

Note: The app will compile with SDK 21, but it doesn't work as expected

# Getting Started

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

# Support

- Stack Overflow: http://stackoverflow.com/questions/tagged/glance+glance-appwidget

If you've found an error in this sample, please file an issue:
https://github.com/android/user-interface

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.
