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

import android.app.Application
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.android.haptics.samples.R
import com.example.android.haptics.samples.ui.theme.DrawerShape
import com.example.android.haptics.samples.ui.theme.HapticSamplerTheme
import com.example.android.haptics.samples.ui.theme.topAppBarBackgroundColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun HapticSamplerApp(application: Application) {
    HapticSamplerTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            HapticSamplerNavigation(navController)
        }
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val systemUiController = rememberSystemUiController()

        val isScrolled = scrollState.value > 0
        val systemAndTopBarColor = if (isScrolled) MaterialTheme.colors.topAppBarBackgroundColor else MaterialTheme.colors.background

        SideEffect {
            systemUiController.setSystemBarsColor(systemAndTopBarColor)
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.destination?.route ?: HapticSamplerDestinations.HOME_ROUTE
        val snackbarHostState = remember { SnackbarHostState() }
        val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {},
                    elevation = animateDpAsState(if (isScrolled) 4.dp else 0.dp).value,
                    backgroundColor = systemAndTopBarColor,
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
                    navigateToResist = navigationActions.navigateToResist,
                    navigateToExpand = navigationActions.navigateToExpand,
                    navigateToBounce = navigationActions.navigateToBounce,
                    closeDrawer = {
                        coroutineScope.launch {
                            toggleDrawer(scaffoldState)
                        }
                    },
                )
            }
        ) {
            Box() {
                HapticSamplerNavGraph(
                    application = application,
                    navController = navController,
                    scaffoldState = scaffoldState,
                    scrollState = scrollState,
                )
            }
        }
    }
}

private suspend fun toggleDrawer(scaffoldState: ScaffoldState) {
    scaffoldState.drawerState.apply {
        if (isClosed) open() else close()
    }
}
