/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.haptics.samples.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = lightYellow,
    onPrimary = darkestYellow,
    primaryVariant = lightYellowVariant,
    secondary = lightOrange,
    secondaryVariant = lightestOrange,
    surface = lightYellow,
    onSurface = darkestYellow,
    background = lightYellow,
    onBackground = darkestYellow,
)

val Colors.drawerButtonUnselected: Color
    get() = darkYellow

val Colors.buttonSurface: Color
    get() = lightestYellow

val Colors.onButtonSurface: Color
    get() = darkYellow

val Colors.subtitleVariant: Color
    get() = darkYellow

@Composable
fun HapticSamplerTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colors = LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
