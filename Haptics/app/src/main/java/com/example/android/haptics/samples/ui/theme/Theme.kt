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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = darkBackgroundColor,
    onPrimary = darkTextColorPrimary,
    primaryVariant = darkOverviewSurface,
    secondary = colorAccentPrimary,
    onSecondary = textColorPrimary,
    surface = darkBackgroundColor,
    onSurface = darkTextColorPrimary,
    background = darkBackgroundColor,
    onBackground = darkTextColorPrimary,
)

private val LightColorPalette = lightColors(
    primary = backgroundColor,
    onPrimary = textColorPrimary,
    primaryVariant = overviewSurface,
    secondary = colorAccentPrimary,
    onSecondary = textColorPrimary,
    surface = backgroundColor,
    onSurface = textColorPrimary,
    background = backgroundColor,
    onBackground = textColorPrimary,
)

val Colors.secondaryText: Color
    @Composable
    get() = if (isSystemInDarkTheme()) darkTextColorSecondary else textColorSecondary

val Colors.buttonSurface: Color
    @Composable
    get() = if (isSystemInDarkTheme()) darkSurfaceColor else surfaceColor

val Colors.buttonSurfaceDisabled: Color
    @Composable
    get() = if (isSystemInDarkTheme()) darkSurfaceColorVariant else surfaceColorVariant

val Colors.onButtonSurface: Color
    @Composable
    get() = if (isSystemInDarkTheme()) darkTextColorPrimary else textColorPrimary

val Colors.onButtonSurfaceDisabled: Color
    @Composable
    get() = if (isSystemInDarkTheme()) darkTextColorTertiary else textColorTertiary

val Colors.topAppBarBackgroundColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) darkSurfaceHeader else surfaceHeader

@Composable
fun HapticSamplerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
