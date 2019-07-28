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

package com.example.android.sliceviewer.ui.list

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.slice.widget.SliceView
import com.example.android.sliceviewer.R
import com.example.android.sliceviewer.R.drawable
import com.example.android.sliceviewer.R.id
import com.example.android.sliceviewer.R.layout
import com.example.android.sliceviewer.R.string
import com.example.android.sliceviewer.ui.ViewModelFactory

/**
 * Example use of SliceView. Uses a search bar to select/auto-complete a slice uri which is
 * then displayed in the selected mode with SliceView.
 */
class SliceViewerActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var sliceAdapter: SliceAdapter
    private lateinit var viewModel: SliceViewModel
    private lateinit var typeMenu: SubMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_slice_viewer)
        val viewModelFactory = ViewModelFactory.getInstance(application)
        findViewById<Toolbar>(id.search_toolbar).let {
            setSupportActionBar(it)
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SliceViewModel::class.java)

        searchView = findViewById<SearchView>(id.search_view).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?) = false
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.addSlice(Uri.parse(query))
                    sliceAdapter.submitList(viewModel.slices)
                    searchView.setQuery("", false)
                    searchView.clearFocus()
                    return false
                }
            })
            setOnClickListener {
                searchView.isIconified = false
            }
            setOnFocusChangeListener { v: View, hasFocus: Boolean ->
                if (!hasFocus) {
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
            queryHint = getString(string.uri_input_hint)
        }

        sliceAdapter = SliceAdapter(
            lifecycleOwner = this,
            selectedMode = viewModel.selectedMode
        )

        findViewById<RecyclerView>(id.slice_list).apply {
            adapter = sliceAdapter
            ItemTouchHelper(SwipeCallback()).attachToRecyclerView(this)
        }
        sliceAdapter.submitList(viewModel.slices)
    }

    inner class SwipeCallback :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            viewModel.removeFromPosition(viewHolder.adapterPosition)
            sliceAdapter.submitList(viewModel.slices)
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
        const val TAG = "SliceViewer"
    }
}