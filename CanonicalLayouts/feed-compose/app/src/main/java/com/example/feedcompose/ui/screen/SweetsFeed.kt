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

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.feedcompose.R
import com.example.feedcompose.data.Category
import com.example.feedcompose.data.DataProvider
import com.example.feedcompose.data.DataProvider.chocolates
import com.example.feedcompose.data.Sweets
import com.example.feedcompose.ui.components.feed.Feed
import com.example.feedcompose.ui.components.feed.action
import com.example.feedcompose.ui.components.feed.footer
import com.example.feedcompose.ui.components.feed.items
import com.example.feedcompose.ui.components.feed.row
import com.example.feedcompose.ui.components.feed.title
import kotlinx.coroutines.launch

@Composable
internal fun SweetsFeed(windowSizeClass: WindowSizeClass, onSweetsSelected: (Sweets) -> Unit = {}) {
    val selectedFilter: MutableState<Filter> = remember {
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
        items(DataProvider.misc, contentType = { "sweets" }, key = { it.id }) {
            SquareSweetsCard(sweets = it, onClick = onSweetsSelected)
        }
        title(contentType = "section-title") {
            SectionTitle(text = stringResource(id = R.string.chocolate))
        }
        row(contentType = "chocolate-list") {
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
        title(contentType = "section-title") {
            SectionTitle(text = stringResource(id = R.string.candy_or_pastry))
        }
        action(
            contentType = "filter-selector",
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterSelector(selectedFilter = selectedFilter.value) { selectedFilter.value = it }
        }
        items(sweets, contentType = { "sweets" }, key = { it.id }) {
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
        WindowWidthSizeClass.Compact -> GridCells.Fixed(1)
        WindowWidthSizeClass.Medium -> GridCells.Fixed(2)
        else -> GridCells.Adaptive(240.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSelector(selectedFilter: Filter, onFilterSelected: (Filter) -> Unit) {
    val filters = listOf(
        Filter.All to R.string.all,
        Filter.Candy to R.string.candy,
        Filter.Pastry to R.string.pastry
    )
    filters.forEach { (filter, labelId) ->
        val selected = selectedFilter == filter
        FilterChip(
            selected = selected,
            onClick = { onFilterSelected(filter) },
            label = { Text(text = stringResource(id = labelId)) },
            leadingIcon = {
                if (selected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_check_24),
                        contentDescription = null
                    )
                }
            }
        )
    }
}

@Composable
private fun BackToTopButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(contentAlignment = Alignment.Center) {
        Button(onClick = onClick, modifier = modifier) {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_arrow_upward_24), null)
            Text(text = "Back to top")
        }
    }
}

@Composable
private fun SquareSweetsCard(
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
private fun PortraitSweetsCard(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SweetsCard(
    sweets: Sweets,
    modifier: Modifier = Modifier,
    onClick: (Sweets) -> Unit = {}
) {
    var isFocused by remember {
        mutableStateOf(false)
    }
    val outlineColor = if (isFocused) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.background
    }

    Card(
        modifier = modifier
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .border(width = 2.dp, color = outlineColor),
        onClick = { onClick(sweets) }
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = sweets.imageUrl,
            contentDescription = stringResource(id = R.string.thumbnail_content_description),
            placeholder = painterResource(id = R.drawable.placeholder_sweets),
            contentScale = ContentScale.Crop
        )
    }
}

sealed class Filter(private val categories: List<Category>) {
    fun apply(sweets: Sweets): Boolean = categories.indexOf(sweets.category) != -1

    object All : Filter(listOf(Category.Candy, Category.Pastry))

    object Candy : Filter(listOf(Category.Candy))

    object Pastry : Filter(listOf(Category.Pastry))
}
