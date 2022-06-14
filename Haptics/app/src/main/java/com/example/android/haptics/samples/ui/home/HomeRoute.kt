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
package com.example.android.haptics.samples.ui.home

import android.os.VibrationEffect
import android.view.View
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.haptics.samples.R
import com.example.android.haptics.samples.ui.theme.HapticSamplerTheme
import com.example.android.haptics.samples.ui.theme.buttonSurface
import com.example.android.haptics.samples.ui.theme.onButtonSurface
import com.example.android.haptics.samples.ui.theme.subtitleVariant
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
) {
    HomeScreen(
        homeUiState = viewModel.homeUiState, onButtonClicked = viewModel::onButtonClicked,
        onSnackbarMessage = viewModel::onSnackbarMessage, scrollState = viewModel.scrollState,
    )
}

@Composable
private fun HomeScreen(
    homeUiState: HomeUiState,
    onButtonClicked: (view: View, hapticCategory: HapticCategoryType, hapticId: Int) -> Unit,
    onSnackbarMessage: (message: String) -> Unit,
    scrollState: ScrollState = rememberScrollState(),
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start

    ) {
        Text(
            text = stringResource(R.string.home_screen_title),
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(top = 36.dp, bottom = 16.dp)
        )

        for (category in homeUiState.hapticCategories) {
            HomeHapticCategory(label = category.label) {

                // 2 buttons for ever row for each haptic feedback category.
                for (buttons in category.buttons.chunked(2)) {
                    Row(
                        Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for ((index, button) in buttons.withIndex()) {
                            // Add some spacing before the second button.
                            if (index == 1) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            HomeHapticButton(
                                label = button.label,
                                isEnabled = button.worksOnUserDevice,
                                onClick = {
                                    if (!button.worksOnUserDevice) {
                                        coroutineScope.launch {
                                            onSnackbarMessage("${button.label} is not supported on this device.")
                                        }
                                        return@HomeHapticButton
                                    }
                                    onButtonClicked(it, category.categoryType, button.hapticId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHapticCategory(
    label: String,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.subtitleVariant
            )
        }
        content()
    }
}

@Composable
private fun HomeHapticButton(
    label: String,
    onClick: (view: View) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    val alpha = if (isEnabled) 1f else 0.5f
    val view = LocalView.current
    TextButton(
        onClick = { onClick(view) },
        modifier = modifier
            .height(64.dp)
            .width(180.dp)
            .padding(4.dp)
            .background(
                color = MaterialTheme.colors.buttonSurface,
                shape = MaterialTheme.shapes.large
            )
            .alpha(alpha)
    ) {
        Text(
            label,
            color = MaterialTheme.colors.onButtonSurface,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.alpha(alpha)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HapticSamplerTheme {

        HomeScreen(
            homeUiState = HomeUiState(
                listOf(
                    HapticCategory(
                        "Effects",
                        categoryType = HapticCategoryType.PREDEFINED_EFFECTS,
                        buttons = listOf(
                            HapticButton(
                                "Tick", true, VibrationEffect.EFFECT_TICK
                            ),
                            HapticButton(
                                "Click", false, VibrationEffect.EFFECT_CLICK
                            )
                        )
                    ),
                    HapticCategory(
                        "Primitives",
                        categoryType = HapticCategoryType.COMPOSITION_PRIMITIVES,
                        buttons = listOf(
                            HapticButton(
                                "Spin",
                                true,
                                VibrationEffect.Composition.PRIMITIVE_SPIN
                            ),
                            HapticButton(
                                "Thud",
                                false,
                                VibrationEffect.Composition.PRIMITIVE_THUD
                            )
                        )
                    )
                )
            ),
            onButtonClicked = { view, hapticCategoryType, hapticId -> },
            onSnackbarMessage = {}
        )
    }
}
