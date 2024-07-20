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

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.feedcompose.R
import androidx.compose.material3.TopAppBar as M3TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBar(onBackPressed: () -> Unit = {}) {
    M3TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = { BackButton(onBackPressed) }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BackButton(onBackPressed: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val backgroundColor = if (isFocused || isHovered) {
        MaterialTheme.colorScheme.outlineVariant
    } else {
        Color.Transparent
    }

    IconButton(
        onClick = onBackPressed,
        modifier = Modifier
            .pointerHoverIcon(PointerIconDefaults.Hand)
            .background(backgroundColor, CircleShape),
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
            contentDescription = null
        )
    }
}
