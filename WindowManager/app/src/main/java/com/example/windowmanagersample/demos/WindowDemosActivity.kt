/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.windowmanagersample.demos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.windowmanagersample.DisplayFeaturesActivity
import com.example.windowmanagersample.R
import com.example.windowmanagersample.RxActivity
import com.example.windowmanagersample.SplitLayoutActivity
import com.example.windowmanagersample.WindowMetricsActivity
import com.example.windowmanagersample.databinding.ActivityWindowDemosBinding
import com.example.windowmanagersample.embedding.SplitActivityList

/**
 * Main activity that launches WindowManager demos.
 */
class WindowDemosActivity : AppCompatActivity() {

    lateinit var binding: ActivityWindowDemosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWindowDemosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val demoItems = listOf(
            DemoItem(
                buttonTitle = getString(R.string.activity_embedding),
                description = getString(R.string.activity_embedding_description),
                clazz = SplitActivityList::class.java
            ),
            DemoItem(
                buttonTitle = getString(R.string.display_features),
                description = getString(R.string.show_all_display_features_description),
                clazz = DisplayFeaturesActivity::class.java
            ),
            DemoItem(
                buttonTitle = getString(R.string.window_metrics),
                description = getString(R.string.window_metrics_description),
                clazz = WindowMetricsActivity::class.java
            ),
            DemoItem(
                buttonTitle = getString(R.string.split_layout),
                description = getString(R.string.split_layout_demo_description),
                clazz = SplitLayoutActivity::class.java
            ),
            DemoItem(
                buttonTitle = getString(R.string.rxJava),
                description = getString(R.string.rx_description),
                clazz = RxActivity::class.java
            )
        )
        val recyclerView = findViewById<RecyclerView>(R.id.demo_recycler_view)

        recyclerView.adapter = DemoAdapter(demoItems)
    }
}
