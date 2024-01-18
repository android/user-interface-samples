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

package com.example.listdetailcompose.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.layout.DisplayFeature
import com.example.listdetailcompose.R
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy

// Create some simple sample data
private val loremIpsum = """
        |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Dui nunc mattis enim ut tellus elementum sagittis. Nunc sed augue lacus viverra vitae. Sit amet dictum sit amet justo donec. Fringilla urna porttitor rhoncus dolor purus non enim praesent elementum. Dictum non consectetur a erat nam at lectus urna. Tellus mauris a diam maecenas sed enim ut sem viverra. Commodo ullamcorper a lacus vestibulum sed arcu non. Lorem mollis aliquam ut porttitor leo a diam sollicitudin tempor. Pellentesque habitant morbi tristique senectus et netus et malesuada. Vitae suscipit tellus mauris a diam maecenas sed. Neque ornare aenean euismod elementum nisi quis. Quam vulputate dignissim suspendisse in est ante in nibh mauris. Tellus in metus vulputate eu scelerisque felis imperdiet proin fermentum. Orci ac auctor augue mauris augue neque gravida.
        |
        |Tempus quam pellentesque nec nam aliquam. Praesent semper feugiat nibh sed. Adipiscing elit duis tristique sollicitudin nibh sit. Netus et malesuada fames ac turpis egestas sed tempus urna. Quis varius quam quisque id diam vel quam. Urna duis convallis convallis tellus id interdum velit laoreet. Id eu nisl nunc mi ipsum. Fermentum dui faucibus in ornare. Nunc lobortis mattis aliquam faucibus. Vulputate mi sit amet mauris commodo quis. Porta nibh venenatis cras sed. Vitae tortor condimentum lacinia quis vel eros donec. Eu non diam phasellus vestibulum.
        """.trimMargin()
private val sampleWords = listOf(
    "Apple" to loremIpsum,
    "Banana" to loremIpsum,
    "Cherry" to loremIpsum,
    "Date" to loremIpsum,
    "Elderberry" to loremIpsum,
    "Fig" to loremIpsum,
    "Grape" to loremIpsum,
    "Honeydew" to loremIpsum,
).map { (word, definition) -> DefinedWord(word, definition) }

private data class DefinedWord(
    val word: String,
    val definition: String
)

@Composable
fun ListDetailSample(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>
) {
    // Query for the current window size class
    val widthSizeClass by rememberUpdatedState(windowSizeClass.widthSizeClass)

    /**
     * The index of the currently selected word, or `null` if none is selected
     */
    var selectedWordIndex: Int? by rememberSaveable { mutableStateOf(null) }

    /**
     * True if the detail is currently open. This is the primary control for "navigation".
     */
    var isDetailOpen by rememberSaveable { mutableStateOf(false) }

    ListDetail(
        isDetailOpen = isDetailOpen,
        setIsDetailOpen = { isDetailOpen = it },
        showListAndDetail =
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> false
                WindowWidthSizeClass.Expanded -> true
                else -> true
            },
        detailKey = selectedWordIndex,
        list = { isDetailVisible ->
            val currentSelectedWordIndex = selectedWordIndex
            ListContent(
                words = sampleWords.map(DefinedWord::word),
                selectionState = if (isDetailVisible && currentSelectedWordIndex != null) {
                    SelectionVisibilityState.ShowSelection(currentSelectedWordIndex)
                } else {
                    SelectionVisibilityState.NoSelection
                },
                onIndexClick = { index ->
                    selectedWordIndex = index
                    // Consider the detail to now be open. This acts like a navigation if
                    // there isn't room for both list and detail, and also will result
                    // in the detail remaining open in the case of resize.
                    isDetailOpen = true
                },
                modifier = if (isDetailVisible) {
                    Modifier.padding(end = 8.dp)
                } else {
                    Modifier
                }
            )
        },
        detail = { isListVisible ->
            val definedWord = selectedWordIndex?.let(sampleWords::get)
            DetailContent(
                definedWord = definedWord,
                modifier = if (isListVisible) {
                    Modifier.padding(start = 8.dp)
                } else {
                    Modifier
                }
            )
        },
        displayFeatures = displayFeatures,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

/**
 * The description of the selection state for the [ListContent]
 */
sealed interface SelectionVisibilityState {

    /**
     * No selection should be shown, and each item should be clickable.
     */
    object NoSelection : SelectionVisibilityState

    /**
     * Selection state should be shown, and each item should be selectable.
     */
    data class ShowSelection(
        /**
         * The index of the word that is selected.
         */
        val selectedWordIndex: Int
    ) : SelectionVisibilityState
}

/**
 * The content for the list pane.
 */
@Composable
private fun ListContent(
    words: List<String>,
    selectionState: SelectionVisibilityState,
    onIndexClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .then(
                when (selectionState) {
                    SelectionVisibilityState.NoSelection -> Modifier
                    is SelectionVisibilityState.ShowSelection -> Modifier.selectableGroup()
                }
            )
    ) {
        itemsIndexed(words) { index, word ->
            val interactionSource = remember { MutableInteractionSource() }

            val interactionModifier = when (selectionState) {
                SelectionVisibilityState.NoSelection -> {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(),
                        onClick = { onIndexClick(index) }
                    )
                }
                is SelectionVisibilityState.ShowSelection -> {
                    Modifier.selectable(
                        selected = index == selectionState.selectedWordIndex,
                        interactionSource = interactionSource,
                        indication = rememberRipple(),
                        onClick = { onIndexClick(index) }
                    )
                }
            }

            val containerColor = when (selectionState) {
                SelectionVisibilityState.NoSelection -> MaterialTheme.colorScheme.surface
                is SelectionVisibilityState.ShowSelection ->
                    if (index == selectionState.selectedWordIndex) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
            }
            val borderStroke = when (selectionState) {
                SelectionVisibilityState.NoSelection -> BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                )
                is SelectionVisibilityState.ShowSelection ->
                    if (index == selectionState.selectedWordIndex) {
                        null
                    } else {
                        BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        )
                    }
            }

            // TODO: Card selection overfills the Card
            Card(
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = borderStroke,
                modifier = Modifier
                    .then(interactionModifier)
                    .fillMaxWidth()
            ) {
                Text(
                    text = word,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

/**
 * The content for the detail pane.
 */
@Composable
private fun DetailContent(
    definedWord: DefinedWord?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        if (definedWord != null) {
            Text(
                text = definedWord.word,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = definedWord.definition
            )
        } else {
            Text(
                text = stringResource(R.string.placeholder)
            )
        }
    }
}
