/*
 * Copyright (C) 2019 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bubbles.ui.chat

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.android.bubbles.R
import com.example.android.bubbles.VoiceCallActivity
import com.example.android.bubbles.getNavigationController

/**
 * The chat screen. This is used in the full app (MainActivity) as well as in the expanded Bubble (BubbleActivity).
 */
class ChatFragment : Fragment() {

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_FOREGROUND = "foreground"

        fun newInstance(id: Long, foreground: Boolean) = ChatFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_ID, id)
                putBoolean(ARG_FOREGROUND, foreground)
            }
        }
    }

    private lateinit var viewModel: ChatViewModel
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition = TransitionInflater.from(context).inflateTransition(R.transition.slide_bottom)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    private val startPostponedTransitionOnEnd = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            startPostponedEnterTransition()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            startPostponedEnterTransition()
            return false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(ARG_ID)
        if (id == null) {
            fragmentManager?.popBackStack()
            return
        }
        val navigationController = getNavigationController()

        viewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)
        viewModel.setChatId(id)

        val messages: RecyclerView = view.findViewById(R.id.messages)
        val voiceCall: ImageButton = view.findViewById(R.id.voice_call)
        input = view.findViewById(R.id.input)
        val send: ImageButton = view.findViewById(R.id.send)

        val messageAdapter = MessageAdapter(view.context) { photo ->
            navigationController.openPhoto(photo)
        }
        val linearLayoutManager = LinearLayoutManager(view.context).apply {
            stackFromEnd = true
        }
        messages.run {
            layoutManager = linearLayoutManager
            adapter = messageAdapter
        }

        viewModel.contact.observe(viewLifecycleOwner, Observer { chat ->
            if (chat == null) {
                Toast.makeText(view.context, "Contact not found", Toast.LENGTH_SHORT).show()
                fragmentManager?.popBackStack()
            } else {
                navigationController.updateAppBar { name, icon ->
                    name.text = chat.name
                    Glide.with(icon)
                        .load(chat.icon)
                        .apply(RequestOptions.circleCropTransform())
                        .dontAnimate()
                        .addListener(startPostponedTransitionOnEnd)
                        .into(icon)
                }
            }
        })

        viewModel.messages.observe(viewLifecycleOwner, Observer {
            messageAdapter.submitList(it)
            linearLayoutManager.scrollToPosition(it.size - 1)
        })

        voiceCall.setOnClickListener {
            voiceCall()
        }
        send.setOnClickListener {
            send()
        }
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                send()
                true
            } else {
                false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val foreground = arguments?.getBoolean(ARG_FOREGROUND) == true
        viewModel.foreground = foreground
    }

    override fun onStop() {
        super.onStop()
        viewModel.foreground = false
    }

    private fun voiceCall() {
        val contact = viewModel.contact.value ?: return
        startActivity(
            Intent(requireActivity(), VoiceCallActivity::class.java)
                .putExtra(VoiceCallActivity.EXTRA_NAME, contact.name)
                .putExtra(VoiceCallActivity.EXTRA_ICON, contact.icon)
        )
    }

    private fun send() {
        val text = input.text.toString()
        if (text.isNotEmpty()) {
            input.text.clear()
            viewModel.send(text)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.chat, menu)
        menu?.findItem(R.id.action_show_as_bubble)?.let { item ->
            viewModel.showAsBubbleVisible.observe(viewLifecycleOwner, Observer {
                item.isVisible = it
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_show_as_bubble -> {
                viewModel.showAsBubble()
                fragmentManager?.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
