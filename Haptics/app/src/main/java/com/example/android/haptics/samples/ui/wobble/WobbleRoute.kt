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
package com.example.android.haptics.samples.ui.wobble

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.android.haptics.samples.R
import com.example.android.haptics.samples.ui.components.Screen
import com.example.android.haptics.samples.ui.shapes.ElasticTopShape
import com.example.android.haptics.samples.ui.theme.HapticSamplerTheme
import com.example.android.haptics.samples.ui.theme.secondaryText
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.random.Random

private val MAX_DRAG_DISTANCE_DP = 400.dp
private const val DRAG_DISTANCE_START = 0f

private const val SPINS_PER_COMPOSITION = 2
private const val DELAY_BETWEEN_COMPOSITIONS_MS = 20

private const val SPIN_MIN_INTENSITY = 0.01f
private const val SPIN_RANDOM_INTENSITY_WINDOW = 0.1f

// The total height of the wobble shape, top is ElasticTopShape and the bottom is RectangleShape.
private val WOBBLE_SHAPE_HEIGHT = 600.dp

@Composable
fun WobbleRoute(viewModel: WobbleViewModel) {
    WobbleScreen(messageToUser = viewModel.messageToUser)
}

@Composable
fun WobbleScreen(messageToUser: String) {
    val maxDragDistance = with(LocalDensity.current) {
        MAX_DRAG_DISTANCE_DP.toPx()
    }
    var dragDistance by remember { mutableStateOf(DRAG_DISTANCE_START) }

    var isWobbling by remember { mutableStateOf(false) } // Whether the elastic is animating.

    // Use drag distance to create an animated float value behaving like a spring.
    val dragDistanceAnimated by animateFloatAsState(
        targetValue = if (dragDistance > DRAG_DISTANCE_START) dragDistance else DRAG_DISTANCE_START,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        finishedListener = {
            if (isWobbling) isWobbling = !isWobbling
        }
    )

    if (isWobbling) {
        // SPIN primitive used in this example not supported < S.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val vibrator = LocalContext.current.getSystemService(Vibrator::class.java)
        LaunchedEffect(Unit) {
            while (true) {
                val intensity = (dragDistanceAnimated / maxDragDistance).absoluteValue.coerceIn(0f, 1f)
                // As the animation nears completion, there comes a point where the springy movement
                // is no longer visually perceptible but even very small intensity can be felt since
                // primitives with an intensity scale of 0 represents min, not no vibration.
                if (intensity > SPIN_MIN_INTENSITY) {
                    vibrate(vibrator, intensity)
                }
                // Delay the next check for a sufficient vibration intensity until the current
                // composition finishes plus a small delay.
                val delay = SPINS_PER_COMPOSITION * vibrator.getPrimitiveDurations(
                    VibrationEffect.Composition.PRIMITIVE_SPIN
                )[0] + DELAY_BETWEEN_COMPOSITIONS_MS
                delay(delay.toLong())
            }
        }
    }

    Screen(
        pageTitle = stringResource(R.string.wobble),
        messageToUser = messageToUser
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .draggable(
                    onDragStopped = {
                        isWobbling = true
                        dragDistance = DRAG_DISTANCE_START
                    },
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        isWobbling = false
                        dragDistance += delta
                        // Only allow drag down in this example.
                        if (dragDistance < DRAG_DISTANCE_START) return@rememberDraggableState

                        if (dragDistance >= maxDragDistance) {
                            dragDistance = maxDragDistance
                        }
                    }
                )
        ) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 150.dp)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = dragDistance === DRAG_DISTANCE_START && !isWobbling
                ) {
                    WobbleInstructions()
                }
            }
            WobbleShape(
                WOBBLE_SHAPE_HEIGHT,
                elasticTopPercent = dragDistanceAnimated / maxDragDistance
            )
        }
    }
}

/**
 * Wobble shape is composed of an ElasticTopShape on top of a RectangleShape.
 */
@Composable
private fun WobbleShape(wobbleShapeHeight: Dp, elasticTopPercent: Float) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        val halfOfTotalWobbleShapeHeight = wobbleShapeHeight / 2
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(halfOfTotalWobbleShapeHeight)
                .clip(ElasticTopShape(elasticTopPercent))
                .background(MaterialTheme.colors.primaryVariant)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(halfOfTotalWobbleShapeHeight)
                .clip(RectangleShape)
                .background(MaterialTheme.colors.primaryVariant)
        )
    }
}

/**
 * Instructions for the user to interact with the WobbleShape.
 */
@Composable
private fun WobbleInstructions() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.wobble_drag_and_release))
        Spacer(modifier = Modifier.height(16.dp))
        Icon(Icons.Rounded.ArrowDownward, null, tint = MaterialTheme.colors.secondaryText)
    }
}

/**
 * Get a random intensity level based on a base intensity and the window.
 *
 * @param baseIntensity Intensity level in which to make the adjustment from.
 * @param window A float value in which to randomly adjust from base intensity, in either positive
 *     or negative direction.
 */
private fun randomIntensity(baseIntensity: Float, window: Float): Float {
    val intensityOffset = Random.Default.nextFloat() * (window * 2) - window
    return (baseIntensity + intensityOffset).coerceIn(0f, 1f)
}

private fun vibrate(vibrator: Vibrator, intensity: Float) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return // Spin is only supported in Android S.
    val spinComposition = VibrationEffect.startComposition()
    repeat(SPINS_PER_COMPOSITION) {
        spinComposition.addPrimitive(
            VibrationEffect.Composition.PRIMITIVE_SPIN,
            randomIntensity(intensity, SPIN_RANDOM_INTENSITY_WINDOW)
        )
    }
    vibrator.vibrate(spinComposition.compose())
}

@Preview(showBackground = true)
@Composable
fun WobbleScreenPreview() {
    HapticSamplerTheme {
        WobbleScreen(
            messageToUser = "A message to display to user."
        )
    }
}
