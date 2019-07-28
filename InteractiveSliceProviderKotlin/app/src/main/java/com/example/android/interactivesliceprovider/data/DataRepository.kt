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

class DataRepository(val dataSource: FakeDataSource) {

    fun getGridData() = dataSource.gridData
    fun getListData() = dataSource.listData

    fun registerGridSliceDataCallback(r: Runnable) {
        dataSource.registerGridDataCallback(r)
        dataSource.triggerGridDataFetch()
    }

    fun unregisterGridSliceDataCallbacks() {
        dataSource.unregisterGridDataCallbacks()
    }

    fun registerListSliceDataCallback(r: Runnable) {
        dataSource.registerListDataCallback(r)
        dataSource.triggerListDataFetch()
    }

    fun unregisterListSliceDataCallbacks() {
        dataSource.unregisterListDataCallbacks()
    }

    companion object {
        const val TAG = "DataRepository"
    }
}

// Model classes

data class GridData(
    val title: String,
    val subtitle: String,
    val home: String,
    val work: String,
    val school: String
)

data class ListData(
    val home: String,
    val work: String,
    val school: String
)