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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.runtime.Composable

@OptIn(ExperimentalFoundationApi::class)
internal class FeedScopeImpl : FeedScope {
    val items = mutableListOf<FeedItem>()

    override fun item(
        key: Any?,
        span: (LazyGridItemSpanScope.() -> GridItemSpan)?,
        contentType: Any?,
        content: @Composable LazyGridItemScope.() -> Unit
    ) {
        items.add(
            FeedItem(
                count = 1,
                key = if (key != null) {
                    { key }
                } else {
                    null
                },
                span = if (span != null) {
                    { span() }
                } else {
                    null
                },
                contentType = { contentType },
                itemContent = { content() }
            )
        )
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        span: (LazyGridItemSpanScope.(index: Int) -> GridItemSpan)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyGridItemScope.(index: Int) -> Unit
    ) {
        items.add(
            FeedItem(
                count = count,
                key = key,
                span = span,
                contentType = contentType,
                itemContent = itemContent
            )
        )
    }
}

internal data class FeedItem(
    val count: Int,
    val key: ((index: Int) -> Any)?,
    val span: (LazyGridItemSpanScope.(index: Int) -> GridItemSpan)?,
    val contentType: (index: Int) -> Any?,
    val itemContent: @Composable LazyGridItemScope.(index: Int) -> Unit
)
