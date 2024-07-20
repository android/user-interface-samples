/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.feedcompose.ui.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

class OutlinedFocusIndication(
    private val shape: Shape,
    private val outlineWidth: Dp,
    private val outlineColor: Color
) : Indication {

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isEnabledState = interactionSource.collectIsFocusedAsState()

        return remember(interactionSource) {
            OutlineIndicationInstance(
                shape = shape,
                outlineWidth = outlineWidth,
                outlineColor = outlineColor,
                isEnabledState = isEnabledState
            )
        }
    }
}

private class OutlineIndicationInstance(
    private val shape: Shape,
    private val outlineWidth: Dp,
    private val outlineColor: Color,
    isEnabledState: State<Boolean>
) : IndicationInstance {
    private val isEnabled by isEnabledState

    override fun ContentDrawScope.drawIndication() {
        drawContent()
        if (isEnabled) {
            drawOutline(
                outline = shape.createOutline(
                    size = size,
                    layoutDirection = layoutDirection,
                    density = this
                ),
                brush = SolidColor(outlineColor),
                style = Stroke(width = outlineWidth.toPx())
            )
        }
    }
}

class HighlightIndication(
    private val highlightColor: Color = Color.White,
    private val alpha: Float = 0.2f,
    private val isEnabled: (isFocused: Boolean, isHovered: Boolean) -> Boolean = { isFocused, isHovered ->
        isFocused || isHovered
    }
) : Indication {

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isFocusedState = interactionSource.collectIsFocusedAsState()
        val isHoveredState = interactionSource.collectIsHoveredAsState()
        return remember(interactionSource) {
            HighlightIndicationInstance(
                isFocusedState = isFocusedState,
                isHoveredState = isHoveredState,
                isEnabled = isEnabled,
                highlightColor = highlightColor,
                alpha = alpha
            )
        }
    }
}

private class HighlightIndicationInstance(
    val highlightColor: Color = Color.White,
    val alpha: Float = 0.2f,
    isFocusedState: State<Boolean>,
    isHoveredState: State<Boolean>,
    val isEnabled: (isFocused: Boolean, isHovered: Boolean) -> Boolean = { isFocused, isHovered ->
        isFocused || isHovered
    }
) : IndicationInstance {
    private val isFocused by isFocusedState
    private val isHovered by isHoveredState

    override fun ContentDrawScope.drawIndication() {
        drawContent()
        if (isEnabled(isFocused, isHovered)) {
            drawRect(
                size = size,
                color = highlightColor,
                alpha = alpha
            )
        }
    }
}
