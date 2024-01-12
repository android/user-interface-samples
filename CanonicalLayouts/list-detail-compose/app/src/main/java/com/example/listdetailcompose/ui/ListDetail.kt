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

package com.example.listdetailcompose.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.window.layout.DisplayFeature
import com.google.accompanist.adaptive.FoldAwareConfiguration
import com.google.accompanist.adaptive.SplitResult
import com.google.accompanist.adaptive.TwoPane
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A higher-order component displaying an opinionated list-detail format.
 *
 * The [list] slot is the primary content, and is in a parent relationship with the content
 * displayed in [detail].
 *
 * This relationship implies that different detail screens may be swapped out for each other, and
 * should be distinguished by passing a [detailKey] to control when a different detail is being
 * shown (to reset instance state.
 *
 * When there is enough space to display both list and detail, pass `true` to [showListAndDetail]
 * to show both the list and the detail at the same time. This content is displayed in a [TwoPane].
 *
 * When there is not enough space to display both list and detail, which slot is displayed is based
 * on [isDetailOpen]. Internally, this state is changed in an opinionated way via [setIsDetailOpen].
 * For instance, when showing just the detail screen, a back button press will call
 * [setIsDetailOpen] passing `false`.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListDetail(
    isDetailOpen: Boolean,
    setIsDetailOpen: (Boolean) -> Unit,
    showListAndDetail: Boolean,
    detailKey: Any?,
    list: @Composable (isDetailVisible: Boolean) -> Unit,
    detail: @Composable (isListVisible: Boolean) -> Unit,
    displayFeatures: List<DisplayFeature>,
    modifier: Modifier = Modifier,
) {
    val currentIsDetailOpen by rememberUpdatedState(isDetailOpen)
    val currentShowListAndDetail by rememberUpdatedState(showListAndDetail)
    val currentDetailKey by rememberUpdatedState(detailKey)

    // Determine whether to show the list and/or the detail.
    // This is a function of current app state, and the width size class.
    val showList by remember {
        derivedStateOf {
            currentShowListAndDetail || !currentIsDetailOpen
        }
    }
    val showDetail by remember {
        derivedStateOf {
            currentShowListAndDetail || currentIsDetailOpen
        }
    }
    // Validity check: we should always be showing something
    check(showList || showDetail)

    val listSaveableStateHolder = rememberSaveableStateHolder()
    val detailSaveableStateHolder = rememberSaveableStateHolder()

    val start = remember {
        movableContentOf {
            // Set up a SaveableStateProvider so the list state will be preserved even while it
            // is hidden if the detail is showing instead.
            listSaveableStateHolder.SaveableStateProvider(0) {
                Box(
                    modifier = Modifier
                        .userInteractionNotification {
                            // When interacting with the list, consider the detail to no longer be
                            // open in the case of resize.
                            setIsDetailOpen(false)
                        }
                ) {
                    list(showDetail)
                }
            }
        }
    }

    val end = remember {
        movableContentOf {
            // Set up a SaveableStateProvider against the selected word index to save detail
            // state while switching between details.
            // If this behavior isn't desired, this can be replaced with a key on the
            // selectedWordIndex.
            detailSaveableStateHolder.SaveableStateProvider(currentDetailKey ?: "null") {
                Box(
                    modifier = Modifier
                        .userInteractionNotification {
                            // When interacting with the detail, consider the detail to be
                            // open in the case of resize.
                            setIsDetailOpen(true)
                        }
                ) {
                    detail(showList)
                }
            }
        }
    }

    val density = LocalDensity.current
    val anchoredDraggableState = rememberSaveable(
        saver = AnchoredDraggableState.Saver(
            animationSpec = spring(),
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 400.dp.toPx() } },
        )
    ) {
        AnchoredDraggableState(
            initialValue = ExpandablePaneState.ListAndDetail,
            animationSpec = spring(),
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 400.dp.toPx() } },
        )
    }

    val coroutineScope = rememberCoroutineScope()

    // Sync the `isDetailOpen` as a side-effect to the expandable pane state.
    LaunchedEffect(isDetailOpen) {
        if (isDetailOpen) {
            when (anchoredDraggableState.currentValue) {
                ExpandablePaneState.ListOnly -> {
                    anchoredDraggableState.animateTo(ExpandablePaneState.DetailOnly)
                }
                ExpandablePaneState.ListAndDetail,
                ExpandablePaneState.DetailOnly
                -> Unit
            }
        } else {
            when (anchoredDraggableState.currentValue) {
                ExpandablePaneState.ListOnly,
                ExpandablePaneState.ListAndDetail -> Unit
                ExpandablePaneState.DetailOnly -> {
                    anchoredDraggableState.animateTo(ExpandablePaneState.ListOnly)
                }
            }
        }
    }

    // Update the `isDetailOpen` boolean as a side-effect of the expandable pane reaching a specific value.
    // We only do this if both the list and detail are capable of being shown, as
    if (showListAndDetail) {
        LaunchedEffect(anchoredDraggableState) {
            snapshotFlow { anchoredDraggableState.currentValue }
                .onEach {
                    when (anchoredDraggableState.currentValue) {
                        ExpandablePaneState.ListOnly -> setIsDetailOpen(false)
                        ExpandablePaneState.ListAndDetail -> setIsDetailOpen(true)
                        ExpandablePaneState.DetailOnly -> setIsDetailOpen(true)
                    }
                }
                .collect()
        }
    }

    // If showing just the detail due to the expandable pane state, allow a back press to hide the detail to return to
    // the list.
    BackHandler(
        enabled = showListAndDetail && anchoredDraggableState.currentValue == ExpandablePaneState.DetailOnly
    ) {
        coroutineScope.launch {
            anchoredDraggableState.animateTo(ExpandablePaneState.ListOnly)
        }
    }

    // If showing just the detail, allow a back press to hide the detail to return to
    // the list.
    BackHandler(
        enabled = !showListAndDetail && !showList
    ) {
        setIsDetailOpen(false)
    }

    val minListPaneWidth = 300.dp
    val minDetailPaneWidth = 300.dp

    Box(
        modifier = modifier.onSizeChanged {
            anchoredDraggableState.updateAnchors(
                newAnchors = DraggableAnchors {
                    ExpandablePaneState.ListOnly at it.width.toFloat()
                    ExpandablePaneState.ListAndDetail at it.width.toFloat() / 2f
                    ExpandablePaneState.DetailOnly at 0f
                },
                // Keep the current target, even if resizing causes the offset to be closer to a different one
                newTarget = anchoredDraggableState.targetValue
            )
        }
    ) {
        if (showList && showDetail) {
            TwoPane(
                first = {
                    // Enforce the minimum list pane width, aligning to the start edge of the screen
                    // Modifier.requiredWidthIn(min = minListPaneWidth) doesn't work because the content
                    // would be centered in the available space
                    Box(
                        Modifier
                            .clipToBounds()
                            .layout { measurable, constraints ->
                                val width = max(minListPaneWidth.roundToPx(), constraints.maxWidth)
                                val placeable = measurable.measure(
                                    constraints.copy(
                                        minWidth = minListPaneWidth.roundToPx(),
                                        maxWidth = width
                                    )
                                )
                                layout(constraints.maxWidth, placeable.height) {
                                    placeable.placeRelative(
                                        x = 0,
                                        y = 0
                                    )
                                }
                            }
                    ) {
                        start()
                    }
                },
                second = {
                    // Enforce the minimum detail pane width, aligning to the end edge of the screen
                    // Modifier.requiredWidthIn(min = minDetailPaneWidth) doesn't work because the content
                    // would be centered in the available space
                    Box(
                        Modifier
                            .clipToBounds()
                            .layout { measurable, constraints ->
                                val width = max(minDetailPaneWidth.roundToPx(), constraints.maxWidth)
                                val placeable = measurable.measure(
                                    constraints.copy(
                                        minWidth = minDetailPaneWidth.roundToPx(),
                                        maxWidth = width
                                    )
                                )
                                layout(constraints.maxWidth, placeable.height) {
                                    placeable.placeRelative(
                                        x = constraints.maxWidth - max(constraints.maxWidth, placeable.width),
                                        y = 0
                                    )
                                }
                            }
                    ) {
                        end()
                    }
                },
                strategy = { _, layoutDirection, layoutCoordinates ->
                    val xOffset = when (layoutDirection) {
                        LayoutDirection.Ltr -> anchoredDraggableState.offset
                        LayoutDirection.Rtl -> layoutCoordinates.size.width - anchoredDraggableState.offset
                    }

                    SplitResult(
                        gapOrientation = Orientation.Vertical,
                        gapBounds = Rect(
                            offset = Offset(xOffset, 0f),
                            size = Size(0f, layoutCoordinates.size.height.toFloat())
                        )
                    )
                },
                displayFeatures = displayFeatures,
                foldAwareConfiguration = FoldAwareConfiguration.VerticalFoldsOnly,
                modifier = Modifier.fillMaxSize(),
            )

            val dragHandleInteractionSource = remember { MutableInteractionSource() }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(64.dp)
                    // Offset back half the width so that we are positing the center of the handle
                    .offset(x = -32.dp)
                    .offset {
                        IntOffset(
                            anchoredDraggableState
                                .requireOffset()
                                .roundToInt(),
                            0
                        )
                    }
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl,
                        orientation = Orientation.Horizontal,
                        interactionSource = dragHandleInteractionSource,
                    )
                    .hoverable(dragHandleInteractionSource)
                    // TODO: Workaround for https://issuetracker.google.com/issues/319881002 to allow isPressed
                    //       to be true
                    .clickable(
                        interactionSource = dragHandleInteractionSource,
                        indication = null,
                        onClickLabel = null,
                        role = null,
                        onClick = {},
                    )
                    // Allow the drag handle to override the system navigation gesture
                    .systemGestureExclusion()
            ) {
                val isHovered by dragHandleInteractionSource.collectIsHoveredAsState()
                val isPressed by dragHandleInteractionSource.collectIsPressedAsState()
                val isDragged by dragHandleInteractionSource.collectIsDraggedAsState()
                val isActive = isHovered || isPressed || isDragged

                val width by animateDpAsState(
                    if (isActive) 12.dp else 4.dp,
                    label = "Drag Handle Width"
                )
                val color by animateColorAsState(
                    if (isActive) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                    label = "Drag Handle Color"
                )

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val height = 48.dp
                    val rectSize = DpSize(width, height).toSize()

                    drawRoundRect(
                        color = color,
                        topLeft = Offset(
                            (size.width - rectSize.width) / 2,
                            (size.height - rectSize.height) / 2,
                        ),
                        size = rectSize,
                        cornerRadius = CornerRadius(rectSize.width / 2f),
                    )
                }
            }
        } else if (showList) {
            start()
        } else {
            end()
        }
    }
}

enum class ExpandablePaneState {
    ListOnly, ListAndDetail, DetailOnly
}
