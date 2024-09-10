/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.supporting.pane.views

val data = mapOf(
    "android" to listOf("kotlin", "java", "flutter"),
    "kotlin" to listOf("backend", "android", "desktop"),
    "desktop" to listOf("kotlin", "java", "flutter"),
    "backend" to listOf("kotlin", "java"),
    "java" to listOf("backend", "android", "desktop"),
    "flutter" to listOf("android", "desktop")
)

data class State(val key: String = data.keys.first(), val items: List<String> = data.values.first())
