/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.google.sample.ae.pb

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.sample.ae.pb.ui.theme.ActivityEmbeddingAndPredictiveBackTheme

/***
 * This sample is an implementation of Activity Embedding API
 * (https://developer.android.com/guide/topics/large-screens/activity-embedding) with a focus on
 * embedding another app when there is enough space on the screen.
 *
 * It will also show how to start another app in multi-window mode from the host app.
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ActivityEmbeddingAndPredictiveBackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    HostUi(
                        multiWindow = this::launchSecondActivityInMultiWindow,
                        activityEmbedding = this::launchSecondActivityEmbedded
                    )
                }
            }
        }
    }

    /***
     * This method launches the embedded app with Activity Embedding API, so that on areas wider
     * than 600dp two activities can be displayed at the same time. On smaller areas the embedded
     * activity will just open at full screen.
     */
    private fun launchSecondActivityEmbedded() {
        val intent = Intent().apply {
            component = ComponentName(
                "com.google.sample.ae.guestapp",
                "com.google.sample.ae.guestapp.MainGuestActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
    }

    /***
     * This method launches the target Activity in Multi-Window mode: it will split the screen in
     * half - either vertically on compact screens, or horizontally on medium and expanded screens -
     * and display the two activities side by side
     */
    private fun launchSecondActivityInMultiWindow() {
        val intent = Intent().apply {
            component = ComponentName(
                "com.google.sample.ae.guestapp",
                "com.google.sample.ae.guestapp.MainGuestActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        }
        startActivity(intent)
    }
}

@Composable
fun HostUi(
    modifier: Modifier = Modifier,
    multiWindow: () -> Unit = {},
    activityEmbedding: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.host_app_message),
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = activityEmbedding) {
            Text(text = stringResource(R.string.activity_embedding_message))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = multiWindow) {
            Text(text = stringResource(R.string.multi_window_message))
        }
    }
}

@Preview(name = "EXPANDED", showBackground = true, widthDp = 840)
@Preview(name = "COMPACT", showBackground = true, showSystemUi = true)
annotation class UiPreview

@UiPreview
@Composable
fun SmallUiPreview() {
    ActivityEmbeddingAndPredictiveBackTheme {
        HostUi()
    }
}