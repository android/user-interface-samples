/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.appwidget.glance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.glance.unit.ColorProvider
import com.example.android.appwidget.R

/**
 * Temporary implementation of theme object for Glance-appwidgets.
 *
 * Important: It will change!
 */
object GlanceTheme {
    val colors: ColorProviders
        @Composable
        @ReadOnlyComposable
        get() = LocalColorProviders.current
}

internal val LocalColorProviders = staticCompositionLocalOf { dynamicThemeColorProviders() }

/**
 * Temporary implementation of Material3 theme for Glance.
 *
 * Note: This still requires manually setting the colors for all Glance components.
 */
@Composable
fun GlanceTheme(colors: ColorProviders = GlanceTheme.colors, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalColorProviders provides colors) {
        content()
    }
}

/**
 * Holds a set of Glance-specific [ColorProvider] following Material naming conventions.
 */
data class ColorProviders(
    val primary: ColorProvider,
    val onPrimary: ColorProvider,
    val primaryContainer: ColorProvider,
    val onPrimaryContainer: ColorProvider,
    val secondary: ColorProvider,
    val onSecondary: ColorProvider,
    val secondaryContainer: ColorProvider,
    val onSecondaryContainer: ColorProvider,
    val tertiary: ColorProvider,
    val onTertiary: ColorProvider,
    val tertiaryContainer: ColorProvider,
    val onTertiaryContainer: ColorProvider,
    val error: ColorProvider,
    val errorContainer: ColorProvider,
    val onError: ColorProvider,
    val onErrorContainer: ColorProvider,
    val background: ColorProvider,
    val onBackground: ColorProvider,
    val surface: ColorProvider,
    val onSurface: ColorProvider,
    val surfaceVariant: ColorProvider,
    val onSurfaceVariant: ColorProvider,
    val outline: ColorProvider,
    val textColorPrimary: ColorProvider,
    val textColorSecondary: ColorProvider,
    val inverseOnSurface: ColorProvider,
    val inverseSurface: ColorProvider,
    val inversePrimary: ColorProvider,
    val inverseTextColorPrimary: ColorProvider,
    val inverseTextColorSecondary: ColorProvider,
)

/**
 * Creates a set of color providers that represents a Material3 style dynamic color theme. On
 * devices that support it, the theme is derived from the user specific platform colors, on other
 * devices this falls back to the Material3 baseline theme.
 */
fun dynamicThemeColorProviders(): ColorProviders {
    return ColorProviders(
        primary = ColorProvider(R.color.colorPrimary),
        onPrimary = ColorProvider(R.color.colorOnPrimary),
        primaryContainer = ColorProvider(R.color.colorPrimaryContainer),
        onPrimaryContainer = ColorProvider(R.color.colorOnPrimaryContainer),
        secondary = ColorProvider(R.color.colorSecondary),
        onSecondary = ColorProvider(R.color.colorOnSecondary),
        secondaryContainer = ColorProvider(R.color.colorSecondaryContainer),
        onSecondaryContainer = ColorProvider(R.color.colorOnSecondaryContainer),
        tertiary = ColorProvider(R.color.colorTertiary),
        onTertiary = ColorProvider(R.color.colorOnTertiary),
        tertiaryContainer = ColorProvider(R.color.colorTertiaryContainer),
        onTertiaryContainer = ColorProvider(R.color.colorOnTertiaryContainer),
        error = ColorProvider(R.color.colorError),
        errorContainer = ColorProvider(R.color.colorErrorContainer),
        onError = ColorProvider(R.color.colorOnError),
        onErrorContainer = ColorProvider(R.color.colorOnErrorContainer),
        background = ColorProvider(R.color.colorBackground),
        onBackground = ColorProvider(R.color.colorOnBackground),
        surface = ColorProvider(R.color.colorSurface),
        onSurface = ColorProvider(R.color.colorOnSurface),
        surfaceVariant = ColorProvider(R.color.colorSurfaceVariant),
        onSurfaceVariant = ColorProvider(R.color.colorOnSurfaceVariant),
        outline = ColorProvider(R.color.colorOutline),
        textColorPrimary = ColorProvider(R.color.textColorPrimary),
        textColorSecondary = ColorProvider(R.color.textColorSecondary),
        inverseOnSurface = ColorProvider(R.color.colorOnSurfaceInverse),
        inverseSurface = ColorProvider(R.color.colorSurfaceInverse),
        inversePrimary = ColorProvider(R.color.colorPrimaryInverse),
        inverseTextColorPrimary = ColorProvider(R.color.textColorPrimaryInverse),
        inverseTextColorSecondary = ColorProvider(R.color.textColorSecondaryInverse),
    )
}