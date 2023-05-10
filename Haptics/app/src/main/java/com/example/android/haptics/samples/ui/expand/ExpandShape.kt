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
package com.example.android.haptics.samples.ui.expand

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * A donut-like shape with adjustable thickness used to simulate an expanding circle.
 */
class ExpandShape(private val thickness: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val outerOval = Path().apply {
            addOval(Rect(0f, 0f, size.width - 1, size.height - 1))
        }
        val ovalToSubtract = Path().apply {
            addOval(
                Rect(
                    thickness,
                    thickness,
                    right = size.width - 1 - thickness,
                    bottom = size.height - 1 - thickness
                )
            )
        }
        val resultPath = Path()
        // Create a donut shape by subtracting the the smaller oval from the larger one.
        resultPath.op(outerOval, ovalToSubtract, PathOperation.Difference)
        return Outline.Generic(resultPath)
    }
}
