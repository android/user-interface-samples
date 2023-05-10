> **Warning**
> This sample has been migraated to the new [platform-samples repository](https://github.com/android/platform-samples)
> and will no longer be maintained. 
> 
> Please use the following [sample](https://github.com/android/platform-samples/tree/main/samples/user-interface/text) instead.
>
> Thank you for your understanding.

Android Text Sample
===================

This sample demonstrates several text-related features in Android.

## Demos

### [Text Span](app/src/main/java/com/example/android/text/demo/textspan/TextSpanFragment.kt)

[`buildSpannedString`](https://developer.android.com/reference/kotlin/androidx/core/text/package-summary#buildSpannedString(kotlin.Function1))
in the [core-ktx](https://developer.android.com/kotlin/ktx#core) library is useful for quickly
building a rich text.

### [Linkify](app/src/main/java/com/example/android/text/demo/linkify/LinkifyFragment.kt)

[LinkifyCompat](https://developer.android.com/reference/androidx/core/text/util/LinkifyCompat)
is useful for creating links in TextViews. The API supports email addresses, phone numbers, and web
URLs out of the box, and you can also use regular expressions to create a custom link pattern.

### [Hyphenation](app/src/main/java/com/example/android/text/demo/hyphenation/HyphenationFragment.kt)

Automatic hyphenation is available for a number of languages including English.

Android 6.0 Marshmallow (API level 23) introduced the
[android:hyphenationFrequency](https://developer.android.com/reference/android/widget/TextView#attr_android:hyphenationFrequency)
attribute to apply automatic hyphenation to a `TextView`.

Android 13 (API level 33) introduced 2 new options, `fullFast` and `normalFast`. These are the same
as `full` and `normal`, but use faster algorithm for better performance.

### [Line break](app/src/main/java/com/example/android/text/demo/linebreak/LineBreakFragment.kt)

This feature is relevant to _languages written without spaces between words_, such as Japanese and
Chinese. This particular demo works only when the device locale is set to Japanese.

Android 13 (API level 33) introduced the `android:lineBreakWordStyle` attribute to `TextView`. Set
this attribute to `"phrase"`, and
[phrases (bunsetsu)](https://ja.wikipedia.org/wiki/%E6%96%87%E7%AF%80)
in the text will not be separated by line breaks.

### [Conversation suggestions](app/src/main/java/com/example/android/text/demo/conversion/ConversionFragment.kt)

This feature is relevant to Japanese language. You need the latest
[Gboard](https://play.google.com/store/apps/details?id=com.google.android.inputmethod.latin)
with Japanese language pack to use this demo.

Android 13 (API level 33) introduced Conversion Suggestion API that allows apps to access pieces of
text before they are committed during a text conversion session. Apps can then use these suggestions
to build incremental search queries, etc.

See
[ConversionEditText.kt](app/src/main/java/com/example/android/text/demo/conversion/ConversionEditText.kt)
and
[ConversionInputConnection.kt](app/src/main/java/com/example/android/text/demo/conversion/ConversionInputConnection.kt)
for the detail on how to use the API.

## Getting Started

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or open the sample folder in Android Studio.

## Support

- Stack Overflow: http://stackoverflow.com/questions/tagged/android

If you've found an error in this sample, please file an issue:
https://github.com/android/user-interface

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.
