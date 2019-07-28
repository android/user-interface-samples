Text Styling
============
This sample shows how to style text on Android using spans, in Java.

Introduction
------------
## Features
Parse some hardcoded text and do the following:
* Paragraphs starting with “> ” are transformed into quotes.
* Text enclosed in “```” will be transformed into inline code block.
* Lines starting with “+ ” or “* ” will be transformed into bullet points.
To update the text, modify the value of `R.string.display_text`.
This project is not meant to fully cover the markdown capabilities and has several limitations; for example, quotes do not support nesting other elements.

## Implementation
The text is parsed in the [`Parser.parse`](https://github.com/googlesamples/android-text/blob/master/TextStyling-Java/app/src/main/java/com/android/example/text/styling/parser/Parser.java#L62) method and the spans are created in the [`MarkdownBuilder.markdownToSpans`](https://github.com/googlesamples/android-text/blob/master/TextStyling-Java/app/src/main/java/com/android/example/text/styling/renderer/MarkdownBuilder.java#L62) method.
To see how to apply a span, check out [`MarkdownBuilder.buildCodeBlockSpan`](https://github.com/googlesamples/android-text/blob/master/TextStyling-Java/app/src/main/java/com/android/example/text/styling/renderer/MarkdownBuilder.java#L120) . To see how to apply multiple spans on the same string, see [`MarkdownBuilder.buildQuoteSpan`](https://github.com/googlesamples/android-text/blob/master/TextStyling-Java/app/src/main/java/com/android/example/text/styling/renderer/MarkdownBuilder.java#L108). For examples of creating custom spans, see [`BulletPointSpan`](https://github.com/googlesamples/android-text/blob/master/TextStyling-Java/app/src/main/java/com/android/example/text/styling/renderer/spans/BulletPointSpan.java), [`CodeBlockSpan`](https://github.com/googlesamples/android-text/blob/master/TextStyling-Java/app/src/main/java/com/android/example/text/styling/renderer/spans/CodeBlockSpan.java) or [`FontSpan`](https://github.com/googlesamples/android-text/blob/master/TextStyling-Java/app/src/main/java/com/android/example/text/styling/renderer/spans/FontSpan.java).

## Testing
Text parsing is tested with JUnit tests in [`ParserTest`](https://github.com/googlesamples/android-text/blob/master/TextStyling-Java/app/src/test/java/com/android/example/text/styling/parser/ParserTest.java). Span building is tested via Android JUnit tests, in [`MarkdownBuilderTest`](https://github.com/googlesamples/android-text/blob/master/TextStyling-Java/app/src/androidTest/java/com/android/example/text/styling/renderer/MarkdownBuilderTest.java).


Getting Started
---------------

Clone this repository, enter the top level directory and run `./gradlew tasks`
to get an overview of all the tasks available for this project.

Some important tasks are:

```
assembleDebug - Assembles all Debug builds.
installDebug - Installs the Debug build.
connectedAndroidTest - Installs and runs the tests for Debug build on connected
devices.
test - Run all unit tests.
```

Screenshots
-----------
<img src="./screenshots/main_activity.png" width="30%" />

Support
-------
- Stack Overflow: http://stackoverflow.com/questions/tagged/android-text

If you've found an error in this sample, please file an issue:
https://github.com/android/user-interface/issues

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub.
