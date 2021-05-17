
AppWidget Sample
===================================

This sample demonstrates how to use the new Widgets APIs introduced in API level 31.
The new set of APIs allows an application to build more engaging and beautiful
widgets.
The initial set of new APIs focuses on improving existing RemoteView APIs.

List of API changes
------------

- **Widget description**

  In API level 31, a widget description is shown for each widget in the widget
  picker if [`description`](app/src/main/res/xml/app_widget_info_grocery_list.xml\#L40) attribute is provided for your `appwidget-provider`

  ```xml
  <appwidget-provider
        android:description="@string/app_widget_grocery_list_description"
    ... />
  ```

- **Preview layout**

  The widget preview is displayed from the layout XML if it's provided as
  [`previewLayout`](app/src/main/res/xml/app_widget_info_grocery_list.xml\#L50) attribute for your `appwidget-provider` instead of a
  static drawable resource using `previewImage` attribute.
  Note that the preview may not reflect the widget after it's actually
  placed (e.g. colors from themes)

  ```xml
    <appwidget-provider
        android:previewLayout="@layout/widget_grocery_list"
    ... />
  ```

  <img src="screenshots/widget_preview.png"
    alt="Image of the widget with description and previewLayout"
    title="Image of the widget with description and previewLayout"
    />

- **Dynamic coloring**

  In API level 31, the colors of your widget are dynamically determined by the system colors.
  You should use the system's default theme in order to apply the dynamic coloring (`Theme.DeviceDefault` and
  `Theme.DeviceDefault.DayNight` for v31).

  [`values/themes.xml`](app/src/main/res/values/themes.xml\#L32)
  ```xml
  <style name="Theme.AppWidget.AppWidgetContainer"
      parent="@android:style/Theme.DeviceDefault" />
  ```

  [`values-v31/themes.xml`](app/src/main/res/values-v31/themes.xml\#L22)
  ```xml
  <style name="Theme.AppWidget.AppWidgetContainer"
      parent="@android:style/Theme.DeviceDefault.DayNight" />
  ```

  [`values-night-v31/themes.xml`](app/src/main/res/values-night-v31/themes.xml\#L20)
  ```xml
  <!-- Having themes.xml for night-v31 because of the priority order of the resource qualifiers. -->
  <style name="Theme.AppWidget.AppWidgetContainer"
      parent="@android:style/Theme.DeviceDefault.DayNight" />
  ```

  [`layout/widget_grocery_list.xml`](app/src/main/res/layout/widget_grocery_list.xml\#L21)
  ```xml
  <LinearLayout
      android:theme="@style/Theme.AppWidget.AppWidgetContainer">
    ...
  </LinearLayout>
  ```

  <img src="screenshots/dynamic_coloring_light_theme.png" width="240px"
      alt="screenshot for dynamic coloring on light theme"
      title="screenshot for dynamic coloring on light theme" />
  <img src="screenshots/dynamic_coloring_dark_theme.png" width="240px"
      alt="screenshot for dynamic coloring on dark theme"
      title="screenshot for dynamic coloring on dark theme" />

- **Rounded corners**

  Following system attributes are introduced in API level 31 for widget
  radius of the background and the views inside the widget to make the rounded corners
  to make the appearance of the widgets consistent with the System UI in
  Android 12.
  ```
  system_app_widget_background_radius: The corner radius of the widget background
  system_app_widget_inner_radius: The corner radius of inner containers
  ```

  The recommended approach is to define custom attributes to match those system parameters
  below the API level 31 to provide backward compatibility.

  [`values/attrs.xml`](app/src/main/res/values/attrs.xml\#L16)
  ```xml
  <declare-styleable name="AppWidgetAttrs">
      <attr name="appWidgetPadding" format="dimension" />
      <attr name="appWidgetInnerRadius" format="dimension" />
      <attr name="appWidgetRadius" format="dimension" />
  </declare-styleable>
  ```

  [`values/themes.xml`](app/src/main/res/values/themes.xml\#L32)
  ```xml
  <style name="Theme.AppWidget.AppWidgetContainerParent" parent="@android:style/Theme.DeviceDefault">
      <!-- Radius of the outer bound of widgets to make the rounded corners -->
      <item name="appWidgetRadius">16dp</item>
      <!--
      Radius of the inner view's bound of widgets to make the rounded corners.
      It needs to be 8dp or less than the value of appWidgetRadius
      -->
      <item name="appWidgetInnerRadius">8dp</item>
  </style>

  <style name="Theme.AppWidget.AppWidgetContainer" parent="Theme.AppWidget.AppWidgetContainerParent">
      <!-- Apply padding to avoid the content of the widget colliding with the rounded corners -->
      <item name="appWidgetPadding">16dp</item>
  </style>
  ```

  [`values-v31/themes.xml`](app/src/main/res/values-v31/themes.xml\#L22)
  ```xml
  <style name="Theme.AppWidget.AppWidgetContainerParent" parent="@android:style/Theme.DeviceDefault.DayNight">
      <item name="appWidgetRadius">@android:dimen/system_app_widget_background_radius</item>
      <item name="appWidgetInnerRadius">@android:dimen/system_app_widget_inner_radius</item>
  </style>
  ```

  [`layout/widget_grocery_list.xml`](app/src/main/res/layout/widget_grocery_list.xml\#L17)
  ```xml
  layout/widget_layout.xml
  <LinearLayout
    style="@style/Widget.AppWidget.AppWidget.Container">
    ...
  </LinearLayout>
  ```

  <img src="screenshots/widget_rounded_corners.png"
      alt="screenshot for a widget with rounded corners"
      title="screenshot for a widget with rounded corners" />

- **Deferrable configurability**

  In API level 31, users are able to reconfigure widgets after they are added to the home screen
  by long pressing on the widget and clicking the reconfigure button(the button visible at the
  bottom right corner after long pressing the widget).
  You need to specify `reconfigurable` value for the `widgetFeatures` attribute.

  [`xml/app_widget_info_grocery_list.xml`](app/src/main/res/xml/app_widget_info_grocery_list.xml\#L39)
  ```xml
  <appwidget-provider
      android:configure="com.example.android.appwidget.ListWidgetConfigureActivity"
      android:widgetFeatures="reconfigurable"
      ... />
  ```

  <img src="screenshots/widget_reconfigure.png"
      alt="screenshot for a widget with reconfigure button"
      title="screenshot for a widget with reconfigure button" />

  By specifying `configuration_optional` for the `widgetFeatures` attribute, you can choose to skip the
  initial configuration (the Activity specified by `configure` is invoked) when the widget is placed
  on the home screen.

  ```xml
  <appwidget-provider
      android:configure="com.example.android.appwidget.ListWidgetConfigureActivity"
      android:widgetFeatures="reconfigurable|configuration_optional"
      ... />
  ```

- **Transitions**

  In API level 31, there is a smooth transition from the widget to the app when the user launches the app from the widget.

  You need to specify the id of the background element of the widget with the [android:id/background](https://developer.android.com/reference/android/R.id#background) to enable this smooth transition.

  ```xml
  <LinearLayout
    android:id="@android:id/background">
    ...
  </LinearLayout>
  ```
  <img src="screenshots/widget_smooth_transition.gif"
      alt="screen record of a smooth transition from the widget to the app"
      title="screen record of a smooth transition from the widget to the app" />

- **Compound buttons**

  In API level 31, therer are three new compound buttons to use in a widget to support stateful behavior.

  <img src="screenshots/widget_items_collection.png" 
       alt="Screenshot of a widget with compound buttons" 
       title="Screenshot of a widget with compound buttons" /> 

  The widget itself is still stateless, so you need to store the state and register a listener for the state change events.

  [`ItemsCollectionAppWidget.kt`](app/src/main/java/com/example/android/appwidget/ItemsCollectionAppWidget.kt\#L139)
  ```kotlin
  // This code will check the Checkbox
  remoteViews.setCompoundButtonChecked(R.id.item_checkbox, true)
  ```

  [`ItemsCollectionAppWidget.kt`](app/src/main/java/com/example/android/appwidget/ItemsCollectionAppWidget.kt\#L142)
  ```kotlin
  // This code will check the item_radio_button2 in the item_radio_group RadioGroup
  remoteViews.setRadioGroupChecked(R.id.item_radio_group, R.id.item_radio_button2))
  ```

  [`ItemsCollectionAppWidget.kt`](app/src/main/java/com/example/android/appwidget/ItemsCollectionAppWidget.kt\#L162)
  ```kotlin
  // Listen for change events.
  // RemoteResponse.fromPendingIntent works on an individual item whereas you can set
  // a PendingIntent template using RemoteViews.setPendingIntentTemplate and
  // distinguish individual on-click by calling RemoteResponse.fromFillInIntent.
  remoteViews.setOnCheckedChangeResponse(
    R.id.item_switch,
    RemoteViews.RemoteResponse.fromPendingIntent(onCheckedChangePendingIntent)
  )
  remoteViews.setOnCheckedChangeResponse(
    R.id.item_switch,
    RemoteViews.RemoteResponse.fromFillInIntent(onCheckedChangeFillInIntent)
  )
  ```
  See the documents for [fromPendingIntent](https://developer.android.com/reference/android/widget/RemoteViews.RemoteResponse#fromPendingIntent(android.app.PendingIntent)) and [fromFillInIntent](https://developer.android.com/reference/android/widget/RemoteViews.RemoteResponse#fromFillInIntent(android.content.Intent)) for more details.


- **Simplified RemoteView collections**
 
  When you use a collection of RemoteViews (ListView) you need to declare and write a [RemoteViewService](https://developer.android.com/reference/android/widget/RemoteViewsService) to return 
  [RemoteViewsFactory](https://developer.android.com/reference/android/widget/RemoteViewsService.RemoteViewsFactory), which complicates the code in AppWidgetProvider and forces the system to bind and start up your service
  even if the collection has only few items.

  In API level 31, a new API allows you to pass a collection of RemoteViews directly when populating a ListView.

  [`ItemsCollectionAppWidget.kt`](app/src/main/java/com/example/android/appwidget/ItemsCollectionAppWidget.kt\#L51)
  ```kotlin
  remoteViews.setRemoteAdapter(
    R.id.items_list_view, 
    RemoteViews.RemoteCollectionItems.Builder()
      .addItem(/* id= */ ID_1, RemoteViews(...))
      .addItem(/* id= */ ID_2, RemoteViews(...))
      ...
      .setViewTypeCount(MAX_NUM_DIFFERENT_REMOTE_VIEWS_LAYOUTS)
      .build()
  )
  ```

  `setViewTypeCount` should be used if you have some RemoteViews items with layouts that are sometimes present and sometimes aren’t.
  If you just have one item type or you have some headers and then a variable number of items, you can ignore this.

- **Specifying size at compile time**

  In API level 31, following new attributes are introduced to ensure more reliable sizes for the widget across devices and size permutations.

  - `targetCellWidth/targetCellHeight`

    Defines the default size of the widget measured in cells of the launcher home. If they are defined, those values will be used instead of `minWidth/minHeight` (that are available in API level < 31).

  - `maxResizeWidth/maxResizeHeight`

    Defines the maximum size of the widget in the launcher home.

  It's recommended to use the new attributes above in addition to existing `minWidth/minHeight` and `minResizeWidth/minResizeHeight` attributes.

  [`xml/app_widget_info_grocery_list.xml`](app/src/main/res/xml/app_widget_info_grocery_list.xml\#L38)
  ```xml
  <appwidget-provider
    android:maxResizeWidth="240dp"
    android:maxResizeHeight="180dp"
    android:minWidth="180dp"
    android:minHeight="110dp"
    android:minResizeWidth="180dp"
    android:minResizeHeight="110dp"
    android:targetCellWidth="3"
    android:targetCellHeight="2"
    ... />
  ```

  <img src="screenshots/widget_resizing.png"
       alt="Screenshot of a widget being resized"
       title="Screenshot of a widget being resized" />

- **Flexible layouts**

  In API level 31, you can specify different layouts depending on the size of the widget. Specifycally you can set a Map of `SizeF` and `RemoteView` to the `AppWidgetManager.updateAppWidget` method.

  [`ListAppWidget.kt`](app/src/main/java/com/example/android/appwidget/ListAppWidget.kt\#L94)
  ```kotlin
  val viewMapping: MutableMap<SizeF, RemoteViews> = mutableMapOf()
  // Specify the maximum width and height in dp and a layout, which you want to use for the
  // specified size
  val viewMapping = mapOf(
      SizeF(150f, 110f) to RemoteViews(
          context.packageName,
          R.layout.widget_grocery_list
      ),
      SizeF(250f, 110f) to RemoteViews(
          context.packageName,
          R.layout.widget_grocery_grid
      ),
  )
  appWidgetManager.updateAppWidget(appWidgetId, RemoteViews(viewMapping))
  ```

  <img src="screenshots/widget_grocery_list.png"
       alt="Screenshot of grocery list widget"
       title="Screenshot of grocery list widget" />

  <img src="screenshots/widget_grocery_grid.png"
       alt="Screenshot of grocery grid widget"
       title="Screenshot of grocery grid widget" />

Pre-requisites
--------------

- Android SDK 31

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
