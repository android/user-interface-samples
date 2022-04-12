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

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.android.haptics.samples.R
import com.example.android.haptics.samples.ui.components.Screen
import com.example.android.haptics.samples.ui.modifiers.noRippleClickable
import com.example.android.haptics.samples.ui.theme.HapticSamplerTheme
import com.example.android.haptics.samples.ui.theme.secondaryText

/**
 * The two possible states states of the expand shape.
 */
private enum class ExpandShapeState {
    Collapsed,
    Expanded
}

private const val DEFAULT_ANIMATE_TO_EXPANDED_DURATION_MS = 650
private const val DEFAULT_ANIMATE_TO_COLLAPSED_DURATION_MS = 500
// Add a tick after the animation between states has occurred and the controls/indicators appear.
private const val ANIMATION_COMPLETE_TICK_DELAY_MS = 30

// We animate size and thickness of the shape. As it gets larger we make the circular shape
// thinner.
private val EXPAND_SHAPE_COLLAPSED_SIZE = 64.dp
private val EXPAND_SHAPE_COLLAPSED_THICKNESS = EXPAND_SHAPE_COLLAPSED_SIZE / 2

private val EXPAND_SHAPE_EXPANDED_SIZE = EXPAND_SHAPE_COLLAPSED_SIZE * 5
private val EXPAND_SHAPE_EXPANDED_THICKNESS = EXPAND_SHAPE_COLLAPSED_SIZE / 4

// Representation of the primitive composition to be played by the vibrator when the indicator is
// expanding.
private val VIBRATION_DATA_FOR_EXPANDING = PrimitiveComposition(
    arrayOf(
        PrimitiveEffect(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE, 0.3f),
        PrimitiveEffect(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL, 0.3f),
        PrimitiveEffect(VibrationEffect.Composition.PRIMITIVE_TICK, 0.6f, ANIMATION_COMPLETE_TICK_DELAY_MS)
    )
)

// Representation of the primitive composition to be played by the vibrator when the indicator is
// collapsing.
private val VIBRATION_DATA_FOR_COLLAPSING = PrimitiveComposition(
    arrayOf(
        PrimitiveEffect(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE),
        PrimitiveEffect(VibrationEffect.Composition.PRIMITIVE_TICK, 1f, ANIMATION_COMPLETE_TICK_DELAY_MS)
    )
)

@Composable
fun ExpandRoute(viewModel: ExpandViewModel) {
    ExpandExampleScreen(messageToUser = viewModel.messageToUser)
}

@Composable
fun ExpandExampleScreen(messageToUser: String) {
    val vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    var currentState by remember { mutableStateOf(ExpandShapeState.Collapsed) }

    val animateToExpandedDuration = remember {
        VIBRATION_DATA_FOR_EXPANDING.getDuration(
            vibrator,
            DEFAULT_ANIMATE_TO_EXPANDED_DURATION_MS
        )
    }
    val animateToCollapsedDuration = remember {
        VIBRATION_DATA_FOR_COLLAPSING.getDuration(
            vibrator,
            DEFAULT_ANIMATE_TO_COLLAPSED_DURATION_MS
        )
    }

    var transitionData = updateTransitionData(
        expandShapeState = currentState,
        animateToExpandedDuration = animateToExpandedDuration,
        animateToCollapsedDuration = animateToCollapsedDuration
    )

    Screen(
        pageTitle = stringResource(R.string.expand_screen_title),
        messageToUser = messageToUser
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .noRippleClickable {
                        currentState =
                            if (currentState == ExpandShapeState.Collapsed) ExpandShapeState.Expanded
                            else ExpandShapeState.Collapsed
                        vibrate(vibrator, transitioningToState = currentState)
                    },
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(EXPAND_SHAPE_EXPANDED_SIZE)
                        .align(Alignment.Center)
                        .offset(y = -(EXPAND_SHAPE_COLLAPSED_SIZE))
                ) {
                    // Draw a box containing either a circle when collapsed or the donut shape
                    // when expanding or expanded.
                    Box(
                        modifier = Modifier
                            .size(transitionData.size)
                            .clip(
                                if (transitionData.isCollapsed)
                                    CircleShape else ExpandShape(transitionData.thickness)
                            )
                            .align(Alignment.Center)
                            .background(MaterialTheme.colors.primaryVariant)
                    )

                    // Draw indicators if there is no ongoing transition and collapsed.
                    if (transitionData.isCollapsed) {
                        Text(
                            stringResource(R.string.expand_screen_tap_to_expand),
                            Modifier
                                .align(Alignment.Center)
                                .offset(y = -(EXPAND_SHAPE_COLLAPSED_SIZE))
                        )
                        Icon(
                            Icons.Outlined.TouchApp,
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondaryText,
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                    }
                    // Draw indicators if there is no ongoing transition and expanded.
                    if (transitionData.isExpanded) {
                        Text(
                            stringResource(R.string.expand_screen_tap_to_minimize),
                            Modifier
                                .align(Alignment.Center)
                                .offset(y = -((EXPAND_SHAPE_COLLAPSED_SIZE.value / 2).dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colors.secondary)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Hold the transition values for expanding between states.
 */
private data class TransitionData(
    val size: Dp,
    val thickness: Float,
    val isCollapsed: Boolean, // Transition is complete and now collapsed.
    val isExpanded: Boolean, // Transition is complete and now expanded.
)

/**
 * Create a transition and return it's animation values for animating between expanding and
 * collapsing.
 */
@Composable
private fun updateTransitionData(
    expandShapeState: ExpandShapeState,
    animateToExpandedDuration: Int,
    animateToCollapsedDuration: Int
): TransitionData {
    val transition = updateTransition(expandShapeState, label = "Transition between ExpandShapeState.Collapsed and Expanded.")
    // For the expanding and collapsing animation, we use a donut-like shape making it larger and
    // and less thick.
    val size by transition.animateDp(
        transitionSpec = getTransitionSpec(animateToExpandedDuration, animateToCollapsedDuration), label = "Transition size between ExpandShapeState.Collapsed and Expanded."
    ) { state ->
        when (state) {
            ExpandShapeState.Collapsed -> EXPAND_SHAPE_COLLAPSED_SIZE
            ExpandShapeState.Expanded -> EXPAND_SHAPE_EXPANDED_SIZE
        }
    }

    val thickness by transition.animateFloat(
        transitionSpec = getTransitionSpec(animateToExpandedDuration, animateToCollapsedDuration), label = "Transition thickness between ExpandShapeState.Collapsed and Expanded."
    ) { state ->
        with(LocalDensity.current) {
            when (state) {
                ExpandShapeState.Collapsed -> { EXPAND_SHAPE_COLLAPSED_THICKNESS.toPx() }
                ExpandShapeState.Expanded -> { EXPAND_SHAPE_EXPANDED_THICKNESS.toPx() }
            }
        }
    }
    val isAtTargetState = transition.currentState === transition.targetState
    return TransitionData(
        size = size,
        thickness = thickness,
        isCollapsed = isAtTargetState && transition.currentState == ExpandShapeState.Collapsed,
        isExpanded = isAtTargetState && transition.currentState == ExpandShapeState.Expanded
    )
}

@Composable
private fun <T> getTransitionSpec(
    animateToExpandedDuration: Int,
    animateToCollapsedDuration: Int,
): @Composable() (Transition.Segment<ExpandShapeState>.() -> FiniteAnimationSpec<T>) =
    {
        when {
            ExpandShapeState.Collapsed isTransitioningTo ExpandShapeState.Expanded ->
                tween(durationMillis = animateToExpandedDuration, easing = LinearEasing)
            else -> tween(durationMillis = animateToCollapsedDuration, easing = LinearEasing)
        }
    }

/**
 * Play vibration effect based on what state the shape is transitioning to.
 */
private fun vibrate(vibrator: Vibrator, transitioningToState: ExpandShapeState) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        return
    }
    var vibrationEffect: VibrationEffect = if (transitioningToState === ExpandShapeState.Expanded) {
        VIBRATION_DATA_FOR_EXPANDING.buildVibrationEffect()
    } else {
        VIBRATION_DATA_FOR_COLLAPSING.buildVibrationEffect()
    }
    vibrator.vibrate(vibrationEffect)
}

/**
 * Representation of an array of primitives that can be played in a sequence.
 */
private class PrimitiveComposition(
    val compositionOfPrimitives: Array<PrimitiveEffect>
) {

    @RequiresApi(Build.VERSION_CODES.R)
    fun allPrimitivesSupported(vibrator: Vibrator): Boolean {
        return compositionOfPrimitives.all { primitiveEffect -> vibrator.areAllPrimitivesSupported(primitiveEffect.effectId) }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun buildVibrationEffect(): VibrationEffect {
        val vibrationEffect = VibrationEffect.startComposition()
        for (primitive in compositionOfPrimitives) {
            vibrationEffect.addPrimitive(primitive.effectId, primitive.scale, primitive.delay)
        }
        return vibrationEffect.compose()
    }

    /**
     * If able, return the duration of the primitive composition.
     *
     * @param vibrator Vibrator service.
     * @param defaultIfUnsupportedPrimitives Value returned when either the current device does
     *     not support checking duration or not all primitives are supported.
     */
    fun getDuration(vibrator: Vibrator, defaultIfUnsupportedPrimitives: Int = 0): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || !allPrimitivesSupported(vibrator))
            return defaultIfUnsupportedPrimitives

        var compositionDuration = 0
        for (primitive in compositionOfPrimitives) {
            val duration = vibrator.getPrimitiveDurations(primitive.effectId)[0]
            compositionDuration += duration
        }
        return compositionDuration
    }
}

/**
 * Representation of a primitive to be played in a composition.
 */
private data class PrimitiveEffect(
    val effectId: Int,
    val scale: Float = 1f,
    val delay: Int = 0,
)

@Preview(showBackground = true)
@Composable
fun ExpandExampleScreenPreview() {
    HapticSamplerTheme {
        ExpandExampleScreen(
            messageToUser = "A message to display to user."
        )
    }
}
