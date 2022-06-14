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
package com.example.android.haptics.samples.ui.resist

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.animation.DecelerateInterpolator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.android.haptics.samples.R
import com.example.android.haptics.samples.ui.components.Screen
import com.example.android.haptics.samples.ui.theme.HapticSamplerTheme
import com.example.android.haptics.samples.ui.utils.lerp
import kotlinx.coroutines.delay

// Vibration related constants for the resist effect.
private const val TICK_INTERVAL_MIN_MS = 30L
private const val TICK_INTERVAL_MAX_MS = 60L
private const val TICK_INTENSITY_MIN = 0.2f
private const val TICK_INTENSITY_MAX = 0.8f

// Start and target values for the resistance indicator on screen.
private val START_SIZE = 64.dp
private val START_STROKE_WIDTH = 8.dp
private const val START_ROTATION = 90f
private val START_Y_OFFSET = 0.dp

private val TARGET_SIZE = 128.dp
private val TARGET_Y_OFFSET = 150.dp
private val TARGET_STROKE_WIDTH = 16.dp
private const val TARGET_ROTATION = START_ROTATION + 360f

private const val TIME_TO_ANIMATE_BACK_MS = 1000

// The max offset we allow the user to drag the indicator on the screen.
private val DRAG_OFFSET_MAX = 350.dp
// The buffer we use before changing indicators (otherwise small unintended finger movement causes flickering).
private val DRAG_OFFSET_BUFFER = 5.dp
// Use an interpolator to simulate resistance as the user approaches end of their drag.
private val DRAG_INTERPOLATOR = DecelerateInterpolator()

@Composable
fun ResistRoute(viewModel: ResistViewModel) {
    ResistScreen(
        isLowTickSupported = viewModel.isLowTickSupported,
        messageToUser = viewModel.messageToUser,
    )
}

@Composable
fun ResistScreen(isLowTickSupported: Boolean, messageToUser: String = "") {
    // Use density of user's device to determine the maxDragOffset (amount of drag we consider animation complete)
    // and the dragOffsetBuffer used so there is no flicker from unintended finger movement.
    val maxDragOffset = with(LocalDensity.current) { DRAG_OFFSET_MAX.toPx() }
    val dragOffsetBuffer = with(LocalDensity.current) { DRAG_OFFSET_BUFFER.toPx() }

    // Control variables for the dragging of the indicator.
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(0f) }

    // Animation to return the indicator to the original position where dragOffset is zero.
    val returnAnimation = animateFloatAsState(
        targetValue = if (isDragging) dragOffset else 0f,
        animationSpec = if (isDragging) tween(0) else tween(TIME_TO_ANIMATE_BACK_MS)
    )

    // Derived state representing the drag gesture, including max offset and buffer for flicker control.
    val dragOffsetData by remember(dragOffset) {
        derivedStateOf { DragOffsetData(dragOffset, maxDragOffset, dragOffsetBuffer) }
    }

    // Only vibrates while the user is dragging
    if (isDragging) {
        val vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Derived state representing the vibration parameters based on the drag gesture.
        val vibrationData by remember(dragOffsetData) {
            derivedStateOf { VibrationData(dragOffsetData, isLowTickSupported) }
        }

        LaunchedEffect(Unit) {
            // We must continuously run this effect because we want vibration to occur even when the
            // view is not being drawn, which is the case if user stops dragging midway through animation.
            while (true) {
                delay(vibrationData.interval)
                vibrate(vibrator, vibrationData)
            }
        }
    } else {
        // Update the drag offset based on the return animation, so the user can pick it up anytime.
        LaunchedEffect(returnAnimation.value) {
            dragOffset = returnAnimation.value
        }
    }

    // Derived state representing the indicator position and shape based on the drag gesture.
    val indicatorData by remember(dragOffsetData) {
        derivedStateOf { IndicatorData(dragOffsetData) }
    }

    Screen(pageTitle = stringResource(R.string.resist_screen_title), messageToUser = messageToUser) {
        Column(
            Modifier
                .draggable(
                    orientation = Orientation.Vertical,
                    onDragStarted = {
                        isDragging = true
                    },
                    onDragStopped = {
                        isDragging = false
                    },
                    state = rememberDraggableState { delta ->
                        dragOffset += delta
                    }
                )
                .fillMaxWidth()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResistIndicator(indicator = indicatorData, dragOffset = dragOffsetData)
        }
    }
}

@Composable
private fun ResistIndicator(indicator: IndicatorData, dragOffset: DragOffsetData) {
    Box() {
        Column(modifier = Modifier.align(Alignment.Center)) {
            CircularProgressIndicator(
                progress = 0.75f,
                modifier = Modifier
                    .padding(8.dp)
                    .size(indicator.size)
                    .offset(y = indicator.offsetY)
                    .rotate(indicator.rotation),
                color = MaterialTheme.colors.primaryVariant,
                strokeWidth = indicator.strokeWidth
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = dragOffset.isAtStart(),
                enter = fadeIn(animationSpec = tween(TIME_TO_ANIMATE_BACK_MS)),
                exit = fadeOut(),

            ) {
                Text(stringResource(R.string.resist_screen_drag_down), Modifier.offset(y = START_SIZE / 2))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = indicator.offsetY)
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = dragOffset.isAtEnd(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {

                // Indicator that max resistance has been reached.
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.secondary)
                )
            }
        }

        Box(modifier = Modifier.align(Alignment.Center)) {
            androidx.compose.animation.AnimatedVisibility(
                visible = dragOffset.isAtStart(),
                enter = fadeIn(animationSpec = tween(TIME_TO_ANIMATE_BACK_MS)),
                exit = fadeOut()
            ) {
                Icon(
                    Icons.Rounded.ArrowDownward,
                    null,
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .offset(y = -(4.dp))
                        .align(Alignment.Center)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 16.dp)
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = dragOffset.isAtEnd(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(stringResource(R.string.resist_screen_and_release))
            }
        }
    }
}

// Vibration parameters based on the current drag offset and the device support.
private class VibrationData(
    dragOffsetData: DragOffsetData,
    isLowTickSupported: Boolean
) {
    val interval: Long
    val effectId: Int
    val intensity: Float

    init {
        val offset = dragOffsetData.currentRelativeOffset()

        // We want the interval to decrease (more frequent vibrations) as user drags down to simulate resistance.
        interval = lerp(TICK_INTERVAL_MAX_MS.toFloat(), TICK_INTERVAL_MIN_MS.toFloat(), offset).toLong()

        // The preferred primitive for this experience is low tick.
        effectId =
            if (isLowTickSupported) VibrationEffect.Composition.PRIMITIVE_LOW_TICK
            else VibrationEffect.Composition.PRIMITIVE_TICK

        // Cut down intensity if low tick is not supported.
        val intensityModifier = if (isLowTickSupported) 1f else .75f
        intensity = intensityModifier * lerp(TICK_INTENSITY_MIN, TICK_INTENSITY_MAX, offset)
    }
}

// Indicator parameters based on the current drag offset.
private class IndicatorData(
    dragOffsetData: DragOffsetData
) {
    val size: Dp
    val strokeWidth: Dp
    val rotation: Float
    val offsetY: Dp

    init {
        val offset = dragOffsetData.currentRelativeOffset()
        size = Dp(lerp(START_SIZE.value, TARGET_SIZE.value, offset))
        strokeWidth = Dp(lerp(START_STROKE_WIDTH.value, TARGET_STROKE_WIDTH.value, offset))
        rotation = lerp(START_ROTATION, TARGET_ROTATION, offset)
        offsetY = Dp(lerp(START_Y_OFFSET.value, TARGET_Y_OFFSET.value, offset))
    }
}

// Drag state based on the current drag offset and the configured gesture bounds.
private class DragOffsetData(
    current: Float,
    max: Float,
    buffer: Float
) {
    val current = current
    val max = max
    val buffer = buffer

    fun currentRelativeOffset(): Float = DRAG_INTERPOLATOR.getInterpolation(current.coerceIn(0f, max) / max)
    fun isAtStart(): Boolean = current <= buffer
    fun isAtEnd(): Boolean = current >= max - buffer
}

private fun vibrate(vibrator: Vibrator, vibrationData: VibrationData) {
    // Composition primitives require Android R.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

    vibrator.vibrate(
        VibrationEffect.startComposition()
            .addPrimitive(vibrationData.effectId, vibrationData.intensity)
            .compose()
    )
}

@Preview(showBackground = true)
@Composable
fun ResistScreenPreview() {
    HapticSamplerTheme {
        ResistScreen(
            isLowTickSupported = false,
            messageToUser = "A message to display to user."
        )
    }
}
