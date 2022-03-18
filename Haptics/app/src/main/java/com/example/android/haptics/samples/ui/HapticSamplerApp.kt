/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.haptics.samples.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.android.haptics.samples.R
import com.example.android.haptics.samples.ui.theme.DrawerShape
import com.example.android.haptics.samples.ui.theme.HapticSamplerTheme
import kotlinx.coroutines.launch

@Composable
fun HapticSamplerApp() {
    HapticSamplerTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            HapticSamplerNavigation(navController)
        }

        val coroutineScope = rememberCoroutineScope()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.destination?.route ?: HapticSamplerDestinations.HOME_ROUTE

        val scaffoldState = rememberScaffoldState()

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {},
                    elevation = 0.dp,
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                toggleDrawer(scaffoldState)
                            }
                        }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.drawer_content_description)
                            )
                        }
                    }
                )
            },
            drawerShape = DrawerShape,
            drawerContent = {
                AppDrawer(
                    currentRoute = currentRoute,
                    navigateToHome = navigationActions.navigateToHome,
                    closeDrawer = {
                        coroutineScope.launch {
                            toggleDrawer(scaffoldState)
                        }
                    },
                )
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                HapticSamplerNavGraph(navController = navController)
            }
        }
    }
}

private suspend fun toggleDrawer(scaffoldState: ScaffoldState) {
    scaffoldState.drawerState.apply {
        if (isClosed) open() else close()
    }
}
