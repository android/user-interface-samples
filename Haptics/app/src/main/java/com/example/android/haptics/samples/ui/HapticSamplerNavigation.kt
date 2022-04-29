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
import androidx.compose.foundation.ScrollState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android.haptics.samples.ui.views.HomeRoute
import com.example.android.haptics.samples.ui.views.HomeViewModel

/**
 * Destinations used in the Haptic Sampler app.
 */
object HapticSamplerDestinations {
    const val HOME_ROUTE = "home"
}

class HapticSamplerNavigation(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(HapticSamplerDestinations.HOME_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations as users select nav items.
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when selecting from nav.
            launchSingleTop = true
            // Restore state.
            restoreState = true
        }
    }
}

@Composable
fun HapticSamplerNavGraph(
    application: Application,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = HapticSamplerDestinations.HOME_ROUTE,
    scaffoldState: ScaffoldState,
    scrollState: ScrollState,
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(HapticSamplerDestinations.HOME_ROUTE) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(application, scaffoldState, scrollState)
            )
            HomeRoute(homeViewModel)
        }
    }
}
