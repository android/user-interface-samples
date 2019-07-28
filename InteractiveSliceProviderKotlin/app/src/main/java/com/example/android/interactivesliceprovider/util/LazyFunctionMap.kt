/*
 * Copyright 2018 The Android Open Source Project
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

package com.example.android.interactivesliceprovider.util

/**
 * Generic map, useful for keeping a map of lambdas that are lazily converted to Runnables.
 */
class LazyFunctionMap<K, V>(val method: (key: K) -> V) {
    val map = hashMapOf<K, Runnable>()
    operator fun get(key: K): Runnable {
        var value = map[key]
        if (value == null) {
            value = Runnable {
                method(key)
            }
            map[key] = value
        }
        return value
    }
}
