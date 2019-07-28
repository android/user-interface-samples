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
package com.example.android.interactivesliceprovider

import android.app.PendingIntent
import android.app.slice.Slice.EXTRA_RANGE_VALUE
import android.app.slice.Slice.EXTRA_TOGGLE_STATE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Handler
import android.widget.Toast
import com.example.android.interactivesliceprovider.util.buildUriWithAuthority

class SliceActionsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            InteractiveSliceProvider.ACTION_WIFI_CHANGED -> {
                val wm = context.applicationContext
                    .getSystemService(Context.WIFI_SERVICE) as WifiManager
                val newState = intent.getBooleanExtra(EXTRA_TOGGLE_STATE, wm.isWifiEnabled)
                wm.isWifiEnabled = newState
                // Wait a bit for wifi to update (TODO: is there a better way to do this?)
                val h = Handler()
                h.postDelayed({
                    val uri = context.buildUriWithAuthority("wifi")
                    context.contentResolver.notifyChange(uri, null)
                }, 1000)
            }
            InteractiveSliceProvider.ACTION_TOAST -> {
                val message = intent.extras!!.getString(
                    InteractiveSliceProvider.EXTRA_TOAST_MESSAGE,
                    "no message"
                )
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            InteractiveSliceProvider.ACTION_TOAST_RANGE_VALUE -> {
                val range = intent.extras!!.getInt(EXTRA_RANGE_VALUE, 0)
                Toast.makeText(context, "value: $range", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun getIntent(context: Context, action: String, message: String?): PendingIntent {
            val intent = Intent(action)
            intent.setClass(context, SliceActionsBroadcastReceiver::class.java)
            // Ensure a new PendingIntent is created for each message.
            var requestCode = 0
            if (message != null) {
                intent.putExtra(InteractiveSliceProvider.EXTRA_TOAST_MESSAGE, message)
                requestCode = message.hashCode()
            }
            return PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}