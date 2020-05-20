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

package com.example.android.people.ui.chat

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.android.people.R
import com.example.android.people.data.Message
import com.example.android.people.databinding.MessageItemBinding

class MessageAdapter(
    context: Context,
    private val onPhotoClicked: (photo: Uri) -> Unit
) : ListAdapter<Message, MessageViewHolder>(DIFF_CALLBACK) {

    private val tint = object {
        val incoming: ColorStateList = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.incoming)
        )
        val outgoing: ColorStateList = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.outgoing)
        )
    }

    private val padding = object {
        val vertical: Int = context.resources.getDimensionPixelSize(
            R.dimen.message_padding_vertical
        )

        val horizontalShort: Int = context.resources.getDimensionPixelSize(
            R.dimen.message_padding_horizontal_short
        )

        val horizontalLong: Int = context.resources.getDimensionPixelSize(
            R.dimen.message_padding_horizontal_long
        )
    }

    private val photoSize = context.resources.getDimensionPixelSize(R.dimen.photo_size)

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val holder = MessageViewHolder(parent)
        holder.binding.message.setOnClickListener {
            val photo = it.getTag(R.id.tag_photo) as Uri?
            if (photo != null) {
                onPhotoClicked(photo)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        val lp = holder.binding.message.layoutParams as FrameLayout.LayoutParams
        if (message.isIncoming) {
            holder.binding.message.run {
                setBackgroundResource(R.drawable.message_incoming)
                ViewCompat.setBackgroundTintList(this, tint.incoming)
                setPadding(
                    padding.horizontalLong, padding.vertical,
                    padding.horizontalShort, padding.vertical
                )
                layoutParams = lp.apply {
                    gravity = Gravity.START
                }
            }
        } else {
            holder.binding.message.run {
                setBackgroundResource(R.drawable.message_outgoing)
                ViewCompat.setBackgroundTintList(this, tint.outgoing)
                setPadding(
                    padding.horizontalShort, padding.vertical,
                    padding.horizontalLong, padding.vertical
                )
                layoutParams = lp.apply {
                    gravity = Gravity.END
                }
            }
        }
        if (message.photoUri != null) {
            holder.binding.message.setTag(R.id.tag_photo, message.photoUri)
            Glide.with(holder.binding.message)
                .load(message.photoUri)
                .into(CompoundBottomTarget(holder.binding.message, photoSize, photoSize))
        } else {
            holder.binding.message.setTag(R.id.tag_photo, null)
            holder.binding.message.setCompoundDrawables(null, null, null, null)
        }
        holder.binding.message.text = message.text
    }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {

    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

}

class MessageViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
) {
    val binding: MessageItemBinding = MessageItemBinding.bind(itemView)
}

/**
 * Sets a drawable into the specified TextView as its bottom compound drawable.
 */
private class CompoundBottomTarget(
    private val view: TextView,
    width: Int,
    height: Int
) : CustomTarget<Drawable>(width, height) {

    override fun onResourceReady(
        resource: Drawable,
        transition: Transition<in Drawable>?
    ) {
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, resource)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        view.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            null,
            placeholder
        )
    }
}
