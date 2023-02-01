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

package com.example.supportingpanecompose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.layout.DisplayFeature

// Create some simple sample data
private val data = mapOf(
    "android" to listOf("kotlin", "java", "flutter"),
    "kotlin" to listOf("backend", "android", "desktop"),
    "desktop" to listOf("kotlin", "java", "flutter"),
    "backend" to listOf("kotlin", "java"),
    "java" to listOf("backend", "android", "desktop"),
    "flutter" to listOf("android", "desktop")
)

@Composable
fun SupportingPaneSample(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>
) {
    var selectedTopic: String by rememberSaveable { mutableStateOf(data.keys.first()) }

    SupportingPane(
        main = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Main Content", style = MaterialTheme.typography.titleLarge)
                Text(selectedTopic)
            }
        },
        supporting = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Related Content", style = MaterialTheme.typography.titleLarge)

                LazyColumn {
                    items(
                        data.getValue(selectedTopic),
                        key = { it }
                    ) { relatedTopic ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTopic = relatedTopic
                                }
                        ) {
                            Text(
                                text = relatedTopic,
                                modifier = Modifier
                                    .padding(16.dp)

                            )
                        }
                    }
                }
            }
        },
        windowSizeClass = windowSizeClass,
        displayFeatures = displayFeatures
    )
}
