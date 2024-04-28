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

package com.example.feedcompose.ui.screen

import android.os.Parcelable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.feedcompose.R
import com.example.feedcompose.data.Category
import com.example.feedcompose.data.DataProvider
import com.example.feedcompose.data.DataProvider.chocolates
import com.example.feedcompose.data.Sweets
import com.example.feedcompose.ui.components.PortraitSweetsCard
import com.example.feedcompose.ui.components.SquareSweetsCard
import com.example.feedcompose.ui.components.TextInput
import com.example.feedcompose.ui.components.feed.Feed
import com.example.feedcompose.ui.components.feed.FeedGridCells
import com.example.feedcompose.ui.components.feed.action
import com.example.feedcompose.ui.components.feed.footer
import com.example.feedcompose.ui.components.feed.items
import com.example.feedcompose.ui.components.feed.section
import com.example.feedcompose.ui.components.feed.title
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Composable
internal fun SweetsFeed(windowSizeClass: WindowSizeClass, onSweetsSelected: (Sweets) -> Unit = {}) {
    val selectedFilter: MutableState<Filter> = rememberSaveable {
        mutableStateOf(Filter.All)
    }
    val sweets = DataProvider.sweets.filter { selectedFilter.value.apply(it) }
    val chocolates = DataProvider.chocolates

    val state = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    Feed(
        columns = rememberColumns(windowSizeClass = windowSizeClass),
        state = state,
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        title(contentType = "feed-title") {
            FeedTitle(text = stringResource(id = R.string.app_name))
        }
        action(
            contentType = "filter-selector",
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterSelector(selectedFilter = selectedFilter.value) { selectedFilter.value = it }
            if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) {
                Spacer(modifier = Modifier.weight(1f))
                SearchTextInput(modifier = Modifier.defaultMinSize(minWidth = 400.dp))
            }
        }
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            action { SearchTextInput(modifier = Modifier.weight(1f)) }
        }
        items(sweets, contentType = { "sweets" }, key = { it.id }) {
            SquareSweetsCard(
                sweets = it,
                onClick = onSweetsSelected
            )
        }
        title(contentType = "section-title") {
            SectionTitle(text = stringResource(id = R.string.chocolate))
        }
        section(contentType = "chocolate-list") {
            HorizontalSweetsList(
                sweets = chocolates,
                cardWidth = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                    240.dp
                } else {
                    128.dp
                },
                onSweetsSelected = onSweetsSelected
            )
        }
        items(DataProvider.misc, contentType = { "sweets" }, key = { it.id }) {
            SquareSweetsCard(sweets = it, onClick = onSweetsSelected)
        }
        footer {
            BackToTopButton(modifier = Modifier.padding(PaddingValues(top = 32.dp))) {
                coroutineScope.launch {
                    state.animateScrollToItem(0)
                }
            }
        }
    }
}

@Composable
private fun rememberColumns(windowSizeClass: WindowSizeClass) = remember(windowSizeClass) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> FeedGridCells.Fixed(1)
        WindowWidthSizeClass.Medium -> FeedGridCells.Fixed(2)
        else -> FeedGridCells.Adaptive(240.dp)
    }
}

@Composable
private fun FeedTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(PaddingValues(vertical = 24.dp))
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(PaddingValues(top = 32.dp, bottom = 8.dp))
    )
}

@Composable
private fun HorizontalSweetsList(
    sweets: List<Sweets>,
    cardWidth: Dp,
    onSweetsSelected: (Sweets) -> Unit = {}
) {
    LazyRow(
        modifier = Modifier.padding(PaddingValues(bottom = 16.dp)),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sweets.size, key = { sweets[it].id }, contentType = { "sweets" }) {
            PortraitSweetsCard(
                sweets = chocolates[it],
                onClick = onSweetsSelected,
                modifier = Modifier.width(cardWidth)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun FilterSelector(selectedFilter: Filter, onFilterSelected: (Filter) -> Unit) {
    val filters = listOf(
        Filter.All to R.string.all,
        Filter.Candy to R.string.candy,
        Filter.Pastry to R.string.pastry
    )
    filters.forEach { (filter, labelId) ->
        val selected = selectedFilter == filter
        val interactionSource = remember {
            MutableInteractionSource()
        }
        val isFocused by interactionSource.collectIsFocusedAsState()
        val borderColor = if (isFocused) {
            MaterialTheme.colorScheme.outline
        } else {
            Color.Transparent
        }
        FilterChip(
            modifier = Modifier
                .pointerHoverIcon(PointerIconDefaults.Hand),
            selected = selected,
            border = FilterChipDefaults.filterChipBorder(borderColor = borderColor),
            onClick = { onFilterSelected(filter) },
            label = { Text(text = stringResource(id = labelId)) },
            leadingIcon = {
                if (selected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_check_24),
                        contentDescription = null
                    )
                }
            },
            interactionSource = interactionSource
        )
    }
}

@Parcelize
sealed class Filter(private val categories: List<Category>) : Parcelable {
    fun apply(sweets: Sweets): Boolean = categories.indexOf(sweets.category) != -1

    object All : Filter(listOf(Category.Candy, Category.Pastry))

    object Candy : Filter(listOf(Category.Candy))

    object Pastry : Filter(listOf(Category.Pastry))
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BackToTopButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(contentAlignment = Alignment.Center) {
        Button(onClick = onClick, modifier = modifier.pointerHoverIcon(PointerIconDefaults.Hand)) {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_arrow_upward_24), null)
            Text(text = stringResource(id = R.string.back_to_top))
        }
    }
}

@Composable
private fun SearchTextInput(modifier: Modifier = Modifier) {
    TextInput(
        placeholderText = stringResource(id = R.string.search_text),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_search_24),
                contentDescription = stringResource(id = R.string.search)
            )
        },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Go
        )
    )
}
