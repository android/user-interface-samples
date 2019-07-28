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

package com.example.android.sliceviewer.ui.single

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.slice.widget.SliceView
import com.example.android.sliceviewer.R
import com.example.android.sliceviewer.R.drawable
import com.example.android.sliceviewer.R.layout
import com.example.android.sliceviewer.ui.ViewModelFactory
import com.example.android.sliceviewer.ui.list.SliceViewerActivity
import com.example.android.sliceviewer.util.bind
import com.example.android.sliceviewer.util.convertToOriginalScheme
import com.example.android.sliceviewer.util.hasSupportedSliceScheme

/**
 * Example use of SliceView.
 */
class SingleSliceViewerActivity : AppCompatActivity() {

    private lateinit var viewModel: SingleSliceViewModel
    private lateinit var sliceView: SliceView
    private lateinit var uriValue: TextView
    private lateinit var typeMenu: SubMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_single_slice_viewer)
        val viewModelFactory = ViewModelFactory.getInstance(application)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SingleSliceViewModel::class.java)

        sliceView = findViewById(R.id.slice)
        uriValue = findViewById(R.id.uri_value)

        // If a URI was passed in has a supported slice scheme, present the Slice and save it to the
        // persistent list of Slices
        if (intent.data != null && intent.data.hasSupportedSliceScheme()) {
            val sliceUri = intent.data.convertToOriginalScheme()
            viewModel.addSlice(sliceUri)
            bindSlice(sliceUri)
        } else {
            // No Slice found, fall back to main page.
            val msg = "No Slice URI found, sending to SliceViewerActivity"
            Log.w(TAG, msg)
            Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SliceViewerActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        typeMenu = menu.addSubMenu(R.string.slice_mode_title).apply {
            setIcon(drawable.ic_large)
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            add(R.string.shortcut_mode)
            add(R.string.small_mode)
            add(R.string.large_mode)
        }

        viewModel.selectedMode.observe(this, Observer {
            when (it) {
                SliceView.MODE_SHORTCUT -> typeMenu.setIcon(R.drawable.ic_shortcut)
                SliceView.MODE_SMALL -> typeMenu.setIcon(R.drawable.ic_small)
                SliceView.MODE_LARGE -> typeMenu.setIcon(R.drawable.ic_large)
            }
        })
        super.onCreateOptionsMenu(menu)
        return true
    }

    private fun bindSlice(uri: Uri) {
        sliceView.bind(
            context = this,
            lifecycleOwner = this,
            uri = uri,
            scrollable = true
        )
        viewModel.selectedMode.observe(this, Observer {
            sliceView.mode = it ?: SliceView.MODE_LARGE
        })
        uriValue.text = uri.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.title.toString()) {
            getString(R.string.shortcut_mode) ->
                viewModel.selectedMode.value = SliceView.MODE_SHORTCUT
            getString(R.string.small_mode) ->
                viewModel.selectedMode.value = SliceView.MODE_SMALL
            getString(R.string.large_mode) ->
                viewModel.selectedMode.value = SliceView.MODE_LARGE
        }
        return true
    }

    companion object {
        const val TAG = "SingleSliceViewer"
    }
}