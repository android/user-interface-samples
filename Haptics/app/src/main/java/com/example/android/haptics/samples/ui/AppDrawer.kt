/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.example.android.haptics.samples.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.haptics.samples.R
import com.example.android.haptics.samples.ui.theme.DrawerButtonShape
import com.example.android.haptics.samples.ui.theme.HapticSamplerTheme
import com.example.android.haptics.samples.ui.theme.drawerButtonUnselected

@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToHome: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.app_name),
            modifier = modifier
                .padding(start = 32.dp)
                .padding(vertical = 16.dp),
            style = MaterialTheme.typography.h5
        )
        DrawerButton(
            icon = Icons.Rounded.Home,
            label = stringResource(R.string.home_screen_title),
            isSelected = currentRoute === HapticSamplerDestinations.HOME_ROUTE,
            onClick = {
                navigateToHome()
                closeDrawer()
            })
    }
}

@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier

) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colors.secondary
    } else {
        Color.Transparent
    }

    Surface(
        color = backgroundColor,
        shape = DrawerButtonShape,
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
    ) {
        TextButton(
            onClick = onClick,
            modifier = modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth()

        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val textAndIconColor = if (isSelected) MaterialTheme.colors.onPrimary
                else MaterialTheme.colors.drawerButtonUnselected
                Icon(icon, contentDescription = null, tint = textAndIconColor)
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    color = textAndIconColor,
                    style = MaterialTheme.typography.subtitle2,
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun AppDrawerPreview() {
    HapticSamplerTheme {
        AppDrawer(HapticSamplerDestinations.HOME_ROUTE, navigateToHome = {}, closeDrawer = {})
    }
}