/*
 * Copyright (C) 2022 The Android Open Source Project
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

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope

sealed interface PermissionStatus {
    object Granted : PermissionStatus
    class Denied(val shouldShowRationale: Boolean) : PermissionStatus
}

class PermissionRequest(
    private val fragment: Fragment,
    private val permission: String
) {

    private val _status = MutableLiveData<PermissionStatus>().also {
        fragment.lifecycleScope.launchWhenStarted {
            it.value = fragment.requireActivity().checkPermissionStatus(permission)
        }
    }

    val status: LiveData<PermissionStatus> = _status

    private val launcher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        _status.value = if (granted) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(
                ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(),
                    permission
                )
            )
        }
    }

    fun launch() {
        launcher.launch(permission)
    }
}

private fun Activity.checkPermissionStatus(permission: String): PermissionStatus {
    val check = ContextCompat.checkSelfPermission(this, permission)
    return if (check == PackageManager.PERMISSION_GRANTED) {
        PermissionStatus.Granted
    } else {
        PermissionStatus.Denied(
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        )
    }
}
