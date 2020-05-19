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

package com.example.android.people.ui.photo

import android.net.Uri
import android.os.Bundle
import android.transition.Fade
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.android.people.R
import com.example.android.people.databinding.PhotoFragmentBinding
import com.example.android.people.getNavigationController
import com.example.android.people.ui.viewBindings

/**
 * Shows the specified [DrawableRes] as a full-screen photo.
 */
class PhotoFragment : Fragment(R.layout.photo_fragment) {

    companion object {
        private const val ARG_PHOTO = "photo"

        fun newInstance(photo: Uri) = PhotoFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_PHOTO, photo)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Fade()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val photo = arguments?.getParcelable<Uri>(ARG_PHOTO)
        if (photo == null) {
            if (isAdded) {
                parentFragmentManager.popBackStack()
            }
            return
        }
        getNavigationController().updateAppBar(hidden = true)
        val binding by viewBindings(PhotoFragmentBinding::bind)
        Glide.with(this).load(photo).into(binding.photo)
    }
}
