# Per App Language Preference, Sample code using Views

This Compose sample demonstrates how to use the new [Per-App Language Preferences](https://developer.android.com/guide/topics/resources/app-languages) APIs introduced in API level 33. The new set of APIs allows an application language to be different from the system language

With this feature now Users will be able to change the language settings from the settings option by selecting:

***Settings → System → Languages & Input → App Languages → [Select the desired App] → [Select the desired Language]***

![Example 1](screenshots/SystemSettings.gif)

---

## Steps required to implement the APIs

### 1. Add the Libraries

Use the latest version of AppCompat Library

```bash
dependencies {
    ...
    implementation "androidx.appcompat:appcompat:$latestAppCompatVersion"
    implementation "androidx.appcompat:appcompat-resources:$latestAppCompatVersion"
    ...
}
```

### 2. Create locale_config.xml file
Create a new file in `values/xml/` directory and name it as `locale_config.xml`. This file should contain a list of all the locales that are supported by the app. The list element should be a string containing a locale tag.

> NOTE: The locale tags must follow the BCP47 syntax ( which is usually {language subtag}–{script subtag}–{country subtag} ). Anything other than that will be filtered out by the system and won't be visible in the system settings.

```xml
<?xml version="1.0" encoding="utf-8"?>
<locale-config xmlns:android="http://schemas.android.com/apk/res/android">
    ...
    <locale android:name="en"/>            <!-- English -->
    <locale android:name="en-GB"/>         <!-- English (United Kingdom) -->
    <locale android:name="fr"/>            <!-- French -->
    <locale android:name="ja"/>            <!-- Japanese -->
    <locale android:name="zh-Hans-MO"/>    <!-- Chinese (Macao) in Simplified Script -->
    <locale android:name="zh-Hant-TW"/>    <!-- Chinese (Taiwan) in Traditional Script -->
    ...
</locale-config>
```

### 3. Add the LocaleConfig in the `AndroidManifest.xml`
Specify this `locale_config.xml` file in the app’s `AndroidManifest.xml`

```xml
<manifest>
    ...
    <application
        ...
        android:localeConfig="@xml/locales_config"
    </application>
</manifest>
```

### 4. Use AndroidX APIs
Use the APIs in your code to set and get the app locales.
```kotlin
val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("xx-YY")

// Call this on the main thread as it may require Activity.restart()
AppCompatDelegate.setApplicationLocales(appLocale)


// Call this to get the selected locale and display it in your App
val selectedLocale = AppCompatDelegate.getApplicationLocales()[0]
```

### 5. Delegate storage to AndroidX
Let AndroidX handle the locale storage so that the user preference persists.

```xml
<application
    ...
    <service
        android:name="androidx.appcompat.app.AppLocalesMetadataHolderService"
        android:enabled="false"
        android:exported="false">
            <meta-data
            android:name="autoStoreLocales"
            android:value="true" />
    </service>
</application>
```

### Additional things to take care of while migrating to the API
If earlier the user preference was stored in, let's say a SharedPreference, then it is recommended to handle that preference to AndroidX,  on the first app launch after the update of Per-App Language Feature. This snippet for migration will ensure a smooth transition and the user is not required to set App Language after updating the app.

```kotlin
// Specify the constants to be used in the below code snippets
companion object {

    // Constants for SharedPreference File
    const val PREFERENCE_NAME = "shared_preference"
    const val PREFERENCE_MODE = Context.MODE_PRIVATE

    // Constants for SharedPreference Keys
    const val FIRST_TIME_MIGRATION = "first_time_migration"
    const val SELECTED_LANGUAGE = "selected_language"

    // Constants for SharedPreference Values
    const val STATUS_DONE = "status_done"
}
```

```kotlin
// Utility method to put a string in a SharedPreference
private fun putString(key: String, value: String) {
    val editor = getSharedPreferences(PREFERENCE_NAME, PREFERENCE_MODE).edit()
    editor.putString(key, value)
    editor.apply()
}

// Utility method to get a string from a SharedPreference
private fun getString(key: String): String? {
    val preference = getSharedPreferences(PREFERENCE_NAME, PREFERENCE_MODE)
    return preference.getString(key, null)
}
```

```kotlin
// Check if the migration has already been done or not
if (getString(FIRST_TIME_MIGRATION) != STATUS_DONE) {
    
    // Fetch the selected language from wherever it was stored. In this case it’s SharedPref
    // In this case let’s assume that it was stored in a key named SELECTED_LANGUAGE
    getString(SELECTED_LANGUAGE)?.let { it →
        
        // Set this locale using the AndroidX library that will handle the storage itself
        val localeList = LocaleListCompat.forLanguageTags(it)
        AppCompatDelegate.setApplicationLocales(localeList)
            
        // Set the migration flag to ensure that this is executed only once
        putString(FIRST_TIME_MIGRATION, STATUS_DONE)
    }
}
```

Once this is done. The app will work seamlessly, with the In-App Language Picker and the System Settings Language Picker as well.

![Sample App walk-through](screenshots/SampleAppWalkThrough.gif)

---

# Getting Started
This sample uses the Gradle build system. To build this project, use the `gradlew build` command or use "Import Project" in Android Studio.

# Support
If you've found an error in this sample, please file an issue: https://github.com/android/user-interface

Patches are encouraged, and may be submitted by forking this project and submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.