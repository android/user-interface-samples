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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.feedcompose.R
import com.example.feedcompose.data.Sweets
import com.example.feedcompose.ui.components.SweetTopAppBar

@Composable
fun SweetsDetails(
    sweets: Sweets,
    windowSizeClass: WindowSizeClass,
    onBackPressed: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> SweetsDetailsHorizontal(
            sweets = sweets,
            scrollState = scrollState,
            onBackPressed = onBackPressed
        )

        WindowWidthSizeClass.Compact -> SweetsDetailsVertical(
            sweets = sweets,
            scrollState = scrollState,
            onBackPressed = onBackPressed
        )

        else -> {
            when (windowSizeClass.heightSizeClass) {
                WindowHeightSizeClass.Expanded -> SweetsDetailsVertical(
                    sweets = sweets,
                    scrollState = scrollState,
                    onBackPressed = onBackPressed
                )

                else -> SweetsDetailsHorizontal(
                    sweets = sweets,
                    scrollState = scrollState,
                    onBackPressed = onBackPressed
                )
            }
        }
    }
}

@Composable
private fun SweetsDetailsVertical(
    sweets: Sweets,
    scrollState: ScrollState,
    onBackPressed: () -> Unit
) {
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        SweetTopAppBar(onBackPressed = onBackPressed)
        AsyncImage(
            model = sweets.imageUrl,
            contentDescription = stringResource(id = R.string.thumbnail_content_description),
            modifier = Modifier
                .aspectRatio(1.414f)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Text(
            text = stringResource(id = sweets.description),
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
private fun SweetsDetailsHorizontal(
    sweets: Sweets,
    scrollState: ScrollState,
    onBackPressed: () -> Unit
) {
    Column {
        SweetTopAppBar(onBackPressed = onBackPressed)
        Row {
            AsyncImage(
                model = sweets.imageUrl,
                contentDescription = stringResource(id = R.string.thumbnail_content_description),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_sweets),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1.0f)
            )
            Text(
                text = stringResource(id = sweets.description),
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                    .weight(1.0f)
                    .verticalScroll(scrollState)
            )
        }
    }
}
