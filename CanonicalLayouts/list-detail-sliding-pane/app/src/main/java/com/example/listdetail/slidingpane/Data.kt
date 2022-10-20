/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.listdetail.slidingpane

private val loremIpsum = """
        |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Dui nunc mattis enim ut tellus elementum sagittis. Nunc sed augue lacus viverra vitae. Sit amet dictum sit amet justo donec. Fringilla urna porttitor rhoncus dolor purus non enim praesent elementum. Dictum non consectetur a erat nam at lectus urna. Tellus mauris a diam maecenas sed enim ut sem viverra. Commodo ullamcorper a lacus vestibulum sed arcu non. Lorem mollis aliquam ut porttitor leo a diam sollicitudin tempor. Pellentesque habitant morbi tristique senectus et netus et malesuada. Vitae suscipit tellus mauris a diam maecenas sed. Neque ornare aenean euismod elementum nisi quis. Quam vulputate dignissim suspendisse in est ante in nibh mauris. Tellus in metus vulputate eu scelerisque felis imperdiet proin fermentum. Orci ac auctor augue mauris augue neque gravida.
        |
        |Tempus quam pellentesque nec nam aliquam. Praesent semper feugiat nibh sed. Adipiscing elit duis tristique sollicitudin nibh sit. Netus et malesuada fames ac turpis egestas sed tempus urna. Quis varius quam quisque id diam vel quam. Urna duis convallis convallis tellus id interdum velit laoreet. Id eu nisl nunc mi ipsum. Fermentum dui faucibus in ornare. Nunc lobortis mattis aliquam faucibus. Vulputate mi sit amet mauris commodo quis. Porta nibh venenatis cras sed. Vitae tortor condimentum lacinia quis vel eros donec. Eu non diam phasellus vestibulum.
        """.trimMargin()
val sampleWords = listOf(
    "Apple" to loremIpsum,
    "Banana" to loremIpsum,
    "Cherry" to loremIpsum,
    "Date" to loremIpsum,
    "Elderberry" to loremIpsum,
    "Fig" to loremIpsum,
    "Grape" to loremIpsum,
    "Honeydew" to loremIpsum,
).map { (word, definition) -> DefinedWord(word, definition) }

data class DefinedWord(
    val word: String,
    val definition: String
)