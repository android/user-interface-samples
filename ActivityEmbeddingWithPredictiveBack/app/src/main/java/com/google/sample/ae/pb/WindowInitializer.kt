/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.google.sample.ae.pb

import android.content.Context
import androidx.startup.Initializer
import androidx.window.embedding.SplitController

/***
 * This class loads the split configuration that defines how the app will show the host Activity
 * and the Placeholder, and it is loaded on areas wider than 600dp. On areas smaller than 600dp
 * instead, this class will have no effect on the behaviour.
 */
class WindowInitializer : Initializer<SplitController> {
    override fun create(context: Context): SplitController {
        SplitController.initialize(context, R.xml.main_split_config)
        return SplitController.getInstance()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}