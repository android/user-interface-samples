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

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.feedcompose.R
import com.example.feedcompose.data.Sweets

@Composable
internal fun SquareSweetsCard(
    sweets: Sweets,
    modifier: Modifier = Modifier,
    onClick: (Sweets) -> Unit = {}
) {
    SweetsCard(
        sweets = sweets,
        modifier = modifier.aspectRatio(1.0f),
        onClick = onClick
    )
}

@Composable
internal fun PortraitSweetsCard(
    sweets: Sweets,
    modifier: Modifier = Modifier,
    onClick: (Sweets) -> Unit = {}
) {
    SweetsCard(
        sweets = sweets,
        modifier = modifier.aspectRatio(0.707f),
        onClick = onClick
    )
}

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
internal fun SweetsCard(
    sweets: Sweets,
    modifier: Modifier = Modifier,
    onClick: (Sweets) -> Unit = {}
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val outlineColor = MaterialTheme.colorScheme.outline
    val focusIndication = remember {
        OutlinedFocusIndication(
            shape = RoundedCornerShape(8.dp),
            outlineWidth = 5.dp,
            outlineColor = outlineColor
        )
    }
    val highlightColor = MaterialTheme.colorScheme.onPrimary
    val hoverIndication = remember {
        HighlightIndication(highlightColor = highlightColor, alpha = 0.4f) { _, isHovered ->
            isHovered
        }
    }

    val optionMenuState: MutableState<DpOffset?> = remember {
        mutableStateOf(null)
    }
    val localDensity = LocalDensity.current

    Card(
        modifier = modifier
            .hoverable(interactionSource, true)
            .indication(interactionSource, focusIndication)
            .indication(interactionSource, hoverIndication)
            .pointerHoverIcon(PointerIconDefaults.Hand)
            .rightClickable { x, y ->
                optionMenuState.value = with(localDensity) {
                    DpOffset(x.toDp(), y.toDp())
                }
            },
        onClick = { onClick(sweets) },
        interactionSource = interactionSource
    ) {
        optionMenuState.value?.let {
            OptionMenu(offset = it) {
                onClick(sweets)
            }
        }
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = sweets.imageUrl,
            contentDescription = stringResource(id = R.string.thumbnail_content_description),
            placeholder = painterResource(id = R.drawable.placeholder_sweets),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun OptionMenu(
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(x = 0.dp, y = 0.dp),
    onMenuItemSelected: () -> Unit = {}
) {
    ContextMenu(offset = offset, modifier = modifier) {
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.show_details)) },
            onClick = { onMenuItemSelected() }
        )
    }
}
