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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.window.layout.DisplayFeature
import com.google.accompanist.adaptive.FoldAwareConfiguration
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.TwoPaneStrategy

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
 * to show both the list and the detail at the same time. This content is displayed in a [TwoPane]
 * with the given [twoPaneStrategy].
 *
 * When there is not enough space to display both list and detail, which slot is displayed is based
 * on [isDetailOpen]. Internally, this state is changed in an opinionated way via [setIsDetailOpen].
 * For instance, when showing just the detail screen, a back button press will call
 * [setIsDetailOpen] passing `false`.
 */
@Composable
fun ListDetail(
    isDetailOpen: Boolean,
    setIsDetailOpen: (Boolean) -> Unit,
    showListAndDetail: Boolean,
    detailKey: Any?,
    list: @Composable (isDetailVisible: Boolean) -> Unit,
    detail: @Composable (isListVisible: Boolean) -> Unit,
    twoPaneStrategy: TwoPaneStrategy,
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

            // If showing just the detail, allow a back press to hide the detail to return to
            // the list.
            if (!showList) {
                BackHandler {
                    setIsDetailOpen(false)
                }
            }
        }
    }

    Box(modifier = modifier) {
        if (showList && showDetail) {
            TwoPane(
                first = {
                    start()
                },
                second = {
                    end()
                },
                strategy = twoPaneStrategy,
                displayFeatures = displayFeatures,
                foldAwareConfiguration = FoldAwareConfiguration.VerticalFoldsOnly,
                modifier = Modifier.fillMaxSize(),
            )
        } else if (showList) {
            start()
        } else {
            end()
        }
    }
}
