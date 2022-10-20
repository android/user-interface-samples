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

package com.example.feedcompose.ui.components.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Feed(
    modifier: Modifier = Modifier,
    columns: GridCells = GridCells.Fixed(1),
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    content: @ExtensionFunctionType FeedScope.() -> Unit
) {
    val feedScope = FeedScopeImpl().apply(content)
    LazyVerticalGrid(
        columns = columns,
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        flingBehavior = flingBehavior
    ) {
        feedScope.items.forEach { feedItem ->
            items(
                count = feedItem.count,
                key = feedItem.key,
                contentType = feedItem.contentType,
                span = feedItem.span,
                itemContent = feedItem.itemContent
            )
        }
    }
}

interface FeedScope {
    fun item(
        key: Any? = null,
        span: (@ExtensionFunctionType LazyGridItemSpanScope.() -> GridItemSpan)? = null,
        contentType: Any? = null,
        content: @Composable LazyGridItemScope.() -> Unit
    )

    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        span: (@ExtensionFunctionType LazyGridItemSpanScope.(index: Int) -> GridItemSpan)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable LazyGridItemScope.(index: Int) -> Unit
    )
}

inline fun <T> FeedScope.items(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline span: (@ExtensionFunctionType LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline itemContent: @Composable LazyGridItemScope.(item: T) -> Unit
) = items(
    count = items.size,
    key = if (key != null) {
        { index: Int -> key(items[index]) }
    } else {
        null
    },
    span = if (span != null) {
        { index: Int -> span(items[index]) }
    } else {
        null
    },
    contentType = { index: Int -> contentType(items[index]) }
) { index ->
    itemContent(items[index])
}

inline fun <T> FeedScope.itemsIndexed(
    items: List<T>,
    noinline key: ((index: Int, item: T) -> Any)? = null,
    noinline span: (@ExtensionFunctionType LazyGridItemSpanScope.(index: Int, item: T) -> GridItemSpan)? = null,
    noinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyGridItemScope.(index: Int, item: T) -> Unit
) = items(
    count = items.size,
    key = if (key != null) {
        { index: Int -> key(index, items[index]) }
    } else {
        null
    },
    span = if (span != null) {
        { index: Int -> span(index, items[index]) }
    } else {
        null
    },
    contentType = { index: Int -> contentType(index, items[index]) }
) { index ->
    itemContent(index, items[index])
}

inline fun <T> FeedScope.items(
    items: Array<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline span: (@ExtensionFunctionType LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline itemContent: @Composable LazyGridItemScope.(item: T) -> Unit
) = items(
    count = items.size,
    key = if (key != null) {
        { index: Int -> key(items[index]) }
    } else {
        null
    },
    span = if (span != null) {
        { index: Int -> span(items[index]) }
    } else {
        null
    },
    contentType = { index: Int -> contentType(items[index]) }
) { index ->
    itemContent(items[index])
}

inline fun <T> FeedScope.itemsIndexed(
    items: Array<T>,
    noinline key: ((index: Int, item: T) -> Any)? = null,
    noinline span: (@ExtensionFunctionType LazyGridItemSpanScope.(index: Int, item: T) -> GridItemSpan)? = null,
    noinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyGridItemScope.(index: Int, item: T) -> Unit
) = items(
    count = items.size,
    key = if (key != null) {
        { index: Int -> key(index, items[index]) }
    } else {
        null
    },
    span = if (span != null) {
        { index: Int -> span(index, items[index]) }
    } else {
        null
    },
    contentType = { index: Int -> contentType(index, items[index]) }
) { index ->
    itemContent(index, items[index])
}

inline fun FeedScope.row(
    key: Any? = null,
    contentType: Any? = null,
    crossinline content: @Composable LazyGridItemScope.() -> Unit
) = item(
    key = key,
    span = { GridItemSpan(maxLineSpan) },
    contentType = contentType
) {
    content()
}

@OptIn(ExperimentalFoundationApi::class)
inline fun FeedScope.title(
    key: Any? = null,
    contentType: Any? = null,
    crossinline content: @Composable LazyGridItemScope.() -> Unit
) = row(key = key, contentType = contentType) { content() }

@OptIn(ExperimentalFoundationApi::class)
inline fun FeedScope.action(
    key: Any? = null,
    contentType: Any? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    crossinline content: @Composable RowScope.() -> Unit
) = row(
    key = key,
    contentType = contentType
) {
    Row(
        modifier = Modifier
            .focusGroup()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = horizontalArrangement
    ) {
        content()
    }
}

inline fun FeedScope.footer(
    key: Any? = null,
    contentType: Any? = null,
    crossinline content: @Composable LazyGridItemScope.() -> Unit
) = item(
    key = key,
    span = { GridItemSpan(maxLineSpan) },
    contentType = contentType
) {
    content()
}
