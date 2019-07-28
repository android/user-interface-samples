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

package com.example.android.interactivesliceprovider.data

import android.os.Handler

class FakeDataSource(val handler: Handler) : DataSource {
    override var gridData = GridData(title = "", subtitle = "", home = "", work = "", school = "")
    override var listData = ListData(home = "", work = "", school = "")

    private val fakeGridData =
            GridData(
                    title = "Heavy traffic in your area",
                    subtitle = "Typical conditions, with delays up to 28 min.",
                    home = "41 min",
                    work = "33 min",
                    school = "12 min"
            )
    private val gridDataCallbacks = mutableSetOf<Runnable>()

    private val fakeListData =
            ListData(
                    home = "41 min",
                    work = "33 min",
                    school = "12 min"
            )
    private val listDataCallbacks = mutableSetOf<Runnable>()

    override fun triggerGridDataFetch() {
        handler.postDelayed({
            gridData = fakeGridData
            gridDataCallbacks.forEach { it.run() }
        }, 1_500L)
    }

    override fun registerGridDataCallback(r: Runnable) {
        gridDataCallbacks.add(r)
    }

    override fun unregisterGridDataCallbacks() {
        gridDataCallbacks.clear()
    }

    override fun triggerListDataFetch() {
        handler.postDelayed({
            listData = fakeListData
            listDataCallbacks.forEach { it.run() }
        }, 1_500L)
    }

    override fun registerListDataCallback(r: Runnable) {
        listDataCallbacks.add(r)
    }

    override fun unregisterListDataCallbacks() {
        listDataCallbacks.clear()
    }
}